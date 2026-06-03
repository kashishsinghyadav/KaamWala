package com.kaamwala.dto.request;

import com.kaamwala.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for verifying an OTP and completing authentication.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerifyRequest {

    /** Phone number that received the OTP. */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{7,14}$", message = "Invalid phone number format")
    private String phone;

    /** The 6-digit OTP code. */
    @NotBlank(message = "OTP is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String otp;

    /** Name of the user (required for new registrations). */
    private String name;

    /** Role to assign (CUSTOMER or WORKER, required for new registrations). */
    private User.UserRole role;
}
