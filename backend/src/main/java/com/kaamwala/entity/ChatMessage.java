package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a chat message between a customer and worker within a booking context.
 *
 * <p>Messages are persisted to the database and also delivered in real-time
 * via WebSocket (STOMP) to the {@code /topic/chat/{bookingId}} destination.</p>
 */
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_booking", columnList = "booking_id"),
        @Index(name = "idx_chat_sender", columnList = "sender_id"),
        @Index(name = "idx_chat_receiver", columnList = "receiver_id"),
        @Index(name = "idx_chat_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(name = "message", nullable = false, length = 4000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false, length = 20)
    @Builder.Default
    private MessageType messageType = MessageType.TEXT;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Types of chat messages. */
    public enum MessageType {
        TEXT,
        IMAGE,
        SYSTEM
    }
}
