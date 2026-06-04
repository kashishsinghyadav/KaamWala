package com.example.kaamwala

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.kaamwala.data.DefaultDataRepository
import com.example.kaamwala.ui.auth.LoginScreen
import com.example.kaamwala.ui.auth.RegisterScreen
import com.example.kaamwala.ui.discovery.DashboardScreen
import com.example.kaamwala.ui.discovery.WorkerDiscoveryViewModel
import com.example.kaamwala.ui.discovery.WorkerListScreen
import com.example.kaamwala.ui.discovery.WorkerProfileScreen

@Composable
fun MainNavigation() {
    // Starting screen is Login, which handles authentication and registration routing
    val backStack = rememberNavBackStack(Login)

    // Instantiate a shared discovery ViewModel scoped to the navigation host
    val discoveryViewModel: WorkerDiscoveryViewModel = viewModel {
        WorkerDiscoveryViewModel(DefaultDataRepository())
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Login> {
                LoginScreen(
                    viewModel = discoveryViewModel,
                    onNavigate = { navKey -> backStack.add(navKey) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<Register> {
                RegisterScreen(
                    viewModel = discoveryViewModel,
                    onNavigate = { navKey -> backStack.add(navKey) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<Dashboard> {
                DashboardScreen(
                    viewModel = discoveryViewModel,
                    onNavigate = { navKey -> backStack.add(navKey) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<WorkerList> { key ->
                WorkerListScreen(
                    category = key.category,
                    viewModel = discoveryViewModel,
                    onBack = { backStack.removeLastOrNull() },
                    onNavigate = { navKey -> backStack.add(navKey) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<WorkerProfile> { key ->
                WorkerProfileScreen(
                    workerId = key.workerId,
                    viewModel = discoveryViewModel,
                    onBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}
