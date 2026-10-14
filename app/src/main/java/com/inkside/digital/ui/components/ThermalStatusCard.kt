package com.inkside.digital.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.sensor.BatteryTempHelper
import com.inkside.digital.viewmodel.AffiliateViewModel

/**
 * ThermalStatusCard — tampilkan suhu baterai + region + charging status.
 * Update real-time dari BatteryTempHelper + LocationHelper.
 */
@Composable
fun ThermalStatusCard(viewModel: AffiliateViewModel) {
    val temp by viewModel.batteryTemp.collectAsState()
    val status by viewModel.tempStatus.collectAsState()
    val level by viewModel.batteryLevel.collectAsState()
    val charging by viewModel.isCharging.collectAsState()
    val region by viewModel.userRegion.collectAsState()
    val miningPaused by viewModel.miningPaused.collectAsState()
    val pauseReason by viewModel.pauseReason.collectAsState()
    val hashrateFactor by viewModel.hashrateFactor.collectAsState()

    // Jangan tampilkan kalau belum ada data suhu
    if (temp == 0f) return

    val bgColor = when (status) {
        BatteryTempHelper.TempStatus.COLD -> Color(0xFFE3F2FD)
        BatteryTempHelper.TempStatus.NORMAL -> Color(0xFFE8F5E9)
        BatteryTempHelper.TempStatus.WARM -> Color(0xFFFFF9C4)
        BatteryTempHelper.TempStatus.HOT -> Color(0xFFFFE0B2)
        BatteryTempHelper.TempStatus.VERY_HOT -> Color(0xFFFFCDD2)
        BatteryTempHelper.TempStatus.CRITICAL -> Color(0xFFEF9A9A)
        BatteryTempHelper.TempStatus.THROTTLE -> Color(0xFFB71C1C)
    }

    val textColor = if (status == BatteryTempHelper.TempStatus.THROTTLE) {
        Color.White
    } else {
        Color(0xFF1F2937)
    }

    // Animasi blink saat status CRITICAL/THROTTLE
    val isCritical = status == BatteryTempHelper.TempStatus.CRITICAL ||
                     status == BatteryTempHelper.TempStatus.THROTTLE
    val infiniteTransition = rememberInfiniteTransition(label = "thermal_blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isCritical) 0.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "thermal_alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(status.emoji, fontSize = 26.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "${String.format("%.1f", temp)}°C",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    )
                    Text(
                        status.label,
                        fontSize = 12.sp,
                        color = textColor.copy(alpha = 0.8f)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "🔋 $level%",
                        fontSize = 12.sp,
                        color = textColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (charging) {
                        Text(
                            "⚡ Charging",
                            fontSize = 10.sp,
                            color = textColor.copy(alpha = 0.8f)
                        )
                    }
                    Text(
                        "📍 $region",
                        fontSize = 10.sp,
                        color = textColor.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { hashrateFactor },
                modifier = Modifier.fillMaxWidth(),
                color = if (miningPaused) {
                    Color(0xFFB71C1C)
                } else if (status == BatteryTempHelper.TempStatus.THROTTLE) {
                    Color.White
                } else {
                    Color(0xFF16A34A)
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Hashrate: ${(hashrateFactor * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = textColor
                )
                Text(
                    if (miningPaused) "Mining dijeda" else "Mining speed adjustment",
                    fontSize = 10.sp,
                    color = textColor.copy(alpha = 0.6f)
                )
            }

            // ============ BANNER PAUSED ============
            if (miningPaused) {
                Spacer(Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFB71C1C),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⛔", fontSize = 20.sp)
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                when (pauseReason) {
                                    "battery_critical" -> "🔋 BATERAI KRITIS"
                                    "temp_critical" -> "⛔ HP TERLALU PANAS"
                                    else -> "⛔ MINING DIJEDA"
                                },
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                when (pauseReason) {
                                    "battery_critical" -> "Charge HP untuk lanjut mining"
                                    "temp_critical" -> "Dinginkan HP sampai < 55°C"
                                    else -> "Tunggu sampai aman"
                                },
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
