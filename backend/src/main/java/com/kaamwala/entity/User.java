package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user on the KaamWala platform.
 *
 * <p>A user can be a {@link UserRole#CUSTOMER} who posts jobs, a {@link UserRole#WORKER}
 * who bids on and fulfils jobs, or an {@link UserRole#ADMIN} who manages the platform.</p>
 *
 * <p>Phone number is the primary identity since auth is OTP-based.</p>
 */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_phone_role", columnList = "phone, role", unique = true),
        @Index(name = "idx_user_role", columnList = "role"),
        @Index(name = "idx_user_location", columnList = "latitude, longitude")
}, uniqueConstraints = {
        @UniqueConstraint(name = "unique_phone_role", columnNames = {"phone", "role"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "fcm_token", length = 500)
    private String fcmToken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Roles a user can hold on the platform.
     */
    public enum UserRole {
        CUSTOMER,
        WORKER,
        ADMIN
    }
}
