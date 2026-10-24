package com.inkside.digital.ui.components

import androidx.compose.foundation.background
import com.inkside.digital.data.preferences.AppThemePreferences
import com.inkside.digital.localization.LanguageManager
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip

enum class DayStatus {
    CHECKED_IN,     // Sudah check-in
    TODAY,          // Hari ini (bisa diklaim)
    RECOVERED,      // Di-recover
    MISSED,         // Terlewat
    FUTURE,         // Belum (locked)
    SPECIAL         // H-7 (Mega Jackpot)
}

/**
 * CalendarDayItem — Card untuk satu hari dalam grid check-in.
 * 
 * Design: kotak rounded dengan icon + RTP + tanggal.
 * Adaptasi dari "Sign In for a Skin" game design.
 */
@Composable
fun CalendarDayItem(
    day: Int,                       // Nomor urut dalam grid (1-28)
    dayLabel: String,               // Label tanggal (mis. "10/14")
    points: Double,                 // RTP reward
    status: DayStatus,
    isWeekend: Boolean = false,
    isSpecial: Boolean = false,     // H-7 Mega Jackpot
    modifier: Modifier = Modifier
) {
    // === Warna berdasarkan status ===
    val (bgColor, borderColor, contentColor) = when (status) {
        DayStatus.CHECKED_IN -> Triple(
            EmeraldLight.copy(alpha = 0.18f),
            EmeraldLight.copy(alpha = 0.6f),
            EmeraldLight
        )
        DayStatus.TODAY -> Triple(
            GoldVip.copy(alpha = 0.25f),
            GoldVip,
            GoldVip
        )
        DayStatus.RECOVERED -> Triple(
            GoldVip.copy(alpha = 0.15f),
            GoldVip.copy(alpha = 0.5f),
            GoldVip
        )
        DayStatus.MISSED -> Triple(
            Color(0xFFEF4444).copy(alpha = 0.12f),
            Color(0xFFEF4444).copy(alpha = 0.4f),
            Color(0xFFEF4444)
        )
        DayStatus.SPECIAL -> Triple(
            GoldVip.copy(alpha = 0.25f),
            GoldVip,
            GoldVip
        )
        DayStatus.FUTURE -> Triple(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }

    val isToday = status == DayStatus.TODAY
    val isActive = status == DayStatus.CHECKED_IN || status == DayStatus.TODAY

    Box(
        modifier = modifier
            .shadow(
                if (isToday) 8.dp else 0.dp,
                RoundedCornerShape(10.dp),
                spotColor = if (isToday) GoldVip else Color.Transparent
            )
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isToday) {
                    Brush.radialGradient(
                        colors = listOf(
                            GoldVip.copy(alpha = 0.35f),
                            GoldVip.copy(alpha = 0.15f)
                        )
                    )
                } else {
                    Brush.radialGradient(listOf(bgColor, bgColor))
                }
            )
            .border(
                if (isToday || isActive) 1.5.dp else 1.dp,
                borderColor,
                RoundedCornerShape(10.dp)
            )
            .padding(vertical = 6.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // === Icon ===
            when (status) {
                DayStatus.CHECKED_IN -> {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = LanguageManager.translate("cd_checked", AppThemePreferences.currentLanguage, "Checked"),
                        tint = EmeraldLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
                DayStatus.TODAY -> {
                    Icon(
                        if (isSpecial) Icons.Default.CardGiftcard else Icons.Default.Star,
                        contentDescription = LanguageManager.translate("cd_today", AppThemePreferences.currentLanguage, "Today"),
                        tint = GoldVip,
                        modifier = Modifier.size(18.dp)
                    )
                }
                DayStatus.FUTURE -> {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = LanguageManager.translate("cd_locked", AppThemePreferences.currentLanguage, "Locked"),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                else -> {
                    Icon(
                        Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // === RTP Reward ===
            Text(
                text = "+${String.format("%.2f", points)}",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = contentColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // === Tanggal ===
            Text(
                text = dayLabel,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }
    }
}
