package com.diet.android.ui.screens.home.components

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
fun ProfileEditDialog(
    visible: Boolean,
    onClose: () -> Unit,
    viewModel: HomeViewModel
) {
    if (!visible) return

    val user = viewModel.userInfo ?: return
    val isDietitian = user.role == "ROLE_DIETITIAN"

    var name by remember { mutableStateOf(user.name ?: "") }
    var notes by remember { mutableStateOf(user.notes ?: "") }
    var instagramUrl by remember { mutableStateOf(user.instagramUrl ?: "") }
    var linkedinUrl by remember { mutableStateOf(user.linkedinUrl ?: "") }
    var youtubeUrl by remember { mutableStateOf(user.youtubeUrl ?: "") }
    var profilePictureUrl by remember { mutableStateOf(user.profilePictureUrl ?: "") }

    // Client fields
    var height by remember { mutableStateOf(user.height?.toString() ?: "") }
    var currentWeight by remember { mutableStateOf(user.currentWeight?.toString() ?: "") }
    var targetWeight by remember { mutableStateOf(user.targetWeight?.toString() ?: "") }
    var category by remember { mutableStateOf(user.category ?: "WEIGHT_MANAGEMENT") }
    var glp1InjectionDay by remember { mutableStateOf(user.glp1InjectionDay ?: "") }
    var glp1Dosage by remember { mutableStateOf(user.glp1Dosage ?: "") }

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
                        text = "✏️ Profil Bilgilerini Düzenle",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                    TextButton(onClick = onClose) {
                        Text("İptal", color = GreenPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Ad Soyad *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isDietitian) {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Klinik / Özgeçmiş Notu") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = instagramUrl,
                        onValueChange = { instagramUrl = it },
                        label = { Text("Instagram Profil Linki") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = linkedinUrl,
                        onValueChange = { linkedinUrl = it },
                        label = { Text("Linkedin Profil Linki") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = youtubeUrl,
                        onValueChange = { youtubeUrl = it },
                        label = { Text("Youtube Kanal Linki") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = height,
                            onValueChange = { height = it },
                            label = { Text("Boy (cm)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = currentWeight,
                            onValueChange = { currentWeight = it },
                            label = { Text("Mevcut Kilo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = targetWeight,
                            onValueChange = { targetWeight = it },
                            label = { Text("Hedef Kilo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Klinik Program Türü", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(6.dp))

                    val categories = listOf("WEIGHT_MANAGEMENT", "GLP_1", "LIPEDEMA", "HORMONAL_BALANCE")
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { category = cat }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(selected = isSelected, onClick = { category = cat })
                            Text(
                                text = when (cat) {
                                    "GLP_1" -> "GLP-1 Takip"
                                    "LIPEDEMA" -> "Lipödem Diyeti"
                                    "HORMONAL_BALANCE" -> "Hormonal Denge"
                                    else -> "Kilo Yönetimi"
                                },
                                color = TextDark,
                                fontSize = 13.sp
                            )
                        }
                    }

                    if (category == "GLP_1") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = glp1InjectionDay,
                                onValueChange = { glp1InjectionDay = it },
                                label = { Text("Enjeksiyon Günü") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = glp1Dosage,
                                onValueChange = { glp1Dosage = it },
                                label = { Text("Dozaj (mg)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = profilePictureUrl,
                    onValueChange = { profilePictureUrl = it },
                    label = { Text("Profil Fotoğrafı URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isNotEmpty()) {
                            viewModel.updateProfileDetails(
                                name = name,
                                notes = notes.ifEmpty { null },
                                instagramUrl = instagramUrl.ifEmpty { null },
                                linkedinUrl = linkedinUrl.ifEmpty { null },
                                youtubeUrl = youtubeUrl.ifEmpty { null },
                                profilePictureUrl = profilePictureUrl.ifEmpty { null },
                                height = height.toDoubleOrNull(),
                                currentWeight = currentWeight.toDoubleOrNull(),
                                targetWeight = targetWeight.toDoubleOrNull(),
                                category = category,
                                glp1InjectionDay = glp1InjectionDay.ifEmpty { null },
                                glp1Dosage = glp1Dosage.ifEmpty { null }
                            )
                            onClose()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Kaydet", color = Color.White)
                }
            }
        }
    }
}
