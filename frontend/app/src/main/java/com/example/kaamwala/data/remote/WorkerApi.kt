package com.example.kaamwala.data.remote

import com.example.kaamwala.data.model.ApiResponse
import com.example.kaamwala.data.model.PagedResponse
import com.example.kaamwala.data.model.PortfolioResponse
import com.example.kaamwala.data.model.WorkerProfileResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit endpoints for Worker discovery, profile, and portfolios.
 */
interface WorkerApi {

    @GET("api/workers")
    suspend fun searchWorkers(
        @Query("category") category: String? = null,
        @Query("city") city: String? = null,
        @Query("sortBy") sortBy: String? = "price_asc",
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponse<WorkerProfileResponse>>

    @GET("api/workers/{id}")
    suspend fun getWorkerProfile(
        @Path("id") workerId: String
    ): ApiResponse<WorkerProfileResponse>

    @GET("api/workers/{id}/portfolio")
    suspend fun getWorkerPortfolio(
        @Path("id") workerId: String
    ): ApiResponse<List<PortfolioResponse>>
}
