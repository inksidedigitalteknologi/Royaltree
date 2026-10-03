package com.inkside.digital.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.SideEffect
import android.app.Activity

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = Color(0xFF003822),
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = EmeraldOnContainer,
    secondary = ElectricBlueLight,
    onSecondary = Color(0xFF002F65),
    secondaryContainer = Color(0xFF1E3A8A),
    onSecondaryContainer = Color(0xFFDBEAFE),
    tertiary = GoldVip,
    onTertiary = Color(0xFF451E00),
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    onSurfaceVariant = DarkTextSecondary,
    outline = BrandNavyCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldOnContainer,
    onPrimaryContainer = EmeraldContainer,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E3A8A),
    tertiary = GoldVip,
    onTertiary = Color.White,
    background = SlateBackground,
    surface = SlateCard,
    surfaceVariant = Color(0xFFF1F5F9),
    onBackground = SlateTextPrimary,
    onSurface = SlateTextPrimary,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateBorder
)

/**
 * SyncStatusBar — pastikan warna icon status bar & navigation bar
 * sesuai dengan dark/light mode. Tanpa ini, icon bisa "hilang"
 * di background yang sama warnanya.
 */
@Composable
fun SyncStatusBar(darkTheme: Boolean) {
    val context = LocalContext.current
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)

            // Dark mode → icon putih (light = false)
            // Light mode → icon hitam (light = true)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // default light, di-override dari AppThemePreferences
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    SyncStatusBar(darkTheme = darkTheme)  // ← tambah ni
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
