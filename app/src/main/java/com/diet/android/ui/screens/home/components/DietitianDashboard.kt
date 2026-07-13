package com.diet.android.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diet.android.ui.screens.home.HomeViewModel
import com.diet.android.ui.theme.*

@Composable
fun DietitianDashboard(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val stats = viewModel.dietitianStats
    val requests = viewModel.connectionRequests
    val appointments = viewModel.dietitianAppointments

    val pendingApps = appointments.filter { it.status == "PENDING" }
    val approvedApps = appointments.filter { it.status == "APPROVED" }

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Klinik İstatistikleri", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark, modifier = Modifier.padding(vertical = 8.dp))

        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            StatCard(emoji = "👥", label = "Toplam Danışan", value = "${stats.total}", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatCard(emoji = "💉", label = "GLP-1 Takip", value = "${stats.glp1}", modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            StatCard(emoji = "🦵", label = "Lipödem Diyeti", value = "${stats.lipedema}", modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            StatCard(emoji = "🧬", label = "Denge & Kilo", value = "${stats.hormonalBalance + stats.weightManagement}", modifier = Modifier.weight(1f))
        }

        if (requests.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("👥 Danışan Çalışma Talepleri (${requests.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(requests) { req ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.width(260.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(req.client.name ?: "Bilinmeyen Danışan", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                            Text(
                                "Boy: ${req.client.height ?: "-"} cm | Kilo: ${req.client.currentWeight ?: "-"} kg",
                                fontSize = 11.sp,
                                color = TextSecondaryDark
                            )
                            val catLabel = when (req.client.category) {
                                "GLP_1" -> "GLP-1 Destekli"
                                "LIPEDEMA" -> "Lipödem Diyeti"
                                "HORMONAL_BALANCE" -> "Hormonal Denge"
                                else -> "Kilo Yönetimi"
                            }
                            Text("Kategori: $catLabel", fontSize = 11.sp, color = TextSecondaryDark)
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(
                                    onClick = { viewModel.approveRequest(req.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Kabul Et", color = Color.White, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { viewModel.rejectRequest(req.id) },
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                ) {
                                    Text("Reddet", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("📅 Bekleyen Randevu Talepleri (${pendingApps.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
        if (pendingApps.isNotEmpty()) {
            pendingApps.forEach { app ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(app.client.name ?: "Danışan", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                            Text(
                                when (app.client.category) {
                                    "GLP_1" -> "GLP-1"
                                    "LIPEDEMA" -> "Lipödem"
                                    "HORMONAL_BALANCE" -> "Hormon"
                                    else -> "Kilo"
                                },
                                fontSize = 10.sp, color = GreenPrimary, fontWeight = FontWeight.Bold
                            )
                        }
                        Text("🗓 Tarih: ${app.appointmentDate} | Saat: ${app.appointmentTime}", fontSize = 12.sp, color = TextDark)
                        if (!app.note.isNullOrBlank()) {
                            Text("Not: \"${app.note}\"", fontSize = 11.sp, color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { viewModel.updateAppointmentStatus(app.id, "APPROVED") },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Onayla", color = Color.White, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.updateAppointmentStatus(app.id, "REJECTED") },
                                modifier = Modifier.weight(1f).height(36.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                            ) {
                                Text("Reddet", fontSize = 12.sp)
                            }
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
                    "🎉 Bekleyen randevu talebi bulunmuyor.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("🗓 Yaklaşan Seanslar (${approvedApps.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
        if (approvedApps.isNotEmpty()) {
            approvedApps.forEach { app ->
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
                        Text(app.client.name ?: "Danışan", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                        Text("${app.appointmentDate} | ${app.appointmentTime}", fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.Bold)
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
                    "Bu haftaya planlanmış seans bulunmuyor.",
                    fontSize = 13.sp,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StatCard(
    emoji: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
            Text(label, fontSize = 11.sp, color = TextSecondaryDark)
        }
    }
}
