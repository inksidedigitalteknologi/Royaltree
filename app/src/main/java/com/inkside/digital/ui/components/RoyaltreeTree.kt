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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

// ============ Royaltree Tree — Colors ============
private val TrunkDark = Color(0xFF064E3B)      // emerald 900
private val TrunkMid = Color(0xFF047857)       // emerald 700
private val TrunkLight = Color(0xFF10B981)     // emerald 500
private val LeafDark = Color(0xFF15803D)       // green 700
private val LeafMid = Color(0xFF22C55E)        // green 500
private val LeafLight = Color(0xFF34D399)      // emerald 400
private val LeafCyan = Color(0xFF06B6D4)       // cyan 500
private val LeafGold = Color(0xFFFFD700)       // gold
private val RootGold = Color(0xFFFFD700)
private val RootAmber = Color(0xFFF59E0B)
private val GlowGold = Color(0x55FFD700)
private val GlowEmerald = Color(0x4034D399)

/**
 * RoyaltreeTree — pohon generatif realistik (Royaltree theme).
 *
 * Ciri:
 * - L-system fractal (bukan rawak)
 * - Batang emerald gradient tebal
 * - Dahan bercabang rapat
 * - Daun berkelompok di hujung (hijau + cyan + emas)
 * - Glow emas radial di belakang
 * - Akar emas menonjol
 * - Idle goyang halus
 *
 * @param level 1..5 (tahap pertumbuhan)
 * @param growthProgress 0..1 (animasi tumbuh)
 */
@Composable
fun RoyaltreeTree(
    level: Int = 3,
    growthProgress: Float = 1f,
    modifier: Modifier = Modifier
) {
    // Idle sway
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

        val rand = Random(seed = level * 17 + 13)

        // ============ 1. Glow belakang ============
        val glowRadius = h * 0.55f * (0.85f + 0.15f * growthProgress)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    GlowGold.copy(alpha = GlowGold.alpha * growthProgress),
                    GlowEmerald.copy(alpha = GlowEmerald.alpha * growthProgress),
                    Color.Transparent
                ),
                center = Offset(cx, h * 0.40f),
                radius = glowRadius
            ),
            radius = glowRadius,
            center = Offset(cx, h * 0.40f)
        )

        // ============ 2. Akar gold ============
        drawRoots(cx, baseY, w, level, growthProgress)

        // ============ 3. Batang (tebal, pendek) ============
        val trunkHeight = h * (0.12f + 0.020f * level) * growthProgress
        val trunkTopY = baseY - trunkHeight
        val trunkWidth = w * (0.035f + 0.006f * level)

        // Batang: gradient emerald dark -> light (MELENGKUNG natural)
        // Guna cubicTo untuk lekuk seperti pohon sungguhan
        val trunkBend = w * 0.025f * (1f + rand.nextFloat() * 0.4f)  // lekuk ke kanan sikit
        val trunkPath = Path().apply {
            moveTo(cx, baseY)
            cubicTo(
                cx - trunkBend * 0.3f, baseY - trunkHeight * 0.35f,   // control 1 (condong kiri sikit)
                cx + trunkBend * 1.2f, trunkTopY + trunkHeight * 0.25f, // control 2 (condong kanan)
                cx + trunkBend * 0.6f, trunkTopY                        // end (off-center)
            )
        }
        drawPath(
            path = trunkPath,
            brush = Brush.verticalGradient(
                colors = listOf(TrunkLight, TrunkMid, TrunkDark),
                startY = trunkTopY,
                endY = baseY
            ),
            style = Stroke(width = trunkWidth, cap = StrokeCap.Round)
        )
        // Simpan titik hujung batang untuk cabang
        val trunkEndX = cx + trunkBend * 0.6f

        // ============ 4. Dahan fractal (L-system) ============
        val branchCount = 2 + level              // 3..7
        val branchLength = trunkHeight * (0.65f + 0.10f * level)
        val maxDepth = 3 + (level / 2)           // 3..5

        for (i in 0 until branchCount) {
            val spreadDeg = 15f + level * 2f
            val angleDeg = -90f + ((i - (branchCount - 1) / 2f) * spreadDeg)
            drawBranch(
                start = Offset(trunkEndX, trunkTopY),
                angleDeg = angleDeg,
                length = branchLength,
                depth = 0,
                maxDepth = maxDepth,
                thickness = trunkWidth * 0.58f,
                rand = rand,
                sway = sway,
                growthProgress = growthProgress
            )
        }

        // ============ 5. Bunga emas di level 5 ============
        if (level >= 5) {
            for (i in 0 until 10) {
                val angleRad = rand.nextFloat() * 2f * PI.toFloat()
                val r = branchLength * (0.8f + rand.nextFloat() * 0.4f)
                val fx = cx + cos(angleRad) * r
                val fy = (trunkTopY - branchLength * 0.7f) + sin(angleRad) * r * 0.7f
                drawCircle(
                    color = RootGold.copy(alpha = 0.9f * growthProgress),
                    radius = 3f,
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
    sway: Float,
    growthProgress: Float
) {
    // Hujung dahan → lukis DAUN berkelompok
    if (depth > maxDepth || length < 5f) {
        drawLeafCluster(start, depth, rand, growthProgress)
        return
    }

    // Arah dengan sway halus
    val swayOffset = sway * (0.8f + depth * 0.4f)
    val angleRad = (angleDeg + swayOffset) * PI.toFloat() / 180f
    val end = Offset(
        x = start.x + cos(angleRad) * length,
        y = start.y + sin(angleRad) * length
    )

    // Warna dahan: makin tinggi makin cerah
    val branchColor = when {
        depth == 0 -> TrunkMid
        depth == 1 -> TrunkLight
        else -> LeafDark
    }

    // Bezier curve — cabang melengkung natural, bukan straight
    val curveOffset = length * 0.18f * (rand.nextFloat() - 0.5f)  // -0.09 to +0.09
    val midX = (start.x + end.x) / 2f + curveOffset
    val midY = (start.y + end.y) / 2f + curveOffset * 0.5f
    val branchPath = Path().apply {
        moveTo(start.x, start.y)
        quadraticBezierTo(midX, midY, end.x, end.y)
    }
    drawPath(
        path = branchPath,
        color = branchColor,
        style = Stroke(width = thickness, cap = StrokeCap.Round)
    )

    // Rekursif — 2 cabang TAK SIMETRI (panjang + sudut berbeza)
    val nextThickness = thickness * 0.62f
    val spreadLeft = 18f + rand.nextFloat() * 14f    // 18-32°
    val spreadRight = 20f + rand.nextFloat() * 16f   // 20-36°
    val lenLeft = length * (0.60f + rand.nextFloat() * 0.15f)   // 0.60-0.75
    val lenRight = length * (0.62f + rand.nextFloat() * 0.18f)  // 0.62-0.80

    drawBranch(
        start = end,
        angleDeg = angleDeg - spreadLeft,
        length = lenLeft,
        depth = depth + 1,
        maxDepth = maxDepth,
        thickness = nextThickness,
        rand = rand,
        sway = sway,
        growthProgress = growthProgress
    )
    drawBranch(
        start = end,
        angleDeg = angleDeg + spreadRight,
        length = lenRight,
        depth = depth + 1,
        maxDepth = maxDepth,
        thickness = nextThickness,
        rand = rand,
        sway = sway,
        growthProgress = growthProgress
    )
}

private fun DrawScope.drawLeafCluster(
    center: Offset,
    depth: Int,
    rand: Random,
    growthProgress: Float
) {
    val leafCount = 5 + rand.nextInt(5)   // 5..9 daun
    val clusterRadius = 8f + rand.nextFloat() * 6f

    for (i in 0 until leafCount) {
        val angle = rand.nextFloat() * 2f * PI.toFloat()
        val r = rand.nextFloat() * clusterRadius
        val lx = center.x + cos(angle) * r
        val ly = center.y + sin(angle) * r
        val radius = 3f + rand.nextFloat() * 3f

        // Warna: leaf + occasional cyan + occasional gold
        val roll = rand.nextFloat()
        val color = when {
            roll > 0.92f -> LeafGold
            roll > 0.80f -> LeafCyan
            roll > 0.55f -> LeafLight
            roll > 0.30f -> LeafMid
            else -> LeafDark
        }

        drawCircle(
            color = color.copy(alpha = 0.85f * growthProgress),
            radius = radius,
            center = Offset(lx, ly)
        )
    }
}

private fun DrawScope.drawRoots(
    cx: Float,
    baseY: Float,
    w: Float,
    level: Int,
    growth: Float
) {
    val rootCount = 3 + level / 2
    val rootLen = w * 0.05f * (1f + level * 0.12f) * growth
    val thickness = w * 0.014f

    for (i in 0 until rootCount) {
        val offset = (i - (rootCount - 1) / 2f) * (w * 0.045f)
        val endX = cx + offset
        val endY = baseY + rootLen

        // Akar melengkung keluar (bukan straight)
        val rootCurve = (endX - cx) * 0.3f
        val rootPath = Path().apply {
            moveTo(cx, baseY)
            cubicTo(
                cx + (endX - cx) * 0.2f, baseY + rootLen * 0.3f,
                cx + (endX - cx) * 0.8f, baseY + rootLen * 0.5f,
                endX, endY
            )
        }
        drawPath(
            path = rootPath,
            brush = Brush.verticalGradient(
                colors = listOf(RootAmber, RootGold),
                startY = baseY,
                endY = endY
            ),
            style = Stroke(width = thickness, cap = StrokeCap.Round)
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
