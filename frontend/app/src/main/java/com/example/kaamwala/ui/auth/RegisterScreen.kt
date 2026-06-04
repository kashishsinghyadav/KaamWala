package com.example.kaamwala.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.kaamwala.Dashboard
import com.example.kaamwala.data.SessionManager
import com.example.kaamwala.ui.discovery.*
import com.example.kaamwala.ui.discovery.WorkerDiscoveryViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.kaamwala.theme.KaamWalaTheme
import com.example.kaamwala.data.DataRepository
import com.example.kaamwala.data.model.ApiResponse
import com.example.kaamwala.data.model.PagedResponse
import com.example.kaamwala.data.model.PortfolioResponse
import com.example.kaamwala.data.model.WorkerProfileResponse
import com.example.kaamwala.data.model.AuthResponse
import com.example.kaamwala.data.model.UpdateWorkerProfileRequest
import com.example.kaamwala.data.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: WorkerDiscoveryViewModel,
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val role = SessionManager.userRole ?: "CUSTOMER"
    
    val userId = SessionManager.userId
    var name by remember(userId) { mutableStateOf("") }
    var email by remember(userId) { mutableStateOf("") }
    var city by remember(userId) { mutableStateOf("Kanpur") }
    
    // Worker specific inputs
    var selectedSkills by remember(userId) { mutableStateOf(setOf("CARPENTER")) }
    var startingPrice by remember(userId) { mutableStateOf("") }
    var bio by remember(userId) { mutableStateOf("") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = remember {
        listOf(
            "CARPENTER" to "Carpenter",
            "ELECTRICIAN" to "Electrician",
            "PLUMBER" to "Plumber",
            "PAINTER" to "Painter",
            "AC_TECHNICIAN" to "AC Technician",
            "HOME_CLEANING" to "Home Cleaning",
            "FURNITURE_MAKER" to "Furniture Repair",
            "MASON" to "Mason",
            "WELDER" to "Welder",
            "CCTV_INSTALLER" to "CCTV Installation",
            "RO_SERVICE" to "RO Installation and Repair"
        )
    }

    val cities = listOf("Kanpur", "Delhi NCR", "Mumbai", "Bengaluru", "Noida", "Gurgaon", "Lucknow")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundNavy)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glowing spots
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopStart)
                .offset(x = (-30).dp, y = (-20).dp)
                .background(Brush.radialGradient(listOf(Color(0x2600F5D4), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 40.dp)
                .background(Brush.radialGradient(listOf(Color(0x33F72585), Color.Transparent)))
        )

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = GlassBg),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp)
                .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Complete Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Provide your details to get started as a ${role.lowercase()}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                errorMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = NeonMagenta,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name", color = TextSecondary) },
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder,
                        cursorColor = NeonCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (optional)", color = TextSecondary) },
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder,
                        cursorColor = NeonCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // City Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = {},
                        label = { Text("Select City", color = TextSecondary) },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select City",
                                tint = NeonCyan,
                                modifier = Modifier.clickable { if (!isLoading) cityDropdownExpanded = true }
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { if (!isLoading) cityDropdownExpanded = true }
                    )
                    DropdownMenu(
                        expanded = cityDropdownExpanded,
                        onDismissRequest = { cityDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f).background(BackgroundNavy)
                    ) {
                        cities.forEach { cityName ->
                            DropdownMenuItem(
                                text = { Text(cityName, color = TextPrimary) },
                                onClick = {
                                    city = cityName
                                    cityDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // If role is WORKER, add extra inputs
                if (role == "WORKER") {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Selection (Multi-select Grid)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Select Service Categories (Choose all that apply)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val chunkedCategories = categories.chunked(2)
                        chunkedCategories.forEach { rowCategories ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowCategories.forEach { (key, label) ->
                                    val isSelected = selectedSkills.contains(key)
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else GlassBg)
                                            .border(
                                                1.dp,
                                                if (isSelected) NeonCyan else GlassBorder,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                selectedSkills = if (isSelected) {
                                                    if (selectedSkills.size > 1) selectedSkills - key else selectedSkills
                                                } else {
                                                    selectedSkills + key
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) NeonCyan else TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                if (rowCategories.size < 2) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Starting Price Input
                    OutlinedTextField(
                        value = startingPrice,
                        onValueChange = { startingPrice = it },
                        label = { Text("Starting Price (₹ per hour/job)", color = TextSecondary) },
                        singleLine = true,
                        enabled = !isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            cursorColor = NeonCyan
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bio Input
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Short Bio / Work Experience", color = TextSecondary) },
                        enabled = !isLoading,
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            cursorColor = NeonCyan
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = NeonCyan)
                } else {
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Please enter your name"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null
                            
                            viewModel.setCity(city)
                            
                            if (role == "WORKER") {
                                val price = startingPrice.toDoubleOrNull() ?: 0.0
                                viewModel.updateWorkerProfile(
                                    name = name,
                                    email = email,
                                    bio = bio,
                                    skills = selectedSkills.toList(),
                                    serviceAreas = listOf(city),
                                    startingPrice = price,
                                    onSuccess = {
                                        isLoading = false
                                        onNavigate(com.example.kaamwala.WorkerDashboard)
                                    },
                                    onError = { err ->
                                        errorMessage = err
                                        isLoading = false
                                    }
                                )
                            } else {
                                // For customer we can bypass profile updating or call relevant client APIs if exist
                                SessionManager.userName = name
                                isLoading = false
                                onNavigate(Dashboard)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = BackgroundDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Complete Registration",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val previewRepository = object : DataRepository {
        override val data: Flow<List<String>> = flow { emit(emptyList()) }
        override suspend fun searchWorkers(c: String?, cy: String?, s: String?, p: Int, sz: Int) = 
            ApiResponse(true, "Success", PagedResponse<WorkerProfileResponse>())
        override suspend fun getWorkerProfile(id: String) = 
            ApiResponse(true, "Success", WorkerProfileResponse("", "", ""))
        override suspend fun getWorkerPortfolio(id: String) = 
            ApiResponse(true, "Success", emptyList<PortfolioResponse>())
        override suspend fun sendOtp(phone: String) = 
            ApiResponse(true, "Success", "123456")
        override suspend fun verifyOtp(phone: String, otp: String, name: String?, role: String?) = 
            ApiResponse(true, "Success", AuthResponse("", "", "", "", "", false))
        override suspend fun updateWorkerProfile(request: UpdateWorkerProfileRequest) = 
            ApiResponse(true, "Success", WorkerProfileResponse("", "", ""))
        override suspend fun getNotifications(): ApiResponse<PagedResponse<Notification>> = 
            ApiResponse(true, "Success", PagedResponse())
        override suspend fun inquireWorker(workerId: String): ApiResponse<Unit?> = 
            ApiResponse(true, "Success", null)
    }
    KaamWalaTheme {
        RegisterScreen(
            viewModel = WorkerDiscoveryViewModel(previewRepository),
            onNavigate = {}
        )
    }
}
