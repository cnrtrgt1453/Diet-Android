package com.diet.android.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.diet.android.ui.screens.home.components.FlowRow
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    viewModel: HomeViewModel,
    onNavigateToHome: (String?) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val userInfo = viewModel.userInfo
    val dietitianId = userInfo?.dietitian?.id

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    var appNote by remember { mutableStateOf("") }
    var selectedSlotId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is HomeUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    appNote = ""
                    selectedSlotId = null
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Randevu Al",
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
                // Anasayfa
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Anasayfa") },
                    label = { Text("Anasayfa", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { onNavigateToHome(null) }
                )
                // Geçmişim
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Geçmişim") },
                    label = { Text("Geçmişim", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = onNavigateToExplore
                )
                // Randevu Al (Seçili)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Randevu Al") },
                    label = { Text("Randevu Al", fontWeight = FontWeight.Medium) },
                    selected = true,
                    onClick = {}
                )
                // Mesajlarım
                NavigationBarItem(
                    icon = {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(Icons.Default.Email, contentDescription = "Mesajlar")
                            if (viewModel.unreadMessagesCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .offset(x = 6.dp, y = (-4).dp)
                                        .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = viewModel.unreadMessagesCount.toString(),
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    label = { Text("Mesajlar", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = onNavigateToMessages
                )
                // Profil
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = onNavigateToProfile
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)) // Deep slate light tone
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Diyetisyen Bilgi Kartı
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Diyetisyen",
                        tint = GreenPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = userInfo?.dietitian?.name ?: "Diyetisyeniniz",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        Text(
                            text = "Uzman Diyetisyen Takip Programı",
                            fontSize = 12.sp,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tarih Aralığı Seçim Kartı
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "📅 Randevu Tarih Aralığı",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Başlangıç Tarihi
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showStartDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = startDate,
                                onValueChange = {},
                                label = { Text("Başlangıç Tarihi", fontSize = 11.sp) },
                                placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
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

                        // Bitiş Tarihi
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showEndDatePicker = true }
                        ) {
                            OutlinedTextField(
                                value = endDate,
                                onValueChange = {},
                                label = { Text("Bitiş Tarihi", fontSize = 11.sp) },
                                placeholder = { Text("YYYY-MM-DD", fontSize = 12.sp) },
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ara Butonu
                    Button(
                        onClick = {
                            if (dietitianId != null && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                                viewModel.searchAvailableSlotsInRange(dietitianId, startDate, endDate)
                            } else {
                                Toast.makeText(context, "Lütfen başlangıç ve bitiş tarihlerini seçiniz.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Ara", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Müsait Saatleri Ara", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Date Pickers
            if (showStartDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showStartDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                                    startDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                }
                                showStartDatePicker = false
                            }
                        ) {
                            Text("Seç", color = GreenPrimary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showStartDatePicker = false }) {
                            Text("İptal", color = TextSecondaryDark)
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            if (showEndDatePicker) {
                val datePickerState = rememberDatePickerState()
                DatePickerDialog(
                    onDismissRequest = { showEndDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val localDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                                    endDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                }
                                showEndDatePicker = false
                            }
                        ) {
                            Text("Seç", color = GreenPrimary)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEndDatePicker = false }) {
                            Text("İptal", color = TextSecondaryDark)
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Arama Sonuçları
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else {
                Text(
                    text = "Mevcut Boş Saat Slotları",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextDark,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )

                if (viewModel.searchedSlots.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B))
                    ) {
                        Text(
                            text = "Arama kriterlerinize uygun boş saat slotu bulunmamaktadır.",
                            color = Color(0xFFB45309),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    // Group slots by date
                    val groupedSlots = remember(viewModel.searchedSlots) {
                        viewModel.searchedSlots.groupBy { it.date }
                    }

                    groupedSlots.forEach { (date, slotsForDate) ->
                        ElevatedCard(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = date,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GreenPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    mainAxisSpacing = 8.dp,
                                    crossAxisSpacing = 8.dp
                                ) {
                                    slotsForDate.forEach { slot ->
                                        val isSelected = selectedSlotId == slot.id
                                        Card(
                                            border = BorderStroke(1.dp, if (isSelected) GreenPrimary else Color.LightGray),
                                            colors = CardDefaults.cardColors(containerColor = if (isSelected) GreenPrimary else Color(0xFFF8FAFC)),
                                            modifier = Modifier.clickable { selectedSlotId = slot.id },
                                            shape = RoundedCornerShape(20.dp)
                                        ) {
                                            Text(
                                                text = "⏰ ${slot.startTime} - ${slot.endTime}",
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                color = if (isSelected) Color.White else TextDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Randevu Notu ve Kayıt
                    OutlinedTextField(
                        value = appNote,
                        onValueChange = { appNote = it },
                        label = { Text("Randevu Notu (Şikayetiniz / İsteğiniz)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            focusedLabelColor = GreenPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            selectedSlotId?.let {
                                viewModel.bookAppointmentSlot(it, appNote)
                            }
                        },
                        enabled = selectedSlotId != null,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("📅 Randevuyu Rezerve Et (Diyetisyen Onayına Gönder)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
