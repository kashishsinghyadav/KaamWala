package com.example.kaamwala.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import com.example.kaamwala.Register
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

@Composable
fun LoginScreen(
    viewModel: WorkerDiscoveryViewModel,
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var revealedOtp by remember { mutableStateOf<String?>(null) }
    var selectedRole by remember { mutableStateOf("CUSTOMER") } // "CUSTOMER" or "WORKER"
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
        // Glowing background nodes
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
                    text = "Welcome to KaamWala",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Hyperlocal on-demand worker marketplace",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // Role Tab selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0C091A))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val roles = listOf("CUSTOMER" to "Customer", "WORKER" to "Worker / Business")
                    roles.forEach { (roleKey, label) ->
                        val isSelected = selectedRole == roleKey
                        Button(
                            onClick = { selectedRole = roleKey },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) NeonCyan else Color.Transparent,
                                contentColor = if (isSelected) BackgroundDark else TextSecondary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Error message
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

                // Phone Input
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number (e.g. +919876543201)", color = TextSecondary) },
                    singleLine = true,
                    enabled = !isOtpSent && !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

                Spacer(modifier = Modifier.height(16.dp))

                // OTP Section
                AnimatedVisibility(visible = isOtpSent) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Dev OTP Info
                        revealedOtp?.let { code ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(NeonGold.copy(alpha = 0.15f))
                                    .border(1.dp, NeonGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "Dev Mode: Verification OTP is $code",
                                    color = NeonGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        OutlinedTextField(
                            value = otp,
                            onValueChange = { otp = it },
                            label = { Text("6-Digit OTP", color = TextSecondary) },
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

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(color = NeonCyan, modifier = Modifier.padding(vertical = 12.dp))
                } else {
                    Button(
                        onClick = {
                            if (!isOtpSent) {
                                if (phone.isBlank()) {
                                    errorMessage = "Please enter a valid phone number"
                                    return@Button
                                }
                                isLoading = true
                                errorMessage = null
                                viewModel.sendOtp(
                                    phone = phone,
                                    onCodeSent = { code ->
                                        isOtpSent = true
                                        revealedOtp = code
                                        isLoading = false
                                    },
                                    onError = { err ->
                                        errorMessage = err
                                        isLoading = false
                                    }
                                )
                            } else {
                                if (otp.isBlank()) {
                                    errorMessage = "Please enter the OTP"
                                    return@Button
                                }
                                isLoading = true
                                errorMessage = null
                                viewModel.verifyOtp(
                                    phone = phone,
                                    otp = otp,
                                    role = selectedRole,
                                    onSuccess = { isNewUser ->
                                        isLoading = false
                                        if (isNewUser) {
                                            onNavigate(Register)
                                        } else {
                                            if (selectedRole == "WORKER") {
                                                onNavigate(com.example.kaamwala.WorkerDashboard)
                                            } else {
                                                onNavigate(Dashboard)
                                            }
                                        }
                                    },
                                    onError = { err ->
                                        errorMessage = err
                                        isLoading = false
                                    }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = BackgroundDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = if (!isOtpSent) "Send OTP" else "Verify & Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = GlassBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Dev Bypass Section
                Text(
                    text = "Developer Bypass (No phone required)",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.bypassLogin("CUSTOMER") {
                                onNavigate(Dashboard)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(NeonCyan, NeonCyan))),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Customer Bypass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.bypassLogin("WORKER") {
                                onNavigate(com.example.kaamwala.WorkerDashboard)
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonMagenta),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(NeonMagenta, NeonMagenta))),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Worker Bypass", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
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
        LoginScreen(
            viewModel = WorkerDiscoveryViewModel(previewRepository),
            onNavigate = {}
        )
    }
}
