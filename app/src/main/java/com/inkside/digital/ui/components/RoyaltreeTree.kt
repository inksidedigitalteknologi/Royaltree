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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============ Royaltree Tree — Colors ============
private val EmeraldDark = Color(0xFF047857)
private val EmeraldMid = Color(0xFF10B981)
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldBright = Color(0xFF6EE7B7)
private val ElectricCyan = Color(0xFF06B6D4)
private val CyanLight = Color(0xFF67E8F9)
private val RootGold = Color(0xFFFFD700)
private val RootAmber = Color(0xFFF59E0B)
private val GlowEmerald = Color(0x6634D399)

/**
 * RoyaltreeTree — generative fractal tree.
 *
 * @param level          1..5 (growth stage)
 * @param growthProgress 0..1 (animasi tumbuh)
 */
@Composable
fun RoyaltreeTree(
    level: Int = 3,
    growthProgress: Float = 1f,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "tree")
    val sway by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val baseY = h * 0.90f

        val rand = Random(seed = level * 7 + 13)

        // ---------- 1. Glow belakang ----------
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlowEmerald.copy(alpha = 0.30f * growthProgress),
                    Color.Transparent
                ),
                center = Offset(cx, h * 0.40f),
                radius = h * 0.40f
            ),
            radius = h * 0.40f,
            center = Offset(cx, h * 0.40f)
        )

        // ---------- 2. Akar (gold) ----------
        drawRoots(cx, baseY, w, level, growthProgress)

        // ---------- 3. Batang ----------
        // Batang LEBIH PENDEK dari sebelum — 0.15..0.22 (dulu 0.25..0.45)
        val trunkHeight = h * (0.15f + 0.018f * level) * growthProgress
        val trunkTopY = baseY - trunkHeight
        // Batang LEBIH TEBAL
        val trunkWidth = w * (0.022f + 0.005f * level)

        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(EmeraldMid, EmeraldDark),
                startY = trunkTopY,
                endY = baseY
            ),
            start = Offset(cx, baseY),
            end = Offset(cx, trunkTopY),
            strokeWidth = trunkWidth,
            cap = StrokeCap.Round
        )

        // ---------- 4. Dahan fractal (rapat & pendek) ----------
        // Dahan dari 3 titik di atas batang supaya nampak macam kanopi
        val branchCount = 2 + level
        val branchLength = trunkHeight * (0.65f + 0.08f * level)  // LEBIH PENDEK dari dulu
        for (i in 0 until branchCount) {
            val spreadDeg = 18f + level * 3f  // lebih rapat
            val angleDeg = -90f + ((i - (branchCount - 1) / 2f) * spreadDeg) + sway * 1.5f
            drawBranch(
                start = Offset(cx, trunkTopY),
                angleDeg = angleDeg,
                length = branchLength,
                depth = 0,
                maxDepth = 3 + level / 2,     // 3..5 tingkat
                thickness = trunkWidth * 0.55f,
                rand = rand,
                sway = sway
            )
        }

        // ---------- 5. Bunga emas kalau level 5 ----------
        if (level >= 5) {
            for (i in 0 until 8) {
                val angleRad = rand.nextFloat() * 2f * PI.toFloat()
                val r = branchLength * (0.7f + rand.nextFloat() * 0.4f)
                val fx = cx + cos(angleRad) * r
                val fy = (trunkTopY - branchLength * 0.6f) + sin(angleRad) * r * 0.7f
                drawCircle(
                    color = RootGold.copy(alpha = 0.95f),
                    radius = 4f,
                    center = Offset(fx, fy)
                )
            }
        }
    }
}

// ============ Draw helpers ============

private fun DrawScope.drawBranch(
    start: Offset,
    angleDeg: Float,
    length: Float,
    depth: Int,
    maxDepth: Int,
    thickness: Float,
    rand: Random,
    sway: Float
) {
    if (depth > maxDepth || length < 6f) {
        // ---- HUJUNG DAHAN: lukis DAUN ----
        // Setiap hujung = kelompok daun
        val leafCount = 4 + rand.nextInt(4)   // 4..7 daun
        for (i in 0 until leafCount) {
            val angle = rand.nextFloat() * 2f * PI.toFloat()
            val r = 6f + rand.nextFloat() * 12f
            val lx = start.x + cos(angle) * r
            val ly = start.y + sin(angle) * r
            val radius = 4f + rand.nextFloat() * 4f
            val color = when {
                rand.nextFloat() > 0.75f -> CyanLight
                rand.nextFloat() > 0.5f -> EmeraldBright
                else -> EmeraldLight
            }
            drawCircle(
                color = color.copy(alpha = 0.90f),
                radius = radius,
                center = Offset(lx, ly)
            )
        }
        return
    }

    // Arah dahan dengan sway
    val swayOffset = sway * (1.5f + depth * 0.5f)
    val angleRad = (angleDeg + swayOffset) * PI.toFloat() / 180f
    val end = Offset(
        x = start.x + cos(angleRad) * length,
        y = start.y + sin(angleRad) * length
    )

    val branchColor = when {
        depth == 0 -> EmeraldMid
        depth == 1 -> EmeraldLight
        else -> EmeraldBright
    }

    drawLine(
        color = branchColor,
        start = start,
        end = end,
        strokeWidth = thickness,
        cap = StrokeCap.Round
    )

    // Rekursif — 2 cabang
    val nextLength = length * 0.68f
    val nextThickness = thickness * 0.60f
    val spread = 22f + rand.nextFloat() * 10f

    drawBranch(
        start = end,
        angleDeg = angleDeg - spread,
        length = nextLength,
        depth = depth + 1,
        maxDepth = maxDepth,
        thickness = nextThickness,
        rand = rand,
        sway = sway
    )
    drawBranch(
        start = end,
        angleDeg = angleDeg + spread,
        length = nextLength,
        depth = depth + 1,
        maxDepth = maxDepth,
        thickness = nextThickness,
        rand = rand,
        sway = sway
    )
}

private fun DrawScope.drawRoots(
    cx: Float,
    baseY: Float,
    w: Float,
    level: Int,
    growth: Float
) {
    val rootCount = 3 + level / 2
    val rootLen = w * 0.04f * (1f + level * 0.1f) * growth
    val thickness = w * 0.010f

    for (i in 0 until rootCount) {
        val offset = (i - (rootCount - 1) / 2f) * (w * 0.040f)
        val endX = cx + offset
        val endY = baseY + rootLen

        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(RootAmber, RootGold),
                startY = baseY,
                endY = endY
            ),
            start = Offset(cx, baseY),
            end = Offset(endX, endY),
            strokeWidth = thickness,
            cap = StrokeCap.Round
        )
    }
}

// ============ Preview ============
@Preview(showBackground = true, backgroundColor = 0xFF05060A)
@Composable
private fun RoyaltreeTreePreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05060A))
    ) {
        RoyaltreeTree(
            level = 3,
            growthProgress = 1f,
            modifier = Modifier.fillMaxSize()
        )
    }
}
