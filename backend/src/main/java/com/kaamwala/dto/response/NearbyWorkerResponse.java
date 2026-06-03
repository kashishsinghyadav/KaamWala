package com.kaamwala.dto.response;

import com.kaamwala.entity.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for nearby worker search results, includes distance from the search point.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyWorkerResponse {

    private UUID userId;
    private String name;
    private String avatarUrl;
    private List<ServiceCategory> skills;
    private BigDecimal startingPrice;
    private BigDecimal ratingAvg;
    private Integer totalJobs;
    private Boolean isVerified;
    private String bio;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
}
