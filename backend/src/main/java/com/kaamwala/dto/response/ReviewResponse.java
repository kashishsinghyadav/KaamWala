package com.kaamwala.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a review with reviewer information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private UUID id;
    private UUID bookingId;
    private UUID reviewerId;
    private String reviewerName;
    private String reviewerAvatarUrl;
    private UUID workerId;
    private Integer rating;
    private String reviewText;
    private List<String> photos;
    private LocalDateTime createdAt;
}
