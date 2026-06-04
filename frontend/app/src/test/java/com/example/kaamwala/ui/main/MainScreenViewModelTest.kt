package com.example.kaamwala.ui.main

import com.example.kaamwala.data.DataRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
  @Test
  fun uiState_initiallyLoading() = runTest {
    val viewModel = MainScreenViewModel(FakeMyModelRepository())
    assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)
  }

  @Test
  fun uiState_onItemSaved_isDisplayed() = runTest {
    val viewModel = MainScreenViewModel(FakeMyModelRepository())
    assertEquals(viewModel.uiState.first(), MainScreenUiState.Loading)
  }
}

private class FakeMyModelRepository : DataRepository {
  override val data: Flow<List<String>> = flow { emit(listOf("Sample")) }

  override suspend fun searchWorkers(
    category: String?,
    city: String?,
    sortBy: String?,
    page: Int,
    size: Int
  ) = com.example.kaamwala.data.model.ApiResponse(
    success = true,
    message = "Success",
    data = com.example.kaamwala.data.model.PagedResponse<com.example.kaamwala.data.model.WorkerProfileResponse>()
  )

  override suspend fun getWorkerProfile(workerId: String) =
    com.example.kaamwala.data.model.ApiResponse(
      success = true,
      message = "Success",
      data = com.example.kaamwala.data.model.WorkerProfileResponse("", "", "")
    )

  override suspend fun getWorkerPortfolio(workerId: String) =
    com.example.kaamwala.data.model.ApiResponse(
      success = true,
      message = "Success",
      data = emptyList<com.example.kaamwala.data.model.PortfolioResponse>()
    )
}
