package com.kaamwala.dto.response;

import com.kaamwala.entity.ServiceCategory;
import com.kaamwala.entity.WorkerProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a worker's full profile information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerProfileResponse {

    private UUID userId;
    private String name;
    private String phone;
    private String email;
    private String avatarUrl;
    private Double latitude;
    private Double longitude;
    private List<ServiceCategory> skills;
    private List<String> serviceAreas;
    private BigDecimal startingPrice;
    private Boolean isVerified;
    private Boolean aadhaarVerified;
    private Boolean panVerified;
    private Boolean selfieVerified;
    private BigDecimal ratingAvg;
    private Integer totalJobs;
    private BigDecimal totalEarnings;
    private String bio;
    private WorkerProfile.SubscriptionTier subscriptionTier;
    private WorkerProfile.AvailabilityStatus availabilityStatus;
}
