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

    private val _workerListState = MutableStateFlow<WorkerListUiState>(WorkerListUiState.Loading)
    val workerListState: StateFlow<WorkerListUiState> = _workerListState.asStateFlow()

    private val _workerProfileState = MutableStateFlow<WorkerProfileUiState>(WorkerProfileUiState.Loading)
    val workerProfileState: StateFlow<WorkerProfileUiState> = _workerProfileState.asStateFlow()

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
}
