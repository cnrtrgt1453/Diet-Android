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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToSlots: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel,
    initialDialog: String? = null
) {
    val context = LocalContext.current
    val userInfo = viewModel.userInfo

    var showAppointmentDialog by remember { mutableStateOf(false) }
    var showApplicationsDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showProfileEditDialog by remember { mutableStateOf(false) }

    LaunchedEffect(initialDialog) {
        when (initialDialog) {
            "appointment" -> showAppointmentDialog = true
            "analytics" -> onNavigateToAnalytics()
            "applications" -> showApplicationsDialog = true
            "profile" -> showProfileEditDialog = true
        }
    }

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
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = if (userInfo?.role == "ROLE_DIETITIAN") "Hoş geldiniz, ${userInfo.name ?: "Uzman"}" else "Merhaba, ${userInfo?.name ?: "Danışan"}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showNotificationDialog = true }) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Bildirimler",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            if (viewModel.unreadCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = viewModel.unreadCount.toString(),
                                        color = MaterialTheme.colorScheme.onError,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    TextButton(onClick = onLogout) {
                        Text("Çıkış Yap", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Ana Sayfa (Tüm roller için ortak)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
                    label = { Text("Ana Sayfa", fontWeight = FontWeight.Medium) },
                    selected = true,
                    onClick = {}
                )

                if (userInfo?.role == "ROLE_DIETITIAN") {
                    // Danışanlar
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.People, contentDescription = "Danışanlar") },
                        label = { Text("Danışanlar", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToExplore
                    )
                    // Çalışma Slotu Ekle
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Slot Ekle") },
                        label = { Text("Slot Ekle", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToSlots
                    )
                    // Mesajlarım
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Email, contentDescription = "Mesajlarım") },
                        label = { Text("Mesajlarım", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToMessages
                    )
                    // Klinik Analitiği
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Analizler") },
                        label = { Text("Analizler", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToAnalytics
                    )
                } else if (userInfo?.role == "ROLE_USER") {
                    // Geçmişim
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.History, contentDescription = "Geçmişim") },
                        label = { Text("Geçmişim", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToExplore
                    )
                    // Randevu Al
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AddCircle, contentDescription = "Randevu Al") },
                        label = { Text("Randevu Al", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = { showAppointmentDialog = true }
                    )
                    // Mesajlarım
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Email, contentDescription = "Mesajlarım") },
                        label = { Text("Mesajlarım", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToMessages
                    )
                } else if (userInfo?.role == "ROLE_ADMIN" || userInfo?.email == "suhedaterat2@gmail.com") {
                    // Başvurular (Admin için)
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Başvurular") },
                        label = { Text("Başvurular", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = { showApplicationsDialog = true }
                    )
                }

                // Profil
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = {
                        if (userInfo?.role == "ROLE_DIETITIAN") {
                            onNavigateToProfile()
                        } else {
                            showProfileEditDialog = true
                        }
                    }
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
