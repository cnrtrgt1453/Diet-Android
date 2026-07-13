package com.diet.android.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diet.android.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    onSaveSuccess: () -> Unit,
    onLogout: () -> Unit,
    viewModel: CompleteProfileViewModel
) {
    var height by remember { mutableStateOf("") }
    var currentWeight by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("WEIGHT_MANAGEMENT") }

    var glp1InjectionDay by remember { mutableStateOf("Pazartesi") }
    var glp1Dosage by remember { mutableStateOf("0.25 mg") }

    var lipedemaStage by remember { mutableStateOf(1) }
    var antiInflammatoryCompliant by remember { mutableStateOf(true) }

    var hormoneTargetCycle by remember { mutableStateOf("Foliküler Faz") }

    var showAlert by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.Success -> {
                    onSaveSuccess()
                }
                is ProfileUiEvent.Error -> {
                    alertMessage = event.message
                    showAlert = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text("🥗", fontSize = 36.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Profilini Tamamla",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Diyetisyeninizin size özel program oluşturabilmesi için bilgilerinizi eksiksiz doldurun.",
                fontSize = 13.sp,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Boy (cm) *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        placeholder = { Text("Örn: 165") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = GreenPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mevcut Ağırlık (kg) *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    OutlinedTextField(
                        value = currentWeight,
                        onValueChange = { currentWeight = it },
                        placeholder = { Text("Örn: 75.5") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = GreenPrimary)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Hedef Ağırlık (kg) *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    OutlinedTextField(
                        value = targetWeight,
                        onValueChange = { targetWeight = it },
                        placeholder = { Text("Örn: 65.0") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = GreenPrimary)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Takip Programı Kategorisi *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val categories = listOf(
                        "WEIGHT_MANAGEMENT" to "Kilo Yönetimi",
                        "GLP_1" to "GLP-1 Takip",
                        "LIPEDEMA" to "Lipödem",
                        "HORMONAL_BALANCE" to "Hormonal Denge"
                    )

                    Column {
                        categories.chunked(2).forEach { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { (catId, catLabel) ->
                                    val isSelected = category == catId
                                    Card(
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) GreenPrimary.copy(alpha = 0.15f) else Color.White
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .clickable { category = catId },
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = 1.dp,
                                            color = if (isSelected) GreenPrimary else Color.LightGray
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                catLabel,
                                                fontSize = 13.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) GreenPrimary else TextDark
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (category == "GLP_1") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("💉 GLP-1 Takip Detayları", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Enjeksiyon Günü", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        val days = listOf("Pazartesi", "Çarşamba", "Cuma", "Pazar")
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            days.forEach { day ->
                                val isSelected = glp1InjectionDay == day
                                Card(
                                    shape = RoundedCornerShape(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) GreenPrimary.copy(alpha = 0.15f) else Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .clickable { glp1InjectionDay = day },
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) GreenPrimary else Color.LightGray
                                    )
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                        Text(day.substring(0, 3), fontSize = 12.sp, color = if (isSelected) GreenPrimary else TextSecondaryDark)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Dozaj", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        OutlinedTextField(
                            value = glp1Dosage,
                            onValueChange = { glp1Dosage = it },
                            placeholder = { Text("Örn: 0.5 mg") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = GreenPrimary)
                        )
                    }

                    if (category == "LIPEDEMA") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("🦵 Lipödem Takip Detayları", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Lipödem Evresi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            (1..4).forEach { stage ->
                                val isSelected = lipedemaStage == stage
                                Card(
                                    shape = RoundedCornerShape(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) GreenPrimary.copy(alpha = 0.15f) else Color.White
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(2.dp)
                                        .clickable { lipedemaStage = stage },
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) GreenPrimary else Color.LightGray
                                    )
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                        Text("Evre $stage", fontSize = 12.sp, color = if (isSelected) GreenPrimary else TextSecondaryDark)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Anti-inflamatuar Diyete Uyum", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Switch(
                                checked = antiInflammatoryCompliant,
                                onCheckedChange = { antiInflammatoryCompliant = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                            )
                        }
                    }

                    if (category == "HORMONAL_BALANCE") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("🧬 Hormonal Denge Detayları", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimary)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Hedef Döngü / Faz", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        val phases = listOf("Menstrüasyon", "Foliküler Faz", "Ovulasyon", "Luteal Faz")
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            phases.chunked(2).forEach { rowPhases ->
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    rowPhases.forEach { phase ->
                                        val isSelected = hormoneTargetCycle == phase
                                        Card(
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) GreenPrimary.copy(alpha = 0.15f) else Color.White
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(2.dp)
                                                .clickable { hormoneTargetCycle = phase },
                                            border = androidx.compose.foundation.BorderStroke(
                                                width = 1.dp,
                                                color = if (isSelected) GreenPrimary else Color.LightGray
                                            )
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                                Text(phase, fontSize = 13.sp, color = if (isSelected) GreenPrimary else TextDark)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            viewModel.submitProfile(
                                height = height,
                                currentWeight = currentWeight,
                                targetWeight = targetWeight,
                                category = category,
                                glp1InjectionDay = glp1InjectionDay,
                                glp1Dosage = glp1Dosage,
                                lipedemaStage = lipedemaStage,
                                antiInflammatoryCompliant = antiInflammatoryCompliant,
                                hormoneTargetCycle = hormoneTargetCycle
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Profilimi Kaydet ve Başla", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Çıkış Yap", color = Color.Red, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Hata", fontWeight = FontWeight.Bold) },
            text = { Text(alertMessage) },
            confirmButton = {
                Button(onClick = { showAlert = false }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text("Tamam", color = Color.White)
                }
            }
        )
    }
}
