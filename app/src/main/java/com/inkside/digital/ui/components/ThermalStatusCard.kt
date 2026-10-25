package com.inkside.digital.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
 * ThermalStatusCard — compact card: thermal + battery + hashrate
 * Update real-time dari BatteryTempHelper.
 */
@Composable
fun ThermalStatusCard(viewModel: AffiliateViewModel) {
    val temp by viewModel.batteryTemp.collectAsState()
    val status by viewModel.tempStatus.collectAsState()
    val level by viewModel.batteryLevel.collectAsState()
    val charging by viewModel.isCharging.collectAsState()
    val miningPaused by viewModel.miningPaused.collectAsState()
    val pauseReason by viewModel.pauseReason.collectAsState()
    val hashrateFactor by viewModel.hashrateFactor.collectAsState()

    if (temp == 0f) return

    val (bgColor, accentColor) = when (status) {
        BatteryTempHelper.TempStatus.COLD -> Color(0xFF0F172A) to Color(0xFF60A5FA)
        BatteryTempHelper.TempStatus.NORMAL -> Color(0xFF0F172A) to Color(0xFF34D399)
        BatteryTempHelper.TempStatus.WARM -> Color(0xFF0F172A) to Color(0xFFFBBF24)
        BatteryTempHelper.TempStatus.HOT -> Color(0xFF0F172A) to Color(0xFFFB923C)
        BatteryTempHelper.TempStatus.VERY_HOT -> Color(0xFF0F172A) to Color(0xFFF87171)
        BatteryTempHelper.TempStatus.CRITICAL -> Color(0xFF0F172A) to Color(0xFFEF4444)
        BatteryTempHelper.TempStatus.THROTTLE -> Color(0xFF450A0A) to Color(0xFFFFFFFF)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1: Icon + suhu + battery + charging
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(status.emoji, fontSize = 22.sp)
                Spacer(Modifier.width(10.dp))

                Text(
                    "${String.format("%.1f", temp)}°C",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = accentColor
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    status.label,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(Modifier.weight(1f))

                Text(
                    "🔋 $level%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                if (charging) {
                    Spacer(Modifier.width(6.dp))
                    Text("⚡", fontSize = 13.sp)
                }
            }

            // Row 2: Hashrate bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Production Rate",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(Modifier.width(8.dp))
                // Custom progress bar
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                ) {
                    Surface(
                        color = when {
                            miningPaused -> Color(0xFFEF4444)
                            status == BatteryTempHelper.TempStatus.THROTTLE -> Color.White
                            else -> accentColor
                        },
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .fillMaxWidth(hashrateFactor.coerceIn(0f, 1f))
                            .height(6.dp)
                    ) {}
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "${(hashrateFactor * 100).toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            // Paused banner
            if (miningPaused) {
                Surface(
                    color = Color(0xFF7F1D1D),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⛔", fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            when (pauseReason) {
                                "battery_critical" -> "Baterai kritis — charge HP"
                                "temp_critical" -> "HP terlalu panas — dinginkan"
                                else -> "Production paused"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
