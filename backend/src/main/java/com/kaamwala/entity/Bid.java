package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a bid placed by a worker on a job posting.
 *
 * <p>Workers propose their price, message, and estimated duration.
 * The customer can then accept one bid which automatically creates a {@link Booking}.</p>
 */
@Entity
@Table(name = "bids", indexes = {
        @Index(name = "idx_bid_job", columnList = "job_post_id"),
        @Index(name = "idx_bid_worker", columnList = "worker_id"),
        @Index(name = "idx_bid_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bid {

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

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "message", length = 1000)
    private String message;

    /** Estimated time to complete the job, in minutes. */
    @Column(name = "estimated_duration")
    private Integer estimatedDuration;

    @Column(name = "is_accepted")
    @Builder.Default
    private Boolean isAccepted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
