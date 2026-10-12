package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.util.GlobalPointsManager
import com.inkside.digital.ui.components.RtpAmount
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.border
import androidx.compose.ui.text.style.TextOverflow
import com.inkside.digital.ui.theme.EmeraldPrimary

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val points: Int,
    val userId: String = "",
    val tier: String = "FREE",
    val isCurrentUser: Boolean = false
)

@Composable
fun LeaderboardScreen(
    currentLanguage: AppLanguage,
    currentUserId: String?,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var entries by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMsg = null
        try {
            com.inkside.digital.data.network.ApiClient.getGameLeaderboard()
                .onSuccess { json ->
                    if (json.optBoolean("success", false)) {
                        val arr = json.optJSONArray("data") ?: return@onSuccess
                        val list = mutableListOf<LeaderboardEntry>()
                        for (i in 0 until arr.length()) {
                            val obj = arr.optJSONObject(i) ?: continue
                            list.add(
                                LeaderboardEntry(
                                    rank = obj.optInt("rank", i + 1),
                                    name = obj.optString("name", "Anonim"),
                                    points = obj.optInt("points", 0),
                                    userId = obj.optString("userId", ""),
                                    tier = obj.optString("tier", "FREE"),
                                    isCurrentUser = obj.optString("userId", "") == currentUserId
                                )
                            )
                        }
                        entries = list
                    } else {
                        errorMsg = json.optString("message", "Gagal memuat leaderboard")
                    }
                }
                .onFailure {
                    errorMsg = it.message ?: "Network error"
                }
        } catch (e: Exception) {
            errorMsg = e.message ?: "Error"
        }
        isLoading = false
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ==== Header ====
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
                Column {
                    Text(
                        text = "🏆 Leaderboard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Top 20 Global Mining",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ==== Top 3 Podium ====
        if (entries.size >= 3) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    PodiumCard(entries[1], 2, Modifier.weight(1f))
                    PodiumCard(entries[0], 1, Modifier.weight(1f))
                    PodiumCard(entries[2], 3, Modifier.weight(1f))
                }
            }
        }

        // ==== List rank 4+ ====
        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            errorMsg != null -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚠️", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMsg ?: "Error",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            entries.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏆", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Belum ada data",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Leaderboard akan muncul setelah ada aktivitas",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            else -> {
                itemsIndexed(entries.drop(3)) { _, entry ->
                    LeaderboardRow(entry, isCurrentUser = entry.userId == currentUserId)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun PodiumCard(entry: LeaderboardEntry, rank: Int, modifier: Modifier = Modifier) {
    val (height, frameRes, medal, glowColor) = when (rank) {
        1 -> Quadruple(150.dp, R.drawable.card_frame_legendary, "👑", Color(0xFFFFD700))
        2 -> Quadruple(125.dp, R.drawable.card_frame_epic, "🥈", Color(0xFFC0C0C0))
        else -> Quadruple(105.dp, R.drawable.card_frame_rare, "🥉", Color(0xFFCD7F32))
    }

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // Frame PNG
        Image(
            painter = painterResource(id = frameRes),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        // Konten
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Medal + Rank
            Text(medal, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(4.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(2.dp, glowColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.name.take(1).uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = glowColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Nama
            Text(
                text = entry.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // RTP
            Text(
                text = "${GlobalPointsManager.formatRtp(entry.points)} RTP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = GoldVip
            )
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry, isCurrentUser: Boolean = false) {
    val borderColor = if (isCurrentUser) EmeraldPrimary else Color(0xFF334155)
    val borderWidth = if (isCurrentUser) 1.5.dp else 1.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Frame PNG
        Image(
            painter = painterResource(id = R.drawable.card_frame_common),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )

        // Border highlight untuk user sendiri
        if (isCurrentUser) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            )
        }

        // Konten
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(1.dp, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${entry.rank}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isCurrentUser) EmeraldPrimary else Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(1.dp, GoldVip.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entry.name.take(1).uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldVip
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Nama
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCurrentUser) EmeraldPrimary else Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isCurrentUser) {
                    Text(
                        text = "You",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary.copy(alpha = 0.7f)
                    )
                }
            }

            // RTP
            RtpAmount(
                amount = "${GlobalPointsManager.formatRtp(entry.points)} RTP",
                fontSize = 12.sp,
                iconSize = 14.dp,
                color = GoldVip,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
