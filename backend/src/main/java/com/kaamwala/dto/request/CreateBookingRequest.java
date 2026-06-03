package com.kaamwala.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a booking from an accepted bid.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "Job post ID is required")
    private UUID jobPostId;

    @NotNull(message = "Bid ID is required")
    private UUID bidId;
}
