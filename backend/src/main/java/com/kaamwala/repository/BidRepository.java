package com.kaamwala.repository;

import com.kaamwala.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Bid} entity operations.
 */
@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    /**
     * Find all bids for a specific job post.
     *
     * @param jobPostId the job post UUID
     * @return list of bids for the job
     */
    List<Bid> findByJobPostId(UUID jobPostId);

    /**
     * Find all bids placed by a specific worker.
     *
     * @param workerId the worker's UUID
     * @param pageable pagination parameters
     * @return page of bids by the worker
     */
    Page<Bid> findByWorkerIdOrderByCreatedAtDesc(UUID workerId, Pageable pageable);

    /**
     * Count bids placed by a worker after a given timestamp.
     * Used to enforce daily bid limits for free-tier workers.
     *
     * @param workerId the worker's UUID
     * @param after    the timestamp to count from
     * @return the number of bids placed after the given timestamp
     */
    long countByWorkerIdAndCreatedAtAfter(UUID workerId, LocalDateTime after);

    /**
     * Check if a worker has already bid on a specific job.
     *
     * @param jobPostId the job post UUID
     * @param workerId  the worker's UUID
     * @return true if the worker has already bid
     */
    boolean existsByJobPostIdAndWorkerId(UUID jobPostId, UUID workerId);
}
