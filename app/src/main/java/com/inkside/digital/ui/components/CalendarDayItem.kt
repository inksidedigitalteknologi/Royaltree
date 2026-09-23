package com.inkside.digital.ui.components

import androidx.compose.foundation.background
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

    Column(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (status == DayStatus.CHECKED_IN) {
            Icon(Icons.Default.Check, null, tint = EmeraldLight, modifier = Modifier.height(16.dp))
        } else if (status == DayStatus.FUTURE) {
            Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.height(14.dp))
        } else {
            Text(day.toString(), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
        Text("+$points", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = textColor.copy(alpha = 0.8f))
    }
}
