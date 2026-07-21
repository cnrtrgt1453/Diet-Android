package com.diet.android.ui.screens.explore

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.diet.android.data.model.*
import com.diet.android.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onNavigateToHome: (String?) -> Unit,
    onNavigateToSlots: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: ExploreViewModel
) {
    val context = LocalContext.current
    val userInfo = viewModel.userInfo
    val isDietitian = userInfo?.role == "ROLE_DIETITIAN"

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    // Dialog state controllers
    var showDetailDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var showAddMeasurementDialog by remember { mutableStateOf(false) }
    var showAddDietDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData(context)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ExploreUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is ExploreUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                ExploreUiEvent.DismissModals -> {
                    showAddDietDialog = false
                    showAddMeasurementDialog = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isDietitian) "Danışan Rehberi" else "Takip Geçmişiniz", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Anasayfa") },
                    label = { Text("Anasayfa", fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1) },
                    selected = false,
                    onClick = { onNavigateToHome(null) }
                )

                if (isDietitian) {
                    // Danışan
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.People, contentDescription = "Danışan") },
                        label = { Text("Danışan", fontWeight = FontWeight.Medium) },
                        selected = true,
                        onClick = {}
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
                                            color = MaterialTheme.colorScheme.onError,
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
                    // Klinik Analitiği
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Analiz") },
                        label = { Text("Analiz", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToAnalytics
                    )
                } else if (userInfo?.role == "ROLE_USER") {
                    // Geçmişim
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.History, contentDescription = "Geçmişim") },
                        label = { Text("Geçmişim", fontWeight = FontWeight.Medium) },
                        selected = true,
                        onClick = {}
                    )
                    // Randevu Al
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AddCircle, contentDescription = "Randevu Al") },
                        label = { Text("Randevu Al", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = { onNavigateToHome("appointment") }
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
                                            color = MaterialTheme.colorScheme.onError,
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
                } else if (userInfo?.role == "ROLE_ADMIN" || userInfo?.email == "suhedaterat2@gmail.com") {
                    // Başvurular (Admin için)
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Başvurular") },
                        label = { Text("Başvurular", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = { onNavigateToHome("applications") }
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

                if (isDietitian) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.filterClients(it, selectedCategory)
                        },
                        placeholder = { Text("İsim veya e-posta ile ara...", color = TextSecondaryDark) },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    // Category filters
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("ALL", "GLP_1", "LIPEDEMA", "HORMONAL_BALANCE").forEach { cat ->
                            val isSelected = selectedCategory == cat
                            val label = when (cat) {
                                "ALL" -> "Hepsi"
                                "GLP_1" -> "GLP-1"
                                "LIPEDEMA" -> "Lipödem"
                                else -> "Hormon"
                            }
                            Card(
                                modifier = Modifier.clickable {
                                    selectedCategory = cat
                                    viewModel.filterClients(searchQuery, cat)
                                },
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) GreenPrimary else Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else GreenPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Client List
                    viewModel.filteredClients.forEach { client ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    viewModel.selectClient(client)
                                    showDetailDialog = true
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(client.name ?: "Danışan", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                                    Text(client.email ?: "", fontSize = 12.sp, color = TextSecondaryDark)
                                    val catLabel = when (client.category) {
                                        "GLP_1" -> "GLP-1 Destekli"
                                        "LIPEDEMA" -> "Lipödem Diyeti"
                                        "HORMONAL_BALANCE" -> "Hormonal Denge"
                                        else -> "Kilo Yönetimi"
                                    }
                                    Text("Kategori: $catLabel", fontSize = 11.sp, color = GreenPrimary)
                                }
                                Button(
                                    onClick = {
                                        viewModel.startChat(client, context)
                                        showChatDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text("💬 Sohbet", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else {
                    // Client View - History & Charts
                    Text("📊 Kilo İlerleme Grafiği", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val weights = viewModel.myMeasurements.map { it.weight }
                            if (weights.isNotEmpty()) {
                                SimpleComposeBarChart(data = weights)
                            } else {
                                Text("İlerleme grafiği için henüz ölçüm eklenmemiş.", fontSize = 13.sp, color = TextSecondaryDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("📋 Ölçüm Geçmişiniz", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.padding(vertical = 8.dp))
                    if (viewModel.myMeasurements.isNotEmpty()) {
                        viewModel.myMeasurements.forEach { m ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("🗓 ${m.date}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${m.weight} kg", fontWeight = FontWeight.Bold, color = GreenPrimary)
                                    }
                                    if (m.bodyFat != null || m.muscleMass != null) {
                                        Text("Yağ: %${m.bodyFat ?: "-"} | Kas: ${m.muscleMass ?: "-"} kg", fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                    if (!m.note.isNullOrBlank()) {
                                        Text("Not: ${m.note}", fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                }
                            }
                        }
                    } else {
                        Text("Kayıtlı ölçüm bulunmuyor.", fontSize = 12.sp, color = TextSecondaryDark, modifier = Modifier.padding(8.dp))
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    // Detail Dialog
    if (showDetailDialog && viewModel.selectedClient != null) {
        val client = viewModel.selectedClient!!
        Dialog(
            onDismissRequest = { showDetailDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(client.name ?: "Danışan Detayı", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                        TextButton(onClick = { showDetailDialog = false }) {
                            Text("Kapat", color = GreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Klinik Durum & Analiz", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Boy: ${client.height ?: "-"} cm | Başlangıç Kilosu: ${client.startingWeight ?: "-"} kg", fontSize = 13.sp)
                            val pred = viewModel.clientPrediction
                            if (pred != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("🔮 Tahmini Hedef Tarih: ${pred.targetAchievedDate ?: "Hesaplanıyor"}", fontWeight = FontWeight.Bold, color = GreenPrimary)
                                Text("Hedefe kalan tahmini süre: ${pred.daysRemaining ?: "-"} gün", fontSize = 12.sp, color = TextSecondaryDark)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showAddMeasurementDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+ Yeni Ölçüm", color = Color.White)
                        }
                        Button(
                            onClick = {
                                viewModel.loadTemplates()
                                showAddDietDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+ Diyet Ata", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("📋 Ölçüm Geçmişi", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                    if (viewModel.clientMeasurements.isNotEmpty()) {
                        viewModel.clientMeasurements.forEach { m ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(m.date, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${m.weight} kg", fontWeight = FontWeight.Bold, color = GreenPrimary)
                                    }
                                    if (m.bodyFat != null || m.muscleMass != null) {
                                        Text("Yağ: %${m.bodyFat ?: "-"} | Kas: ${m.muscleMass ?: "-"} kg", fontSize = 11.sp, color = TextSecondaryDark)
                                    }
                                }
                            }
                        }
                    } else {
                        Text("Kayıtlı ölçüm bulunmuyor.", fontSize = 12.sp, color = TextSecondaryDark)
                    }
                }
            }
        }
    }

    // Add Measurement Dialog
    if (showAddMeasurementDialog) {
        var weightInput by remember { mutableStateOf("") }
        var fatInput by remember { mutableStateOf("") }
        var muscleInput by remember { mutableStateOf("") }
        var noteInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMeasurementDialog = false },
            title = { Text("Yeni Ölçüm Girişi") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Ağırlık (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fatInput,
                        onValueChange = { fatInput = it },
                        label = { Text("Yağ Oranı (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = muscleInput,
                        onValueChange = { muscleInput = it },
                        label = { Text("Kas Kütlesi (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Klinik Not") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = weightInput.toDoubleOrNull()
                        if (w != null) {
                            viewModel.addMeasurement(
                                Measurement(
                                    date = java.time.LocalDate.now().toString(),
                                    weight = w,
                                    bodyFat = fatInput.toDoubleOrNull(),
                                    muscleMass = muscleInput.toDoubleOrNull(),
                                    note = noteInput.takeIf { it.isNotBlank() }
                                )
                            )
                            showAddMeasurementDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Kaydet", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMeasurementDialog = false }) {
                    Text("İptal", color = Color.Gray)
                }
            }
        )
    }

    // Add Diet Dialog
    if (showAddDietDialog) {
        var dTitle by remember { mutableStateOf("") }
        var dBreakfast by remember { mutableStateOf("") }
        var dLunch by remember { mutableStateOf("") }
        var dDinner by remember { mutableStateOf("") }
        var dSnacks by remember { mutableStateOf("") }
        var dCalories by remember { mutableStateOf("") }
        var saveAsTemplate by remember { mutableStateOf(false) }
        var templateTitle by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = { showAddDietDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Diyet Programı Atama", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        TextButton(onClick = { showAddDietDialog = false }) {
                            Text("Kapat", color = GreenPrimary)
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    OutlinedTextField(value = dTitle, onValueChange = { dTitle = it }, label = { Text("Program Başlığı (örn: Pazartesi Detoksu)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = dBreakfast, onValueChange = { dBreakfast = it }, label = { Text("Kahvaltı Öğünü") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = dLunch, onValueChange = { dLunch = it }, label = { Text("Öğle Öğünü") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = dDinner, onValueChange = { dDinner = it }, label = { Text("Akşam Öğünü") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = dSnacks, onValueChange = { dSnacks = it }, label = { Text("Ara Öğünler") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dCalories,
                        onValueChange = { dCalories = it },
                        label = { Text("Hedef Kalori (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = saveAsTemplate, onCheckedChange = { saveAsTemplate = it }, colors = CheckboxDefaults.colors(checkedColor = GreenPrimary))
                        Text("Şablon kütüphanesine kaydet", fontSize = 13.sp)
                    }
                    if (saveAsTemplate) {
                        OutlinedTextField(value = templateTitle, onValueChange = { templateTitle = it }, label = { Text("Şablon Adı") }, modifier = Modifier.fillMaxWidth())
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val cal = dCalories.toIntOrNull()
                            if (dTitle.isNotBlank() && dBreakfast.isNotBlank() && cal != null) {
                                viewModel.addDiet(
                                    DietPlan(
                                        id = 0,
                                        title = dTitle,
                                        date = java.time.LocalDate.now().toString(),
                                        breakfast = dBreakfast,
                                        lunch = dLunch.takeIf { it.isNotBlank() },
                                        dinner = dDinner.takeIf { it.isNotBlank() },
                                        snacks = dSnacks.takeIf { it.isNotBlank() },
                                        targetCalories = cal,
                                        targetProteinGrams = null,
                                        targetCarbsGrams = null,
                                        targetFatGrams = null,
                                        completed = false
                                    ),
                                    saveAsTemplate = saveAsTemplate,
                                    templateTitle = templateTitle
                                )
                                showAddDietDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Programı Atamayı Tamamla", color = Color.White)
                    }
                }
            }
        }
    }

    // Chat Dialog
    if (showChatDialog && viewModel.chatWithUser != null) {
        val partner = viewModel.chatWithUser!!
        var messageText by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = {
                viewModel.endChat()
                showChatDialog = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GreenPrimary)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(partner.name ?: "Sohbet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Çevrimiçi (WebSocket aktif)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                        TextButton(
                            onClick = {
                                viewModel.endChat()
                                showChatDialog = false
                            }
                        ) {
                            Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Messages Stream
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState(), reverseScrolling = true),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.chatMessages.forEach { msg ->
                            val isMe = msg.sender.id == userInfo?.id
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isMe) 12.dp else 0.dp,
                                        bottomEnd = if (isMe) 0.dp else 12.dp
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isMe) GreenPrimary else Color.White
                                    ),
                                    modifier = Modifier.widthIn(max = 280.dp),
                                    border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(msg.content, color = if (isMe) Color.White else TextDark, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Message Input field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Mesajınızı yazın...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendChatMessage(messageText)
                                    messageText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Gönder", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleComposeBarChart(data: List<Double>) {
    val maxVal = if (data.isNotEmpty()) data.maxOrNull() ?: 1.0 else 1.0
    val minVal = if (data.isNotEmpty()) data.minOrNull() ?: 0.0 else 0.0
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.takeLast(7).forEachIndexed { idx, value ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // simple scaling
                val scale = if (maxVal > 0) (value / maxVal).toFloat() else 0.1f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(scale.coerceAtLeast(0.1f))
                        .background(GreenPrimary, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format("%.1f", value),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }
    }
}
