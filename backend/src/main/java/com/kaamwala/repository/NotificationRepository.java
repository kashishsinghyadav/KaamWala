package com.kaamwala.repository;

import com.kaamwala.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link Notification} entity operations.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Find all notifications for a user, ordered by creation date descending.
     *
     * @param userId   the user's UUID
     * @param pageable pagination parameters
     * @return page of notifications for the user
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * Count unread notifications for a user.
     *
     * @param userId the user's UUID
     * @param isRead the read status (pass false to count unread)
     * @return number of unread notifications
     */
    long countByUserIdAndIsRead(UUID userId, boolean isRead);
}
