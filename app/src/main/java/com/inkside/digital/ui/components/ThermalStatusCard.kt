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

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                progress = { status.hashrateFactor },
                modifier = Modifier.fillMaxWidth(),
                color = if (status == BatteryTempHelper.TempStatus.THROTTLE) {
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
                    "Hashrate: ${(status.hashrateFactor * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = textColor
                )
                Text(
                    "Mining speed adjustment",
                    fontSize = 10.sp,
                    color = textColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}
