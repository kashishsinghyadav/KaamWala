package com.example.kaamwala.data

import com.example.kaamwala.data.model.ApiResponse
import com.example.kaamwala.data.model.PagedResponse
import com.example.kaamwala.data.model.PortfolioResponse
import com.example.kaamwala.data.model.WorkerProfileResponse
import com.example.kaamwala.data.model.AuthResponse
import com.example.kaamwala.data.model.UpdateWorkerProfileRequest
import com.example.kaamwala.data.model.Notification
import com.example.kaamwala.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DataRepository {
    val data: Flow<List<String>>

    suspend fun searchWorkers(
        category: String?,
        city: String?,
        sortBy: String?,
        page: Int,
        size: Int
    ): ApiResponse<PagedResponse<WorkerProfileResponse>>

    suspend fun getWorkerProfile(
        workerId: String
    ): ApiResponse<WorkerProfileResponse>

    suspend fun getWorkerPortfolio(
        workerId: String
    ): ApiResponse<List<PortfolioResponse>>

    suspend fun sendOtp(phone: String): ApiResponse<String>

    suspend fun verifyOtp(
        phone: String,
        otp: String,
        name: String? = null,
        role: String? = null
    ): ApiResponse<AuthResponse>

    suspend fun updateWorkerProfile(
        request: UpdateWorkerProfileRequest
    ): ApiResponse<WorkerProfileResponse>

    suspend fun getNotifications(): ApiResponse<PagedResponse<Notification>>

    suspend fun inquireWorker(workerId: String): ApiResponse<Unit?>
}

class DefaultDataRepository : DataRepository {
    override val data: Flow<List<String>> = flow { emit(listOf("Android")) }

    override suspend fun searchWorkers(
        category: String?,
        city: String?,
        sortBy: String?,
        page: Int,
        size: Int
    ): ApiResponse<PagedResponse<WorkerProfileResponse>> {
        return RetrofitClient.workerApi.searchWorkers(category, city, sortBy, page, size)
    }

    override suspend fun getWorkerProfile(workerId: String): ApiResponse<WorkerProfileResponse> {
        return RetrofitClient.workerApi.getWorkerProfile(workerId)
    }

    override suspend fun getWorkerPortfolio(workerId: String): ApiResponse<List<PortfolioResponse>> {
        return RetrofitClient.workerApi.getWorkerPortfolio(workerId)
    }

    override suspend fun sendOtp(phone: String): ApiResponse<String> {
        return RetrofitClient.authApi.sendOtp(com.example.kaamwala.data.model.OtpRequest(phone))
    }

    override suspend fun verifyOtp(
        phone: String,
        otp: String,
        name: String?,
        role: String?
    ): ApiResponse<AuthResponse> {
        return RetrofitClient.authApi.verifyOtp(
            com.example.kaamwala.data.model.OtpVerifyRequest(phone, otp, name, role)
        )
    }

    override suspend fun updateWorkerProfile(
        request: UpdateWorkerProfileRequest
    ): ApiResponse<WorkerProfileResponse> {
        return RetrofitClient.workerApi.updateProfile(request)
    }

    override suspend fun getNotifications(): ApiResponse<PagedResponse<Notification>> {
        return RetrofitClient.notificationApi.getNotifications()
    }

    override suspend fun inquireWorker(workerId: String): ApiResponse<Unit?> {
        return RetrofitClient.notificationApi.inquireWorker(workerId)
    }
}
