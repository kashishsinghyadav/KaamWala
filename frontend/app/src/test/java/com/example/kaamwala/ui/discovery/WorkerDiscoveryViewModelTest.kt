package com.example.kaamwala.ui.discovery

import com.example.kaamwala.data.DataRepository
import com.example.kaamwala.data.model.ApiResponse
import com.example.kaamwala.data.model.PagedResponse
import com.example.kaamwala.data.model.PortfolioResponse
import com.example.kaamwala.data.model.WorkerProfileResponse
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkerDiscoveryViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun workerListState_initiallyLoading() = runTest {
        val fakeRepository = FakeWorkerRepository()
        val viewModel = WorkerDiscoveryViewModel(fakeRepository)
        
        // Assert initial state is Loading
        assertEquals(WorkerListUiState.Loading, viewModel.workerListState.value)
    }

    @Test
    fun search_success_updatesStateToSuccess() = runTest {
        val fakeRepository = FakeWorkerRepository()
        val mockWorker = WorkerProfileResponse(
            userId = "123",
            name = "Ramesh Kumar",
            phone = "+919876543210",
            startingPrice = 300.0,
            isVerified = true
        )
        fakeRepository.searchResponse = ApiResponse(
            success = true,
            message = "Success",
            data = PagedResponse(content = listOf(mockWorker))
        )

        val viewModel = WorkerDiscoveryViewModel(fakeRepository)
        viewModel.search("CARPENTER", "Kanpur", "price_asc")

        // Assert API was called with correct parameters
        assertEquals(Triple("CARPENTER", "Kanpur", "price_asc"), fakeRepository.searchCalledWith)
        
        // Assert UI state updated to Success with results
        val state = viewModel.workerListState.value
        assertTrue(state is WorkerListUiState.Success)
        val successState = state as WorkerListUiState.Success
        assertEquals(1, successState.workers.size)
        assertEquals("Ramesh Kumar", successState.workers[0].name)
    }

    @Test
    fun search_failure_updatesStateToError() = runTest {
        val fakeRepository = FakeWorkerRepository()
        fakeRepository.searchResponse = ApiResponse(
            success = false,
            message = "Database connection error",
            data = null
        )

        val viewModel = WorkerDiscoveryViewModel(fakeRepository)
        viewModel.search("CARPENTER", "Kanpur", "price_asc")

        val state = viewModel.workerListState.value
        assertTrue(state is WorkerListUiState.Error)
        assertEquals("Database connection error", (state as WorkerListUiState.Error).message)
    }

    @Test
    fun loadProfile_success_updatesProfileStateToSuccess() = runTest {
        val fakeRepository = FakeWorkerRepository()
        val mockProfile = WorkerProfileResponse(
            userId = "abc-123",
            name = "Anil Sharma",
            phone = "+919876543215"
        )
        val mockPortfolio = listOf(
            PortfolioResponse(
                id = "p-1",
                workerId = "abc-123",
                title = "Kitchen Cabinets",
                category = "CARPENTER",
                createdAt = "2026-06-04"
            )
        )

        fakeRepository.profileResponse = ApiResponse(true, "Success", mockProfile)
        fakeRepository.portfolioResponse = ApiResponse(true, "Success", mockPortfolio)

        val viewModel = WorkerDiscoveryViewModel(fakeRepository)
        viewModel.loadProfile("abc-123")

        // Assert profile calls
        assertEquals("abc-123", fakeRepository.profileIdCalled)
        assertEquals("abc-123", fakeRepository.portfolioIdCalled)

        // Assert state is Success
        val state = viewModel.workerProfileState.value
        assertTrue(state is WorkerProfileUiState.Success)
        val successState = state as WorkerProfileUiState.Success
        assertEquals("Anil Sharma", successState.profile.name)
        assertEquals(1, successState.portfolio.size)
        assertEquals("Kitchen Cabinets", successState.portfolio[0].title)
    }
}

private class FakeWorkerRepository : DataRepository {
    override val data: Flow<List<String>> = flow { emit(emptyList()) }

    var searchResponse: ApiResponse<PagedResponse<WorkerProfileResponse>> = ApiResponse(true, "Success", PagedResponse())
    var profileResponse: ApiResponse<WorkerProfileResponse> = ApiResponse(true, "Success", WorkerProfileResponse("", "", ""))
    var portfolioResponse: ApiResponse<List<PortfolioResponse>> = ApiResponse(true, "Success", emptyList())

    var searchCalledWith: Triple<String?, String?, String?>? = null
    var profileIdCalled: String? = null
    var portfolioIdCalled: String? = null

    override suspend fun searchWorkers(
        category: String?,
        city: String?,
        sortBy: String?,
        page: Int,
        size: Int
    ): ApiResponse<PagedResponse<WorkerProfileResponse>> {
        searchCalledWith = Triple(category, city, sortBy)
        return searchResponse
    }

    override suspend fun getWorkerProfile(workerId: String): ApiResponse<WorkerProfileResponse> {
        profileIdCalled = workerId
        return profileResponse
    }

    override suspend fun getWorkerPortfolio(workerId: String): ApiResponse<List<PortfolioResponse>> {
        portfolioIdCalled = workerId
        return portfolioResponse
    }
}
