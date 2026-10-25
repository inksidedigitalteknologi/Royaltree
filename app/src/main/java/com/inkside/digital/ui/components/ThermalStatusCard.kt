package com.inkside.digital.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.sensor.BatteryTempHelper
import com.inkside.digital.viewmodel.AffiliateViewModel

/**
 * ThermalStatusCard — compact: suhu, baterai, device.
 * Tanpa Production Rate, tanpa Efficiency, tanpa Paused banner.
 */
@Composable
fun ThermalStatusCard(viewModel: AffiliateViewModel) {
    val temp by viewModel.batteryTemp.collectAsState()
    val status by viewModel.tempStatus.collectAsState()
    val level by viewModel.batteryLevel.collectAsState()
    val charging by viewModel.isCharging.collectAsState()

    if (temp == 0f) return

    val accentColor = when (status) {
        BatteryTempHelper.TempStatus.COLD -> Color(0xFF60A5FA)
        BatteryTempHelper.TempStatus.NORMAL -> Color(0xFF34D399)
        BatteryTempHelper.TempStatus.WARM -> Color(0xFFFBBF24)
        BatteryTempHelper.TempStatus.HOT -> Color(0xFFFB923C)
        BatteryTempHelper.TempStatus.VERY_HOT -> Color(0xFFF87171)
        BatteryTempHelper.TempStatus.CRITICAL -> Color(0xFFEF4444)
        BatteryTempHelper.TempStatus.THROTTLE -> Color(0xFFFFFFFF)
    }

    Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.85f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: Suhu + status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌡️", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "${String.format("%.1f", temp)}°C",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = accentColor
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    status.label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }

            // Row 2: Baterai · Device
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔋", fontSize = 13.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        "$level%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (level < 20) Color(0xFFEF4444) else Color.White.copy(alpha = 0.85f)
                    )
                    if (charging) {
                        Spacer(Modifier.width(2.dp))
                        Text("⚡", fontSize = 11.sp)
                    }
                }

                Text("·", fontSize = 11.sp, color = Color.White.copy(alpha = 0.3f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📱", fontSize = 11.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        android.os.Build.MODEL ?: "Device",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
