package com.kaamwala.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a bid placed on a job post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidResponse {

    private UUID id;
    private UUID jobPostId;
    private UUID workerId;
    private String workerName;
    private String workerAvatarUrl;
    private BigDecimal workerRatingAvg;
    private Integer workerTotalJobs;
    private BigDecimal price;
    private String message;
    private Integer estimatedDuration;
    private Boolean isAccepted;
    private LocalDateTime createdAt;
}
