package com.kaamwala.controller;

import com.kaamwala.dto.request.OtpRequest;
import com.kaamwala.dto.request.OtpVerifyRequest;
import com.kaamwala.dto.response.ApiResponse;
import com.kaamwala.dto.response.AuthResponse;
import com.kaamwala.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for OTP-based authentication.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "OTP-based authentication endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Send a 6-digit OTP to the given phone number.
     *
     * @param request the OTP request containing the phone number
     * @return success response with OTP (visible in dev; in production, sent via SMS)
     */
    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Generate and send a 6-digit OTP to the phone number")
    public ResponseEntity<ApiResponse<String>> sendOtp(@Valid @RequestBody OtpRequest request) {
        String otp = authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success(otp, "OTP sent successfully"));
    }

    /**
     * Verify the OTP and authenticate the user.
     *
     * @param request the OTP verification request
     * @return the authentication response with JWT token
     */
    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verify OTP and receive JWT token. Creates new user if phone is unregistered.")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        AuthResponse authResponse = authService.verifyOtp(request);
        String message = authResponse.isNewUser() ? "Registration successful" : "Login successful";
        return ResponseEntity.ok(ApiResponse.success(authResponse, message));
    }
}
