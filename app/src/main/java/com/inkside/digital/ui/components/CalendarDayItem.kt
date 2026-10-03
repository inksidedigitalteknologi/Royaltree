package com.inkside.digital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.GoldVip

enum class DayStatus {
    CHECKED_IN, TODAY, RECOVERED, MISSED, FUTURE, SPECIAL
}

@Composable
fun CalendarDayItem(
    day: Int,
    points: Int,
    status: DayStatus,
    isWeekend: Boolean,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (status) {
        DayStatus.CHECKED_IN -> EmeraldLight.copy(alpha = 0.15f) to EmeraldLight
        DayStatus.TODAY -> ElectricBlue.copy(alpha = 0.2f) to ElectricBlue
        DayStatus.RECOVERED -> GoldVip.copy(alpha = 0.15f) to GoldVip
        DayStatus.MISSED -> Color(0xFFEF4444).copy(alpha = 0.15f) to Color(0xFFEF4444)
        DayStatus.SPECIAL -> GoldVip.copy(alpha = 0.2f) to GoldVip
        DayStatus.FUTURE -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    // ============ CIRCLE STYLE ============
    val isToday = status == DayStatus.TODAY
    val isActive = status == DayStatus.CHECKED_IN || status == DayStatus.TODAY || status == DayStatus.RECOVERED
    
    Box(
        modifier = modifier
            .size(52.dp)                              // ← dari height 68dp
            .clip(CircleShape)                        // ← circle, bukan kotak
            .then(
                if (isToday) {
                    Modifier
                        .shadow(8.dp, CircleShape)   // ← glow shadow untuk TODAY
                        .border(2.dp, ElectricBlue, CircleShape)  // ← border TODAY
                } else Modifier
            )
            .background(
                if (isActive && status == DayStatus.CHECKED_IN) {
                    Brush.radialGradient(
                        colors = listOf(
                            EmeraldLight.copy(alpha = 0.3f),
                            EmeraldLight.copy(alpha = 0.1f)
                        )
                    )
                } else if (isToday) {
                    Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.4f),
                            ElectricBlue.copy(alpha = 0.15f)
                        )
                    )
                } else {
                    Brush.radialGradient(listOf(bgColor, bgColor))
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (status) {
                DayStatus.CHECKED_IN -> {
                    Icon(
                        Icons.Default.Check,
                        null,
                        tint = EmeraldLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
                DayStatus.FUTURE -> {
                    Icon(
                        Icons.Default.Lock,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(12.dp)
                    )
                }
                else -> {
                    Text(
                        day.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = textColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                "+$points",
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                color = textColor.copy(alpha = 0.85f)
            )
        }
    }
}
