package com.diet.android.ui.screens.profile

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.diet.android.R
import com.diet.android.ui.screens.home.HomeViewModel
import com.diet.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: HomeViewModel,
    onNavigateToHome: (String?) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToSlots: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToProfileEdit: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val userInfo = viewModel.userInfo ?: return

    fun openUrl(url: String) {
        try {
            val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                "https://$url"
            } else {
                url
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Bağlantı açılamadı", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profilim",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Ana Sayfa
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Anasayfa") },
                    label = { Text("Anasayfa", fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1) },
                    selected = false,
                    onClick = { onNavigateToHome(null) }
                )

                if (userInfo.role == "ROLE_DIETITIAN") {
                    // Danışanlar
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.People, contentDescription = "Danışanlar") },
                        label = { Text("Danışanlar", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToExplore
                    )
                    // Çalışma Slotu Ekle
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Slot Ekle") },
                        label = { Text("Slot Ekle", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToSlots
                    )
                    // Mesajlarım
                    NavigationBarItem(
                        icon = {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(Icons.Default.Email, contentDescription = "Mesajlar")
                                if (viewModel.unreadMessagesCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .offset(x = 6.dp, y = (-4).dp)
                                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = viewModel.unreadMessagesCount.toString(),
                                            color = MaterialTheme.colorScheme.onError,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        },
                        label = { Text("Mesajlar", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToMessages
                    )
                    // Klinik Analitiği
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Analiz") },
                        label = { Text("Analiz", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = onNavigateToAnalytics
                    )
                } else if (userInfo.role == "ROLE_USER") {
                    // Geçmişim
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.History, contentDescription = "Geçmişim") },
                        label = { Text("Geçmişim", fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1) },
                        selected = false,
                        onClick = onNavigateToExplore
                    )
                    // Randevu Al
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AddCircle, contentDescription = "Randevu Al") },
                        label = { Text("Randevu Al", fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1) },
                        selected = false,
                        onClick = { onNavigateToHome("appointment") }
                    )
                    // Mesajlar
                    NavigationBarItem(
                        icon = {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(Icons.Default.Email, contentDescription = "Mesajlar")
                                if (viewModel.unreadMessagesCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .offset(x = 6.dp, y = (-4).dp)
                                            .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = viewModel.unreadMessagesCount.toString(),
                                            color = MaterialTheme.colorScheme.onError,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        },
                        label = { Text("Mesajlar", fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1) },
                        selected = false,
                        onClick = onNavigateToMessages
                    )
                }

                // Profil (Seçili)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontWeight = FontWeight.Medium) },
                    selected = true,
                    onClick = {}
                )
            }
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
                Spacer(modifier = Modifier.height(16.dp))

                // Profile Image
                val imageModel = if (!userInfo.profilePictureUrl.isNullOrBlank()) {
                    userInfo.profilePictureUrl
                } else {
                    "https://images.unsplash.com/photo-1594824813573-246434de83fb?q=80&w=256&auto=format&fit=crop"
                }

                AsyncImage(
                    model = imageModel,
                    contentDescription = "Profil Resmi",
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name Surname
                Text(
                    text = userInfo.name ?: "Ad Soyad Belirtilmemiş",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                if (userInfo.role == "ROLE_USER") {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Health Metrics Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Sağlık Ölçümleri",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = GreenPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Boy", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${userInfo.height ?: 0.0} cm", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Mevcut Kilo", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${userInfo.currentWeight ?: 0.0} kg", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Hedef Kilo", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("${userInfo.targetWeight ?: 0.0} kg", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("VKI (BMI)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    val w = userInfo.currentWeight ?: 0.0
                                    val h = userInfo.height ?: 0.0
                                    val bmi = if (w > 0 && h > 0) String.format("%.1f", w / ((h / 100) * (h / 100))) else "-"
                                    Text(bmi, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = GreenPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Program Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Klinik Program Detayları",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = GreenPrimary
                            )
                            
                            val categoryName = when (userInfo.category) {
                                "GLP_1" -> "GLP-1 Destekli Takip"
                                "LIPEDEMA" -> "Lipödem Diyeti"
                                "HORMONAL_BALANCE" -> "Hormonal Denge"
                                else -> "Kilo Yönetimi"
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Program Türü", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(categoryName, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }

                            if (userInfo.category == "GLP_1") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Enjeksiyon Günü", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(userInfo.glp1InjectionDay ?: "-", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Enjeksiyon Dozu", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(userInfo.glp1Dosage ?: "-", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            } else if (userInfo.category == "LIPEDEMA") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Lipödem Evresi", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Evre ${userInfo.lipedemaStage ?: 1}", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Anti-inflamatuar Diyet", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(if (userInfo.antiInflammatoryCompliant == true) "Uyumlu" else "Uyumsuz", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            } else if (userInfo.category == "HORMONAL_BALANCE") {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Hedef Döngü Fazı", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(userInfo.hormoneTargetCycle ?: "-", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("E-posta Adresi", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(userInfo.email, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                } else {
                    // Social Media Row
                    val socialMedia = listOf(
                        Triple(userInfo.linkedinUrl, R.drawable.ic_linkedin, "LinkedIn"),
                        Triple(userInfo.instagramUrl, R.drawable.ic_instagram, "Instagram"),
                        Triple(userInfo.youtubeUrl, R.drawable.ic_youtube, "YouTube"),
                        Triple(userInfo.xUrl, R.drawable.ic_x, "X"),
                        Triple(userInfo.facebookUrl, R.drawable.ic_facebook, "Facebook")
                    )

                    // Show only configured URLs
                    val activeSocials = socialMedia.filter { !it.first.isNullOrBlank() }

                    if (activeSocials.isNotEmpty()) {
                        val greenGradient = androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(Color(0xFF34D399), Color(0xFF10B981))
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            activeSocials.forEach { (url, iconRes, desc) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.width(60.dp)
                                ) {
                                    IconButton(
                                        onClick = { openUrl(url!!) },
                                        modifier = Modifier
                                            .size(50.dp)
                                            .shadow(4.dp, RoundedCornerShape(16.dp))
                                            .background(greenGradient, shape = RoundedCornerShape(16.dp))
                                    ) {
                                        Image(
                                            painter = painterResource(id = iconRes),
                                            contentDescription = desc,
                                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = desc,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Hakkımda / Özgeçmiş",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = GreenPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (!userInfo.notes.isNullOrBlank()) userInfo.notes else "Henüz bir açıklama eklenmemiş.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Edit Button
                Button(
                    onClick = onNavigateToProfileEdit,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Profili Düzenle",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Profili Düzenle",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onLogout,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Çıkış Yap",
                        tint = Color(0xFFEF4444)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Çıkış Yap",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
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
