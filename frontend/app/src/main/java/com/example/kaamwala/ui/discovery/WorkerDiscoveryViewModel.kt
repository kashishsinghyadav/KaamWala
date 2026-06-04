package com.example.kaamwala.ui.discovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaamwala.data.DataRepository
import com.example.kaamwala.data.model.PortfolioResponse
import com.example.kaamwala.data.model.WorkerProfileResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.example.kaamwala.data.SessionManager
import com.example.kaamwala.data.model.Notification
import kotlinx.coroutines.flow.asStateFlow

sealed interface WorkerListUiState {
    data object Loading : WorkerListUiState
    data class Success(val workers: List<WorkerProfileResponse>) : WorkerListUiState
    data class Error(val message: String) : WorkerListUiState
}

sealed interface WorkerProfileUiState {
    data object Loading : WorkerProfileUiState
    data class Success(
        val profile: WorkerProfileResponse,
        val portfolio: List<PortfolioResponse>
    ) : WorkerProfileUiState
    data class Error(val message: String) : WorkerProfileUiState
}

class WorkerDiscoveryViewModel(
    private val repository: DataRepository
) : ViewModel() {

    val selectedCity = MutableStateFlow(SessionManager.userCity)

    fun setCity(city: String) {
        SessionManager.userCity = city
        selectedCity.value = city
    }

    private val _workerListState = MutableStateFlow<WorkerListUiState>(WorkerListUiState.Loading)
    val workerListState: StateFlow<WorkerListUiState> = _workerListState.asStateFlow()

    private val _workerProfileState = MutableStateFlow<WorkerProfileUiState>(WorkerProfileUiState.Loading)
    val workerProfileState: StateFlow<WorkerProfileUiState> = _workerProfileState.asStateFlow()

    fun bypassLogin(role: String, onSuccess: () -> Unit) {
        SessionManager.token = "dev-bypass-token"
        SessionManager.userRole = role
        if (role == "WORKER") {
            SessionManager.userName = "Ramesh Kumar"
            SessionManager.userPhone = "+919876543201"
        } else {
            SessionManager.userName = "Test Customer"
            SessionManager.userPhone = "+919999999999"
        }
        onSuccess()
    }

    fun sendOtp(phone: String, onCodeSent: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.sendOtp(phone)
                if (response.success && response.data != null) {
                    onCodeSent(response.data)
                } else {
                    onError(response.message ?: "Failed to send OTP")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun verifyOtp(
        phone: String,
        otp: String,
        name: String? = null,
        role: String? = null,
        onSuccess: (Boolean) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.verifyOtp(phone, otp, name, role)
                if (response.success && response.data != null) {
                    val auth = response.data
                    SessionManager.token = auth.token
                    SessionManager.userRole = auth.role
                    SessionManager.userName = auth.name
                    SessionManager.userPhone = auth.phone
                    SessionManager.userId = auth.userId
                    
                    if (auth.role == "WORKER") {
                        try {
                            val profileRes = repository.getWorkerProfile(auth.userId)
                            if (profileRes.success && profileRes.data != null) {
                                val profile = profileRes.data
                                val hasPrice = profile.startingPrice > 0.0
                                val hasSkills = profile.skills.isNotEmpty()
                                if (hasPrice && hasSkills) {
                                    onSuccess(false) // Bypass onboarding since profile exists
                                } else {
                                    onSuccess(true)  // Empty/incomplete profile -> go to register
                                }
                            } else {
                                onSuccess(true)
                            }
                        } catch (e: Exception) {
                            onSuccess(true)
                        }
                    } else {
                        onSuccess(auth.newUser)
                    }
                } else {
                    onError(response.message ?: "Invalid OTP code")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Verification failed")
            }
        }
    }

    fun updateWorkerProfile(
        name: String,
        email: String,
        bio: String,
        skills: List<String>,
        serviceAreas: List<String>,
        startingPrice: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = com.example.kaamwala.data.model.UpdateWorkerProfileRequest(
                    name = name,
                    email = email,
                    bio = bio,
                    skills = skills,
                    serviceAreas = serviceAreas,
                    startingPrice = startingPrice,
                    availabilityStatus = "AVAILABLE"
                )
                val response = repository.updateWorkerProfile(request)
                if (response.success) {
                    SessionManager.userName = name
                    onSuccess()
                } else {
                    onError(response.message ?: "Failed to update profile")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Profile update error")
            }
        }
    }

    // Cache parameters to re-trigger search when sorting/filtering changes
    private var currentCategory: String? = null
    private var currentCity: String? = null
    private var currentSort: String = "price_asc"

    fun search(category: String, city: String? = null, sortBy: String = "price_asc") {
        currentCategory = category
        currentCity = city
        currentSort = sortBy

        _workerListState.value = WorkerListUiState.Loading

        viewModelScope.launch {
            try {
                val response = repository.searchWorkers(
                    category = category,
                    city = if (city.isNullOrBlank()) null else city,
                    sortBy = sortBy,
                    page = 0,
                    size = 50
                )
                if (response.success && response.data != null) {
                    _workerListState.value = WorkerListUiState.Success(response.data.content)
                } else {
                    _workerListState.value = WorkerListUiState.Error(response.message ?: "Failed to fetch workers")
                }
            } catch (e: Exception) {
                _workerListState.value = WorkerListUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun loadProfile(workerId: String) {
        _workerProfileState.value = WorkerProfileUiState.Loading

        viewModelScope.launch {
            try {
                val profileResult = repository.getWorkerProfile(workerId)
                val portfolioResult = repository.getWorkerPortfolio(workerId)

                if (profileResult.success && profileResult.data != null && portfolioResult.success) {
                    _workerProfileState.value = WorkerProfileUiState.Success(
                        profile = profileResult.data,
                        portfolio = portfolioResult.data ?: emptyList()
                    )
                } else {
                    val errMsg = profileResult.message ?: portfolioResult.message ?: "Failed to load profile details"
                    _workerProfileState.value = WorkerProfileUiState.Error(errMsg)
                }
            } catch (e: Exception) {
                _workerProfileState.value = WorkerProfileUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    private val _notificationsState = MutableStateFlow<List<Notification>>(emptyList())
    val notificationsState: StateFlow<List<Notification>> = _notificationsState.asStateFlow()

    fun fetchNotifications() {
        viewModelScope.launch {
            try {
                val response = repository.getNotifications()
                if (response.success && response.data != null) {
                    _notificationsState.value = response.data.content
                }
            } catch (e: Exception) {
                // Keep current or empty list
            }
        }
    }

    fun inquireWorker(
        workerId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = repository.inquireWorker(workerId)
                if (response.success) {
                    onSuccess()
                } else {
                    onError(response.message ?: "Inquiry request failed")
                }
            } catch (e: Exception) {
                onError(e.message ?: "An unexpected error occurred")
            }
        }
    }
}
