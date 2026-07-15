package com.diet.android.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun DietitianApplicationsDialog(
    visible: Boolean,
    onClose: () -> Unit,
    viewModel: HomeViewModel
) {
    if (!visible) return

    var rejectTargetId by remember { mutableStateOf<Long?>(null) }
    var rejectionReason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🧑‍⚕️ Diyetisyen Başvuruları",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    val apps = viewModel.dietitianApplications
                    if (apps.isEmpty()) {
                        Text(
                            text = "Aktif/Bekleyen başvuru bulunmamaktadır.",
                            fontSize = 13.sp,
                            color = TextSecondaryDark,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                        )
                    } else {
                        apps.forEach { app ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7F6)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = app.fullName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextDark
                                        )
                                        val statusColor = when (app.status) {
                                            "UNDER_REVIEW" -> Color(0xFFEF6C00)
                                            else -> Color(0xFF2E7D32)
                                        }
                                        Text(
                                            text = if (app.status == "UNDER_REVIEW") "İnceleniyor" else "Bekliyor",
                                            color = statusColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("📧 E-posta: ${app.email}", fontSize = 11.sp, color = TextSecondaryDark)
                                    Text("🎓 Üniversite: ${app.university ?: "-"}", fontSize = 11.sp, color = TextSecondaryDark)
                                    Text("📜 Diploma No: ${app.diplomaNumber ?: "-"}", fontSize = 11.sp, color = TextSecondaryDark)
                                    Text("💼 Deneyim: ${app.experienceYears ?: 0} Yıl", fontSize = 11.sp, color = TextSecondaryDark)
                                    app.note?.let {
                                        Text("📝 Not: \"$it\"", fontSize = 11.sp, color = TextSecondaryDark)
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        if (app.status == "PENDING") {
                                            Button(
                                                onClick = { viewModel.startReviewApplication(app.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("🔍 İncelemeyi Başlat", color = Color.White, fontSize = 12.sp)
                                            }
                                        } else if (app.status == "UNDER_REVIEW") {
                                            Button(
                                                onClick = { viewModel.approveApplication(app.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("✔️ Onayla", color = Color.White, fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = { rejectTargetId = app.id },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("❌ Reddet", color = Color.White, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Kapat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    if (rejectTargetId != null) {
        AlertDialog(
            onDismissRequest = { rejectTargetId = null },
            title = { Text("Reddetme Gerekçesi") },
            text = {
                OutlinedTextField(
                    value = rejectionReason,
                    onValueChange = { rejectionReason = it },
                    label = { Text("Gerekçe yazınız...") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        rejectTargetId?.let {
                            viewModel.rejectApplication(it, rejectionReason)
                            rejectTargetId = null
                            rejectionReason = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Reddet", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectTargetId = null }) {
                    Text("İptal")
                }
            }
        )
    }
}
