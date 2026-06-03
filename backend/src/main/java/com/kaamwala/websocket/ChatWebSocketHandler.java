package com.kaamwala.websocket;

import com.kaamwala.entity.ChatMessage;
import com.kaamwala.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

/**
 * WebSocket controller for real-time chat messaging.
 *
 * <p>Handles incoming chat messages via STOMP protocol at {@code /app/chat.send/{bookingId}}
 * and broadcasts them to {@code /topic/chat/{bookingId}} for all participants in the booking.</p>
 *
 * <p>Also sends a private notification to the receiver's queue at
 * {@code /queue/chat/notification} for unread message badge updates.</p>
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle incoming chat messages and broadcast to the booking's topic.
     *
     * <p>Client sends to: {@code /app/chat.send/{bookingId}}<br>
     * Subscribers on: {@code /topic/chat/{bookingId}} receive the message</p>
     *
     * @param bookingId the booking UUID from the destination path
     * @param message   the incoming chat message payload
     * @return the saved chat message (broadcast to subscribers)
     */
    @MessageMapping("/chat.send/{bookingId}")
    @SendTo("/topic/chat/{bookingId}")
    public ChatMessage handleChatMessage(
            @DestinationVariable String bookingId,
            @Payload ChatMessage message) {

        // Ensure booking ID is set from the path
        message.setBookingId(UUID.fromString(bookingId));

        // Persist the message
        ChatMessage saved = chatService.saveMessage(message);

        log.debug("Chat message sent in booking {}: {} → {}",
                bookingId, saved.getSenderId(), saved.getReceiverId());

        // Send notification to the receiver's personal queue
        messagingTemplate.convertAndSend(
                "/queue/chat/notification/" + saved.getReceiverId(),
                saved
        );

        return saved;
    }
}
