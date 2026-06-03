package com.kaamwala.dto.request;

import com.kaamwala.entity.ServiceCategory;
import com.kaamwala.entity.WorkerProfile;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for updating a worker's profile information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkerProfileRequest {

    private String name;

    private String email;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    private String bio;

    private List<ServiceCategory> skills;

    private List<String> serviceAreas;

    @DecimalMin(value = "0", message = "Starting price must be non-negative")
    private BigDecimal startingPrice;

    private WorkerProfile.AvailabilityStatus availabilityStatus;

    private Double latitude;

    private Double longitude;
}
