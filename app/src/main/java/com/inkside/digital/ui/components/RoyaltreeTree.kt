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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============ Royaltree Tree — Colors ============
private val EmeraldDark = Color(0xFF047857)
private val EmeraldMid = Color(0xFF10B981)
private val EmeraldLight = Color(0xFF34D399)
private val ElectricCyan = Color(0xFF06B6D4)
private val CyanLight = Color(0xFF67E8F9)
private val RootGold = Color(0xFFFFD700)
private val RootAmber = Color(0xFFF59E0B)
private val GlowEmerald = Color(0x6634D399)

/**
 * RoyaltreeTree — generative fractal tree, menggantikan loading orb.
 *
 * @param level          1..5 (growth stage)
 * @param growthProgress 0..1 (animasi tumbuh — kalau 1, tunjuk penuh)
 * @param accentColor    warna aksen (default emerald/cyan)
 */
@Composable
fun RoyaltreeTree(
    level: Int = 3,
    growthProgress: Float = 1f,
    accentColor: Color = EmeraldMid,
    modifier: Modifier = Modifier
) {
    // Idle animation — goyang lembut
    val infinite = rememberInfiniteTransition(label = "tree")
    val sway by infinite.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val baseY = h * 0.92f

        // Seed tetap supaya pohon tidak berubah tiap recompose
        val rand = Random(seed = level * 7 + 13)

        // ---------- 1. Glow belakang ----------
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlowEmerald.copy(alpha = 0.35f * growthProgress),
                    Color.Transparent
                ),
                center = Offset(cx, h * 0.55f),
                radius = h * 0.45f
            ),
            radius = h * 0.45f,
            center = Offset(cx, h * 0.55f)
        )

        // ---------- 2. Akar (gold) ----------
        drawRoots(cx, baseY, w, level, growthProgress)

        // ---------- 3. Batang ----------
        val trunkHeight = h * (0.25f + 0.04f * level) * growthProgress
        val trunkTopY = baseY - trunkHeight
        val trunkWidth = w * (0.018f + 0.004f * level)

        // Batang: gradient emerald dark → light
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

        // ---------- 4. Dahan fractal ----------
        val branchCount = 2 + level         // level 1: 3, level 5: 7
        val branchLength = trunkHeight * (0.45f + 0.05f * level)
        for (i in 0 until branchCount) {
            val angleDeg = -90f + ((i - (branchCount - 1) / 2f) * (55f / branchCount)) + sway * 2f
            drawBranch(
                start = Offset(cx, trunkTopY),
                angleDeg = angleDeg,
                length = branchLength,
                depth = 0,
                maxDepth = minOf(level, 4),
                thickness = trunkWidth * 0.6f,
                rand = rand,
                sway = sway
            )
        }

        // ---------- 5. Daun (particles di hujung dahan) ----------
        val leafCount = 8 + level * 4
        for (i in 0 until leafCount) {
            val angleRad = rand.nextFloat() * 2f * PI.toFloat()
            val r = trunkHeight * (0.55f + rand.nextFloat() * 0.45f)
            val lx = cx + cos(angleRad) * r * 0.85f
            val ly = (trunkTopY - trunkHeight * 0.35f) + sin(angleRad) * r * 0.55f
            val radius = (3f + rand.nextFloat() * 4f) * (1f + level * 0.15f)
            val color = if (rand.nextFloat() > 0.7f) CyanLight else EmeraldLight
            drawCircle(
                color = color.copy(alpha = 0.85f * growthProgress),
                radius = radius,
                center = Offset(lx, ly)
            )
        }

        // ---------- 6. Bunga emas kalau level 5 ----------
        if (level >= 5) {
            for (i in 0 until 6) {
                val angleRad = rand.nextFloat() * 2f * PI.toFloat()
                val r = trunkHeight * (0.6f + rand.nextFloat() * 0.3f)
                val fx = cx + cos(angleRad) * r * 0.7f
                val fy = (trunkTopY - trunkHeight * 0.3f) + sin(angleRad) * r * 0.5f
                drawCircle(
                    color = RootGold.copy(alpha = 0.9f),
                    radius = 3.5f,
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
    if (depth > maxDepth || length < 4f) return

    val angleRad = (angleDeg + sway * 2f) * PI.toFloat() / 180f
    val end = Offset(
        x = start.x + cos(angleRad) * length,
        y = start.y + sin(angleRad) * length
    )

    // Warna dahan: makin tinggi makin cerah
    val branchColor = when {
        depth == 0 -> EmeraldDark
        depth == 1 -> EmeraldMid
        else -> EmeraldLight
    }

    drawLine(
        color = branchColor,
        start = start,
        end = end,
        strokeWidth = thickness,
        cap = StrokeCap.Round
    )

    // Rekursif — 2 cabang
    val nextLength = length * 0.72f
    val nextThickness = thickness * 0.65f
    val spread = 28f + rand.nextFloat() * 8f

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
    val rootLen = w * 0.05f * (1f + level * 0.1f) * growth
    val thickness = w * 0.012f

    for (i in 0 until rootCount) {
        val offset = (i - (rootCount - 1) / 2f) * (w * 0.045f)
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
