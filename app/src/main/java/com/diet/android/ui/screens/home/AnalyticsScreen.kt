package com.diet.android.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diet.android.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: HomeViewModel,
    onNavigateToHome: (String?) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToSlots: () -> Unit
) {
    val userInfo = viewModel.userInfo

    LaunchedEffect(Unit) {
        viewModel.loadClinicAnalytics()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Klinik Analitiği",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
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
                    selected = false,
                    onClick = { onNavigateToHome(null) }
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
                    // Klinik Analitiği
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Analizler") },
                        label = { Text("Analizler", fontWeight = FontWeight.Medium) },
                        selected = true,
                        onClick = {
                            viewModel.loadClinicAnalytics()
                        }
                    )
                }

                // Profil (Tüm roller için ortak)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { onNavigateToHome("profile") }
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

                // 1. Kohort Analizi Kartı
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "👥 Kohort Analizi (Aylık Kilo Kaybı)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GreenPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val cohorts = viewModel.cohortsData
                        if (cohorts.isEmpty()) {
                            Text(
                                text = "Analiz için yeterli kohort verisi yok.",
                                fontSize = 13.sp,
                                color = TextSecondaryDark
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF5F7F6), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                // Headers
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "Kohort Ayı",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Başl. Kilo",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.width(70.dp)
                                    )
                                    Text(
                                        text = "Güncel Kilo",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.width(70.dp)
                                    )
                                    Text(
                                        text = "Ort. Kayıp",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.width(70.dp)
                                    )
                                }
                                // Rows
                                cohorts.forEach { c ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = c.cohortMonth,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = String.format("%.1f kg", c.averageStartingWeight),
                                            fontSize = 13.sp,
                                            color = TextDark,
                                            modifier = Modifier.width(70.dp)
                                        )
                                        Text(
                                            text = String.format("%.1f kg", c.averageCurrentWeight),
                                            fontSize = 13.sp,
                                            color = TextDark,
                                            modifier = Modifier.width(70.dp)
                                        )
                                        Text(
                                            text = String.format("-%.1f kg", c.averageWeightLoss),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier.width(70.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Kategori Bazlı Uyum Oranları
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "🎯 Kategori Bazlı Uyum Oranları",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GreenPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val compliance = viewModel.complianceData
                        if (compliance.isEmpty()) {
                            Text(
                                text = "Uyum verisi bulunamadı.",
                                fontSize = 13.sp,
                                color = TextSecondaryDark
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                compliance.forEach { item ->
                                    val complianceVal = item.complianceRate
                                    val barColor = when {
                                        complianceVal >= 80 -> Color(0xFF2E7D32)
                                        complianceVal >= 50 -> Color(0xFFF9A825)
                                        else -> Color(0xFFC62828)
                                    }
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = when (item.category) {
                                                    "GLP_1" -> "GLP-1 Destekli"
                                                    "LIPEDEMA" -> "Lipödem Diyeti"
                                                    "HORMONAL_BALANCE" -> "Hormonal Denge"
                                                    else -> "Kilo Yönetimi"
                                                },
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextDark
                                            )
                                            Text(
                                                text = String.format("%%.0f Uyum", complianceVal),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = barColor
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .background(Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
                                        ) {
                                            val progressFraction = (complianceVal / 100.0).toFloat().coerceIn(0f, 1f)
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(progressFraction)
                                                    .fillMaxHeight()
                                                    .background(barColor, RoundedCornerShape(4.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Kilo Kayıp Hızları
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "📉 Haftalık Kilo Kayıp Hızları",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GreenPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val rates = viewModel.weightLossRates
                        if (rates.isEmpty()) {
                            Text(
                                text = "Kaydedilmiş kilo kaybı hızı bulunamadı.",
                                fontSize = 13.sp,
                                color = TextSecondaryDark
                            )
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF5F7F6), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                // Headers
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    Text(
                                        text = "Danışan Adı",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.weight(1.2f)
                                    )
                                    Text(
                                        text = "Kategori",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Haftalık Hız",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryDark,
                                        modifier = Modifier.width(80.dp)
                                    )
                                }
                                // Rows
                                rates.forEach { r ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = r.clientName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDark,
                                            modifier = Modifier.weight(1.2f)
                                        )
                                        Text(
                                            text = when (r.category) {
                                                "GLP_1" -> "GLP-1"
                                                "LIPEDEMA" -> "Lipödem"
                                                "HORMONAL_BALANCE" -> "Hormon"
                                                else -> "Kilo Yön."
                                            },
                                            fontSize = 12.sp,
                                            color = TextSecondaryDark,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = String.format("%.2f kg/h", r.weightLossRateKgPerWeek),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier.width(80.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
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
        }
    }
}
