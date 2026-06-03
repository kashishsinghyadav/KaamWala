package com.kaamwala.repository;

import com.kaamwala.entity.JobPost;
import com.kaamwala.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link JobPost} entity operations.
 */
@Repository
public interface JobPostRepository extends JpaRepository<JobPost, UUID> {

    /**
     * Find job posts filtered by status and category, ordered by creation date descending.
     *
     * @param status   the job status to filter by
     * @param category the service category to filter by
     * @param pageable pagination parameters
     * @return page of matching job posts
     */
    Page<JobPost> findByStatusAndCategoryOrderByCreatedAtDesc(
            JobPost.JobStatus status, ServiceCategory category, Pageable pageable);

    /**
     * Find nearby jobs using the Haversine formula.
     * Only returns jobs with OPEN or BIDDING status.
     *
     * @param latitude  the reference latitude
     * @param longitude the reference longitude
     * @param radiusKm  the search radius in kilometres
     * @param pageable  pagination parameters
     * @return page of nearby job posts
     */
    @Query(value = """
            SELECT jp.* FROM job_posts jp
            WHERE jp.status IN ('OPEN', 'BIDDING')
              AND (6371 * acos(
                    cos(radians(:lat)) * cos(radians(jp.latitude))
                    * cos(radians(jp.longitude) - radians(:lng))
                    + sin(radians(:lat)) * sin(radians(jp.latitude))
                  )) <= :radius
            ORDER BY jp.created_at DESC
            """,
            countQuery = """
                    SELECT count(*) FROM job_posts jp
                    WHERE jp.status IN ('OPEN', 'BIDDING')
                      AND (6371 * acos(
                            cos(radians(:lat)) * cos(radians(jp.latitude))
                            * cos(radians(jp.longitude) - radians(:lng))
                            + sin(radians(:lat)) * sin(radians(jp.latitude))
                          )) <= :radius
                    """,
            nativeQuery = true)
    Page<JobPost> findNearbyJobs(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radiusKm,
            Pageable pageable);

    /**
     * Find all jobs posted by a specific customer.
     *
     * @param customerId the customer's UUID
     * @param pageable   pagination parameters
     * @return page of job posts by the customer
     */
    Page<JobPost> findByCustomerIdOrderByCreatedAtDesc(UUID customerId, Pageable pageable);
}
