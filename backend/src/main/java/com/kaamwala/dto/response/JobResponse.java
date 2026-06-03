package com.kaamwala.dto.response;

import com.kaamwala.entity.JobPost;
import com.kaamwala.entity.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a single job post with full details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {

    private UUID id;
    private UUID customerId;
    private String customerName;
    private String customerAvatarUrl;
    private ServiceCategory category;
    private String title;
    private String description;
    private List<String> photos;
    private Double latitude;
    private Double longitude;
    private String address;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private JobPost.Urgency urgency;
    private JobPost.JobStatus status;
    private int bidCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
