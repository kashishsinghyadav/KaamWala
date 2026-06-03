package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a notification sent to a user.
 *
 * <p>Notifications cover job lifecycle events, bid updates, payment confirmations,
 * and system-level announcements. The {@code data} field holds a JSON string with
 * context-specific payload (e.g., job ID, booking ID).</p>
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notif_user", columnList = "user_id"),
        @Index(name = "idx_notif_read", columnList = "is_read"),
        @Index(name = "idx_notif_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "body", nullable = false, length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    /** JSON string containing context-specific data (e.g., jobId, bookingId). */
    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Categories of notification events. */
    public enum NotificationType {
        NEW_JOB,
        NEW_BID,
        BID_ACCEPTED,
        BOOKING_CONFIRMED,
        PAYMENT,
        REVIEW,
        SYSTEM
    }
}
