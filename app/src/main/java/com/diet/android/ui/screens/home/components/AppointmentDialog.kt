package com.diet.android.ui.screens.home.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.diet.android.ui.screens.home.HomeViewModel
import com.diet.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDialog(
    visible: Boolean,
    onClose: () -> Unit,
    viewModel: HomeViewModel
) {
    if (!visible) return

    val isDietitian = viewModel.userInfo?.role == "ROLE_DIETITIAN"

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
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
                        text = if (isDietitian) "📅 Çalışma Saati Slotu Ekle" else "📅 Randevu Al",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                    TextButton(onClick = onClose) {
                        Text("Kapat", color = GreenPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isDietitian) {
                    var slotDate by remember { mutableStateOf("") }
                    var slotStartTime by remember { mutableStateOf("") }
                    var slotEndTime by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = slotDate,
                        onValueChange = { slotDate = it },
                        label = { Text("Tarih (YYYY-MM-DD)") },
                        placeholder = { Text("örn: 2026-07-15") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = slotStartTime,
                        onValueChange = { slotStartTime = it },
                        label = { Text("Başlangıç Saati (SS:DD)") },
                        placeholder = { Text("örn: 09:00") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = slotEndTime,
                        onValueChange = { slotEndTime = it },
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
                                onClose()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("➕ Çalışma Slotu Oluştur", color = Color.White)
                    }
                } else {
                    var appDate by remember { mutableStateOf("") }
                    var appNote by remember { mutableStateOf("") }
                    var selectedSlotId by remember { mutableStateOf<Long?>(null) }
                    val dietitianId = viewModel.userInfo?.dietitian?.id

                    OutlinedTextField(
                        value = appDate,
                        onValueChange = {
                            appDate = it
                            if (it.length == 10 && dietitianId != null) {
                                viewModel.fetchAvailableSlots(dietitianId, it)
                            }
                        },
                        label = { Text("Tarih Seçin (YYYY-MM-DD)") },
                        placeholder = { Text("örn: 2026-07-15") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Mevcut Boş Saatler",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val slots = viewModel.availableSlots
                    if (slots.isEmpty()) {
                        Text(
                            text = "Seçtiğiniz tarihte boş çalışma saati bulunmamaktadır.",
                            color = Color(0xFFFF9800),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            slots.forEach { slot ->
                                val isSelected = selectedSlotId == slot.id
                                Card(
                                    border = BorderStroke(1.dp, if (isSelected) GreenPrimary else Color.LightGray),
                                    colors = CardDefaults.cardColors(containerColor = if (isSelected) GreenPrimary else Color(0xFFF5F7F6)),
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

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = appNote,
                        onValueChange = { appNote = it },
                        label = { Text("Randevu Notu (Şikayetiniz / İsteğiniz)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            selectedSlotId?.let {
                                viewModel.bookAppointmentSlot(it, appNote)
                                onClose()
                            }
                        },
                        enabled = selectedSlotId != null,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("📅 Randevuyu Rezerve Et", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
        val layoutWidth = constraints.maxWidth
        val lines = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentLine = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var currentLineWidth = 0

        placeables.forEach { placeable ->
            if (currentLineWidth + placeable.width + mainAxisSpacing.roundToPx() > layoutWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = mutableListOf()
                currentLineWidth = 0
            }
            currentLine.add(placeable)
            currentLineWidth += placeable.width + mainAxisSpacing.roundToPx()
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }

        var height = 0
        lines.forEachIndexed { index, line ->
            val maxLineHeight = line.maxOf { it.height }
            height += maxLineHeight
            if (index < lines.size - 1) {
                height += crossAxisSpacing.roundToPx()
            }
        }

        layout(layoutWidth, maxOf(constraints.minHeight, height)) {
            var y = 0
            lines.forEach { line ->
                var x = 0
                val maxLineHeight = line.maxOf { it.height }
                line.forEach { placeable ->
                    placeable.placeRelative(x, y + (maxLineHeight - placeable.height) / 2)
                    x += placeable.width + mainAxisSpacing.roundToPx()
                }
                y += maxLineHeight + crossAxisSpacing.roundToPx()
            }
        }
    }
}
