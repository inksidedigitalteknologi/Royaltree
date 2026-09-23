package com.inkside.digital.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin

/**
 * Background gradient beranimasi — untuk Login/Register screen.
 * Menggunakan Canvas untuk menggambar 3 lingkaran gradient yang bergerak.
 */
@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Warna tema: ungu-biru gradient
    val colorTopLeft = Color(0xFF6366F1)      // Indigo
    val colorTopRight = Color(0xFF8B5CF6)     // Violet
    val colorBottomRight = Color(0xFFEC4899)  // Pink
    val colorBottomLeft = Color(0xFF3B82F6)   // Blue

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(colorTopLeft, colorTopRight, colorBottomRight, colorBottomLeft)
                )
            )
    ) {
        // Overlay lingkaran gradient yang bergerak
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = size.minDimension * 0.6f * scale

            // Lingkaran 1 — violet
            val angleRad1 = Math.toRadians(angle.toDouble())
            val x1 = center.x + (cos(angleRad1) * size.width * 0.3).toFloat()
            val y1 = center.y + (sin(angleRad1) * size.height * 0.3).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.7f), Color.Transparent),
                    center = Offset(x1, y1),
                    radius = radius
                ),
                radius = radius,
                center = Offset(x1, y1)
            )

            // Lingkaran 2 — pink
            val angleRad2 = Math.toRadians((angle + 120).toDouble())
            val x2 = center.x + (cos(angleRad2) * size.width * 0.35).toFloat()
            val y2 = center.y + (sin(angleRad2) * size.height * 0.35).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFEC4899).copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(x2, y2),
                    radius = radius * 0.9f
                ),
                radius = radius * 0.9f,
                center = Offset(x2, y2)
            )

            // Lingkaran 3 — biru
            val angleRad3 = Math.toRadians((angle + 240).toDouble())
            val x3 = center.x + (cos(angleRad3) * size.width * 0.32).toFloat()
            val y3 = center.y + (sin(angleRad3) * size.height * 0.32).toFloat()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF3B82F6).copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(x3, y3),
                    radius = radius * 0.85f
                ),
                radius = radius * 0.85f,
                center = Offset(x3, y3)
            )
        }

        content()
    }
}
