package com.diet.android.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.diet.android.ui.screens.home.HomeViewModel
import com.diet.android.ui.theme.*

@Composable
fun ClinicAnalyticsDialog(
    visible: Boolean,
    onClose: () -> Unit,
    viewModel: HomeViewModel
) {
    if (!visible) return

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📊 Klinik Analitiği Raporları",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                    TextButton(onClick = onClose) {
                        Text("Kapat", color = GreenPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Kohort Analizi Kartı
                Text(
                    text = "👥 Kohort Analizi (Aylık Kilo Kaybı)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val cohorts = viewModel.cohortsData
                if (cohorts.isEmpty()) {
                    Text("Analiz için yeterli kohort verisi yok.", fontSize = 12.sp, color = TextSecondaryDark)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F7F6), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        // Headers
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                            Text("Kohort Ayı", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, modifier = Modifier.weight(1f))
                            Text("Başl. Kilo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, modifier = Modifier.width(65.dp))
                            Text("Güncel Kilo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, modifier = Modifier.width(65.dp))
                            Text("Ort. Kayıp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, modifier = Modifier.width(65.dp))
                        }
                        // Rows
                        cohorts.forEach { c ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(c.cohortMonth ?: "-", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark, modifier = Modifier.weight(1f))
                                Text(String.format("%.1f kg", c.averageStartingWeight ?: 0.0), fontSize = 12.sp, color = TextDark, modifier = Modifier.width(65.dp))
                                Text(String.format("%.1f kg", c.averageCurrentWeight ?: 0.0), fontSize = 12.sp, color = TextDark, modifier = Modifier.width(65.dp))
                                Text(String.format("-%.1f kg", c.averageWeightLoss ?: 0.0), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), modifier = Modifier.width(65.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Kategori Bazlı Uyum Oranları
                Text(
                    text = "🎯 Kategori Bazlı Uyum Oranları",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val compliance = viewModel.complianceData
                if (compliance.isEmpty()) {
                    Text("Uyum verisi bulunamadı.", fontSize = 12.sp, color = TextSecondaryDark)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        compliance.forEach { item ->
                            val complianceVal = item.complianceRate ?: 0.0
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
                                        text = String.format("%.0f%% Uyum", complianceVal),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = barColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
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

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Kilo Kayıp Hızları
                Text(
                    text = "📉 Haftalık Kilo Kayıp Hızları",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GreenPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val rates = viewModel.weightLossRates
                if (rates.isEmpty()) {
                    Text("Kaydedilmiş kilo kaybı hızı bulunamadı.", fontSize = 12.sp, color = TextSecondaryDark)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F7F6), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        // Headers
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                            Text("Danışan Adı", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, modifier = Modifier.weight(1.2f))
                            Text("Kategori", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, modifier = Modifier.weight(1f))
                            Text("Haftalık Hız", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondaryDark, modifier = Modifier.width(75.dp))
                        }
                        // Rows
                        rates.forEach { r ->
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Text(r.clientName ?: "Danışan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDark, modifier = Modifier.weight(1.2f))
                                Text(
                                    text = when (r.category) {
                                        "GLP_1" -> "GLP-1"
                                        "LIPEDEMA" -> "Lipödem"
                                        "HORMONAL_BALANCE" -> "Hormon"
                                        else -> "Kilo Yön."
                                    },
                                    fontSize = 11.sp,
                                    color = TextSecondaryDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = String.format("%.2f kg/h", r.weightLossRateKgPerWeek ?: 0.0),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.width(75.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
