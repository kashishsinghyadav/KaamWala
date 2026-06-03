package com.kaamwala.service;

import com.kaamwala.entity.Notification;
import com.kaamwala.entity.User;
import com.kaamwala.exception.ResourceNotFoundException;
import com.kaamwala.repository.NotificationRepository;
import com.kaamwala.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing in-app and push notifications.
 *
 * <p>Currently supports in-app notifications stored in the database.
 * FCM push notification integration is stubbed for future implementation.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    /**
     * Create and persist a notification for a user.
     *
     * @param userId the target user's UUID
     * @param title  the notification title
     * @param body   the notification body
     * @param type   the notification type
     * @param data   optional JSON string with context-specific data
     * @return the created notification
     */
    @Transactional
    public Notification createNotification(UUID userId, String title, String body,
                                            Notification.NotificationType type, String data) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .body(body)
                .type(type)
                .data(data)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);
        log.info("Notification created for user {}: {}", userId, title);

        // Stub: Send FCM push notification if user has an FCM token
        sendPushNotification(user, title, body, data);

        return notification;
    }

    /**
     * Mark a notification as read.
     *
     * @param notificationId the notification UUID
     * @param userId         the authenticated user's UUID
     */
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    /**
     * Get the count of unread notifications for a user.
     *
     * @param userId the user's UUID
     * @return the unread notification count
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsRead(userId, false);
    }

    /**
     * Get paginated notifications for a user.
     *
     * @param userId the user's UUID
     * @param page   the page number (0-based)
     * @param size   the page size
     * @return page of notifications
     */
    @Transactional(readOnly = true)
    public Page<Notification> getUserNotifications(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Stub for FCM push notification.
     * In production, integrate with Firebase Cloud Messaging SDK.
     */
    private void sendPushNotification(User user, String title, String body, String data) {
        if (user.getFcmToken() != null && !user.getFcmToken().isBlank()) {
            log.info("FCM push notification stub - would send to token: {} | title: {} | body: {}",
                    user.getFcmToken(), title, body);
            // TODO: Integrate with Firebase Admin SDK
            // Message message = Message.builder()
            //     .setToken(user.getFcmToken())
            //     .setNotification(com.google.firebase.messaging.Notification.builder()
            //         .setTitle(title)
            //         .setBody(body)
            //         .build())
            //     .build();
            // FirebaseMessaging.getInstance().send(message);
        }
    }
}
