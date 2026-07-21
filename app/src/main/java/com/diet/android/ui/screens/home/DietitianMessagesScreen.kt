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
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

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
        viewModel.loadInitialData(context)
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
            val isClient = userInfo?.role == "ROLE_USER"
            val dietitian = userInfo?.dietitian
            val titleText = if (isClient && dietitian != null) {
                dietitian.name ?: "Diyetisyenim"
            } else {
                "Mesajlar"
            }
            TopAppBar(
                title = { Text(titleText, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                if (userInfo?.role == "ROLE_DIETITIAN") {
                    // Ana Sayfa
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Anasayfa") },
                        label = { Text("Anasayfa", fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToHome(null)
                        }
                    )

                    // Danışan
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.People, contentDescription = "Danışan") },
                        label = { Text("Danışan", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToExplore()
                        }
                    )

                    // Slot Ekle
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.DateRange, contentDescription = "Slot Ekle") },
                        label = { Text("Slot Ekle", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToSlots()
                        }
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
                        selected = true,
                        onClick = {}
                    )

                    // Klinik Analitiği
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Analytics, contentDescription = "Analiz") },
                        label = { Text("Analiz", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToAnalytics()
                        }
                    )

                    // Profil
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                        label = { Text("Profil", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToProfile()
                        }
                    )
                } else if (userInfo?.role == "ROLE_USER") {
                    // Ana Sayfa
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Anasayfa") },
                        label = { Text("Anasayfa", fontWeight = FontWeight.Medium, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 1) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToHome(null)
                        }
                    )

                    // Geçmişim
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.History, contentDescription = "Geçmişim") },
                        label = { Text("Geçmişim", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToExplore()
                        }
                    )

                    // Randevu Al
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AddCircle, contentDescription = "Randevu Al") },
                        label = { Text("Randevu Al", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToHome("appointment")
                        }
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
                        selected = true,
                        onClick = {}
                    )

                    // Profil
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                        label = { Text("Profil", fontWeight = FontWeight.Medium) },
                        selected = false,
                        onClick = {
                            viewModel.endChat()
                            onNavigateToHome("profile")
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        val isClient = userInfo?.role == "ROLE_USER"
        val dietitian = userInfo?.dietitian

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

                if (isClient && dietitian == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Henüz bir diyetisyeniniz bulunmuyor.",
                            color = TextSecondaryDark,
                            fontSize = 14.sp
                        )
                    }
                } else if (viewModel.visibleInboxList.isEmpty() && !viewModel.isLoading) {
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
                    viewModel.visibleInboxList.forEach { conversation ->
                        var menuExpanded by remember { mutableStateOf(false) }

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
                                        role = if (isClient) "ROLE_DIETITIAN" else "ROLE_USER",
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
                                        profilePictureUrl = conversation.profilePictureUrl,
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
                                // Avatar Placeholder or Profile Picture
                                if (!conversation.profilePictureUrl.isNullOrBlank()) {
                                    Card(
                                        shape = CircleShape,
                                        modifier = Modifier.size(48.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                    ) {
                                        AsyncImage(
                                            model = conversation.profilePictureUrl,
                                            contentDescription = "Profil Resmi",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                } else {
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
                                            text = conversation.partnerName ?: "Diyetisyen",
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

                                Spacer(modifier = Modifier.width(4.dp))

                                // Hamburger / Options Menu
                                Box {
                                    IconButton(
                                        onClick = { menuExpanded = true }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Sohbet Menüsü",
                                            tint = Color.Gray
                                        )
                                    }

                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Okundu Olarak İşaretle", fontSize = 13.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = GreenPrimary)
                                            },
                                            onClick = {
                                                menuExpanded = false
                                                viewModel.markConversationAsRead(conversation.partnerId)
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Okunmadı Olarak İşaretle", fontSize = 13.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Default.Email, contentDescription = null, tint = TextDark)
                                            },
                                            onClick = {
                                                menuExpanded = false
                                                viewModel.markConversationAsUnread(conversation.partnerId)
                                            }
                                        )
                                        HorizontalDivider()
                                        DropdownMenuItem(
                                            text = { Text("Sohbeti Sil", color = Color(0xFFEF4444), fontSize = 13.sp) },
                                            leadingIcon = {
                                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                                            },
                                            onClick = {
                                                menuExpanded = false
                                                viewModel.hideConversation(conversation.partnerId)
                                            }
                                        )
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!partner.profilePictureUrl.isNullOrBlank()) {
                                Card(
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    AsyncImage(
                                        model = partner.profilePictureUrl,
                                        contentDescription = "Profil Resmi",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (partner.name?.take(1) ?: "D"),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(partner.name ?: "Sohbet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Çevrimiçi (WebSocket aktif)", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
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

