package com.kaamwala.dto.response;

import com.kaamwala.entity.ServiceCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for a worker's portfolio item.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {

    private UUID id;
    private UUID workerId;
    private String title;
    private String description;
    private String beforeImageUrl;
    private String afterImageUrl;
    private String videoUrl;
    private ServiceCategory category;
    private LocalDateTime createdAt;
}
