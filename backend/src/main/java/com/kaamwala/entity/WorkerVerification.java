package com.kaamwala.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stores verification documents and status for a worker's identity verification.
 *
 * <p>Workers submit Aadhaar, PAN, and a selfie for KYC verification.
 * Document numbers are stored encrypted. An admin reviews and approves/rejects.</p>
 */
@Entity
@Table(name = "worker_verifications", indexes = {
        @Index(name = "idx_wv_worker", columnList = "worker_id", unique = true),
        @Index(name = "idx_wv_status", columnList = "verification_status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worker_id", nullable = false)
    private User worker;

    /** Encrypted Aadhaar number. */
    @Column(name = "aadhaar_number", length = 500)
    private String aadhaarNumber;

    /** Encrypted PAN number. */
    @Column(name = "pan_number", length = 500)
    private String panNumber;

    @Column(name = "selfie_url", length = 500)
    private String selfieUrl;

    @Column(name = "aadhaar_doc_url", length = 500)
    private String aadhaarDocUrl;

    @Column(name = "pan_doc_url", length = 500)
    private String panDocUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Status of the verification process. */
    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }
}
