package com.example.kaamwala.data.remote

import com.example.kaamwala.data.model.ApiResponse
import com.example.kaamwala.data.model.AuthResponse
import com.example.kaamwala.data.model.OtpRequest
import com.example.kaamwala.data.model.OtpVerifyRequest
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit endpoints for OTP Authentication.
 */
interface AuthApi {

    @POST("api/auth/send-otp")
    suspend fun sendOtp(
        @Body request: OtpRequest
    ): ApiResponse<String>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Body request: OtpVerifyRequest
    ): ApiResponse<AuthResponse>
}
