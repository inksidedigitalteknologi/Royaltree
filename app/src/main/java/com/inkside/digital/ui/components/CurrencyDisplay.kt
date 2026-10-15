package com.inkside.digital.ui.components

import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.data.preferences.AppThemePreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.R

/**
 * RTP Amount dengan coin icon.
 * Guna di mana-mana untuk konsisten display RTP.
 *
 * @param amount Text RTP (contoh: "200 RTP" atau "200.5000 RTP")
 * @param fontSize Saiz text (default 14sp)
 * @param iconSize Saiz icon RTP (default 16dp)
 * @param color Warna text (default Unspecified — ikut theme)
 * @param fontWeight Weight text (default Bold)
 */
@Composable
fun RtpAmount(
    amount: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    iconSize: Dp = 16.dp,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.rtp_coin),
            contentDescription = LanguageManager.translate("cd_rtp", AppThemePreferences.currentLanguage, "RTP"),
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = amount,
            fontSize = fontSize,
            color = color,
            fontWeight = fontWeight
        )
    }
}
