package com.example.kaamwala

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object Main : NavKey

@Serializable data object Dashboard : NavKey

@Serializable data class WorkerList(val category: String) : NavKey

@Serializable data class WorkerProfile(val workerId: String) : NavKey

@Serializable data object Login : NavKey

@Serializable data object Register : NavKey

@Serializable data object WorkerDashboard : NavKey
