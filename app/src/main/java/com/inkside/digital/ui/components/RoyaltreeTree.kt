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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============ Royaltree Tree — Colors ============
private val TrunkDeep = Color(0xFF022C22)      // emerald 950
private val TrunkDark = Color(0xFF064E3B)      // emerald 900
private val TrunkMid = Color(0xFF047857)       // emerald 700
private val TrunkLight = Color(0xFF10B981)     // emerald 500
private val BarkHighlight = Color(0xFF34D399)  // emerald 400 — alur batang

private val LeafDeep = Color(0xFF14532D)       // green 900
private val LeafDark = Color(0xFF15803D)       // green 700
private val LeafMid = Color(0xFF22C55E)        // green 500
private val LeafLight = Color(0xFF34D399)      // emerald 400
private val LeafCyan = Color(0xFF06B6D4)       // cyan 500
private val LeafGold = Color(0xFFFFD700)       // gold

private val RootDark = Color(0xFFB45309)       // amber 700
private val RootAmber = Color(0xFFF59E0B)      // amber 500
private val RootGold = Color(0xFFFFD700)       // gold

private val GlowGold = Color(0x66FFD700)
private val GlowEmerald = Color(0x5034D399)

/**
 * RoyaltreeTree — pohon realistik dengan L-system fractal + bezier natural.
 *
 * Ciri natural:
 * - Batang TAPER (mengecil ke atas) + lekukan organik
 * - Cabang recursive dengan sudut & panjang random (bukan simetris)
 * - Akar menyebar + taper
 * - Texture alur batang (bark lines)
 * - Leaf cluster elliptik (bukan circle polos)
 * - Idle sway halus bertingkat (makin tinggi makin goyang)
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
            animation = tween(4500, easing = LinearEasing),
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

        // ============ 2. Akar menyebar + taper ============
        drawRoots(cx, baseY, w, level, growthProgress, rand)

        // ============ 3. Batang: tinggi, taper, lekuk natural ============
        // Batang lebih tinggi (25-32% tinggi canvas)
        // Level 1-2 (sprout): batang pendek + tebal
        // Level 3+ (tree): batang tinggi + taper
        val isSprout = level <= 2
        val trunkHeightFactor = if (isSprout) 0.10f + 0.03f * level else 0.24f + 0.025f * level
        val trunkHeight = h * trunkHeightFactor * growthProgress
        val trunkTopY = baseY - trunkHeight

        // Lebar pangkal & ujung — TAPER (mengecil ke atas)
        val trunkBaseWidth = if (isSprout) w * (0.055f + 0.008f * level) else w * (0.048f + 0.006f * level)
        val trunkTopWidth = if (isSprout) trunkBaseWidth * 0.55f else trunkBaseWidth * 0.35f

        // Lekukan batang — 2 control points untuk S-curve
        val bend1 = w * 0.018f * (rand.nextFloat() - 0.5f) * 2f
        val bend2 = w * 0.014f * (rand.nextFloat() - 0.5f) * 2f

        // Draw batang sebagai polygon (untuk taper)
        drawTaperedTrunk(
            cx = cx,
            baseY = baseY,
            topY = trunkTopY,
            baseWidth = trunkBaseWidth,
            topWidth = trunkTopWidth,
            bend1 = bend1,
            bend2 = bend2
        )

        // Simpan posisi ujung batang
        val trunkEndX = cx + bend1 * 0.5f + bend2 * 0.5f

        // ============ 4. Texture alur batang (bark lines) ============
        drawBarkLines(cx, baseY, trunkTopY, trunkBaseWidth, growthProgress, rand)

        // ============ 5. Dahan fractal ============
        // Level 1-2: sedikit dahan tapi daun banyak (rimbun mini)
        // Level 3+: dahan banyak, daun penuh
        val branchCount = if (isSprout) 3 else 2 + level
        val branchLength = if (isSprout) trunkHeight * 1.4f else trunkHeight * (0.75f + 0.10f * level)
        val maxDepth = if (isSprout) 3 else 4 + (level / 2)
        val baseAngleStep = if (isSprout) 35f else 22f + level * 2.5f

        for (i in 0 until branchCount) {
            // Sudut tidak simetris — pakai random offset
            val offset = (i - (branchCount - 1) / 2f)
            val jitter = (rand.nextFloat() - 0.5f) * 8f
            val angleDeg = -90f + offset * baseAngleStep + jitter

            drawBranch(
                start = Offset(trunkEndX, trunkTopY),
                angleDeg = angleDeg,
                length = branchLength * (0.85f + rand.nextFloat() * 0.3f),
                depth = 0,
                maxDepth = maxDepth,
                thickness = trunkTopWidth * 0.9f,
                rand = rand,
                sway = sway,
                growthProgress = growthProgress,
                baseY = baseY
            )
        }

        // ============ 6. Bunga emas (level 5) ============
        if (level >= 5) {
            for (i in 0 until 14) {
                val angleRad = rand.nextFloat() * 2f * PI.toFloat()
                val r = branchLength * (0.9f + rand.nextFloat() * 0.5f)
                val fx = trunkEndX + cos(angleRad) * r
                val fy = (trunkTopY - branchLength * 0.65f) + sin(angleRad) * r * 0.75f
                drawCircle(
                    color = RootGold.copy(alpha = 0.9f * growthProgress),
                    radius = 2.5f + rand.nextFloat() * 1.5f,
                    center = Offset(fx, fy)
                )
            }
        }
    }
}

// ============ Draw helpers ============

/**
 * Batang dengan TAPER (polygon) + lekuk natural.
 */
private fun DrawScope.drawTaperedTrunk(
    cx: Float,
    baseY: Float,
    topY: Float,
    baseWidth: Float,
    topWidth: Float,
    bend1: Float,
    bend2: Float
) {
    val halfBaseW = baseWidth / 2f
    val halfTopW = topWidth / 2f
    val midY = (baseY + topY) / 2f

    // S-curve offset
    val midBend = bend1 * 0.6f + bend2 * 0.4f
    val topBend = bend1 * 0.4f + bend2 * 0.6f

    val path = Path().apply {
        // Kiri bawah → kiri tengah → kiri atas
        moveTo(cx - halfBaseW, baseY)
        cubicTo(
            cx - halfBaseW + bend1 * 0.3f, baseY - (baseY - topY) * 0.3f,
            cx - halfTopW + midBend, midY + (baseY - topY) * 0.1f,
            cx + topBend - halfTopW, topY
        )
        // Kanan atas → kanan tengah → kanan bawah
        lineTo(cx + topBend + halfTopW, topY)
        cubicTo(
            cx + halfTopW + midBend, midY + (baseY - topY) * 0.1f,
            cx + halfBaseW + bend1 * 0.3f, baseY - (baseY - topY) * 0.3f,
            cx + halfBaseW, baseY
        )
        close()
    }

    // Gradient horizontal untuk depth (kiri terang, kanan gelap)
    drawPath(
        path = path,
        brush = Brush.horizontalGradient(
            colors = listOf(TrunkMid, TrunkLight, TrunkMid, TrunkDark),
            startX = cx - halfBaseW,
            endX = cx + halfBaseW
        )
    )
}

/**
 * Alur batang (bark lines) — 2-3 garis melengkung natural.
 */
private fun DrawScope.drawBarkLines(
    cx: Float,
    baseY: Float,
    topY: Float,
    trunkWidth: Float,
    growth: Float,
    rand: Random
) {
    val lineCount = 2 + rand.nextInt(2)
    val trunkHeight = baseY - topY

    for (i in 0 until lineCount) {
        val offsetRatio = (i - (lineCount - 1) / 2f) / (lineCount - 1f)  // -1..1
        val xOffset = trunkWidth * 0.22f * offsetRatio
        val curX = cx + xOffset

        val path = Path().apply {
            moveTo(curX, baseY - trunkHeight * 0.05f)
            cubicTo(
                curX + (rand.nextFloat() - 0.5f) * trunkWidth * 0.15f,
                baseY - trunkHeight * 0.35f,
                curX + (rand.nextFloat() - 0.5f) * trunkWidth * 0.15f,
                baseY - trunkHeight * 0.65f,
                curX + (rand.nextFloat() - 0.5f) * trunkWidth * 0.2f,
                topY + trunkHeight * 0.15f
            )
        }
        drawPath(
            path = path,
            color = TrunkDeep.copy(alpha = 0.55f * growth),
            style = Stroke(width = trunkWidth * 0.06f, cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawBranch(
    start: Offset,
    angleDeg: Float,
    length: Float,
    depth: Int,
    maxDepth: Int,
    thickness: Float,
    rand: Random,
    sway: Float,
    growthProgress: Float,
    baseY: Float
) {
    // Berhenti → lukis daun
    if (depth > maxDepth || length < 4f || thickness < 0.6f) {
        drawLeafCluster(start, depth, rand, growthProgress)
        return
    }

    // Sway lebih besar di dahan tinggi (natural — angin menggerakkan dahan atas)
    val heightRatio = ((baseY - start.y) / baseY).coerceIn(0f, 1f)
    val swayOffset = sway * (0.5f + depth * 0.8f) * (0.5f + heightRatio)

    val angleRad = (angleDeg + swayOffset) * PI.toFloat() / 180f
    val end = Offset(
        x = start.x + cos(angleRad) * length,
        y = start.y + sin(angleRad) * length
    )

    // Warna dahan
    val branchColor = when {
        depth == 0 -> TrunkMid
        depth == 1 -> TrunkLight
        depth == 2 -> LeafDark
        else -> LeafDeep
    }

    // Curve natural — cabang bukan straight
    val curveBend = length * 0.20f * (rand.nextFloat() - 0.5f) * 2f
    val perpAngleRad = angleRad + PI.toFloat() / 2f
    val midX = (start.x + end.x) / 2f + cos(perpAngleRad) * curveBend
    val midY = (start.y + end.y) / 2f + sin(perpAngleRad) * curveBend

    val path = Path().apply {
        moveTo(start.x, start.y)
        quadraticTo(midX, midY, end.x, end.y)
    }

    // Taper: batang lebih tebal di pangkal, nipis di ujung
    drawPath(
        path = path,
        color = branchColor,
        style = Stroke(width = thickness, cap = StrokeCap.Round)
    )

    // Rekursif — 2-3 cabang dengan sudut & panjang random
    val nextThickness = thickness * 0.62f
    val nextLength = length * (0.65f + rand.nextFloat() * 0.15f)

    // Cabang kiri
    val spreadLeft = 18f + rand.nextFloat() * 18f
    drawBranch(
        start = end,
        angleDeg = angleDeg - spreadLeft,
        length = nextLength * (0.9f + rand.nextFloat() * 0.2f),
        depth = depth + 1,
        maxDepth = maxDepth,
        thickness = nextThickness,
        rand = rand,
        sway = sway,
        growthProgress = growthProgress,
        baseY = baseY
    )

    // Cabang kanan
    val spreadRight = 20f + rand.nextFloat() * 20f
    drawBranch(
        start = end,
        angleDeg = angleDeg + spreadRight,
        length = nextLength * (0.9f + rand.nextFloat() * 0.2f),
        depth = depth + 1,
        maxDepth = maxDepth,
        thickness = nextThickness,
        rand = rand,
        sway = sway,
        growthProgress = growthProgress,
        baseY = baseY
    )

    // Cabang tengah (kalau dalam) — bikin lebih padat
    if (depth >= 2 && rand.nextFloat() < 0.4f) {
        drawBranch(
            start = end,
            angleDeg = angleDeg + (rand.nextFloat() - 0.5f) * 10f,
            length = nextLength * 0.85f,
            depth = depth + 1,
            maxDepth = maxDepth,
            thickness = nextThickness * 0.85f,
            rand = rand,
            sway = sway,
            growthProgress = growthProgress,
            baseY = baseY
        )
    }
}

/**
 * Leaf cluster elliptik — bukan circle polos.
 * Setiap daun pakai ellipse yang di-rotate.
 */
private fun DrawScope.drawLeafCluster(
    center: Offset,
    depth: Int,
    rand: Random,
    growthProgress: Float
) {
    val leafCount = 6 + rand.nextInt(5)   // 6..10 daun
    val clusterRadius = 10f + rand.nextFloat() * 8f

    for (i in 0 until leafCount) {
        val angle = rand.nextFloat() * 2f * PI.toFloat()
        val r = rand.nextFloat() * clusterRadius
        val lx = center.x + cos(angle) * r
        val ly = center.y + sin(angle) * r

        // Daun elliptik (bukan circle)
        val leafW = 3.5f + rand.nextFloat() * 3f
        val leafH = leafW * (1.4f + rand.nextFloat() * 0.4f)  // lebih tinggi dari lebar
        val rotation = (rand.nextFloat() - 0.5f) * 90f

        val color = when {
            rand.nextFloat() > 0.93f -> LeafGold
            rand.nextFloat() > 0.82f -> LeafCyan
            rand.nextFloat() > 0.55f -> LeafLight
            rand.nextFloat() > 0.28f -> LeafMid
            else -> LeafDark
        }

        rotate(degrees = rotation, pivot = Offset(lx, ly)) {
            drawOval(
                color = color.copy(alpha = 0.88f * growthProgress),
                topLeft = Offset(lx - leafW / 2f, ly - leafH / 2f),
                size = Size(leafW, leafH)
            )
        }
    }
}

/**
 * Akar menyebar + taper (melebar keluar + curve natural).
 */
private fun DrawScope.drawRoots(
    cx: Float,
    baseY: Float,
    w: Float,
    level: Int,
    growth: Float,
    rand: Random
) {
    // Level 1-2: akar MINI (tanam di tanah, tidak menggantung)
    val isSprout = level <= 2
    val rootCount = if (isSprout) 3 else 3 + level / 2
    val rootLen = if (isSprout) w * 0.04f * growth else w * 0.08f * (1f + level * 0.15f) * growth

    for (i in 0 until rootCount) {
        val offset = (i - (rootCount - 1) / 2f) * (w * 0.05f)
        val angle = offset / w * 1.5f  // sudut menyebar

        // Taper: tebal di pangkal, nipis di ujung
        val baseThickness = w * 0.016f
        val tipThickness = w * 0.004f

        // Bezier — level 1-2 akar horizontal (tanam), level 3+ turun & keluar
        val endX = cx + offset * (if (isSprout) 1.6f else 1f)
        val endY = if (isSprout) {
            baseY + rootLen * 0.15f  // hanya sedikit turun — tanam di tanah
        } else {
            baseY + rootLen * (0.85f + rand.nextFloat() * 0.3f)
        }

        // Draw sebagai polygon (taper)
        val path = Path().apply {
            moveTo(cx - baseThickness / 2f, baseY)
            cubicTo(
                cx - baseThickness * 0.4f + offset * 0.3f, baseY + rootLen * 0.4f,
                endX - tipThickness * 0.8f, endY - rootLen * 0.2f,
                endX - tipThickness / 2f, endY
            )
            lineTo(endX + tipThickness / 2f, endY)
            cubicTo(
                endX + tipThickness * 0.8f, endY - rootLen * 0.2f,
                cx + baseThickness * 0.4f + offset * 0.3f, baseY + rootLen * 0.4f,
                cx + baseThickness / 2f, baseY
            )
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(RootAmber, RootDark),
                startY = baseY,
                endY = endY
            )
        )

        // Highlight emas di ujung akar
        drawCircle(
            color = RootGold.copy(alpha = 0.7f * growth),
            radius = tipThickness * 0.6f,
            center = Offset(endX, endY)
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
            level = 5,
            growthProgress = 1f,
            modifier = Modifier.fillMaxSize()
        )
    }
}
