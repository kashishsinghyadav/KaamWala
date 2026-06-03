package com.kaamwala.service;

import com.kaamwala.dto.response.BookingResponse;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.entity.Booking;
import com.kaamwala.entity.JobPost;
import com.kaamwala.entity.User;
import com.kaamwala.entity.WorkerProfile;
import com.kaamwala.exception.BadRequestException;
import com.kaamwala.exception.ResourceNotFoundException;
import com.kaamwala.exception.UnauthorizedException;
import com.kaamwala.repository.BookingRepository;
import com.kaamwala.repository.WorkerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing bookings through their lifecycle.
 *
 * <p>Enforces a state machine for booking status transitions:
 * CONFIRMED → WORKER_EN_ROUTE → IN_PROGRESS → COMPLETED.
 * OTP verification is required to start work (transition to IN_PROGRESS).</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final WorkerProfileRepository workerProfileRepository;

    /**
     * Get a booking by its ID. Only the involved worker or customer can view it.
     *
     * @param bookingId the booking UUID
     * @param userId    the authenticated user's UUID
     * @return the booking response
     */
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (!booking.getWorker().getId().equals(userId) &&
                !booking.getCustomer().getId().equals(userId)) {
            throw new UnauthorizedException("You do not have access to this booking");
        }

        return mapToBookingResponse(booking);
    }

    /**
     * Get all bookings for the authenticated user (as either worker or customer).
     *
     * @param userId the authenticated user's UUID
     * @param role   the user's role
     * @param page   the page number (0-based)
     * @param size   the page size
     * @return paged list of bookings
     */
    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> getMyBookings(UUID userId, User.UserRole role,
                                                         int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookingPage;

        if (role == User.UserRole.WORKER) {
            bookingPage = bookingRepository.findByWorkerIdOrderByCreatedAtDesc(userId, pageable);
        } else {
            bookingPage = bookingRepository.findByCustomerIdOrderByCreatedAtDesc(userId, pageable);
        }

        List<BookingResponse> content = bookingPage.getContent().stream()
                .map(this::mapToBookingResponse)
                .toList();

        return PagedResponse.<BookingResponse>builder()
                .content(content)
                .page(bookingPage.getNumber())
                .size(bookingPage.getSize())
                .totalElements(bookingPage.getTotalElements())
                .totalPages(bookingPage.getTotalPages())
                .last(bookingPage.isLast())
                .build();
    }

    /**
     * Update booking status through the state machine.
     *
     * <p>Valid transitions:
     * <ul>
     *   <li>CONFIRMED → WORKER_EN_ROUTE (by worker)</li>
     *   <li>WORKER_EN_ROUTE → IN_PROGRESS (by worker, requires OTP verification)</li>
     *   <li>IN_PROGRESS → COMPLETED (by worker)</li>
     *   <li>Any non-completed → CANCELLED (by either party)</li>
     * </ul>
     * </p>
     *
     * @param bookingId the booking UUID
     * @param newStatus the target status
     * @param userId    the authenticated user's UUID
     * @return the updated booking response
     */
    @Transactional
    public BookingResponse updateBookingStatus(UUID bookingId, Booking.BookingStatus newStatus,
                                                UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        // Authorization check
        boolean isWorker = booking.getWorker().getId().equals(userId);
        boolean isCustomer = booking.getCustomer().getId().equals(userId);
        if (!isWorker && !isCustomer) {
            throw new UnauthorizedException("You do not have access to this booking");
        }

        validateStatusTransition(booking.getStatus(), newStatus, isWorker);

        booking.setStatus(newStatus);

        if (newStatus == Booking.BookingStatus.WORKER_EN_ROUTE) {
            // Worker is on the way
            log.info("Worker en route for booking {}", bookingId);
        } else if (newStatus == Booking.BookingStatus.IN_PROGRESS) {
            booking.setWorkerStartedAt(LocalDateTime.now());
        } else if (newStatus == Booking.BookingStatus.COMPLETED) {
            booking.setCompletedAt(LocalDateTime.now());
            booking.setPaymentStatus(Booking.PaymentStatus.CAPTURED);
            // Update worker stats
            updateWorkerStatsOnCompletion(booking);
        } else if (newStatus == Booking.BookingStatus.CANCELLED) {
            booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);
        }

        booking = bookingRepository.save(booking);
        log.info("Booking {} status updated to {} by user {}", bookingId, newStatus, userId);

        return mapToBookingResponse(booking);
    }

    /**
     * Verify the start OTP to transition booking to IN_PROGRESS.
     *
     * @param bookingId the booking UUID
     * @param otp       the OTP code entered by the worker
     * @param workerId  the authenticated worker's UUID
     * @return the updated booking response
     */
    @Transactional
    public BookingResponse verifyStartOtp(UUID bookingId, String otp, UUID workerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (!booking.getWorker().getId().equals(workerId)) {
            throw new UnauthorizedException("Only the assigned worker can verify the OTP");
        }

        if (booking.getStatus() != Booking.BookingStatus.WORKER_EN_ROUTE) {
            throw new BadRequestException("Booking must be in WORKER_EN_ROUTE status to verify OTP");
        }

        if (!booking.getOtpCode().equals(otp)) {
            throw new BadRequestException("Invalid OTP code");
        }

        booking.setStatus(Booking.BookingStatus.IN_PROGRESS);
        booking.setWorkerStartedAt(LocalDateTime.now());
        booking = bookingRepository.save(booking);

        log.info("OTP verified, booking {} started", bookingId);

        return mapToBookingResponse(booking);
    }

    /**
     * Cancel a booking.
     *
     * @param bookingId the booking UUID
     * @param userId    the authenticated user's UUID
     * @return the updated booking response
     */
    @Transactional
    public BookingResponse cancelBooking(UUID bookingId, UUID userId) {
        return updateBookingStatus(bookingId, Booking.BookingStatus.CANCELLED, userId);
    }

    /**
     * Validate that a status transition is allowed.
     */
    private void validateStatusTransition(Booking.BookingStatus current,
                                          Booking.BookingStatus target,
                                          boolean isWorker) {
        if (target == Booking.BookingStatus.CANCELLED) {
            if (current == Booking.BookingStatus.COMPLETED) {
                throw new BadRequestException("Cannot cancel a completed booking");
            }
            return;
        }

        boolean valid = switch (current) {
            case CONFIRMED -> target == Booking.BookingStatus.WORKER_EN_ROUTE && isWorker;
            case WORKER_EN_ROUTE -> target == Booking.BookingStatus.IN_PROGRESS && isWorker;
            case IN_PROGRESS -> target == Booking.BookingStatus.COMPLETED && isWorker;
            default -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                    String.format("Invalid status transition from %s to %s", current, target));
        }
    }

    /**
     * Update worker profile stats after a booking is completed.
     */
    private void updateWorkerStatsOnCompletion(Booking booking) {
        workerProfileRepository.findByUserId(booking.getWorker().getId())
                .ifPresent(profile -> {
                    profile.setTotalJobs(profile.getTotalJobs() + 1);
                    profile.setTotalEarnings(
                            profile.getTotalEarnings().add(booking.getFinalPrice()));
                    workerProfileRepository.save(profile);
                });
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
