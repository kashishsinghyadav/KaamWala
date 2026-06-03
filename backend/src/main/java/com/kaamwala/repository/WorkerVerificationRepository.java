package com.kaamwala.repository;

import com.kaamwala.entity.WorkerVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link WorkerVerification} entity operations.
 */
@Repository
public interface WorkerVerificationRepository extends JpaRepository<WorkerVerification, UUID> {

    /**
     * Find verification records for a specific worker.
     *
     * @param workerId the worker's UUID
     * @return an optional containing the verification record if found
     */
    Optional<WorkerVerification> findByWorkerId(UUID workerId);
}
