package com.example.kaamwala.data.model

import kotlinx.serialization.Serializable

/**
 * Request body for OTP sending.
 */
@Serializable
data class OtpRequest(
    val phone: String
)

/**
 * Request body for OTP verification.
 */
@Serializable
data class OtpVerifyRequest(
    val phone: String,
    val otp: String,
    val name: String? = null,
    val role: String? = null // "CUSTOMER" or "WORKER"
)

/**
 * Successful authentication response content.
 */
@Serializable
data class AuthResponse(
    val token: String,
    val userId: String,
    val name: String,
    val phone: String,
    val role: String,
    val newUser: Boolean
)

/**
 * Generic API wrapper returning from Spring Boot backend.
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)
