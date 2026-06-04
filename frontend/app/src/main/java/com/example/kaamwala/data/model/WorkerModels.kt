package com.example.kaamwala.data.model

import kotlinx.serialization.Serializable

/**
 * Worker profile response representation.
 */
@Serializable
data class WorkerProfileResponse(
    val userId: String,
    val name: String,
    val phone: String,
    val email: String? = null,
    val avatarUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val skills: List<String> = emptyList(),
    val serviceAreas: List<String> = emptyList(),
    val startingPrice: Double = 0.0,
    val isVerified: Boolean = false,
    val aadhaarVerified: Boolean = false,
    val panVerified: Boolean = false,
    val selfieVerified: Boolean = false,
    val ratingAvg: Double = 0.0,
    val totalJobs: Int = 0,
    val totalEarnings: Double = 0.0,
    val bio: String? = null,
    val subscriptionTier: String = "FREE",
    val availabilityStatus: String = "OFFLINE"
)

/**
 * Worker portfolio item representation.
 */
@Serializable
data class PortfolioResponse(
    val id: String,
    val workerId: String,
    val title: String,
    val description: String? = null,
    val beforeImageUrl: String? = null,
    val afterImageUrl: String? = null,
    val videoUrl: String? = null,
    val category: String,
    val createdAt: String
)

/**
 * Paginated API container wrapper matching PagedResponse in backend.
 */
@Serializable
data class PagedResponse<T>(
    val content: List<T> = emptyList(),
    val page: Int = 0,
    val size: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val last: Boolean = true
)
