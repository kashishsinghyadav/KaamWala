package com.kaamwala.repository;

import com.kaamwala.entity.ServiceCategory;
import com.kaamwala.entity.WorkerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link WorkerProfile} entity operations.
 */
@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, UUID> {

    /**
     * Find a worker profile by the associated user ID.
     *
     * @param userId the user's UUID
     * @return an optional containing the worker profile if found
     */
    Optional<WorkerProfile> findByUserId(UUID userId);

    /**
     * Find nearby workers using the Haversine formula.
     * Returns workers within the given radius (in km) from the specified lat/lng,
     * filtered by availability status AVAILABLE.
     *
     * @param latitude  the reference latitude
     * @param longitude the reference longitude
     * @param radiusKm  the search radius in kilometres
     * @param pageable  pagination parameters
     * @return page of nearby worker profiles
     */
    @Query(value = """
            SELECT wp.* FROM worker_profiles wp
            JOIN users u ON wp.user_id = u.id
            WHERE wp.availability_status = 'AVAILABLE'
              AND u.is_active = true
              AND (6371 * acos(
                    cos(radians(:lat)) * cos(radians(u.latitude))
                    * cos(radians(u.longitude) - radians(:lng))
                    + sin(radians(:lat)) * sin(radians(u.latitude))
                  )) <= :radius
            ORDER BY (6371 * acos(
                    cos(radians(:lat)) * cos(radians(u.latitude))
                    * cos(radians(u.longitude) - radians(:lng))
                    + sin(radians(:lat)) * sin(radians(u.latitude))
                  )) ASC
            """,
            countQuery = """
                    SELECT count(*) FROM worker_profiles wp
                    JOIN users u ON wp.user_id = u.id
                    WHERE wp.availability_status = 'AVAILABLE'
                      AND u.is_active = true
                      AND (6371 * acos(
                            cos(radians(:lat)) * cos(radians(u.latitude))
                            * cos(radians(u.longitude) - radians(:lng))
                            + sin(radians(:lat)) * sin(radians(u.latitude))
                          )) <= :radius
                    """,
            nativeQuery = true)
    Page<WorkerProfile> findNearbyWorkers(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radiusKm,
            Pageable pageable);

    /**
     * Find workers who have a specific skill in their skills list.
     *
     * @param category the service category to search for
     * @param pageable pagination parameters
     * @return page of matching worker profiles
     */
    @Query("SELECT wp FROM WorkerProfile wp JOIN wp.skills s WHERE s = :category")
    Page<WorkerProfile> findBySkillsContaining(@Param("category") ServiceCategory category, Pageable pageable);
}
