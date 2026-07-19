package com.diet.android.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.diet.android.data.model.ChatMessage
import com.diet.android.data.model.ConversationSummary
import com.diet.android.data.model.UserInfo
import com.diet.android.ui.screens.explore.ExploreUiEvent
import com.diet.android.ui.screens.explore.ExploreViewModel
import com.diet.android.ui.theme.*
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietitianMessagesScreen(
    viewModel: ExploreViewModel,
    onNavigateToHome: (String?) -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToSlots: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val userInfo = viewModel.userInfo
    var showChatDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadInbox()
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ExploreUiEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
                is ExploreUiEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mesajlarım", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                    icon = { Icon(Icons.Default.Home, contentDescription = "Ana Sayfa") },
                    label = { Text("Ana Sayfa", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = { onNavigateToHome(null) }
                )

                // Danışanlar
                NavigationBarItem(
                    icon = { Icon(Icons.Default.People, contentDescription = "Danışanlar") },
                    label = { Text("Danışanlar", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = onNavigateToExplore
                )

                // Slot Ekle
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Slot Ekle") },
                    label = { Text("Slot Ekle", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = onNavigateToSlots
                )

                // Mesajlarım
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = "Mesajlarım") },
                    label = { Text("Mesajlarım", fontWeight = FontWeight.Medium) },
                    selected = true,
                    onClick = {}
                )

                // Klinik Analitiği
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Analytics, contentDescription = "Analizler") },
                    label = { Text("Analizler", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = onNavigateToAnalytics
                )

                // Profil
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil", fontWeight = FontWeight.Medium) },
                    selected = false,
                    onClick = onNavigateToProfile
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
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if (viewModel.inboxList.isEmpty() && !viewModel.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Henüz aktif bir mesajlaşma bulunmuyor.",
                            color = TextSecondaryDark,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    viewModel.inboxList.forEach { conversation ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    val partnerUserInfo = UserInfo(
                                        id = conversation.partnerId,
                                        email = conversation.partnerEmail ?: "",
                                        name = conversation.partnerName,
                                        role = "ROLE_USER",
                                        provider = null,
                                        providerId = null,
                                        height = null,
                                        currentWeight = null,
                                        targetWeight = null,
                                        category = conversation.partnerCategory,
                                        notes = null,
                                        glp1InjectionDay = null,
                                        glp1Dosage = null,
                                        lipedemaStage = null,
                                        antiInflammatoryCompliant = null,
                                        hormoneTargetCycle = null,
                                        dietitianApplicationStatus = null,
                                        dietitianRejectionReason = null,
                                        instagramUrl = null,
                                        linkedinUrl = null,
                                        youtubeUrl = null,
                                        profilePictureUrl = null,
                                        fcmToken = null
                                    )
                                    viewModel.startChat(partnerUserInfo, context)
                                    showChatDialog = true
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar Placeholder
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(GreenPrimary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (conversation.partnerName?.take(1) ?: "D"),
                                        color = GreenPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // Main Text Content
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = conversation.partnerName ?: "Danışan",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = TextDark
                                        )

                                        // Time tag
                                        if (conversation.lastMessageSentAt != null) {
                                            Text(
                                                text = conversation.lastMessageSentAt.substringAfter("T").take(5),
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = conversation.lastMessage ?: "Henüz mesaj gönderilmedi",
                                            fontSize = 13.sp,
                                            color = if (conversation.unreadCount > 0) Color.Black else TextSecondaryDark,
                                            fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (conversation.unreadCount > 0) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(Color(0xFF2E7D32), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = conversation.unreadCount.toString(),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    // Chat Dialog
    if (showChatDialog && viewModel.chatWithUser != null) {
        val partner = viewModel.chatWithUser!!
        var messageText by remember { mutableStateOf("") }

        Dialog(
            onDismissRequest = {
                viewModel.endChat()
                showChatDialog = false
                viewModel.loadInbox()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GreenPrimary)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(partner.name ?: "Sohbet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Çevrimiçi (WebSocket aktif)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        }
                        TextButton(
                            onClick = {
                                viewModel.endChat()
                                showChatDialog = false
                                viewModel.loadInbox()
                            }
                        ) {
                            Text("Kapat", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Messages Stream
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState(), reverseScrolling = true),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.chatMessages.forEach { msg ->
                            val isMe = msg.sender.id == userInfo?.id
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    shape = RoundedCornerShape(
                                        topStart = 12.dp,
                                        topEnd = 12.dp,
                                        bottomStart = if (isMe) 12.dp else 0.dp,
                                        bottomEnd = if (isMe) 0.dp else 12.dp
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isMe) GreenPrimary else Color.White
                                    ),
                                    modifier = Modifier.widthIn(max = 280.dp),
                                    border = if (isMe) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(msg.content, color = if (isMe) Color.White else TextDark, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Message Input field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Mesajınızı yazın...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    viewModel.sendChatMessage(messageText)
                                    messageText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text("Gönder", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
