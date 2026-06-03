package com.kaamwala.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for initiating OTP-based authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequest {

    /** Phone number in E.164 format (e.g., +919876543210). */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Invalid phone number format")
    private String phone;
}
