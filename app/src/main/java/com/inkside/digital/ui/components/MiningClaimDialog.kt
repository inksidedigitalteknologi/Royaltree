package com.inkside.digital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

private val MiningGold = Color(0xFFFFD700)
private val MiningEmerald = Color(0xFF10B981)
private val MiningAmber = Color(0xFFF59E0B)

/**
 * MiningClaimDialog — dialog konfirmasi klaim mining.
 *
 * 2 mode:
 * 1. Cooldown aktif → tunjuk countdown (tidak bisa klaim)
 * 2. Cooldown habis → tunjuk konfirmasi "Tonton Iklan"
 *
 * @param pointsToClaim jumlah poin yang bisa diklaim
 * @param cooldownRemainingMs sisa cooldown (ms)
 * @param isLoading true kalau sedang proses
 * @param onConfirm callback bila user tekan "Tonton Iklan"
 * @param onDismiss tutup dialog
 */
@Composable
fun MiningClaimDialog(
    pointsToClaim: Double,
    cooldownRemainingMs: Long,
    isLoading: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    // Live countdown
    var remaining by remember { mutableLongStateOf(cooldownRemainingMs) }
    val isCooldown = remaining > 0

    LaunchedEffect(cooldownRemainingMs) {
        remaining = cooldownRemainingMs
        while (remaining > 0) {
            delay(1000L)
            remaining = (remaining - 1000L).coerceAtLeast(0L)
        }
    }

    Dialog(onDismissRequest = { if (!isLoading) onDismiss() }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // ===== ICON HEADER =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (isCooldown) listOf(MiningAmber, Color(0xFFB45309))
                                    else listOf(MiningGold, MiningAmber)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCooldown) "⏳" else "⛏️",
                            fontSize = 32.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== TITLE =====
                Text(
                    text = if (isCooldown) "Tunggu Sebentar" else "Klaim Hasil Mining",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // ===== SUBTITLE =====
                Text(
                    text = if (isCooldown)
                        "Klaim berikutnya tersedia setelah cooldown habis"
                    else
                        "Tonton iklan dulu untuk klaim hasil mining",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ===== POINTS + COUNTDOWN =====
                if (isCooldown) {
                    // Countdown display
                    Surface(
                        color = MiningAmber.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Klaim lagi dalam",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatCountdown(remaining),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MiningAmber
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Poin tersimpan: ${pointsToClaim.toInt()}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // Points display
                    Surface(
                        color = MiningGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Poin Siap Diklaim",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${pointsToClaim.toInt()}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = MiningGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Setelah klaim, cooldown 1 jam berlaku",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ===== BUTANG =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tutup", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onConfirm,
                        enabled = !isCooldown && !isLoading && pointsToClaim >= 1,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MiningGold,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.weight(1.4f)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = Color.Black
                            )
                        } else {
                            Text(
                                text = if (isCooldown) "Cooldown" else "🎬 Tonton Iklan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isCooldown) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun formatCountdown(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
