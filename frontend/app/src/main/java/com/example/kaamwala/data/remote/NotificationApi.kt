package com.example.kaamwala.data.remote

import com.example.kaamwala.data.model.ApiResponse
import com.example.kaamwala.data.model.PagedResponse
import com.example.kaamwala.data.model.Notification
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit endpoints for Notifications and Inquiries.
 */
interface NotificationApi {

    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PagedResponse<Notification>>

    @POST("api/workers/{id}/inquire")
    suspend fun inquireWorker(
        @Path("id") workerId: String
    ): ApiResponse<Unit?>
}
