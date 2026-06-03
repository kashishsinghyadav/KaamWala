package com.kaamwala.service;

import com.kaamwala.dto.request.CreateJobRequest;
import com.kaamwala.dto.response.JobResponse;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.entity.JobPost;
import com.kaamwala.entity.ServiceCategory;
import com.kaamwala.entity.User;
import com.kaamwala.exception.BadRequestException;
import com.kaamwala.exception.ResourceNotFoundException;
import com.kaamwala.exception.UnauthorizedException;
import com.kaamwala.repository.BidRepository;
import com.kaamwala.repository.JobPostRepository;
import com.kaamwala.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for managing job postings.
 *
 * <p>Handles creation, retrieval, nearby search, status updates, and deletion of jobs.
 * When a job status changes to BOOKED, all other pending bids are auto-rejected.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    /**
     * Create a new job posting.
     *
     * @param request  the job creation request
     * @param customerId the authenticated customer's UUID
     * @return the created job response
     */
    @Transactional
    public JobResponse createJob(CreateJobRequest request, UUID customerId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", customerId));

        if (customer.getRole() != User.UserRole.CUSTOMER && customer.getRole() != User.UserRole.ADMIN) {
            throw new BadRequestException("Only customers can post jobs");
        }

        JobPost jobPost = JobPost.builder()
                .customer(customer)
                .category(request.getCategory())
                .title(request.getTitle())
                .description(request.getDescription())
                .photos(request.getPhotos() != null ? request.getPhotos() : java.util.List.of())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .budgetMin(request.getBudgetMin())
                .budgetMax(request.getBudgetMax())
                .urgency(request.getUrgency() != null ? request.getUrgency() : JobPost.Urgency.NORMAL)
                .status(JobPost.JobStatus.OPEN)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        jobPost = jobPostRepository.save(jobPost);
        log.info("Job created: {} by customer {}", jobPost.getId(), customerId);

        return mapToJobResponse(jobPost);
    }

    /**
     * Get a job post by its ID.
     *
     * @param jobId the job UUID
     * @return the job response
     */
    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID jobId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPost", "id", jobId));
        return mapToJobResponse(jobPost);
    }

    /**
     * Find nearby jobs using geolocation.
     *
     * @param latitude  the search latitude
     * @param longitude the search longitude
     * @param radiusKm  the search radius in kilometres
     * @param page      the page number (0-based)
     * @param size      the page size
     * @return paged list of nearby jobs
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobResponse> getNearbyJobs(double latitude, double longitude,
                                                     double radiusKm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> jobPage = jobPostRepository.findNearbyJobs(latitude, longitude, radiusKm, pageable);
        return mapToPagedResponse(jobPage);
    }

    /**
     * Find jobs filtered by status and category.
     *
     * @param status   the job status filter
     * @param category the service category filter
     * @param page     the page number (0-based)
     * @param size     the page size
     * @return paged list of matching jobs
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobResponse> getJobsByCategory(JobPost.JobStatus status,
                                                         ServiceCategory category,
                                                         int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> jobPage = jobPostRepository.findByStatusAndCategoryOrderByCreatedAtDesc(
                status, category, pageable);
        return mapToPagedResponse(jobPage);
    }

    /**
     * Get all jobs posted by the authenticated customer.
     *
     * @param customerId the customer's UUID
     * @param page       the page number (0-based)
     * @param size       the page size
     * @return paged list of the customer's jobs
     */
    @Transactional(readOnly = true)
    public PagedResponse<JobResponse> getMyPostedJobs(UUID customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> jobPage = jobPostRepository.findByCustomerIdOrderByCreatedAtDesc(customerId, pageable);
        return mapToPagedResponse(jobPage);
    }

    /**
     * Update the status of a job post.
     *
     * <p>When status changes to BOOKED, all non-accepted bids are auto-rejected.</p>
     *
     * @param jobId      the job UUID
     * @param newStatus  the new status
     * @param customerId the authenticated customer's UUID
     * @return the updated job response
     */
    @Transactional
    public JobResponse updateJobStatus(UUID jobId, JobPost.JobStatus newStatus, UUID customerId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPost", "id", jobId));

        if (!jobPost.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You can only update your own jobs");
        }

        jobPost.setStatus(newStatus);

        // Auto-reject other bids when job is booked
        if (newStatus == JobPost.JobStatus.BOOKED) {
            bidRepository.findByJobPostId(jobId).forEach(bid -> {
                if (!bid.getIsAccepted()) {
                    bid.setIsAccepted(false);
                    bidRepository.save(bid);
                }
            });
        }

        jobPost = jobPostRepository.save(jobPost);
        log.info("Job {} status updated to {}", jobId, newStatus);

        return mapToJobResponse(jobPost);
    }

    /**
     * Delete a job post. Only the owning customer can delete and only if status is OPEN.
     *
     * @param jobId      the job UUID
     * @param customerId the authenticated customer's UUID
     */
    @Transactional
    public void deleteJob(UUID jobId, UUID customerId) {
        JobPost jobPost = jobPostRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("JobPost", "id", jobId));

        if (!jobPost.getCustomer().getId().equals(customerId)) {
            throw new UnauthorizedException("You can only delete your own jobs");
        }

        if (jobPost.getStatus() != JobPost.JobStatus.OPEN) {
            throw new BadRequestException("Can only delete jobs with OPEN status");
        }

        jobPostRepository.delete(jobPost);
        log.info("Job {} deleted by customer {}", jobId, customerId);
    }

    /**
     * Map a JobPost entity to a JobResponse DTO.
     */
    private JobResponse mapToJobResponse(JobPost jobPost) {
        int bidCount = bidRepository.findByJobPostId(jobPost.getId()).size();
        User customer = jobPost.getCustomer();

        return JobResponse.builder()
                .id(jobPost.getId())
                .customerId(customer.getId())
                .customerName(customer.getName())
                .customerAvatarUrl(customer.getAvatarUrl())
                .category(jobPost.getCategory())
                .title(jobPost.getTitle())
                .description(jobPost.getDescription())
                .photos(jobPost.getPhotos())
                .latitude(jobPost.getLatitude())
                .longitude(jobPost.getLongitude())
                .address(jobPost.getAddress())
                .budgetMin(jobPost.getBudgetMin())
                .budgetMax(jobPost.getBudgetMax())
                .urgency(jobPost.getUrgency())
                .status(jobPost.getStatus())
                .bidCount(bidCount)
                .createdAt(jobPost.getCreatedAt())
                .expiresAt(jobPost.getExpiresAt())
                .build();
    }

    /**
     * Map a Page of JobPost entities to a PagedResponse of JobResponse DTOs.
     */
    private PagedResponse<JobResponse> mapToPagedResponse(Page<JobPost> jobPage) {
        return PagedResponse.<JobResponse>builder()
                .content(jobPage.getContent().stream().map(this::mapToJobResponse).toList())
                .page(jobPage.getNumber())
                .size(jobPage.getSize())
                .totalElements(jobPage.getTotalElements())
                .totalPages(jobPage.getTotalPages())
                .last(jobPage.isLast())
                .build();
    }
}
