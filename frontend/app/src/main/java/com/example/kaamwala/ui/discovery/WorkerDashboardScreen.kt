package com.example.kaamwala.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
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
import com.example.kaamwala.Login
import com.example.kaamwala.data.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDashboardScreen(
    viewModel: WorkerDiscoveryViewModel,
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val userId = SessionManager.userId
    var name by remember(userId) { mutableStateOf(SessionManager.userName ?: "Worker") }
    var email by remember(userId) { mutableStateOf("") }
    var city by remember(userId) { mutableStateOf(SessionManager.userCity) }
    var startingPrice by remember(userId) { mutableStateOf("500") }
    var bio by remember(userId) { mutableStateOf("Professional hyperlocal worker offering premium services.") }
    var selectedSkills by remember(userId) { mutableStateOf(setOf("CARPENTER")) }

    var cityDropdownExpanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val profileState by viewModel.workerProfileState.collectAsState()
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadProfile(userId)
        }
        viewModel.fetchNotifications()
    }

    var hasInitialized by remember(userId) { mutableStateOf(false) }
    LaunchedEffect(profileState) {
        if (profileState is WorkerProfileUiState.Success && !hasInitialized) {
            val profile = (profileState as WorkerProfileUiState.Success).profile
            name = profile.name
            email = profile.email ?: ""
            if (profile.serviceAreas.isNotEmpty()) {
                city = profile.serviceAreas.first()
            }
            startingPrice = profile.startingPrice.toInt().toString()
            bio = profile.bio ?: ""
            selectedSkills = profile.skills.toSet()
            hasInitialized = true
        }
    }

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
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundNavy)
                )
            )
    ) {
        // Glowing spots
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopStart)
                .offset(x = (-40).dp, y = (-20).dp)
                .background(Brush.radialGradient(listOf(Color(0x3300F5D4), Color.Transparent)))
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .background(Brush.radialGradient(listOf(Color(0x26F72585), Color.Transparent)))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Row (Title + Logout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Worker Portal",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Manage your shop profile & skills",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = {
                        SessionManager.token = null
                        SessionManager.userRole = null
                        SessionManager.userName = null
                        SessionManager.userPhone = null
                        onNavigate(Login)
                    },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = GlassBg),
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, GlassBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = NeonMagenta
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Stats Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GlassBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (profileState is WorkerProfileUiState.Success) {
                                "${(profileState as WorkerProfileUiState.Success).profile.totalJobs} jobs fulfilled"
                            } else {
                                "0 jobs fulfilled"
                            },
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0C091A))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = NeonGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (profileState is WorkerProfileUiState.Success) {
                                String.format("%.2f", (profileState as WorkerProfileUiState.Success).profile.ratingAvg)
                            } else {
                                "4.90"
                            },
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Customer Inquiries Section
            Text(
                text = "Customer Inquiries",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val notifications by viewModel.notificationsState.collectAsState()

            if (notifications.isEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GlassBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
                        .padding(vertical = 24.dp)
                ) {
                    Text(
                        text = "No active service inquiries yet.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    notifications.forEach { notification ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GlassBg.copy(alpha = 0.8f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GlassBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = notification.title,
                                        color = NeonCyan,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "New",
                                        color = NeonMagenta,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NeonMagenta.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = notification.body,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = notification.createdAt.take(16).replace("T", " "),
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Profile Form
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GlassBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Edit Profile Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    successMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x2600F5D4))
                                .border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg,
                                color = NeonCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    errorMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x26FF007F))
                                .border(1.dp, NeonMagenta, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = msg,
                                color = NeonMagenta,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Name Input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Display Name", color = TextSecondary) },
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

                    // Starting Price Input
                    OutlinedTextField(
                        value = startingPrice,
                        onValueChange = { startingPrice = it },
                        label = { Text("Starting Price (₹)", color = TextSecondary) },
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
                        label = { Text("Work Bio / Details", color = TextSecondary) },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // City Selection Dropdown
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = {},
                            label = { Text("Service City", color = TextSecondary) },
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
                            modifier = Modifier.fillMaxWidth(0.7f).background(BackgroundNavy)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Skills Selector
                    Text(
                        text = "Select Skills / Services",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
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

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        CircularProgressIndicator(
                            color = NeonCyan,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = "Name cannot be empty"
                                    return@Button
                                }
                                val price = startingPrice.toDoubleOrNull() ?: 0.0
                                isLoading = true
                                errorMessage = null
                                successMessage = null

                                viewModel.setCity(city)
                                viewModel.updateWorkerProfile(
                                    name = name,
                                    email = email,
                                    bio = bio,
                                    skills = selectedSkills.toList(),
                                    serviceAreas = listOf(city),
                                    startingPrice = price,
                                    onSuccess = {
                                        isLoading = false
                                        successMessage = "Profile updated successfully!"
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = BackgroundDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Save Profile",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
