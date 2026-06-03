package com.kaamwala.service;

import com.kaamwala.dto.request.CreateReviewRequest;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.dto.response.ReviewResponse;
import com.kaamwala.entity.Booking;
import com.kaamwala.entity.Review;
import com.kaamwala.entity.User;
import com.kaamwala.entity.WorkerProfile;
import com.kaamwala.exception.BadRequestException;
import com.kaamwala.exception.ResourceNotFoundException;
import com.kaamwala.exception.UnauthorizedException;
import com.kaamwala.repository.BookingRepository;
import com.kaamwala.repository.ReviewRepository;
import com.kaamwala.repository.WorkerProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing reviews and worker rating calculations.
 *
 * <p>Enforces business rules: only one review per booking, only after completion,
 * and only by the customer. Automatically recalculates the worker's average rating.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final WorkerProfileRepository workerProfileRepository;

    /**
     * Create a review for a completed booking.
     *
     * @param bookingId  the booking UUID
     * @param request    the review creation request
     * @param reviewerId the authenticated customer's UUID
     * @return the created review response
     */
    @Transactional
    public ReviewResponse createReview(UUID bookingId, CreateReviewRequest request, UUID reviewerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        // Only the customer can leave a review
        if (!booking.getCustomer().getId().equals(reviewerId)) {
            throw new UnauthorizedException("Only the customer can review this booking");
        }

        // Only completed bookings can be reviewed
        if (booking.getStatus() != Booking.BookingStatus.COMPLETED) {
            throw new BadRequestException("Reviews can only be left for completed bookings");
        }

        // One review per booking
        if (reviewRepository.existsByBookingId(bookingId)) {
            throw new BadRequestException("A review has already been submitted for this booking");
        }

        Review review = Review.builder()
                .booking(booking)
                .reviewer(booking.getCustomer())
                .worker(booking.getWorker())
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .photos(request.getPhotos() != null ? request.getPhotos() : List.of())
                .build();

        review = reviewRepository.save(review);

        // Update worker's average rating
        updateWorkerAverageRating(booking.getWorker().getId());

        log.info("Review created for booking {} by customer {}", bookingId, reviewerId);

        return mapToReviewResponse(review);
    }

    /**
     * Get all reviews for a worker.
     *
     * @param workerId the worker's UUID
     * @param page     the page number (0-based)
     * @param size     the page size
     * @return paged list of review responses
     */
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getWorkerReviews(UUID workerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByWorkerIdOrderByCreatedAtDesc(workerId, pageable);

        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(this::mapToReviewResponse)
                .toList();

        return PagedResponse.<ReviewResponse>builder()
                .content(content)
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .last(reviewPage.isLast())
                .build();
    }

    /**
     * Recalculate and update a worker's average rating from all reviews.
     *
     * @param workerId the worker's UUID
     */
    private void updateWorkerAverageRating(UUID workerId) {
        Double avgRating = reviewRepository.calculateAverageRatingByWorkerId(workerId);
        workerProfileRepository.findByUserId(workerId).ifPresent(profile -> {
            if (avgRating != null) {
                profile.setRatingAvg(BigDecimal.valueOf(avgRating).setScale(2, RoundingMode.HALF_UP));
            }
            workerProfileRepository.save(profile);
        });
        log.debug("Worker {} average rating updated to {}", workerId, avgRating);
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        User reviewer = review.getReviewer();
        return ReviewResponse.builder()
                .id(review.getId())
                .bookingId(review.getBooking().getId())
                .reviewerId(reviewer.getId())
                .reviewerName(reviewer.getName())
                .reviewerAvatarUrl(reviewer.getAvatarUrl())
                .workerId(review.getWorker().getId())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .photos(review.getPhotos())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
