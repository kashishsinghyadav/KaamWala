package com.kaamwala.controller;

import com.kaamwala.dto.request.BookingStatusUpdate;
import com.kaamwala.dto.response.ApiResponse;
import com.kaamwala.dto.response.BookingResponse;
import com.kaamwala.dto.response.PagedResponse;
import com.kaamwala.entity.User;
import com.kaamwala.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for booking management.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management endpoints")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Get a specific booking by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get booking", description = "Get booking details by ID")
    public ResponseEntity<ApiResponse<BookingResponse>> getBooking(
            @PathVariable UUID id,
            @AuthenticationPrincipal UUID userId) {
        BookingResponse booking = bookingService.getBookingById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    /**
     * Get all bookings for the authenticated user.
     */
    @GetMapping("/my-bookings")
    @Operation(summary = "My bookings", description = "Get all bookings for the authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<BookingResponse>>> getMyBookings(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User.UserRole role = getCurrentUserRole();
        PagedResponse<BookingResponse> bookings = bookingService.getMyBookings(userId, role, page, size);
        return ResponseEntity.ok(ApiResponse.success(bookings));
    }

    /**
     * Update the status of a booking.
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update booking status", description = "Advance booking through the state machine")
    public ResponseEntity<ApiResponse<BookingResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody BookingStatusUpdate statusUpdate,
            @AuthenticationPrincipal UUID userId) {
        BookingResponse booking = bookingService.updateBookingStatus(id, statusUpdate.getStatus(), userId);
        return ResponseEntity.ok(ApiResponse.success(booking, "Booking status updated"));
    }

    /**
     * Verify OTP to start work on a booking.
     */
    @PostMapping("/{id}/verify-otp")
    @Operation(summary = "Verify start OTP", description = "Worker verifies OTP to start work")
    public ResponseEntity<ApiResponse<BookingResponse>> verifyOtp(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UUID userId) {
        String otp = body.get("otp");
        BookingResponse booking = bookingService.verifyStartOtp(id, otp, userId);
        return ResponseEntity.ok(ApiResponse.success(booking, "OTP verified, work started"));
    }

    /**
     * Extract the current user's role from the security context.
     */
    private User.UserRole getCurrentUserRole() {
        var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.contains(new SimpleGrantedAuthority("ROLE_WORKER"))) {
            return User.UserRole.WORKER;
        } else if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return User.UserRole.ADMIN;
        }
        return User.UserRole.CUSTOMER;
    }
}
