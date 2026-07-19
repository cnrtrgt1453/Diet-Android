package com.diet.android.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diet.android.data.model.DailyLog
import com.diet.android.ui.screens.home.HomeViewModel
import com.diet.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDashboard(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val userInfo = viewModel.userInfo
    val todayDiet = viewModel.todayDiet
    val appointments = viewModel.clientAppointments
    val prediction = viewModel.predictionData

    var waterIntake by remember { mutableStateOf(250) }
    var glp1Nausea by remember { mutableStateOf(2) }
    var sideEffectLevel by remember { mutableStateOf(1) }
    var painLevel by remember { mutableStateOf(2) }
    var glutenFree by remember { mutableStateOf(true) }
    var sugarFree by remember { mutableStateOf(true) }
    var fastingGlucose by remember { mutableStateOf("") }
    var insulinLevel by remember { mutableStateOf("") }

    val todayLog = viewModel.todayDailyLog
    LaunchedEffect(todayLog) {
        if (todayLog != null && todayLog.date == java.time.LocalDate.now().toString()) {
            glp1Nausea = todayLog.glp1Nausea ?: 2
            sideEffectLevel = todayLog.sideEffectLevel ?: 1
            painLevel = todayLog.painLevel ?: 2
            glutenFree = todayLog.glutenFree ?: true
            sugarFree = todayLog.sugarFree ?: true
            fastingGlucose = todayLog.fastingBloodGlucose?.toString() ?: ""
            insulinLevel = todayLog.insulinLevel?.toString() ?: ""
        } else {
            glp1Nausea = 2
            sideEffectLevel = 1
            painLevel = 2
            glutenFree = true
            sugarFree = true
            fastingGlucose = ""
            insulinLevel = ""
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (userInfo?.category) {
                            "GLP_1" -> "GLP-1 Destekli"
                            "LIPEDEMA" -> "Lipödem Diyeti"
                            "HORMONAL_BALANCE" -> "Hormonal Denge"
                            else -> "Kilo Yönetimi"
                        },
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Text("Boy: ${userInfo?.height ?: 0} cm", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Başlangıç", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${userInfo?.currentWeight ?: 0.0} kg", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column {
                        Text("Hedef", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${userInfo?.targetWeight ?: 0.0} kg", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Column {
                        Text("Vücut Kitle İndeksi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val w = userInfo?.currentWeight ?: 0.0
                        val h = userInfo?.height ?: 0.0
                        val bmi = if (w > 0 && h > 0) String.format("%.1f", w / ((h / 100) * (h / 100))) else "-"
                        Text(bmi, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        Text("Bugünkü Diyet Programınız", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 8.dp))
        if (todayDiet != null) {
            OutlinedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.outlinedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🥗 ${todayDiet.title ?: "Günlük Menü"}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("${todayDiet.targetCalories ?: 0} kcal", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🍳 Kahvaltı: ${todayDiet.breakfast ?: "Planlanmadı"}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("🍲 Öğle: ${todayDiet.lunch ?: "Planlanmadı"}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("🥗 Akşam: ${todayDiet.dinner ?: "Planlanmadı"}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    if (!todayDiet.snacks.isNullOrBlank()) {
                        Text("☕ Ara Öğün: ${todayDiet.snacks}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.toggleDietCompleted(todayDiet.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (todayDiet.completed) MaterialTheme.colorScheme.primary else Color.Gray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (todayDiet.completed) "✓ Bugünü Başarıyla Tamamladım!" else "Bugün Diyetime Uydum", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    "📭 Diyetisyeniniz henüz bugün için bir diyet planı atamadı.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Text("Günlük Takip Kaydı", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 8.dp))
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("💧 Su Tüketimi", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Hedef: 2500 - 3000 ml", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { waterIntake = Math.max(0, waterIntake - 250) }) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("$waterIntake ml", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        TextButton(onClick = { waterIntake += 250 }) {
                            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                if (userInfo?.category == "GLP_1") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("💉 GLP-1 Yan Etki Seviyesi", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        (1..5).forEach { num ->
                            val isSel = sideEffectLevel == num
                            ElevatedCard(
                                modifier = Modifier.size(36.dp).clickable { sideEffectLevel = num },
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("$num", color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                if (userInfo?.category == "LIPEDEMA") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌾 Şekersiz/Glütensiz Beslenme", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                        Switch(
                            checked = glutenFree,
                            onCheckedChange = { glutenFree = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                val isToday = viewModel.todayDailyLog?.date == java.time.LocalDate.now().toString()
                val currentTotalWater = if (isToday) viewModel.todayDailyLog?.waterIntakeMl ?: 0 else 0

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val currentLog = viewModel.todayDailyLog
                        viewModel.saveDailyLog(
                            DailyLog(
                                id = if (isToday) currentLog?.id else null,
                                date = java.time.LocalDate.now().toString(),
                                waterIntakeMl = currentTotalWater + waterIntake,
                                sideEffectLevel = if (userInfo?.category == "GLP_1") sideEffectLevel else if (isToday) currentLog?.sideEffectLevel else null,
                                glp1Nausea = if (userInfo?.category == "GLP_1") glp1Nausea else if (isToday) currentLog?.glp1Nausea else null,
                                painLevel = if (userInfo?.category == "LIPEDEMA") painLevel else if (isToday) currentLog?.painLevel else null,
                                glutenFree = if (userInfo?.category == "LIPEDEMA") glutenFree else if (isToday) currentLog?.glutenFree else null,
                                sugarFree = if (userInfo?.category == "LIPEDEMA") sugarFree else if (isToday) currentLog?.sugarFree else null,
                                fastingBloodGlucose = fastingGlucose.toDoubleOrNull() ?: if (isToday) currentLog?.fastingBloodGlucose else null,
                                insulinLevel = insulinLevel.toDoubleOrNull() ?: if (isToday) currentLog?.insulinLevel else null
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("💧 Bugünkü Su Tüketimine Ekle", color = Color.White, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bugün gerçekten toplam tüketilen su: $currentTotalWater ml",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        if (userInfo?.dietitian != null) {
            Spacer(modifier = Modifier.height(12.dp))
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("👩‍⚕️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Klinik Diyetisyeniniz", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Text(userInfo.dietitian.name ?: "Diyetisyen", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text(userInfo.dietitian.notes ?: "Klinik Sorumlusu", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f))
                    }
                }
            }
        }

        Text("Seans Randevularım", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 12.dp))
        if (appointments.isNotEmpty()) {
            appointments.forEach { app ->
                ElevatedCard(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🗓 ${app.appointmentDate} | ⏰ ${app.appointmentTime}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            if (!app.note.isNullOrBlank()) {
                                Text("Not: \"${app.note}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        val statusLabel = when (app.status) {
                            "APPROVED" -> "Onaylandı"
                            "REJECTED" -> "Reddedildi"
                            else -> "Bekliyor"
                        }
                        val statusBg = when (app.status) {
                            "APPROVED" -> EmeraldLight
                            "REJECTED" -> RoseLight
                            else -> Color(0xFFFFF3E0)
                        }
                        val statusColor = when (app.status) {
                            "APPROVED" -> EmeraldGreen
                            "REJECTED" -> RoseRed
                            else -> Color(0xFFEF6C00)
                        }
                        ElevatedCard(
                            colors = CardDefaults.elevatedCardColors(containerColor = statusBg),
                            shape = RoundedCornerShape(6.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        } else {
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    "Kayıtlı aktif randevunuz bulunmamaktadır.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (prediction != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("🔮 Klinik Tahminleme Raporu", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(vertical = 4.dp))
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Geçmiş kilo kayıp trendlerinize göre hedef kilonuza ulaşacağınız tahmini tarih:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🎯 Tahmini Tarih: ${prediction.targetAchievedDate ?: "-"}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
                        Text("${prediction.daysRemaining ?: 0} gün kaldı", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
