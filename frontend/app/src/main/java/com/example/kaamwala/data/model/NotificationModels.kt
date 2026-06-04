package com.example.kaamwala.data.model

import kotlinx.serialization.Serializable

/**
 * Representation of in-app Notification.
 */
@Serializable
data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val data: String? = null,
    val isRead: Boolean = false,
    val createdAt: String
)
