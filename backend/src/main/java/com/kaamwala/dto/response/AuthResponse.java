package com.kaamwala.dto.response;

import com.kaamwala.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Response DTO returned after successful OTP verification.
 * Contains the JWT token and basic user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** JWT authentication token. */
    private String token;

    /** The authenticated user's UUID. */
    private UUID userId;

    /** The user's display name. */
    private String name;

    /** The user's phone number. */
    private String phone;

    /** The user's role on the platform. */
    private User.UserRole role;

    /** Whether this is a new user registration. */
    private boolean newUser;
}
