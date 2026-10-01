package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.ui.components.BannerAdView
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight

data class InboxMessage(
    val id: String,
    val senderName: String,
    val senderRole: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean,
    val isVerified: Boolean = true
)

@Composable
fun InboxScreen(
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val messages: List<InboxMessage> = remember { emptyList() }
    var filter by remember { mutableStateOf("ALL") }

    val filtered = when (filter) {
        "UNREAD" -> messages.filter { !it.isRead }
        else -> messages
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // ============ HEADER ============
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Kotak Masuk",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                        )
                        val unread = messages.count { !it.isRead }
                        if (unread > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = ElectricBlue, shape = CircleShape) {
                                Text(
                                    "$unread",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "Pesan dari tim Royaltree & admin",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ============ FILTER ============
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "Semua",
                    selected = filter == "ALL",
                    onClick = { filter = "ALL" }
                )
                FilterChip(
                    label = "Belum Dibaca",
                    selected = filter == "UNREAD",
                    onClick = { filter = "UNREAD" }
                )
            }
        }

        // ============ Banner AdMob ============
        item {
            BannerAdView()
        }

        // ============ CONTENT ============
        if (filtered.isEmpty()) {
            item { EmptyInbox(filter = filter) }
        } else {
            items(filtered.size) { index ->
                InboxItem(filtered[index])
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) ElectricBlue.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
            color = if (selected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun EmptyInbox(filter: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("📬", fontSize = 40.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (filter == "UNREAD") "Tidak Ada Pesan Baru" else "Belum Ada Pesan",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (filter == "UNREAD")
                    "Semua pesan sudah dibaca. Bagus!"
                else
                    "Pesan dari tim Royaltree akan muncul di sini",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InboxItem(message: InboxMessage) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (message.isRead)
                MaterialTheme.colorScheme.surface
            else
                ElectricBlue.copy(alpha = 0.06f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: open message detail */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(EmeraldLight, ElectricBlue))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.senderName.firstOrNull()?.uppercase() ?: "R",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }

                if (message.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.senderName,
                        fontWeight = if (!message.isRead) FontWeight.Black else FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (!message.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue)
                        )
                    }
                }
                Text(
                    text = message.senderRole,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.message,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    lineHeight = 15.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.timestamp,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
