package com.example.kaamwala.data

import com.example.kaamwala.data.model.ApiResponse
import com.example.kaamwala.data.model.PagedResponse
import com.example.kaamwala.data.model.PortfolioResponse
import com.example.kaamwala.data.model.WorkerProfileResponse
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
}
