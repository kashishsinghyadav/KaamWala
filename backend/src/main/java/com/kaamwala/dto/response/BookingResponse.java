package com.kaamwala.dto.response;

import com.kaamwala.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a booking with full details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private UUID id;
    private UUID jobPostId;
    private String jobTitle;
    private UUID workerId;
    private String workerName;
    private String workerPhone;
    private String workerAvatarUrl;
    private UUID customerId;
    private String customerName;
    private String customerPhone;
    private String customerAvatarUrl;
    private BigDecimal finalPrice;
    private Booking.BookingStatus status;
    private Booking.PaymentStatus paymentStatus;
    private String otpCode;
    private LocalDateTime workerStartedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}
