package com.diet.android.ui.screens.status

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
import androidx.compose.ui.window.Dialog
import com.diet.android.data.api.ApiClient
import com.diet.android.data.model.DietitianApplicationDto
import com.diet.android.data.model.UserInfo
import com.diet.android.data.repository.AuthRepository
import com.diet.android.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationStatusScreen(
    onLogout: () -> Unit,
    onStatusApproved: () -> Unit,
    authRepository: AuthRepository
) {
    val scope = rememberCoroutineScope()
    var userInfo by remember { mutableStateOf<UserInfo?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isApplyModalOpen by remember { mutableStateOf(false) }
    var showAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var isSuccessAlert by remember { mutableStateOf(false) }

    val fetchUserInfo = {
        isLoading = true
        scope.launch {
            authRepository.getCurrentUser()
                .onSuccess { info ->
                    userInfo = info
                    if (info.dietitianApplicationStatus == "APPROVED" || info.role == "ROLE_DIETITIAN") {
                        onStatusApproved()
                    }
                }
                .onFailure {
                    alertTitle = "Hata"
                    alertMessage = "Kullanıcı bilgileri güncellenemedi."
                    isSuccessAlert = false
                    showAlert = true
                }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchUserInfo()
    }

    val status = userInfo?.dietitianApplicationStatus ?: "PENDING"
    val reason = userInfo?.dietitianRejectionReason

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.verticalScroll(rememberScrollState())
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
                "DietApp",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = GreenPrimary
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (status == "PENDING") {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFF8E1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⏳", fontSize = 32.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Başvurunuz İncelemede",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFB300),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Diyetisyenlik başvurunuz başarıyla alındı. Yönetici diyetisyenimiz diploma numaranızı ve mezuniyet bilgilerinizi inceledikten sonra hesabınız aktif edilecektir.",
                            fontSize = 13.sp,
                            color = TextDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "İşlem tamamlandığında bir sonraki girişinizde paneliniz otomatik olarak açılacaktır. Lütfen aralıklarla kontrol ediniz.",
                                fontSize = 11.sp,
                                color = TextSecondaryDark,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("❌", fontSize = 32.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Başvurunuz Reddedildi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Girdiğiniz bilgiler doğrulanamadığı için diyetisyenlik başvurunuz reddedilmiştir.",
                            fontSize = 13.sp,
                            color = TextDark,
                            textAlign = TextAlign.Center
                        )
                        if (!reason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Reddedilme Nedeni:", fontWeight = FontWeight.Bold, color = Color(0xFFC62828), fontSize = 12.sp)
                                    Text(reason, color = Color(0xFFD32F2F), fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { isApplyModalOpen = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Bilgileri Güncelle ve Yeniden Başvur", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { fetchUserInfo() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("🔄 Durumu Güncelle", color = TextDark)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                        Text("Çıkış Yap", color = Color.Red, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (isLoading) {
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
            title = { Text(alertTitle, fontWeight = FontWeight.Bold) },
            text = { Text(alertMessage) },
            confirmButton = {
                Button(
                    onClick = { showAlert = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSuccessAlert) GreenPrimary else Color.Red
                    )
                ) {
                    Text("Tamam", color = Color.White)
                }
            }
        )
    }

    if (isApplyModalOpen && userInfo != null) {
        var name by remember { mutableStateOf(userInfo?.name ?: "") }
        var email by remember { mutableStateOf(userInfo?.email ?: "") }
        var university by remember { mutableStateOf("") }
        var diploma by remember { mutableStateOf("") }
        var experience by remember { mutableStateOf("") }
        var docUrl by remember { mutableStateOf("") }
        var note by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { isApplyModalOpen = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Bilgileri Güncelle 🥗",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "Başvurunuzu düzeltmek için lütfen aşağıdaki alanları güncelleyip tekrar gönderin.",
                        fontSize = 12.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Ad Soyad *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email, onValueChange = { },
                        label = { Text("E-posta *") }, singleLine = true, enabled = false, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = university, onValueChange = { university = it },
                        label = { Text("Mezun Olunan Üniversite *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = diploma, onValueChange = { diploma = it },
                        label = { Text("Diploma Numarası *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = experience, onValueChange = { experience = it },
                        label = { Text("Deneyim Yılı *") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = docUrl, onValueChange = { docUrl = it },
                        label = { Text("Özgeçmiş / Belge Linki") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = note, onValueChange = { note = it },
                        label = { Text("Başvuru Notu / Motivasyon") }, modifier = Modifier.fillMaxWidth().height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { isApplyModalOpen = false }) {
                            Text("İptal", color = TextSecondaryDark)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (name.isBlank() || university.isBlank() || diploma.isBlank() || experience.isBlank()) {
                                    alertTitle = "Hata"
                                    alertMessage = "Lütfen tüm zorunlu alanları (*) doldurunuz."
                                    isSuccessAlert = false
                                    showAlert = true
                                    return@Button
                                }
                                val expYears = experience.toIntOrNull() ?: 0
                                isApplyModalOpen = false
                                isLoading = true
                                scope.launch {
                                    authRepository.applyDietitian(
                                        DietitianApplicationDto(
                                            fullName = name,
                                            email = email,
                                            university = university,
                                            diplomaNumber = diploma,
                                            experienceYears = expYears,
                                            documentUrl = if (docUrl.isBlank()) null else docUrl,
                                            note = if (note.isBlank()) null else note,
                                            password = ""
                                        )
                                    ).onSuccess {
                                        alertTitle = "Başarılı"
                                        alertMessage = "Diyetisyen başvurunuz yeniden alındı. Değerlendirme süreci yeniden başlamıştır."
                                        isSuccessAlert = true
                                        showAlert = true
                                        fetchUserInfo()
                                    }.onFailure { err ->
                                        alertTitle = "Başvuru Hatası"
                                        alertMessage = err.localizedMessage ?: "Başvuru yapılırken bir hata oluştu."
                                        isSuccessAlert = false
                                        showAlert = true
                                    }
                                    isLoading = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            Text("Yeniden Başvur", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
