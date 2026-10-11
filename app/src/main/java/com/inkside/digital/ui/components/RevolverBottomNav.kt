package com.inkside.digital.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.inkside.digital.viewmodel.AppScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// ==== DESIGN SYSTEM COLORS ====
private val SurfaceDark = Color(0xFF0F172A)
private val SurfaceDeep = Color(0xFF020617)
private val EmeraldPrimary = Color(0xFF10B981)
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldDark = Color(0xFF047857)
private val EmeraldDeep = Color(0xFF064E3B)
private val GoldVip = Color(0xFFFBBF24)
private val GoldBright = Color(0xFFFDE68A)
private val GoldAmber = Color(0xFFF59E0B)
private val GoldDark = Color(0xFFB45309)
private val ChromeDark = Color(0xFF475569)
private val MetalDeep = Color(0xFF1E293B)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFCBD5E1)

data class RevolverNavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun RevolverBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
    isAdminMode: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val items = remember(isAdminMode) {
        if (isAdminMode) {
            listOf(
                RevolverNavItem(AppScreen.ADMIN, "Admin", Icons.Filled.Settings),
                RevolverNavItem(AppScreen.HOME, "Home", Icons.Filled.Home),
                RevolverNavItem(AppScreen.MISSIONS, "Missions", Icons.Filled.Star),
                RevolverNavItem(AppScreen.GAME_ROOM, "Game", Icons.Filled.PlayArrow),
                RevolverNavItem(AppScreen.PROFILE, "Profile", Icons.Filled.AccountCircle)
            )
        } else {
            listOf(
                RevolverNavItem(AppScreen.GAME_MINING, "Miner", Icons.Filled.PlayArrow),
                RevolverNavItem(AppScreen.GAME_ROOM, "Game", Icons.Filled.PlayArrow),
                RevolverNavItem(AppScreen.PROFILE, "Profile", Icons.Filled.AccountCircle),
                RevolverNavItem(AppScreen.HOME, "Home", Icons.Filled.Home),
                RevolverNavItem(AppScreen.MISSIONS, "Missions", Icons.Filled.Star)
            )
        }
    }

    var isExpanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val rotationAnim = remember { Animatable(0f) }
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }

    val totalIcons = items.size
    val stepAngle = 360f / totalIcons

    fun indexToBaseAngle(index: Int): Float = index * stepAngle

    fun nearestIndexForRotation(rot: Float): Int {
        var best = 0
        var bestDist = Float.MAX_VALUE
        for (i in 0 until totalIcons) {
            val angle = ((indexToBaseAngle(i) + rot) % 360f + 360f) % 360f
            val norm = if (angle > 180f) angle - 360f else angle
            val dist = abs(norm)
            if (dist < bestDist) {
                bestDist = dist
                best = i
            }
        }
        return best
    }

    LaunchedEffect(currentScreen, isExpanded) {
        if (!isExpanded) {
            val idx = items.indexOfFirst { it.screen == currentScreen }
            if (idx >= 0) {
                selectedIndex = idx
                rotationAnim.snapTo(-indexToBaseAngle(idx))
            }
        }
    }

    LaunchedEffect(rotationAnim.value) {
        if (isExpanded) {
            val newIdx = nearestIndexForRotation(rotationAnim.value)
            if (newIdx != selectedIndex) {
                selectedIndex = newIdx
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    val containerHeight = 180.dp
    val centerSize = 64.dp
    val centerGlow = 88.dp
    val orbitRadius = 96.dp

    Box(modifier = modifier.fillMaxSize()) {
        // ==== OVERLAY GELAP ====
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isExpanded = false
                            }
                        )
                    }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.BottomCenter
        ) {
            // ==== CHAMBER ARC ====
            if (isExpanded) {
                Canvas(
                    modifier = Modifier
                        .size(orbitRadius * 2 + 60.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    val radius = size.minDimension / 2

                    drawCircle(
                        color = EmeraldPrimary.copy(alpha = 0.08f),
                        radius = radius,
                        style = Stroke(width = 24.dp.toPx())
                    )

                    drawCircle(
                        color = EmeraldPrimary.copy(alpha = 0.35f),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawCircle(
                        color = EmeraldLight.copy(alpha = 0.15f),
                        radius = radius - 6.dp.toPx(),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    drawCircle(
                        color = EmeraldPrimary.copy(alpha = 0.15f),
                        radius = radius + 10.dp.toPx(),
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 14f), 0f)
                        )
                    )
                }
            }

            // ==== ORBIT ICONS ====
            if (isExpanded) {
                items.forEachIndexed { index, item ->
                    val baseAngle = indexToBaseAngle(index)
                    val currentAngle = baseAngle + rotationAnim.value
                    val isSelected = index == selectedIndex

                    val iconSize = if (isSelected) 56.dp else 44.dp

                    val rad = (currentAngle - 90f) * PI.toFloat() / 180f
                    val xPx = orbitRadius.value * cos(rad)
                    val yPx = orbitRadius.value * sin(rad)

                    // Glow radial gradient
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 24.dp)
                                .offset(x = xPx.dp, y = yPx.dp)
                                .size(iconSize + 28.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            EmeraldPrimary.copy(alpha = 0.55f),
                                            EmeraldPrimary.copy(alpha = 0.25f),
                                            Color.Transparent
                                        )
                                    ),
                                    CircleShape
                                )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .offset(x = xPx.dp, y = yPx.dp)
                            .size(iconSize)
                            .scale(if (isSelected) 1.1f else 1f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = if (isSelected) {
                                        listOf(EmeraldDeep, EmeraldDark)
                                    } else {
                                        listOf(MetalDeep, SurfaceDeep)
                                    }
                                )
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) EmeraldPrimary else ChromeDark,
                                shape = CircleShape
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    rotationAnim.animateTo(
                                        targetValue = -baseAngle,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                    delay(250L)
                                    onNavigate(item.screen)
                                    isExpanded = false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) TextPrimary else TextSecondary,
                            modifier = Modifier.size(if (isSelected) 28.dp else 22.dp)
                        )
                    }
                }
            }

            // ==== CENTER BUTTON — Layer 1: Glow ====
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .size(centerGlow)
                    .scale(if (isExpanded) 1.05f else 1f)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GoldVip.copy(alpha = 0.65f),
                                GoldAmber.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    )
            )

            // ==== CENTER BUTTON — Layer 2: Main ====
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .size(centerSize * 0.85f)
                    .scale(if (isExpanded) 1.05f else 1f)
                    .shadow(
                        elevation = 12.dp,
                        shape = CircleShape,
                        ambientColor = GoldAmber,
                        spotColor = GoldAmber
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                GoldBright,
                                GoldVip,
                                GoldAmber,
                                GoldDark
                            ),
                            center = Offset(0.3f, 0.3f),
                            radius = 120f
                        )
                    )
                    .border(
                        width = 2.dp,
                        color = GoldDark,
                        shape = CircleShape
                    )
                    .pointerInput(isExpanded) {
                        detectDragGestures(
                            onDragStart = {
                                if (!isExpanded) {
                                    isExpanded = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                accumulatedDrag = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                accumulatedDrag += dragAmount.x
                                scope.launch {
                                    rotationAnim.snapTo(rotationAnim.value + dragAmount.x * 0.7f)
                                }
                            },
                            onDragEnd = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                scope.launch {
                                    val targetBase = indexToBaseAngle(selectedIndex)
                                    val currentRot = rotationAnim.value
                                    var bestRot = currentRot
                                    var bestDist = Float.MAX_VALUE
                                    for (k in -2..2) {
                                        val candidate = -targetBase + k * 360f
                                        val d = abs(candidate - currentRot)
                                        if (d < bestDist) {
                                            bestDist = d
                                            bestRot = candidate
                                        }
                                    }
                                    rotationAnim.animateTo(
                                        targetValue = bestRot,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                    delay(300L)
                                    onNavigate(items[selectedIndex].screen)
                                    isExpanded = false
                                }
                            }
                        )
                    }
                    .pointerInput(isExpanded) {
                        detectTapGestures(
                            onTap = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                if (isExpanded) {
                                    scope.launch {
                                        delay(150L)
                                        onNavigate(items[selectedIndex].screen)
                                        isExpanded = false
                                    }
                                } else {
                                    isExpanded = true
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(centerSize - 12.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent
                                ),
                                center = Offset(0.3f, 0.3f),
                                radius = 80f
                            )
                        )
                )
                Image(
                    painter = painterResource(id = com.inkside.digital.R.drawable.rtp_coin),
                    contentDescription = "RTP",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
