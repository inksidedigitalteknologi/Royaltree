package com.inkside.digital.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import kotlinx.coroutines.delay
import kotlin.random.Random

// ==== Colors — Neon Cyberpunk ====
private val NeonCyan = Color(0xFF06B6D4)
private val NeonMagenta = Color(0xFFEC4899)
private val NeonYellow = Color(0xFFFACC15)
private val DarkDeep = Color(0xFF05060F)
private val DarkCard = Color(0xFF0F172A)

private data class Coin(
    val id: Int,
    val xFraction: Float,   // 0..1
    val yFraction: Float,   // 0..1
    val spawnTimeMs: Long
)

@Composable
fun CoinRushScreen(
    user: UserEntity?,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onFinish: (score: Int) -> Unit
) {
    // ====== GAME STATE ======
    var score by remember { mutableIntStateOf(0) }
    var timeLeftMs by remember { mutableIntStateOf(30_000) }
    var isPlaying by remember { mutableStateOf(false) }
    var isFinished by remember { mutableStateOf(false) }
    var coinIdCounter by remember { mutableIntStateOf(0) }

    val coins = remember { mutableStateListOf<Coin>() }

    // Countdown
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (timeLeftMs > 0 && isPlaying) {
            delay(100)
            timeLeftMs -= 100
        }
        if (timeLeftMs <= 0) {
            isPlaying = false
            isFinished = true
            onFinish(score)
        }
    }

    // Spawn coin setiap 700ms semasa bermain
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (isPlaying) {
            delay(700L)
            if (coins.size < 6) {
                coinIdCounter += 1
                coins.add(
                    Coin(
                        id = coinIdCounter,
                        xFraction = Random.nextFloat().coerceIn(0.05f, 0.85f),
                        yFraction = Random.nextFloat().coerceIn(0.15f, 0.85f),
                        spawnTimeMs = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // Auto-hilang coin selepas 1.5s
    LaunchedEffect(isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        while (isPlaying) {
            delay(200L)
            val now = System.currentTimeMillis()
            coins.removeAll { now - it.spawnTimeMs > 1500L }
        }
    }

    // ====== UI ======
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DarkDeep, DarkCard, DarkDeep))
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Coin Rush", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                    Text("Tap koin sebelum hilang!", color = NeonCyan, fontSize = 11.sp)
                }
            }

            // HUD — skor + masa
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HudCard(label = "SKOR", value = "$score", accent = NeonYellow, modifier = Modifier.weight(1f))
                HudCard(label = "MASA", value = "${timeLeftMs / 1000}s", accent = NeonMagenta, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PLAY AREA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                NeonCyan.copy(alpha = 0.12f),
                                NeonMagenta.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
                    .background(DarkCard.copy(alpha = 0.4f))
            ) {
                // Coin sprites — guna Box dengan absolute offset
                androidx.compose.foundation.layout.BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val maxW = maxWidth
                    val maxH = maxHeight
                    coins.forEach { coin ->
                        val xDp = (maxW * coin.xFraction)
                        val yDp = (maxH * coin.yFraction)
                        CoinSprite(
                            xDp = xDp,
                            yDp = yDp,
                            onClick = {
                                if (isPlaying) {
                                    coins.remove(coin)
                                    score += 10
                                }
                            }
                        )
                    }
                }

                // Center overlay — Start / Finish
                if (!isPlaying && !isFinished) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎰", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Tekan MULA untuk bermain",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                score = 0
                                timeLeftMs = 30_000
                                coins.clear()
                                isPlaying = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("MULA", color = Color.Black, fontWeight = FontWeight.Black)
                        }
                    }
                }

                if (isFinished) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏁", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Skor Akhir", color = Color.White, fontSize = 13.sp)
                        Text("$score", color = NeonYellow, fontSize = 42.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    score = 0
                                    timeLeftMs = 30_000
                                    coins.clear()
                                    isFinished = false
                                    isPlaying = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("MAIN LAGI", color = Color.Black, fontWeight = FontWeight.Black)
                            }
                            Button(
                                onClick = { onBack() },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("KELUAR", color = Color.White, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HudCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = accent.copy(alpha = 0.15f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(label, color = accent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun CoinSprite(
    xDp: androidx.compose.ui.unit.Dp,
    yDp: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.7f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "coinScale"
    )

    Box(
        modifier = Modifier
            .offset(x = xDp, y = yDp)
            .size(56.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(listOf(NeonYellow, Color(0xFFFFA500)))
            )
            .clickable {
                pressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text("💰", fontSize = 28.sp)
    }
}
