package com.inkside.digital.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.theme.DarkSurfaceVariant
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.viewmodel.AppScreen
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

data class RevolverNavItem(
    val screen: AppScreen,
    val emoji: String
)

/**
 * Revolver Bottom Nav — FULL CIRCLE VERSION
 *
 * 6 menu dalam bulatan penuh (360°, 60° apart).
 * Center button tap → expand circle.
 * Drag pada center → rotate seluruh circle.
 * Background tetap kelihatan (tak ada overlay hitam).
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

    // 6 menu untuk full circle
    val items = remember(isAdminMode) {
        if (isAdminMode) {
            listOf(
                RevolverNavItem(AppScreen.ADMIN, "⚙️"),
                RevolverNavItem(AppScreen.HOME, "🏠"),
                RevolverNavItem(AppScreen.MISSIONS, "🎯"),
                RevolverNavItem(AppScreen.GAME_ROOM, "🎮"),
                RevolverNavItem(AppScreen.PROFILE, "👤"),
                RevolverNavItem(AppScreen.HOME, "🏠")
            )
        } else {
            listOf(
                RevolverNavItem(AppScreen.GAME_MINING, "⛏️"),   // 0° — atas
                RevolverNavItem(AppScreen.GAME_ROOM, "🎮"),     // 60°
                RevolverNavItem(AppScreen.PROFILE, "👤"),       // 120°
                RevolverNavItem(AppScreen.HOME, "🏠"),          // 180° — bawah
                RevolverNavItem(AppScreen.MISSIONS, "🎯"),      // 240°
                RevolverNavItem(AppScreen.SETTINGS, "⚙️")       // 300°
            )
        }
    }

    var isExpanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val rotationAnim = remember { Animatable(0f) }
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }

    val totalIcons = items.size
    val stepAngle = 360f / totalIcons   // 60° untuk 6 icon

    // Icon index → base angle (0° = atas)
    fun indexToBaseAngle(index: Int): Float = index * stepAngle

    // Cari index yang paling dekat dengan atas (0°)
    fun nearestIndexForRotation(rot: Float): Int {
        var best = 0
        var bestDist = Float.MAX_VALUE
        for (i in 0 until totalIcons) {
            // angle dalam world = base + rotation
            val angle = ((indexToBaseAngle(i) + rot) % 360f + 360f) % 360f
            // Normalize ke [-180, 180]
            val norm = if (angle > 180f) angle - 360f else angle
            val dist = abs(norm)
            if (dist < bestDist) {
                bestDist = dist
                best = i
            }
        }
        return best
    }

    // Set initial selected + rotation based on current screen
    LaunchedEffect(currentScreen, isExpanded) {
        if (!isExpanded) {
            val idx = items.indexOfFirst { it.screen == currentScreen }
            if (idx >= 0) {
                selectedIndex = idx
                rotationAnim.snapTo(-indexToBaseAngle(idx))
            }
        }
    }

    // Ticker — update selectedIndex ikut rotation
    LaunchedEffect(rotationAnim.value) {
        if (isExpanded) {
            val newIdx = nearestIndexForRotation(rotationAnim.value)
            if (newIdx != selectedIndex) {
                selectedIndex = newIdx
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    val containerHeight = 200.dp
    val centerSize = 64.dp
    val orbitRadius = 72.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(containerHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Faint dashed circle (visual guide — TAK block content)
        if (isExpanded) {
            Canvas(
                modifier = Modifier
                    .size(orbitRadius * 2 + 40.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
            ) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.12f),
                    radius = size.minDimension / 2,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
        }

        // ORBIT ICONS — hanya kalau expanded
        if (isExpanded) {
            items.forEachIndexed { index, item ->
                val baseAngle = indexToBaseAngle(index)
                val currentAngle = baseAngle + rotationAnim.value
                val isSelected = index == selectedIndex

                val iconSize = if (isSelected) 52.dp else 40.dp

                // Kira posisi: 0° = atas, rotate clockwise
                val rad = (currentAngle - 90f) * PI.toFloat() / 180f
                val xPx = orbitRadius.value * cos(rad)
                val yPx = orbitRadius.value * sin(rad)

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 18.dp)   // sama dengan center button
                        .offset(
                            x = xPx.dp,
                            y = yPx.dp
                        )
                        .size(iconSize)
                        .scale(if (isSelected) 1.15f else 1f)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) EmeraldPrimary
                            else DarkSurfaceVariant.copy(alpha = 0.95f)
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
                                onNavigate(item.screen)
                                isExpanded = false
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = item.emoji, fontSize = if (isSelected) 24.sp else 20.sp)
                }
            }
        }

        // CENTER BUTTON — anchor + gesture target
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
                .size(centerSize)
                .scale(if (isExpanded) 1.05f else 1f)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GoldVip, GoldVip.copy(alpha = 0.85f))
                    )
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
                            change.consume()   // ← CONSUME — jangan propagate ke sidebar/content
                            accumulatedDrag += dragAmount.x
                            scope.launch {
                                rotationAnim.snapTo(rotationAnim.value + dragAmount.x * 0.7f)
                            }
                        },
                        onDragEnd = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch {
                                // Snap ke icon terdekat
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
                                onNavigate(items[selectedIndex].screen)
                                isExpanded = false
                            } else {
                                isExpanded = true
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.Close else Icons.Filled.Menu,
                contentDescription = if (isExpanded) "Close" else "Open",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
