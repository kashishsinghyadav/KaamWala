package com.kaamwala.service;

import com.kaamwala.entity.ChatMessage;
import com.kaamwala.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing chat messages between customers and workers.
 *
 * <p>Messages are persisted to the database. Real-time delivery is handled
 * by the WebSocket layer in {@link com.kaamwala.websocket.ChatWebSocketHandler}.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    /**
     * Save a chat message to the database.
     *
     * @param message the chat message entity to save
     * @return the saved message with generated ID and timestamp
     */
    @Transactional
    public ChatMessage saveMessage(ChatMessage message) {
        ChatMessage saved = chatMessageRepository.save(message);
        log.debug("Chat message saved: {} from {} to {} in booking {}",
                saved.getId(), saved.getSenderId(), saved.getReceiverId(), saved.getBookingId());
        return saved;
    }

    /**
     * Get the full chat history for a booking in chronological order.
     *
     * @param bookingId the booking UUID
     * @return list of chat messages ordered by creation time ascending
     */
    @Transactional(readOnly = true)
    public List<ChatMessage> getHistory(UUID bookingId) {
        return chatMessageRepository.findByBookingIdOrderByCreatedAtAsc(bookingId);
    }

    /**
     * Mark all messages sent to a user in a booking as read.
     *
     * @param bookingId  the booking UUID
     * @param receiverId the receiver's UUID
     */
    @Transactional
    public void markAsRead(UUID bookingId, UUID receiverId) {
        List<ChatMessage> messages = chatMessageRepository
                .findByBookingIdOrderByCreatedAtAsc(bookingId);

        messages.stream()
                .filter(m -> m.getReceiverId().equals(receiverId) && !m.getIsRead())
                .forEach(m -> {
                    m.setIsRead(true);
                    chatMessageRepository.save(m);
                });

        log.debug("Marked messages as read for receiver {} in booking {}", receiverId, bookingId);
    }

    /**
     * Get the count of unread messages for a user.
     *
     * @param userId the user's UUID
     * @return the unread message count
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return chatMessageRepository.countByReceiverIdAndIsRead(userId, false);
    }
}
