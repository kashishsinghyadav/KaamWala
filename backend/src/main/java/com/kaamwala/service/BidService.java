package com.kaamwala.service;

import com.kaamwala.dto.request.CreateBidRequest;
import com.kaamwala.dto.response.BidResponse;
import com.kaamwala.dto.response.BookingResponse;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.entity.*;
import com.kaamwala.exception.BadRequestException;
import com.kaamwala.exception.ResourceNotFoundException;
import com.kaamwala.exception.UnauthorizedException;
import com.kaamwala.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing bids on job postings.
 *
 * <p>Handles bid creation with daily limit enforcement for free-tier workers,
 * bid listing, and bid acceptance which auto-creates a booking.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {

    private static final int FREE_TIER_DAILY_BID_LIMIT = 5;

    private final BidRepository bidRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final BookingRepository bookingRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Create a bid on a job post.
     *
     * <p>Validates that:
     * <ul>
     *   <li>The worker has a matching skill for the job category</li>
     *   <li>The worker hasn't already bid on this job</li>
     *   <li>The job is in OPEN or BIDDING status</li>
     *   <li>Free-tier workers haven't exceeded their daily bid limit</li>
     * </ul>
     * </p>
     *
     * @param jobId    the job post UUID
     * @param request  the bid creation request
     * @param workerId the authenticated worker's UUID
     * @return the created bid response
     */
    @Transactional
    public BidResponse createBid(UUID jobId, CreateBidRequest request, UUID workerId) {
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", workerId));

        if (worker.getRole() != User.UserRole.WORKER) {
            throw new BadRequestException("Only workers can place bids");
        }

        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPost", "id", jobId));

        // Validate job status
        if (jobPost.getStatus() != JobPost.JobStatus.OPEN &&
                jobPost.getStatus() != JobPost.JobStatus.BIDDING) {
            throw new BadRequestException("This job is no longer accepting bids");
        }

        // Check duplicate bid
        if (bidRepository.existsByJobPostIdAndWorkerId(jobId, workerId)) {
            throw new BadRequestException("You have already placed a bid on this job");
        }

        // Validate worker has matching skill
        WorkerProfile workerProfile = workerProfileRepository.findByUserId(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("WorkerProfile", "userId", workerId));

        if (!workerProfile.getSkills().contains(jobPost.getCategory())) {
            throw new BadRequestException("You don't have the required skill for this job category: "
                    + jobPost.getCategory());
        }

        // Check daily bid limit for free-tier workers
        if (workerProfile.getSubscriptionTier() == WorkerProfile.SubscriptionTier.FREE) {
            LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
            long todayBidCount = bidRepository.countByWorkerIdAndCreatedAtAfter(workerId, startOfDay);
            if (todayBidCount >= FREE_TIER_DAILY_BID_LIMIT) {
                throw new BadRequestException(
                        "Free tier workers can place maximum " + FREE_TIER_DAILY_BID_LIMIT +
                                " bids per day. Upgrade to Premium for unlimited bids.");
            }
        }

        Bid bid = Bid.builder()
                .jobPost(jobPost)
                .worker(worker)
                .price(request.getPrice())
                .message(request.getMessage())
                .estimatedDuration(request.getEstimatedDuration())
                .isAccepted(false)
                .build();

        bid = bidRepository.save(bid);

        // Update job status to BIDDING if it was OPEN
        if (jobPost.getStatus() == JobPost.JobStatus.OPEN) {
            jobPost.setStatus(JobPost.JobStatus.BIDDING);
            jobPostRepository.save(jobPost);
        }

        log.info("Bid {} created by worker {} on job {}", bid.getId(), workerId, jobId);

        return mapToBidResponse(bid, workerProfile);
    }

    /**
     * Get all bids for a specific job post.
     *
     * @param jobId the job post UUID
     * @return list of bid responses
     */
    @Transactional(readOnly = true)
    public List<BidResponse> getBidsForJob(UUID jobId) {
        if (!jobPostRepository.existsById(jobId)) {
            throw new ResourceNotFoundException("JobPost", "id", jobId);
        }

        return bidRepository.findByJobPostId(jobId).stream()
                .map(bid -> {
                    WorkerProfile wp = workerProfileRepository.findByUserId(bid.getWorker().getId())
                            .orElse(null);
                    return mapToBidResponse(bid, wp);
                })
                .toList();
    }

    /**
     * Get all bids placed by the authenticated worker.
     *
     * @param workerId the worker's UUID
     * @param page     the page number (0-based)
     * @param size     the page size
     * @return paged list of bid responses
     */
    @Transactional(readOnly = true)
    public PagedResponse<BidResponse> getMyBids(UUID workerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bid> bidPage = bidRepository.findByWorkerIdOrderByCreatedAtDesc(workerId, pageable);

        List<BidResponse> content = bidPage.getContent().stream()
                .map(bid -> {
                    WorkerProfile wp = workerProfileRepository.findByUserId(bid.getWorker().getId())
                            .orElse(null);
                    return mapToBidResponse(bid, wp);
                })
                .toList();

        return PagedResponse.<BidResponse>builder()
                .content(content)
                .page(bidPage.getNumber())
                .size(bidPage.getSize())
                .totalElements(bidPage.getTotalElements())
                .totalPages(bidPage.getTotalPages())
                .last(bidPage.isLast())
                .build();
    }

    /**
     * Accept a bid, which auto-creates a booking and changes the job status to BOOKED.
     *
     * @param bidId      the bid UUID to accept
     * @param customerId the authenticated customer's UUID (must own the job)
     * @return the created booking response
     */
    @Transactional
    public BookingResponse acceptBid(UUID bidId, UUID customerId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid", "id", bidId));

        JobPost jobPost = bid.getJobPost();

        // Verify ownership
        if (!jobPost.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You can only accept bids on your own jobs");
        }

        // Validate job status
        if (jobPost.getStatus() != JobPost.JobStatus.OPEN &&
                jobPost.getStatus() != JobPost.JobStatus.BIDDING) {
            throw new BadRequestException("This job already has an accepted bid");
        }

        // Accept this bid
        bid.setIsAccepted(true);
        bidRepository.save(bid);

        // Update job status to BOOKED
        jobPost.setStatus(JobPost.JobStatus.BOOKED);
        jobPostRepository.save(jobPost);

        // Generate OTP for booking verification
        String otpCode = String.format("%04d", secureRandom.nextInt(10000));

        // Auto-create booking
        Booking booking = Booking.builder()
                .jobPost(jobPost)
                .worker(bid.getWorker())
                .customer(jobPost.getCustomer())
                .finalPrice(bid.getPrice())
                .status(Booking.BookingStatus.CONFIRMED)
                .paymentStatus(Booking.PaymentStatus.PENDING)
                .otpCode(otpCode)
                .build();

        booking = bookingRepository.save(booking);

        // Reject all other bids
        bidRepository.findByJobPostId(jobPost.getId()).forEach(otherBid -> {
            if (!otherBid.getId().equals(bidId)) {
                otherBid.setIsAccepted(false);
                bidRepository.save(otherBid);
            }
        });

        log.info("Bid {} accepted, booking {} created for job {}", bidId, booking.getId(), jobPost.getId());

        return mapToBookingResponse(booking);
    }

    /**
     * Reject a specific bid.
     *
     * @param bidId      the bid UUID to reject
     * @param customerId the authenticated customer's UUID
     */
    @Transactional
    public void rejectBid(UUID bidId, UUID customerId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid", "id", bidId));

        if (!bid.getJobPost().getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You can only reject bids on your own jobs");
        }

        bid.setIsAccepted(false);
        bidRepository.save(bid);
        log.info("Bid {} rejected by customer {}", bidId, customerId);
    }

    private BidResponse mapToBidResponse(Bid bid, WorkerProfile wp) {
        User worker = bid.getWorker();
        return BidResponse.builder()
                .id(bid.getId())
                .jobPostId(bid.getJobPost().getId())
                .workerId(worker.getId())
                .workerName(worker.getName())
                .workerAvatarUrl(worker.getAvatarUrl())
                .workerRatingAvg(wp != null ? wp.getRatingAvg() : null)
                .workerTotalJobs(wp != null ? wp.getTotalJobs() : null)
                .price(bid.getPrice())
                .message(bid.getMessage())
                .estimatedDuration(bid.getEstimatedDuration())
                .isAccepted(bid.getIsAccepted())
                .createdAt(bid.getCreatedAt())
                .build();
    }

    private BookingResponse mapToBookingResponse(Booking booking) {
        User worker = booking.getWorker();
        User customer = booking.getCustomer();

        return BookingResponse.builder()
                .id(booking.getId())
                .jobPostId(booking.getJobPost().getId())
                .jobTitle(booking.getJobPost().getTitle())
                .workerId(worker.getId())
                .workerName(worker.getName())
                .workerPhone(worker.getPhone())
                .workerAvatarUrl(worker.getAvatarUrl())
                .customerId(customer.getId())
                .customerName(customer.getName())
                .customerPhone(customer.getPhone())
                .customerAvatarUrl(customer.getAvatarUrl())
                .finalPrice(booking.getFinalPrice())
                .status(booking.getStatus())
                .paymentStatus(booking.getPaymentStatus())
                .otpCode(booking.getOtpCode())
                .workerStartedAt(booking.getWorkerStartedAt())
                .completedAt(booking.getCompletedAt())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
