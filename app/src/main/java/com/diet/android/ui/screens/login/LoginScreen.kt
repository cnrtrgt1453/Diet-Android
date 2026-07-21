package com.diet.android.ui.screens.login

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.res.painterResource
import com.diet.android.R
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.diet.android.data.model.DietitianApplicationDto
import com.diet.android.ui.theme.*
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

enum class LoginRole { NONE, DIETITIAN, CLIENT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (role: String, isProfileComplete: Boolean, applicationStatus: String?) -> Unit,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var role by remember { mutableStateOf(LoginRole.NONE) }
    var showAlert by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertMessage by remember { mutableStateOf("") }
    var isSuccessAlert by remember { mutableStateOf(false) }

    var isApplyModalOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is LoginUiEvent.Success -> {
                    onLoginSuccess(event.role, event.isProfileComplete, event.applicationStatus)
                }
                is LoginUiEvent.Error -> {
                    alertTitle = "Hata"
                    alertMessage = event.message
                    isSuccessAlert = false
                    showAlert = true
                }
                is LoginUiEvent.ShowAlert -> {
                    alertTitle = event.title
                    alertMessage = event.message
                    isSuccessAlert = event.isSuccess
                    showAlert = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = role == LoginRole.NONE,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                RoleSelectionView(
                    onSelectDietitian = { role = LoginRole.DIETITIAN },
                    onSelectClient = { role = LoginRole.CLIENT }
                )
            }

            AnimatedVisibility(
                visible = role == LoginRole.DIETITIAN,
                enter = fadeIn() + slideInHorizontally { it },
                exit = fadeOut() + slideOutHorizontally { it }
            ) {
                DietitianLoginView(
                    viewModel = viewModel,
                    context = context,
                    onBack = { role = LoginRole.NONE },
                    onOpenApply = { isApplyModalOpen = true }
                )
            }

            AnimatedVisibility(
                visible = role == LoginRole.CLIENT,
                enter = fadeIn() + slideInHorizontally { -it },
                exit = fadeOut() + slideOutHorizontally { -it }
            ) {
                ClientLoginView(
                    viewModel = viewModel,
                    context = context,
                    onBack = { role = LoginRole.NONE },
                    onGoogleClick = {
                        val webClientId = context.getString(com.diet.android.R.string.google_web_client_id)
                        if (webClientId == "YOUR_GOOGLE_WEB_CLIENT_ID" || webClientId.isBlank()) {
                            Toast.makeText(
                                context,
                                "Geliştirici Uyarısı: Web Client ID yapılandırılmadı. Geçici hesapla giriş yapılıyor.",
                                Toast.LENGTH_LONG
                            ).show()
                            viewModel.loginWithSocial(context, "google", "mock-token-google")
                            return@ClientLoginView
                        }

                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(webClientId)
                            .setAutoSelectEnabled(false)
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        coroutineScope.launch {
                            try {
                                val result = credentialManager.getCredential(
                                    context = context,
                                    request = request
                                )
                                val credential = result.credential
                                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    viewModel.loginWithSocial(context, "google", googleIdTokenCredential.idToken)
                                } else {
                                    Toast.makeText(context, "Bilinmeyen kimlik doğrulama tipi.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Google Girişi İptal Edildi veya Başarısız: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )
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

    // Alert Dialog
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text(alertTitle, fontWeight = FontWeight.Bold) },
            text = { Text(alertMessage) },
            confirmButton = {
                Button(
                    onClick = { showAlert = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSuccessAlert) GreenPrimary else MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Tamam", color = Color.White)
                }
            }
        )
    }

    // Apply Modal Dialog
    if (isApplyModalOpen) {
        DietitianApplyDialog(
            onDismiss = { isApplyModalOpen = false },
            onSubmit = { dto ->
                isApplyModalOpen = false
                viewModel.applyDietitian(dto)
            }
        )
    }
}

@Composable
fun LogoSection(small: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(bottom = if (small) 16.dp else 40.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(if (small) 60.dp else 90.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "DietApp",
            fontSize = if (small) 24.sp else 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        if (!small) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Diyetisyen ve Danışan Yönetim Platformu",
                fontSize = 14.sp,
                color = TextSecondaryDark,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun RoleSelectionView(
    onSelectDietitian: () -> Unit,
    onSelectClient: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LogoSection(small = false)
        
        Text(
            text = "Lütfen giriş yapmak için rolünüzü seçin:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectDietitian() }
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👩‍⚕️", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Diyetisyen Girişi / Başvurusu",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 16.sp
                    )
                    Text(
                        "E-posta ve şifre ile giriş yapın veya sisteme katılmak için başvurun.",
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectClient() }
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("👤", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        "Danışan Girişi",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                    Text(
                        "Google veya Facebook hesabınızla hızlıca giriş yapın.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietitianLoginView(
    viewModel: LoginViewModel,
    context: Context,
    onBack: () -> Unit,
    onOpenApply: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onBack, modifier = Modifier.padding(bottom = 8.dp)) {
            Text("◀ Geri Dön", color = TextSecondaryDark)
        }

        LogoSection(small = true)

        Text(
            text = "👩‍⚕️ Diyetisyen Portalı",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta Adresi") },
            placeholder = { Text("diyetisyen@example.com") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GreenPrimary,
                focusedLabelColor = GreenPrimary
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            placeholder = { Text("••••••••") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = GreenPrimary,
                focusedLabelColor = GreenPrimary
            )
        )

        TextButton(
            onClick = { viewModel.forgotPassword(email) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Şifremi Unuttum", color = GreenPrimary, fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = { viewModel.loginWithPassword(context, email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Giriş Yap", fontWeight = FontWeight.Bold, color = Color.White)
        }

        OutlinedButton(
            onClick = onOpenApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenPrimary)
        ) {
            Text("Sisteme Başvuru Yap", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ClientLoginView(
    viewModel: LoginViewModel,
    context: Context,
    onBack: () -> Unit,
    onGoogleClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onBack, modifier = Modifier.padding(bottom = 8.dp)) {
            Text("◀ Geri Dön", color = TextSecondaryDark)
        }

        LogoSection(small = true)

        Text(
            text = "👤 Danışan Portalı",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Giriş yapmak veya yeni profil oluşturmak için sosyal hesap kullanın.",
            fontSize = 12.sp,
            color = TextSecondaryDark,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable {
                    onGoogleClick()
                }
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("G", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Google ile Giriş Yap", fontWeight = FontWeight.Bold, color = TextDark)
            }
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1877F2)),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable {
                    viewModel.loginWithSocial(context, "facebook", "mock-token-facebook")
                }
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("f", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Facebook ile Giriş Yap", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietitianApplyDialog(
    onDismiss: () -> Unit,
    onSubmit: (DietitianApplicationDto) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var diploma by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var docUrl by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
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
                    "Diyetisyen Başvuru Formu 🥗",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    "Sisteme diyetisyen olarak katılmak için lütfen aşağıdaki bilgileri eksiksiz doldurun.",
                    fontSize = 12.sp,
                    color = TextSecondaryDark,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Ad Soyad *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("E-posta Adresi *") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    label = { Text("Şifre *") }, singleLine = true,
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth()
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
                    TextButton(onClick = onDismiss) {
                        Text("İptal", color = TextSecondaryDark)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            val expYears = experience.toIntOrNull() ?: 0
                            onSubmit(
                                DietitianApplicationDto(
                                    fullName = name,
                                    email = email,
                                    university = university,
                                    diplomaNumber = diploma,
                                    experienceYears = expYears,
                                    documentUrl = if (docUrl.isBlank()) null else docUrl,
                                    note = if (note.isBlank()) null else note,
                                    password = password
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Başvuruyu Gönder", color = Color.White)
                    }
                }
            }
        }
    }
}
