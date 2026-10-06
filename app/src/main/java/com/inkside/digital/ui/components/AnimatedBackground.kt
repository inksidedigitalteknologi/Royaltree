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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============ Colors ============
private val AuroraDeep = Color(0xFF0A0E1A)  // M53: soft dark navy
private val AuroraMid = Color(0xFF0F1729)
private val AuroraSoft = Color(0xFF1E3A4F)
private val AuroraTeal = Color(0xFF0D9488)
private val EmeraldAccent = Color(0xFF34D399)
private val CyanAccent = Color(0xFF06B6D4)
private val GoldAccent = Color(0xFFFFD700)

private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val phase: Float,
    val speed: Float,
    val isGold: Boolean
)

// M53: Leaf particle — daun jatuh dengan wind
private data class LeafParticle(
    val x: Float,              // 0..1 (start position)
    val startY: Float,         // -0.5..0 (start above screen)
    val size: Float,           // px (small: 4-8, medium: 8-14)
    val fallSpeed: Float,      // 0.3..0.8
    val windInfluence: Float,  // 0.5..1.5 (sensitivity to wind)
    val swayPhase: Float,      // 0..2π
    val rotationSpeed: Float,  // -1..1
    val color: Color,
    val isLeafShape: Boolean   // true = leaf, false = circle dot
)

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = EmeraldAccent,
    enableAurora: Boolean = true,
    enableStars: Boolean = true,
    enableGlow: Boolean = true,
    enableParticles: Boolean = true,
    starCount: Int = 35,  // M53: fewer stars
    particleCount: Int = 10,  // M53: fewer leaves
    enableWind: Boolean = true,  // ← M53: wind effect
    content: @Composable BoxScope.() -> Unit = {}
) {
    val infinite = rememberInfiniteTransition(label = "bg")
    val time by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    // Wind wave — kawal arah angin (sin wave, 24s cycle)
    val windPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat() * 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wind_phase"
    )

    val stars = remember(starCount) {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = 0.8f + Random.nextFloat() * 1.2f,  // M53: smaller stars
                phase = Random.nextFloat() * 2f * PI.toFloat(),
                speed = 0.5f + Random.nextFloat() * 1f,
                isGold = Random.nextFloat() > 0.8f
            )
        }
    }

    val particles = remember(particleCount) {
        List(particleCount) { i ->
            LeafParticle(
                x = Random.nextFloat(),
                startY = -Random.nextFloat() * 0.5f,
                size = 3f + Random.nextFloat() * 5f,  // M53: 3-8px smaller
                fallSpeed = 0.25f + Random.nextFloat() * 0.4f,
                windInfluence = 0.5f + Random.nextFloat() * 1.0f,
                swayPhase = Random.nextFloat() * 2f * PI.toFloat(),
                rotationSpeed = (Random.nextFloat() - 0.5f) * 1f,  // M53: slower rotation
                color = when (i % 4) {
                    0 -> EmeraldAccent.copy(alpha = 0.25f)   // M53: subtler
                    1 -> CyanAccent.copy(alpha = 0.20f)      // M53: subtler
                    2 -> GoldAccent.copy(alpha = 0.22f)      // M53: subtler
                    else -> Color(0xFF10B981).copy(alpha = 0.18f)
                },
                isLeafShape = i % 3 != 2  // 2/3 daun, 1/3 dot
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuroraDeep)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val t = time * 2f * PI.toFloat()

            // ============ Layer 1: Aurora gradient ============
            if (enableAurora) {
                val offset = sin(t) * h * 0.15f  // M53: subtler movement
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AuroraDeep,
                            AuroraMid,
                            AuroraSoft.copy(alpha = 0.35f),  // M53: softer
                            AuroraMid,
                            AuroraDeep
                        ),
                        startY = -h * 0.5f + offset,
                        endY = h * 1.5f + offset
                    )
                )

                val accentOffset = cos(t * 0.7f) * w * 0.4f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.02f),  // M53: subtle  // M53: softer
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f + accentOffset, h * 0.3f),
                        radius = h * 0.4f
                    ),
                    radius = h * 0.4f,
                    center = Offset(w * 0.5f + accentOffset, h * 0.3f)
                )
            }

            // ============ Layer 2: Stars ============
            if (enableStars) {
                stars.forEach { star ->
                    val alpha = 0.15f + 0.4f * (  // M53: subtler twinkle
                        0.5f + 0.5f * sin(t * star.speed + star.phase)
                    )
                    val color = if (star.isGold) GoldAccent else Color.White
                    drawCircle(
                        color = color.copy(alpha = alpha),
                        radius = star.size,
                        center = Offset(star.x * w, star.y * h)
                    )
                }
            }

            // ============ Layer 3: Glow bernafas ============
            if (enableGlow) {
                val glowScale = 0.98f + 0.06f * sin(t * 0.4f)  // M53: subtle pulse
                val glowRadius = h * 0.55f * glowScale
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldAccent.copy(alpha = 0.10f),  // M53: softer glow
                            accentColor.copy(alpha = 0.07f),  // M53: softer
                            accentColor.copy(alpha = 0.02f),  // M53: subtle
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f, h * 0.55f),
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = Offset(w * 0.5f, h * 0.55f)
                )
            }

            // ============ Layer 4: Leaf particles + Wind ============
            if (enableParticles) {
                // Global wind strength: -1 (kiri) → +1 (kanan)
                val windStrength = if (enableWind) sin(windPhase) else 0f

                particles.forEach { p ->
                    // Fall progression
                    val yProgress = (time * p.fallSpeed + p.startY + 1f) % 1f

                    // Wind push (horizontal)
                    val windPush = windStrength * 40f * p.windInfluence  // M53: gentler wind

                    // Individual sway
                    val swayX = sin(t * 0.6f + p.swayPhase) * w * 0.04f  // M53: gentler

                    val x = p.x * w + windPush + swayX
                    val y = yProgress * h

                    // Fade in/out
                    val alpha = when {
                        yProgress < 0.1f -> yProgress * 10f
                        yProgress > 0.9f -> (1f - yProgress) * 10f
                        else -> 1f
                    }.coerceIn(0f, 1f)

                    val finalColor = p.color.copy(alpha = p.color.alpha * alpha)

                    if (p.isLeafShape) {
                        // Rotasi daun
                        val rotation = yProgress * 180f * p.rotationSpeed +  // M53: slower spin
                                (if (enableWind) windStrength * 25f * p.windInfluence else 0f)
                        drawLeaf(
                            center = Offset(x, y),
                            size = p.size,
                            rotation = rotation,
                            color = finalColor
                        )
                    } else {
                        // Dot
                        drawCircle(
                            color = finalColor,
                            radius = p.size * 0.6f,
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        content()
    }
}

/**
 * Draw leaf shape (pointed ellipse)
 */
private fun DrawScope.drawLeaf(
    center: Offset,
    size: Float,
    rotation: Float,
    color: Color
) {
    rotate(degrees = rotation, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y - size)
            cubicTo(
                center.x + size * 0.8f, center.y - size * 0.3f,
                center.x + size * 0.5f, center.y + size * 0.4f,
                center.x, center.y + size
            )
            cubicTo(
                center.x - size * 0.5f, center.y + size * 0.4f,
                center.x - size * 0.8f, center.y - size * 0.3f,
                center.x, center.y - size
            )
            close()
        }
        drawPath(path, color)
    }
}
