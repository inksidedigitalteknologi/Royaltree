package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.viewmodel.AppScreen
import com.inkside.digital.ui.components.BannerAdView

// ==== Colors ====
private val NeonPurple = Color(0xFF8B5CF6)
private val NeonBlue = Color(0xFF3B82F6)
private val NeonPink = Color(0xFFEC4899)
private val RetroGold = Color(0xFFFFD700)
private val RetroBrown = Color(0xFF78350F)
private val RetroAmber = Color(0xFFF59E0B)
private val DarkBg = Color(0xFF0A0E1A)
private val TextMuted = Color(0xFF94A3B8)

@Composable
fun GameHubScreen(
    user: UserEntity?,
    currentLanguage: AppLanguage,
    onNavigate: (AppScreen) -> Unit,
    onBack: () -> Unit,
    onLoadGameState: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        onLoadGameState()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(DarkBg, Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ==== TOP BAR ====
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(NeonPurple, NeonBlue))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (user?.name?.take(1) ?: "U").uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user?.name ?: "User",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "Rank #--",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    Surface(
                        color = RetroGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RetroGold.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💰", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user?.points ?: 0}",
                                color = RetroGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // ==== Banner AdMob ====
            item {
                BannerAdView()
            }

            // ==== GRID 2×2 FULL WIDTH ====
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameGridCard(
                        title = "Mining Tycoon",
                        emoji = "⛏️",
                        gradient = listOf(RetroGold, RetroBrown),
                        available = true,
                        onClick = { onNavigate(AppScreen.GAME_MINING) },
                        modifier = Modifier.weight(1f)
                    )
                    GameGridCard(
                        title = "Coin Rush",
                        emoji = "🎰",
                        gradient = listOf(NeonPurple, NeonBlue),
                        available = false,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GameGridCard(
                        title = "Dice Roll",
                        emoji = "🎲",
                        gradient = listOf(NeonPink, NeonPurple),
                        available = false,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                    GameGridCard(
                        title = "Spin Wheel",
                        emoji = "🎯",
                        gradient = listOf(Color(0xFF10B981), Color(0xFF059669)),
                        available = false,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ==== LEADERBOARD PREVIEW ====
            item {
                LeaderboardPreviewCard(
                    onViewAll = { onNavigate(AppScreen.LEADERBOARD) }
                )
            }

            // ==== MENU FLOATING (compact) ====
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .border(1.dp, RetroBorder2.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MenuIconButton("🛒", "Shop") { onNavigate(AppScreen.MISSIONS) }
                    MenuIconButton("🎁", "Events") { onNavigate(AppScreen.MISSIONS) }
                    MenuIconButton("📊", "Stats") { onNavigate(AppScreen.LEADERBOARD) }
                    MenuIconButton("⚙️", "Set") { onNavigate(AppScreen.SETTINGS) }
                    MenuIconButton("❓", "Help") { onNavigate(AppScreen.FAQ) }
                }
            }

            // ==== TOMBOL BESAR ====
            item {
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(RetroGold, RetroAmber))
                        )
                        .clickable { onNavigate(AppScreen.GAME_MINING) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MAINKAN MINING",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

private val RetroBorder2 = Color(0xFFD97706)

@Composable
private fun MenuIconButton(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GameGridCard(
    title: String,
    emoji: String,
    gradient: List<Color>,
    available: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1.05f)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (available) Brush.linearGradient(gradient)
                else Brush.linearGradient(gradient.map { it.copy(alpha = 0.4f) })
            )
            .border(
                1.dp,
                if (available) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .clickable(enabled = available) { onClick() }
    ) {
        Text(
            text = emoji,
            fontSize = 80.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 4.dp),
            color = Color.White.copy(alpha = 0.15f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = emoji, fontSize = 30.sp)
            Column {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 15.sp
                )
                if (!available) {
                    Text(
                        text = "SOON",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardPreviewCard(onViewAll: () -> Unit) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, RetroGold.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewAll() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🏆", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TOP PLAYER",
                    color = RetroGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Lihat →",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "🥇 MiningKing         15.420",
                color = Color.White,
                fontSize = 11.sp
            )
            Text(
                text = "🥈 CryptoHunter       12.380",
                color = Color.White,
                fontSize = 11.sp
            )
        }
    }
}
