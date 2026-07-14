package com.diet.android.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialog(
    visible: Boolean,
    onClose: () -> Unit,
    viewModel: HomeViewModel
) {
    if (!visible) return

    val notifications = viewModel.notifications
    val unreadCount = viewModel.unreadCount

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔔 Bildirimler",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextDark
                        )
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(containerColor = GreenPrimary) {
                                Text(
                                    text = unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (unreadCount > 0) {
                            TextButton(onClick = { viewModel.markAllNotificationsAsRead() }) {
                                Text("Hepsini Oku", color = GreenPrimary, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        TextButton(onClick = onClose) {
                            Text("Kapat", color = GreenPrimary, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (notifications.isEmpty()) {
                    Text(
                        text = "Henüz bildiriminiz bulunmuyor.",
                        fontSize = 13.sp,
                        color = TextSecondaryDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    )
                } else {
                    notifications.forEach { notif ->
                        val cardBg = if (notif.isRead) Color(0xFFF5F7F6) else Color(0xFFE8F5E9)
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    if (!notif.isRead) {
                                        viewModel.markNotificationAsRead(notif.id)
                                    }
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = (if (notif.isRead) "✉️ " else "📩 ") + notif.title,
                                        fontWeight = if (notif.isRead) FontWeight.Medium else FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextDark,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (!notif.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(GreenPrimary, RoundedCornerShape(4.dp))
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.message,
                                    fontSize = 12.sp,
                                    color = TextSecondaryDark
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = notif.createdAt.replace("T", " ").substringBefore("."),
                                    fontSize = 9.sp,
                                    color = TextSecondaryDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
