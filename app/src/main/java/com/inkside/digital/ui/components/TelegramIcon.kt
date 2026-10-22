package com.inkside.digital.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val TelegramLogo: ImageVector by lazy {
    ImageVector.Builder(
        name = "Telegram",
        defaultWidth = 24.dp, defaultHeight = 24.dp,
        viewportWidth = 24f, viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFFFFFFF))) {
            moveTo(11.944f, 0.000f)
            curveTo(5.355f, 0.000f, 0.000f, 5.355f, 0.000f, 11.944f)
            curveTo(0.000f, 18.533f, 5.355f, 23.888f, 11.944f, 23.888f)
            curveTo(18.533f, 23.888f, 23.888f, 18.533f, 23.888f, 11.944f)
            curveTo(23.888f, 5.355f, 18.533f, 0.000f, 11.944f, 0.000f)
            close()
            moveTo(17.502f, 8.168f)
            lineTo(15.638f, 16.955f)
            curveTo(15.498f, 17.585f, 15.123f, 17.739f, 14.596f, 17.442f)
            lineTo(11.739f, 15.335f)
            lineTo(10.362f, 16.664f)
            curveTo(10.209f, 16.817f, 10.081f, 16.945f, 9.787f, 16.945f)
            lineTo(9.992f, 14.038f)
            lineTo(15.205f, 9.326f)
            curveTo(15.431f, 9.128f, 15.156f, 9.019f, 14.855f, 9.218f)
            lineTo(8.418f, 13.272f)
            lineTo(5.458f, 12.348f)
            curveTo(4.815f, 12.147f, 4.803f, 11.705f, 5.591f, 11.408f)
            lineTo(16.607f, 7.163f)
            curveTo(17.143f, 6.966f, 17.612f, 7.286f, 17.502f, 8.168f)
            close()
        }
    }.build()
}

@Composable
fun TelegramIcon(size: Int = 24, tint: Color = Color.White) {
    Icon(
        imageVector = TelegramLogo,
        contentDescription = "Telegram",
        modifier = Modifier.size(size.dp),
        tint = tint
    )
}
