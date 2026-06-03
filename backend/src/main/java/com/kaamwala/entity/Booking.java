package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a confirmed booking between a customer and a worker.
 *
 * <p>Created when a customer accepts a {@link Bid}. The booking follows a state machine:
 * CONFIRMED → WORKER_EN_ROUTE → IN_PROGRESS → COMPLETED.
 * An OTP code is generated for the worker to verify physical presence before starting work.</p>
 */
@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_booking_worker", columnList = "worker_id"),
        @Index(name = "idx_booking_customer", columnList = "customer_id"),
        @Index(name = "idx_booking_status", columnList = "status"),
        @Index(name = "idx_booking_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_post_id", nullable = false)
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /** 4-digit OTP code shared with customer; worker must enter it to start work. */
    @Column(name = "otp_code", length = 6)
    private String otpCode;

    @Column(name = "worker_started_at")
    private LocalDateTime workerStartedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Lifecycle status of a booking. */
    public enum BookingStatus {
        CONFIRMED,
        WORKER_EN_ROUTE,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        DISPUTED
    }

    /** Payment processing status. */
    public enum PaymentStatus {
        PENDING,
        AUTHORIZED,
        CAPTURED,
        REFUNDED
    }
}
