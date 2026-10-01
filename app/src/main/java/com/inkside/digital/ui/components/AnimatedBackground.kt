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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============ Colors ============
private val AuroraDeep = Color(0xFF070A18)
private val AuroraMid = Color(0xFF0F1729)
private val AuroraSoft = Color(0xFF1E293B)
private val EmeraldAccent = Color(0xFF34D399)
private val CyanAccent = Color(0xFF06B6D4)
private val GoldAccent = Color(0xFFFFD700)

private data class Star(
    val x: Float,           // 0..1
    val y: Float,           // 0..1
    val size: Float,        // px
    val phase: Float,       // 0..2π
    val speed: Float,       // 0.5..1.5
    val isGold: Boolean
)

private data class Particle(
    val x: Float,           // 0..1
    val startY: Float,      // 0..1 (bisa di atas dari skrin)
    val size: Float,        // px
    val speed: Float,       // px/s relatif
    val sway: Float,        // horizontal sway
    val color: Color
)

/**
 * AnimatedBackground — background animated dengan 4 layer:
 *
 * Layer 1: Aurora gradient bergerak (vertical)
 * Layer 2: Bintang berkelip (background)
 * Layer 3: Glow radial bernafas (tengah)
 * Layer 4: Particle daun berguguran
 *
 * @param accentColor warna accent utama
 * @param enableAurora Layer 1
 * @param enableStars Layer 2
 * @param enableGlow Layer 3
 * @param enableParticles Layer 4
 * @param starCount bilangan bintang (default 40)
 * @param particleCount bilangan partikel (default 20)
 * @param content content di atas background
 */
@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    accentColor: Color = EmeraldAccent,
    enableAurora: Boolean = true,
    enableStars: Boolean = true,
    enableGlow: Boolean = true,
    enableParticles: Boolean = true,
    starCount: Int = 40,
    particleCount: Int = 20,
    content: @Composable BoxScope.() -> Unit = {}
) {
    // Idle animation — time loop
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

    // Seed-based stars — tetap antara recompose
    val stars = remember(starCount) {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = 1f + Random.nextFloat() * 1.8f,
                phase = Random.nextFloat() * 2f * PI.toFloat(),
                speed = 0.5f + Random.nextFloat() * 1f,
                isGold = Random.nextFloat() > 0.8f
            )
        }
    }

    // Seed-based particles — tetap antara recompose
    val particles = remember(particleCount) {
        List(particleCount) {
            Particle(
                x = Random.nextFloat(),
                startY = -Random.nextFloat() * 0.5f,  // mulai di atas
                size = 2f + Random.nextFloat() * 4f,
                speed = 0.3f + Random.nextFloat() * 0.5f,
                sway = (Random.nextFloat() - 0.5f) * 0.4f,
                color = when (Random.nextInt(3)) {
                    0 -> EmeraldAccent.copy(alpha = 0.55f)
                    1 -> CyanAccent.copy(alpha = 0.45f)
                    else -> GoldAccent.copy(alpha = 0.4f)
                }
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
            val t = time * 2f * PI.toFloat()  // full cycle 0..2π

            // ============ Layer 1: Aurora gradient ============
            if (enableAurora) {
                // Gradient bergerak vertikal
                val offset = sin(t) * h * 0.3f
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AuroraDeep,
                            AuroraMid,
                            AuroraSoft.copy(alpha = 0.6f),
                            AuroraMid,
                            AuroraDeep
                        ),
                        startY = -h * 0.5f + offset,
                        endY = h * 1.5f + offset
                    )
                )

                // Aksen warna accent bergerak
                val accentOffset = cos(t * 0.7f) * w * 0.4f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.08f),
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
                    val alpha = 0.3f + 0.6f * (
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
                val glowScale = 0.95f + 0.15f * sin(t * 0.6f)
                val glowRadius = h * 0.55f * glowScale
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.15f),
                            accentColor.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.5f, h * 0.55f),
                        radius = glowRadius
                    ),
                    radius = glowRadius,
                    center = Offset(w * 0.5f, h * 0.55f)
                )
            }

            // ============ Layer 4: Particles (daun gugur) ============
            if (enableParticles) {
                particles.forEach { p ->
                    // Posisi Y bergerak dari atas ke bawah, loop
                    val yProgress = (time * p.speed + p.startY + 1f) % 1f
                    val baseX = p.x * w
                    val swayX = sin(t * 0.8f + p.startY * 10f) * p.sway * w * 0.1f
                    val x = baseX + swayX
                    val y = yProgress * h

                    // Fade in/out di hujung
                    val alpha = when {
                        yProgress < 0.1f -> yProgress * 10f
                        yProgress > 0.9f -> (1f - yProgress) * 10f
                        else -> 1f
                    }.coerceIn(0f, 1f)

                    // Sway rotation effect (small)
                    drawCircle(
                        color = p.color.copy(alpha = p.color.alpha * alpha),
                        radius = p.size,
                        center = Offset(x, y)
                    )
                }
            }
        }

        content()
    }
}
