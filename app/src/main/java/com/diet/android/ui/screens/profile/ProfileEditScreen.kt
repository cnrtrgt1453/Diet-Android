package com.diet.android.ui.screens.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.diet.android.ui.screens.home.HomeViewModel
import com.diet.android.ui.theme.GreenPrimary
import com.diet.android.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val user = viewModel.userInfo ?: return

    val isDietitian = user.role == "ROLE_DIETITIAN"

    var name by remember { mutableStateOf(user.name ?: "") }
    var notes by remember { mutableStateOf(user.notes ?: "") }
    var instagramUrl by remember { mutableStateOf(user.instagramUrl ?: "") }
    var linkedinUrl by remember { mutableStateOf(user.linkedinUrl ?: "") }
    var youtubeUrl by remember { mutableStateOf(user.youtubeUrl ?: "") }
    var xUrl by remember { mutableStateOf(user.xUrl ?: "") }
    var facebookUrl by remember { mutableStateOf(user.facebookUrl ?: "") }

    // Client fields
    var height by remember { mutableStateOf(user.height?.toString() ?: "") }
    var currentWeight by remember { mutableStateOf(user.currentWeight?.toString() ?: "") }
    var targetWeight by remember { mutableStateOf(user.targetWeight?.toString() ?: "") }
    var category by remember { mutableStateOf(user.category ?: "WEIGHT_MANAGEMENT") }
    var glp1InjectionDay by remember { mutableStateOf(user.glp1InjectionDay ?: "") }
    var glp1Dosage by remember { mutableStateOf(user.glp1Dosage ?: "") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadProfilePicture(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profili Düzenle",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Photo Section
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier
                        .size(110.dp)
                        .clickable { photoPickerLauncher.launch("image/*") }
                ) {
                    val imageModel = if (!user.profilePictureUrl.isNullOrBlank()) {
                        user.profilePictureUrl
                    } else {
                        "https://images.unsplash.com/photo-1594824813573-246434de83fb?q=80&w=256&auto=format&fit=crop"
                    }

                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Profil Fotoğrafı",
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(2.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .shadow(1.dp, CircleShape)
                            .background(GreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Fotoğraf Yükle",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fotoğrafı Değiştirmek İçin Dokunun",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Card wrapping fields
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = if (isDietitian) "Kişisel Bilgiler" else "Profil Bilgileri",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = GreenPrimary
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Ad Soyad *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        if (isDietitian) {
                            OutlinedTextField(
                                value = notes,
                                onValueChange = { notes = it },
                                label = { Text("Hakkımda / Özgeçmiş Açıklaması") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 5,
                                minLines = 3
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Sosyal Medya Linkleri",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = GreenPrimary
                            )

                            OutlinedTextField(
                                value = linkedinUrl,
                                onValueChange = { linkedinUrl = it },
                                label = { Text("LinkedIn Profil Linki") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = instagramUrl,
                                onValueChange = { instagramUrl = it },
                                label = { Text("Instagram Profil Linki") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = youtubeUrl,
                                onValueChange = { youtubeUrl = it },
                                label = { Text("YouTube Kanal Linki") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = xUrl,
                                onValueChange = { xUrl = it },
                                label = { Text("X (Twitter) Profil Linki") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = facebookUrl,
                                onValueChange = { facebookUrl = it },
                                label = { Text("Facebook Profil Linki") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
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
                                    label = { Text("Kilo (kg)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = targetWeight,
                                    onValueChange = { targetWeight = it },
                                    label = { Text("Hedef (kg)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Klinik Program Türü",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = GreenPrimary
                            )

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
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            if (category == "GLP_1") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
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
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Save Button
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.updateProfileDetails(
                                name = name,
                                notes = if (isDietitian) notes.ifEmpty { null } else null,
                                instagramUrl = if (isDietitian) instagramUrl.ifEmpty { null } else null,
                                linkedinUrl = if (isDietitian) linkedinUrl.ifEmpty { null } else null,
                                youtubeUrl = if (isDietitian) youtubeUrl.ifEmpty { null } else null,
                                xUrl = if (isDietitian) xUrl.ifEmpty { null } else null,
                                facebookUrl = if (isDietitian) facebookUrl.ifEmpty { null } else null,
                                profilePictureUrl = user.profilePictureUrl, // keep existing URL, as photo was already uploaded & set separately
                                height = if (isDietitian) user.height else height.toDoubleOrNull(),
                                currentWeight = if (isDietitian) user.currentWeight else currentWeight.toDoubleOrNull(),
                                targetWeight = if (isDietitian) user.targetWeight else targetWeight.toDoubleOrNull(),
                                category = if (isDietitian) user.category else category,
                                glp1InjectionDay = if (isDietitian) user.glp1InjectionDay else if (category == "GLP_1") glp1InjectionDay.ifEmpty { null } else null,
                                glp1Dosage = if (isDietitian) user.glp1Dosage else if (category == "GLP_1") glp1Dosage.ifEmpty { null } else null
                            )
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Lütfen isim soyisim alanını doldurun.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Değişiklikleri Kaydet",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
