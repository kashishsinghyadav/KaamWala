package com.kaamwala.repository;

import com.kaamwala.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link PortfolioItem} entity operations.
 */
@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, UUID> {

    /**
     * Find all portfolio items for a specific worker, ordered by creation date descending.
     *
     * @param workerId the worker's UUID
     * @return list of portfolio items
     */
    List<PortfolioItem> findByWorkerIdOrderByCreatedAtDesc(UUID workerId);
}
