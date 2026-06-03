package com.kaamwala.repository;

import com.kaamwala.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link ChatMessage} entity operations.
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /**
     * Find all chat messages for a booking, ordered by creation time ascending.
     *
     * @param bookingId the booking UUID
     * @return list of messages in chronological order
     */
    List<ChatMessage> findByBookingIdOrderByCreatedAtAsc(UUID bookingId);

    /**
     * Count unread messages for a specific receiver.
     *
     * @param receiverId the receiver's UUID
     * @param isRead     the read status (pass false to count unread)
     * @return number of unread messages
     */
    long countByReceiverIdAndIsRead(UUID receiverId, boolean isRead);
}
