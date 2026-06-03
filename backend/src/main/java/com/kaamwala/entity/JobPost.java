package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a job posted by a customer seeking a worker.
 *
 * <p>A job goes through the lifecycle: OPEN → BIDDING → BOOKED → IN_PROGRESS → COMPLETED.
 * It can also be CANCELLED at any point before completion.</p>
 */
@Entity
@Table(name = "job_posts", indexes = {
        @Index(name = "idx_job_status", columnList = "status"),
        @Index(name = "idx_job_category", columnList = "category"),
        @Index(name = "idx_job_customer", columnList = "customer_id"),
        @Index(name = "idx_job_location", columnList = "latitude, longitude"),
        @Index(name = "idx_job_created", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private ServiceCategory category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "job_photos", joinColumns = @JoinColumn(name = "job_post_id"))
    @Column(name = "photo_url", length = 500)
    @Builder.Default
    private List<String> photos = new ArrayList<>();

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "address", nullable = false, length = 500)
    private String address;

    @Column(name = "budget_min", precision = 10, scale = 2)
    private BigDecimal budgetMin;

    @Column(name = "budget_max", precision = 10, scale = 2)
    private BigDecimal budgetMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency", nullable = false, length = 20)
    @Builder.Default
    private Urgency urgency = Urgency.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /** Urgency levels for a job posting. */
    public enum Urgency {
        NORMAL,
        URGENT,
        EMERGENCY
    }

    /** Lifecycle status of a job posting. */
    public enum JobStatus {
        OPEN,
        BIDDING,
        BOOKED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
