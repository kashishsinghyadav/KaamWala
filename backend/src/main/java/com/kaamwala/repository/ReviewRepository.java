package com.kaamwala.repository;

import com.kaamwala.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Review} entity operations.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    /**
     * Find all reviews for a specific worker, ordered by creation date descending.
     *
     * @param workerId the worker's UUID
     * @param pageable pagination parameters
     * @return page of reviews for the worker
     */
    Page<Review> findByWorkerIdOrderByCreatedAtDesc(UUID workerId, Pageable pageable);

    /**
     * Calculate the average rating for a worker.
     *
     * @param workerId the worker's UUID
     * @return the average rating, or null if no reviews exist
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.worker.id = :workerId")
    Double calculateAverageRatingByWorkerId(@Param("workerId") UUID workerId);

    /**
     * Find a review by the booking ID.
     *
     * @param bookingId the booking UUID
     * @return an optional containing the review if found
     */
    Optional<Review> findByBookingId(UUID bookingId);

    /**
     * Check if a review exists for a specific booking.
     *
     * @param bookingId the booking UUID
     * @return true if a review already exists for this booking
     */
    boolean existsByBookingId(UUID bookingId);
}
