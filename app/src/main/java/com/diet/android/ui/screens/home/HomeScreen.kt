package com.diet.android.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diet.android.ui.screens.home.components.*
import com.diet.android.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToExplore: () -> Unit,
    viewModel: HomeViewModel
) {
    val context = LocalContext.current
    val userInfo = viewModel.userInfo

    var showAppointmentDialog by remember { mutableStateOf(false) }
    var showClinicAnalyticsDialog by remember { mutableStateOf(false) }
    var showApplicationsDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showProfileEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData(context)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is HomeUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DietApp Platformu",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = if (userInfo?.role == "ROLE_DIETITIAN") "Hoş geldiniz, ${userInfo.name ?: "Uzman"}" else "Merhaba, ${userInfo?.name ?: "Danışan"}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showProfileEditDialog = true }) {
                        Text("👤", fontSize = 18.sp)
                    }

                    IconButton(onClick = { showNotificationDialog = true }) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Text("🔔", fontSize = 18.sp)
                            if (viewModel.unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(Color.Red, RoundedCornerShape(7.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = viewModel.unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    IconButton(onClick = { showAppointmentDialog = true }) {
                        Text("📅", fontSize = 18.sp)
                    }

                    if (userInfo?.role == "ROLE_DIETITIAN") {
                        IconButton(onClick = { showClinicAnalyticsDialog = true }) {
                            Text("📊", fontSize = 18.sp)
                        }
                    }

                    if (userInfo?.role == "ROLE_ADMIN") {
                        IconButton(onClick = { showApplicationsDialog = true }) {
                            Text("⚙️", fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = onLogout) {
                        Text("Çıkış Yap", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Text("🏠", fontSize = 20.sp) },
                    label = { Text("Ana Sayfa") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Text("🔍", fontSize = 20.sp) },
                    label = { Text(if (userInfo?.role == "ROLE_DIETITIAN") "Danışanlar" else "Geçmişim") },
                    selected = false,
                    onClick = onNavigateToExplore
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                if (userInfo?.role == "ROLE_DIETITIAN") {
                    DietitianDashboard(viewModel = viewModel)
                } else if (userInfo?.role == "ROLE_USER") {
                    ClientDashboard(viewModel = viewModel)
                } else if (userInfo?.role == "ROLE_ADMIN") {
                    // Admin dashboard simply shows dietitian application list or button
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { showApplicationsDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("🧑‍⚕️ Diyetisyen Başvurularını Yönet", color = Color.White)
                        }
                    }
                }
            }

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            // Dialogs
            AppointmentDialog(
                visible = showAppointmentDialog,
                onClose = { showAppointmentDialog = false },
                viewModel = viewModel
            )

            ClinicAnalyticsDialog(
                visible = showClinicAnalyticsDialog,
                onClose = { showClinicAnalyticsDialog = false },
                viewModel = viewModel
            )

            DietitianApplicationsDialog(
                visible = showApplicationsDialog,
                onClose = { showApplicationsDialog = false },
                viewModel = viewModel
            )

            NotificationDialog(
                visible = showNotificationDialog,
                onClose = { showNotificationDialog = false },
                viewModel = viewModel
            )

            ProfileEditDialog(
                visible = showProfileEditDialog,
                onClose = { showProfileEditDialog = false },
                viewModel = viewModel
            )
        }
    }
}
