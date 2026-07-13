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

    var waterIntake by remember { mutableStateOf(1000) }
    var glp1Nausea by remember { mutableStateOf(2) }
    var sideEffectLevel by remember { mutableStateOf(1) }
    var painLevel by remember { mutableStateOf(2) }
    var glutenFree by remember { mutableStateOf(true) }
    var sugarFree by remember { mutableStateOf(true) }
    var fastingGlucose by remember { mutableStateOf("") }
    var insulinLevel by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text("Boy: ${userInfo?.height ?: 0} cm", color = TextSecondaryDark, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Başlangıç", fontSize = 12.sp, color = TextSecondaryDark)
                        Text("${userInfo?.currentWeight ?: 0.0} kg", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }
                    Column {
                        Text("Hedef", fontSize = 12.sp, color = TextSecondaryDark)
                        Text("${userInfo?.targetWeight ?: 0.0} kg", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }
                    Column {
                        Text("Vücut Kitle İndeksi", fontSize = 12.sp, color = TextSecondaryDark)
                        val w = userInfo?.currentWeight ?: 0.0
                        val h = userInfo?.height ?: 0.0
                        val bmi = if (w > 0 && h > 0) String.format("%.1f", w / ((h / 100) * (h / 100))) else "-"
                        Text(bmi, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GreenPrimary)
                    }
                }
            }
        }

        Text("Bugünkü Diyet Programınız", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.padding(vertical = 8.dp))
        if (todayDiet != null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🥗 ${todayDiet.title ?: "Günlük Menü"}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                        Text("${todayDiet.targetCalories ?: 0} kcal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GreenPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🍳 Kahvaltı: ${todayDiet.breakfast ?: "Planlanmadı"}", fontSize = 13.sp, color = TextDark)
                    Text("🍲 Öğle: ${todayDiet.lunch ?: "Planlanmadı"}", fontSize = 13.sp, color = TextDark)
                    Text("🥗 Akşam: ${todayDiet.dinner ?: "Planlanmadı"}", fontSize = 13.sp, color = TextDark)
                    if (!todayDiet.snacks.isNullOrBlank()) {
                        Text("☕ Ara Öğün: ${todayDiet.snacks}", fontSize = 13.sp, color = TextDark)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.toggleDietCompleted(todayDiet.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (todayDiet.completed) GreenPrimary else Color.Gray
                        )
                    ) {
                        Text(if (todayDiet.completed) "✓ Bugünü Başarıyla Tamamladım!" else "Bugün Diyetime Uydum", color = Color.White)
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    "📭 Diyetisyeniniz henüz bugün için bir diyet planı atamadı.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Text("Günlük Takip Kaydı", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.padding(vertical = 8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("💧 Su Tüketimi", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        Text("Hedef: 2500 - 3000 ml", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { waterIntake = Math.max(0, waterIntake - 250) }) {
                            Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                        Text("$waterIntake ml", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        TextButton(onClick = { waterIntake += 250 }) {
                            Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                    }
                }

                if (userInfo?.category == "GLP_1") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("💉 GLP-1 Yan Etki Seviyesi", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        (1..5).forEach { num ->
                            val isSel = sideEffectLevel == num
                            Card(
                                modifier = Modifier.size(36.dp).clickable { sideEffectLevel = num },
                                colors = CardDefaults.cardColors(containerColor = if (isSel) GreenPrimary else Color.LightGray)
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("$num", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                        Text("🌾 Şekersiz/Glütensiz Beslenme", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                        Switch(
                            checked = glutenFree,
                            onCheckedChange = { glutenFree = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = GreenPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.saveDailyLog(
                            DailyLog(
                                date = java.time.LocalDate.now().toString(),
                                waterIntakeMl = waterIntake,
                                sideEffectLevel = if (userInfo?.category == "GLP_1") sideEffectLevel else null,
                                glp1Nausea = if (userInfo?.category == "GLP_1") glp1Nausea else null,
                                painLevel = if (userInfo?.category == "LIPEDEMA") painLevel else null,
                                glutenFree = if (userInfo?.category == "LIPEDEMA") glutenFree else null,
                                sugarFree = if (userInfo?.category == "LIPEDEMA") sugarFree else null,
                                fastingBloodGlucose = fastingGlucose.toDoubleOrNull(),
                                insulinLevel = insulinLevel.toDoubleOrNull()
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("💾 Bugünkü Kaydı Kaydet", color = Color.White)
                }
            }
        }

        if (userInfo?.dietitian != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("👩‍⚕️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Klinik Diyetisyeniniz", fontSize = 11.sp, color = TextSecondaryDark)
                        Text(userInfo.dietitian.name ?: "Diyetisyen", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                        Text(userInfo.dietitian.notes ?: "Klinik Sorumlusu", fontSize = 11.sp, color = TextSecondaryDark)
                    }
                }
            }
        }

        Text("Seans Randevularım", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.padding(vertical = 12.dp))
        if (appointments.isNotEmpty()) {
            appointments.forEach { app ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("🗓 ${app.appointmentDate} | ⏰ ${app.appointmentTime}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                            if (!app.note.isNullOrBlank()) {
                                Text("Not: \"${app.note}\"", fontSize = 11.sp, color = TextSecondaryDark)
                            }
                        }
                        val statusLabel = when (app.status) {
                            "APPROVED" -> "Onaylandı"
                            "REJECTED" -> "Reddedildi"
                            else -> "Bekliyor"
                        }
                        val statusBg = when (app.status) {
                            "APPROVED" -> Color(0xFFE8F5E9)
                            "REJECTED" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFFFF3E0)
                        }
                        val statusColor = when (app.status) {
                            "APPROVED" -> Color(0xFF2E7D32)
                            "REJECTED" -> Color(0xFFC62828)
                            else -> Color(0xFFEF6C00)
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = statusBg),
                            shape = RoundedCornerShape(6.dp)
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
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Text(
                    "Kayıtlı aktif randevunuz bulunmamaktadır.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (prediction != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("🔮 Klinik Tahminleme Raporu", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.padding(vertical = 4.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Geçmiş kilo kayıp trendlerinize göre hedef kilonuza ulaşacağınız tahmini tarih:", fontSize = 12.sp, color = TextSecondaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("🎯 Tahmini Tarih: ${prediction.targetAchievedDate ?: "-"}", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 13.sp)
                        Text("${prediction.daysRemaining ?: 0} gün kaldı", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 13.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}
