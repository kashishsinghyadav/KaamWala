package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Extended profile for users with the {@link User.UserRole#WORKER} role.
 *
 * <p>Contains skills, service areas, verification status, earnings, ratings,
 * and subscription tier information. Linked 1-to-1 with a {@link User}.</p>
 */
@Entity
@Table(name = "worker_profiles", indexes = {
        @Index(name = "idx_wp_user_id", columnList = "user_id", unique = true),
        @Index(name = "idx_wp_availability", columnList = "availability_status"),
        @Index(name = "idx_wp_verified", columnList = "is_verified")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Set of service categories the worker is skilled in.
     */
    @ElementCollection(targetClass = ServiceCategory.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "worker_skills", joinColumns = @JoinColumn(name = "worker_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "skill")
    @Builder.Default
    private List<ServiceCategory> skills = new ArrayList<>();

    /**
     * List of area names / pin-codes the worker is willing to serve.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "worker_service_areas", joinColumns = @JoinColumn(name = "worker_profile_id"))
    @Column(name = "area", length = 200)
    @Builder.Default
    private List<String> serviceAreas = new ArrayList<>();

    @Column(name = "starting_price", precision = 10, scale = 2)
    private BigDecimal startingPrice;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "aadhaar_verified", nullable = false)
    @Builder.Default
    private Boolean aadhaarVerified = false;

    @Column(name = "pan_verified", nullable = false)
    @Builder.Default
    private Boolean panVerified = false;

    @Column(name = "selfie_verified", nullable = false)
    @Builder.Default
    private Boolean selfieVerified = false;

    @Column(name = "rating_avg", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal ratingAvg = BigDecimal.ZERO;

    @Column(name = "total_jobs", nullable = false)
    @Builder.Default
    private Integer totalJobs = 0;

    @Column(name = "total_earnings", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column(name = "bio", length = 1000)
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_tier", nullable = false, length = 20)
    @Builder.Default
    private SubscriptionTier subscriptionTier = SubscriptionTier.FREE;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 20)
    @Builder.Default
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.OFFLINE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Subscription tiers controlling feature access and bid limits. */
    public enum SubscriptionTier {
        FREE,
        PREMIUM
    }

    /** Current availability of the worker for new jobs. */
    public enum AvailabilityStatus {
        AVAILABLE,
        BUSY,
        OFFLINE
    }
}
