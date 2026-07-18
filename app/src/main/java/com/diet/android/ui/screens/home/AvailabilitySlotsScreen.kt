package com.diet.android.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.diet.android.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailabilitySlotsScreen(
    viewModel: HomeViewModel,
    onNavigateToHome: (String?) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val userInfo = viewModel.userInfo

    var slotDate by remember { mutableStateOf("") }
    var slotStartTime by remember { mutableStateOf("") }
    var slotEndTime by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    var currentPage by remember { mutableStateOf(0) }
    val itemsPerPage = 10

    LaunchedEffect(Unit) {
        viewModel.loadDietitianSlots()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is HomeUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    // Clear inputs on success
                    slotDate = ""
                    slotStartTime = ""
                    slotEndTime = ""
                }
            }
        }
    }

    // Sort active slots by ID descending (newest first)
    val sortedSlots = remember(viewModel.dietitianSlots) {
        viewModel.dietitianSlots.sortedByDescending { it.id ?: 0L }
    }

    // Reset current page if the list shrinks below page bounds
    val totalPages = ((sortedSlots.size + itemsPerPage - 1) / itemsPerPage).coerceAtLeast(1)
    if (currentPage >= totalPages) {
        currentPage = totalPages - 1
    }

    val pageSlots = remember(sortedSlots, currentPage) {
        val start = currentPage * itemsPerPage
        if (start < sortedSlots.size) {
            sortedSlots.slice(start until minOf(start + itemsPerPage, sortedSlots.size))
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Çalışma Saatleri",
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
                        selected = true,
                        onClick = {
                            // Update list and clear expired slots
                            viewModel.loadDietitianSlots()
                        }
                    )
                    // Klinik Analitiği
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Analizler") },
                        label = { Text("Analizler", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToAnalytics
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
                            onNavigateToHome("profile")
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

                // Create Slot Form Card
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
                            text = "📅 Yeni Çalışma Slotu Ekle",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = slotDate,
                                onValueChange = {},
                                label = { Text("Tarih (YYYY-MM-DD)") },
                                placeholder = { Text("Tarih seçmek için tıklayın") },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true,
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = TextDark,
                                    disabledBorderColor = Color.LightGray,
                                    disabledLabelColor = TextSecondaryDark,
                                    disabledPlaceholderColor = TextSecondaryDark
                                )
                            )
                        }

                        if (showDatePicker) {
                            val datePickerState = rememberDatePickerState()
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            datePickerState.selectedDateMillis?.let { millis ->
                                                val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                                                slotDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                            }
                                            showDatePicker = false
                                        }
                                    ) {
                                        Text("Seç", color = GreenPrimary)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("İptal", color = TextSecondaryDark)
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = slotStartTime,
                            onValueChange = { slotStartTime = formatTimeInputLocal(it, slotStartTime) },
                            label = { Text("Başlangıç Saati (SS:DD)") },
                            placeholder = { Text("örn: 09:00") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = slotEndTime,
                            onValueChange = { slotEndTime = formatTimeInputLocal(it, slotEndTime) },
                            label = { Text("Bitiş Saati (SS:DD)") },
                            placeholder = { Text("örn: 10:00") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (slotDate.isNotEmpty() && slotStartTime.isNotEmpty() && slotEndTime.isNotEmpty()) {
                                    viewModel.addAvailabilitySlot(slotDate, slotStartTime, slotEndTime)
                                } else {
                                    Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("➕ Çalışma Slotu Oluştur", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // List Title
                Text(
                    text = "📋 Oluşturulan Çalışma Slotları (${sortedSlots.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (sortedSlots.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Kayıtlı aktif çalışma slotu bulunmuyor.",
                                color = TextSecondaryDark,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Slots List
                    pageSlots.forEach { slot ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "🗓 Tarih: ${slot.date}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = TextDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "⏰ Saat: ${slot.startTime} - ${slot.endTime}",
                                        fontSize = 13.sp,
                                        color = TextSecondaryDark
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val statusText = if (slot.isBooked) "Rezerve Edildi" else "Boşta"
                                    val statusColor = if (slot.isBooked) GreenPrimary else Color.Gray
                                    Text(
                                        text = "Durum: $statusText",
                                        fontSize = 12.sp,
                                        color = statusColor,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        slot.id?.let { id ->
                                            viewModel.deleteAvailabilitySlot(id)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Sil",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    // Pagination Controls
                    if (totalPages > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { if (currentPage > 0) currentPage-- },
                                enabled = currentPage > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenPrimary,
                                    disabledContainerColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Geri", color = Color.White)
                            }

                            Text(
                                text = "${currentPage + 1} / $totalPages",
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                fontSize = 14.sp
                            )

                            Button(
                                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                                enabled = currentPage < totalPages - 1,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GreenPrimary,
                                    disabledContainerColor = Color.LightGray
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("İleri", color = Color.White)
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

private fun formatTimeInputLocal(input: String, previous: String): String {
    if (input.length < previous.length) {
        if (previous.endsWith(":") && !input.contains(":")) {
            val clean = input.filter { it.isDigit() }
            return if (clean.isNotEmpty()) clean.dropLast(1) else ""
        }
        return input
    }
    
    val clean = input.filter { it.isDigit() }
    if (clean.isEmpty()) return ""
    
    val hourPart = if (clean.length >= 2) clean.substring(0, 2) else clean
    val hourVal = hourPart.toIntOrNull() ?: 0
    val formattedHour = if (hourVal > 23) {
        "00"
    } else {
        hourPart
    }
    
    if (clean.length <= 2) {
        return if (clean.length == 2) "$formattedHour:" else formattedHour
    }
    
    val minPart = if (clean.length >= 4) clean.substring(2, 4) else clean.substring(2)
    val minVal = minPart.toIntOrNull() ?: 0
    val formattedMin = if (minVal > 59) {
        "00"
    } else {
        minPart
    }
    
    return "$formattedHour:$formattedMin"
}
