package com.kaamwala.controller;

import com.kaamwala.dto.response.ApiResponse;
import com.kaamwala.entity.Notification;
import com.kaamwala.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for notification management.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get paginated notifications for the authenticated user.
     */
    @GetMapping
    @Operation(summary = "Get notifications", description = "Get paginated notifications for the authenticated user")
    public ResponseEntity<ApiResponse<Page<Notification>>> getNotifications(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Notification> notifications = notificationService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * Mark a notification as read.
     */
    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark a specific notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification marked as read"));
    }

    /**
     * Get the count of unread notifications.
     */
    @GetMapping("/unread-count")
    @Operation(summary = "Unread count", description = "Get the number of unread notifications")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@AuthenticationPrincipal UUID userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
