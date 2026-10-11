package com.inkside.digital.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private val ChromeLight = Color(0xFFE2E8F0)
private val Chrome = Color(0xFF94A3B8)
private val ChromeDark = Color(0xFF475569)
private val MetalDeep = Color(0xFF1E293B)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFCBD5E1)
private val TextMuted = Color(0xFF64748B)

data class RevolverNavItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)

/**
 * Revolver Bottom Nav — Premium Royal Sci-Fi HUD
 *
 * UX:
 * - Tap center → expand + overlay gelap
 * - Drag center → rotate chamber
 * - Tap outside → dismiss
 * - Tap icon → navigate
 */
@Composable
fun RevolverBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
    isAdminMode: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // 5 menu — Miner di top center (paling visual)
    val items = remember(isAdminMode) {
        if (isAdminMode) {
            listOf(
                RevolverNavItem(AppScreen.ADMIN, "Admin", Icons.Filled.Settings),
                RevolverNavItem(AppScreen.HOME, "Home", Icons.Filled.Home),
                RevolverNavItem(AppScreen.MISSIONS, "Missions", Icons.Filled.Star),
                RevolverNavItem(AppScreen.GAME_ROOM, "Game", Icons.Filled.PlayArrow),
                RevolverNavItem(AppScreen.PROFILE, "Profile", Icons.Filled.Person)
            )
        } else {
            listOf(
                RevolverNavItem(AppScreen.GAME_MINING, "Miner", Icons.Filled.PlayArrow),  // 0° — atas
                RevolverNavItem(AppScreen.GAME_ROOM, "Game", Icons.Filled.PlayArrow),     // 72°
                RevolverNavItem(AppScreen.PROFILE, "Profile", Icons.Filled.Person),       // 144°
                RevolverNavItem(AppScreen.HOME, "Home", Icons.Filled.Home),               // 216°
                RevolverNavItem(AppScreen.MISSIONS, "Missions", Icons.Filled.Star)        // 288°
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
    val orbitRadius = 96.dp

    Box(modifier = modifier.fillMaxSize()) {
        // ==== OVERLAY GELAP saat expanded ====
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

        // ==== CHAMBER + ICONS + CENTER ====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight)
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.BottomCenter
        ) {
            // ==== CHAMBER ARC (dashed emerald) ====
            if (isExpanded) {
                Canvas(
                    modifier = Modifier
                        .size(orbitRadius * 2 + 40.dp)
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    drawCircle(
                        color = EmeraldPrimary.copy(alpha = 0.25f),
                        radius = size.minDimension / 2,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 12f), 0f)
                        )
                    )
                    drawCircle(
                        color = EmeraldPrimary.copy(alpha = 0.08f),
                        radius = size.minDimension / 2 - 4.dp.toPx(),
                        style = Stroke(width = 12.dp.toPx())
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

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                            .offset(x = xPx.dp, y = yPx.dp)
                            .size(iconSize)
                            .scale(if (isSelected) 1.1f else 1f)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) EmeraldDeep
                                else MetalDeep
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

            // ==== CENTER BUTTON (Gold Core) ====
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
                    .size(centerSize)
                    .scale(if (isExpanded) 1.05f else 1f)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GoldBright, GoldVip, GoldAmber)
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
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = if (isExpanded) "Close" else "Open",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
