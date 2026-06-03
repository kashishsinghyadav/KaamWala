package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a review left by a customer after a booking is completed.
 *
 * <p>Only one review is permitted per {@link Booking}. The rating (1–5) directly
 * contributes to the worker's average rating calculation.</p>
 */
@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_worker", columnList = "worker_id"),
        @Index(name = "idx_review_booking", columnList = "booking_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    /** Rating from 1 (poor) to 5 (excellent). */
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "review_text", length = 2000)
    private String reviewText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "review_photos", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "photo_url", length = 500)
    @Builder.Default
    private List<String> photos = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
