package com.kaamwala.repository;

import com.kaamwala.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Booking} entity operations.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /**
     * Find all bookings for a specific worker.
     *
     * @param workerId the worker's UUID
     * @param pageable pagination parameters
     * @return page of bookings for the worker
     */
    Page<Booking> findByWorkerIdOrderByCreatedAtDesc(UUID workerId, Pageable pageable);

    /**
     * Find all bookings for a specific customer.
     *
     * @param customerId the customer's UUID
     * @param pageable   pagination parameters
     * @return page of bookings for the customer
     */
    Page<Booking> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);

    /**
     * Find bookings by status.
     *
     * @param status the booking status to filter by
     * @return list of bookings with the given status
     */
    List<Booking> findByStatus(Booking.BookingStatus status);

    /**
     * Count completed bookings for a worker.
     *
     * @param workerId the worker's UUID
     * @param status   the booking status
     * @return number of bookings matching the criteria
     */
    long countByWorkerIdAndStatus(UUID workerId, Booking.BookingStatus status);
}
