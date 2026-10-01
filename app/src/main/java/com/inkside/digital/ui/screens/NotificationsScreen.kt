package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.NotificationEntity
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.components.BannerAdView
import java.util.Calendar

@Composable
fun NotificationsScreen(
    notifications: List<NotificationEntity>,
    onMarkAllRead: () -> Unit
) {
    val unreadCount = notifications.count { !it.isRead }

    // Group by tanggal
    val grouped = notifications.groupBy { notif ->
        groupLabel(notif.timestamp)
    }
    val orderedGroups = listOf("Hari Ini", "Kemarin", "Lebih Lama")
        .filter { grouped.containsKey(it) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // ============ HEADER ============
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Notifikasi",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        )
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = ElectricBlue,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (unreadCount > 0)
                            "$unreadCount belum dibaca"
                        else
                            "Semua sudah dibaca",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (unreadCount > 0) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable { onMarkAllRead() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Tandai",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // ============ Banner AdMob ============
        item {
            BannerAdView()
        }

        // ============ CONTENT ============
        if (notifications.isEmpty()) {
            item { EmptyNotifications() }
        } else {
            orderedGroups.forEach { groupName ->
                item {
                    Text(
                        text = groupName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }

                items(grouped[groupName] ?: emptyList()) { notif ->
                    NotificationCard(notification = notif)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun EmptyNotifications() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Text("🔔", fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                "Belum Ada Notifikasi",
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Update komisi, check-in, dan reward akan muncul di sini",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationEntity) {
    val (emoji, accentColor) = when (notification.type) {
        "COMMISSION" -> "💰" to EmeraldLight
        "WITHDRAWAL" -> "💸" to GoldVip
        "COUPON" -> "🎟️" to ElectricBlue
        "UPGRADE" -> "👑" to GoldVip
        "SECURITY" -> "🔒" to Color(0xFFEF4444)
        "REWARD" -> "🎁" to GoldVip
        "CHECKIN" -> "✅" to EmeraldLight
        "MISSION" -> "🎯" to ElectricBlue
        else -> "🔔" to ElectricBlue
    }

    val isUnread = !notification.isRead

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnread)
                accentColor.copy(alpha = 0.08f)
            else
                MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: navigate by actionDeepLink */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon + unread dot
            Box {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 20.sp)
                }

                if (isUnread) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    fontWeight = if (isUnread) FontWeight.Black else FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = notification.message,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = relativeTime(notification.timestamp),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ============ HELPER ============

private fun groupLabel(timestamp: Long): String {
    val now = Calendar.getInstance()
    val cal = Calendar.getInstance().apply { timeInMillis = timestamp }

    val nowDay = now.get(Calendar.DAY_OF_YEAR)
    val nowYear = now.get(Calendar.YEAR)
    val calDay = cal.get(Calendar.DAY_OF_YEAR)
    val calYear = cal.get(Calendar.YEAR)

    return when {
        nowYear == calYear && nowDay == calDay -> "Hari Ini"
        nowYear == calYear && nowDay - calDay == 1 -> "Kemarin"
        else -> "Lebih Lama"
    }
}

private fun relativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        days < 7 -> "$days hari lalu"
        days < 30 -> "${days / 7} minggu lalu"
        else -> "${days / 30} bulan lalu"
    }
}
