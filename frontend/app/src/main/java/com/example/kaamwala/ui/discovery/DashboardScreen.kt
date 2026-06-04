package com.example.kaamwala.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.kaamwala.WorkerList

// Premium color palette
val BackgroundDark = Color(0xFF0C091A)
val BackgroundNavy = Color(0xFF130E2A)
val GlassBg = Color(0x1AFFFFFF)
val GlassBorder = Color(0x26FFFFFF)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA5A1B8)
val NeonCyan = Color(0xFF00F5D4)
val NeonMagenta = Color(0xFFFF007F)
val NeonGold = Color(0xFFFFD166)

data class ServiceCategoryItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val startColor: Color,
    val endColor: Color
)

@Composable
fun DashboardScreen(
    viewModel: WorkerDiscoveryViewModel,
    onNavigate: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCity by viewModel.selectedCity.collectAsState()
    var showLocationDialog by remember { mutableStateOf(false) }

    val categories = remember {
        listOf(
            ServiceCategoryItem(
                "CARPENTER", "Carpenter", "Modular kitchens, wood works & repairs",
                Icons.Default.Build, Color(0xFFF72585), Color(0xFF7209B7)
            ),
            ServiceCategoryItem(
                "ELECTRICIAN", "Electrician", "House wiring, smart switches & faults",
                Icons.Default.Settings, Color(0xFFFFB703), Color(0xFFFB8500)
            ),
            ServiceCategoryItem(
                "PLUMBER", "Plumber", "Pipelines, blockages & bathroom fixtures",
                Icons.Default.Build, Color(0xFF4361EE), Color(0xFF4CC9F0)
            ),
            ServiceCategoryItem(
                "PAINTER", "Painter", "Emulsion painting, putty & wall textures",
                Icons.Default.Home, Color(0xFF7209B7), Color(0xFF3F37C9)
            ),
            ServiceCategoryItem(
                "AC_TECHNICIAN", "AC Tech", "Cooling checks, filter servicing & fixing",
                Icons.Default.Settings, Color(0xFF00F5D4), Color(0xFF00BBF9)
            ),
            ServiceCategoryItem(
                "HOME_CLEANING", "Home Cleaning", "Deep scrubbing, sanitizing & kitchen clean",
                Icons.Default.Home, Color(0xFF4CAF50), Color(0xFF009688)
            ),
            ServiceCategoryItem(
                "FURNITURE_MAKER", "Furniture Repair", "Sofa setups, dining repair & polishing",
                Icons.Default.Build, Color(0xFFFF5722), Color(0xFFE91E63)
            ),
            ServiceCategoryItem(
                "MASON", "Mason", "Brick works, tiles leveling & cement work",
                Icons.Default.Build, Color(0xFF9E9E9E), Color(0xFF607D8B)
            ),
            ServiceCategoryItem(
                "WELDER", "Welder", "Grilles, gates, railings & metal builds",
                Icons.Default.Settings, Color(0xFFFFC107), Color(0xFFFF5722)
            ),
            ServiceCategoryItem(
                "CCTV_INSTALLER", "CCTV Install", "Dome cameras, NVR configurations & wiring",
                Icons.Default.Settings, Color(0xFF673AB7), Color(0xFF3F51B5)
            ),
            ServiceCategoryItem(
                "RO_SERVICE", "RO Purifier", "RO filter change, TDS tune & servicing",
                Icons.Default.Settings, Color(0xFF03A9F4), Color(0xFF00BCD4)
            )
        )
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Select Your Area", color = TextPrimary, fontWeight = FontWeight.Bold) },
            containerColor = BackgroundNavy,
            text = {
                Column {
                    listOf("Kanpur", "Delhi NCR", "Mumbai", "Bengaluru", "Noida", "Gurgaon", "Lucknow").forEach { cityName ->
                        val isSelected = activeCity == cityName
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    viewModel.setCity(cityName)
                                    showLocationDialog = false
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = cityName,
                                color = if (isSelected) NeonCyan else TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Close", color = NeonCyan)
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BackgroundDark, BackgroundNavy)
                )
            )
    ) {
        // Decorative glowing background spots to simulate glassmorphism
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x33F72585), Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x2600F5D4), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header Section
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KaamWala",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hyperlocal Worker Services",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Clickable location display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlassBg)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .clickable { showLocationDialog = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = activeCity,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Logout button
                    IconButton(
                        onClick = {
                            com.example.kaamwala.data.SessionManager.token = null
                            com.example.kaamwala.data.SessionManager.userRole = null
                            com.example.kaamwala.data.SessionManager.userName = null
                            com.example.kaamwala.data.SessionManager.userPhone = null
                            onNavigate(com.example.kaamwala.Login)
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = GlassBg),
                        modifier = Modifier
                            .size(36.dp)
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = NeonMagenta,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Promotion banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF7209B7), Color(0xFFF72585))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                    Text(
                        text = "Reverse Bidding Active",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Post a job and let local workers bid their price!",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Discover Services",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Category Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = {
                            onNavigate(WorkerList(category.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: ServiceCategoryItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GlassBg),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Service Category Icon Glowing Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(category.startColor, category.endColor)
                        )
                    )
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Category Text Detail
            Column {
                Text(
                    text = category.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.description,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}
