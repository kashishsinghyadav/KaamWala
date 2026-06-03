package com.kaamwala.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for placing a bid on a job post.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBidRequest {

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1", message = "Price must be at least 1")
    private BigDecimal price;

    @Size(max = 1000, message = "Message must not exceed 1000 characters")
    private String message;

    /** Estimated time to complete, in minutes. */
    @Min(value = 1, message = "Estimated duration must be at least 1 minute")
    private Integer estimatedDuration;
}
