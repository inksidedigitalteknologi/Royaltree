package com.inkside.digital.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
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
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Revolver Bottom Nav
 *
 * 5 icon disusun dalam half-circle (180°).
 * Center button (compass) boleh tap untuk expand, drag untuk rotate.
 * Release → snap ke icon terdekat → navigate.
 *
 * Layout (half-circle, -90° to +90°):
 *      0°   → ⛏️ Miner (top center)
 *    -45°   → 🏠 Home
 *    +45°   → 🎮 Game
 *    -90°   → 🎯 Missions
 *    +90°   → 👤 Profile
 */
data class RevolverNavItem(
    val route: String,
    val label: String,
    val emoji: String,
    val iconRes: Int? = null
)

@Composable
fun RevolverBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
    isAdminMode: Boolean = false
) {
    // 5 menu item — admin mode pun guna layout sama
    val items: List<RevolverNavItem> = if (isAdminMode) {
        listOf(
            RevolverNavItem(route = AppScreen.ADMIN.name, label = "Admin", emoji = "⚙️"),
            RevolverNavItem(route = AppScreen.ADMIN.name, label = "Admin", emoji = "⚙️"),
            RevolverNavItem(route = AppScreen.HOME.name, label = "Home", emoji = "🏠"),
            RevolverNavItem(route = AppScreen.HOME.name, label = "Home", emoji = "🏠"),
            RevolverNavItem(route = AppScreen.HOME.name, label = "Home", emoji = "🏠")
        )
    } else {
        listOf(
            RevolverNavItem(route = AppScreen.MISSIONS.name, label = "Missions", emoji = "🎯"),
            RevolverNavItem(route = AppScreen.HOME.name, label = "Home", emoji = "🏠"),
            RevolverNavItem(route = AppScreen.GAME_MINING.name, label = "Miner", emoji = "⛏️"),
            RevolverNavItem(route = AppScreen.GAME_ROOM.name, label = "Game", emoji = "🎮"),
            RevolverNavItem(route = AppScreen.PROFILE.name, label = "Profile", emoji = "👤")
        )
    }

    if (items.isEmpty()) return
    
    val currentRoute = currentScreen.name
    
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    
    var isExpanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val rotationAnim = remember { Animatable(0f) }
    
    // Setup: 5 icon, -90° to +90°, step 45° each (180° / 4)
    val totalIcons = items.size
    val arcStart = -90f
    val arcEnd = 90f
    val arcRange = arcEnd - arcStart
    val stepAngle = if (totalIcons > 1) arcRange / (totalIcons - 1) else 0f
    
    // Map index → angle
    fun indexToAngle(index: Int): Float = arcStart + (index * stepAngle)
    
    // Given a rotation, which index is closest to top-center (0°)?
    fun rotationToNearestIndex(rotation: Float): Int {
        // effective angle for index i = indexToAngle(i) + rotation
        // we want angle ≡ 0° (mod 360°) → but closest to 0
        var bestIndex = 0
        var bestDist = Float.MAX_VALUE
        for (i in 0 until totalIcons) {
            val angle = ((indexToAngle(i) + rotation + 180f) % 360f + 360f) % 360f - 180f
            val dist = abs(angle)
            if (dist < bestDist) {
                bestDist = dist
                bestIndex = i
            }
        }
        return bestIndex
    }
    
    // Set initial selectedIndex based on currentRoute
    LaunchedEffect(currentRoute, isExpanded) {
        if (!isExpanded) {
            val idx = items.indexOfFirst { it.route == currentRoute }
            if (idx >= 0) {
                selectedIndex = idx
                rotationAnim.snapTo(-indexToAngle(idx))
            }
        }
    }
    
    // Radius of half-circle menu
    val menuRadius: Dp = 110.dp
    val centerButtonSize: Dp = 64.dp
    val navBarHeight: Dp = 100.dp
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isExpanded) 260.dp else navBarHeight),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Dim overlay when expanded
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        isExpanded = false
                    }
            )
        }
        
        // Half-circle chamber (only when expanded)
        if (isExpanded) {
            // Arc guide (dashed circle)
            Canvas(
                modifier = Modifier
                    .size(menuRadius * 2)
                    .align(Alignment.BottomCenter)
                    .offset(y = -(navBarHeight / 2))
            ) {
                drawArc(
                    color = Color.White.copy(alpha = 0.15f),
                    startAngle = arcStart,
                    sweepAngle = arcRange,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect
                            .dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                )
            }
            
            // 5 icon along the arc
            items.forEachIndexed { index, item ->
                val baseAngle = indexToAngle(index)
                val currentAngle = baseAngle + rotationAnim.value
                val isSelected = index == selectedIndex
                
                val iconSize = if (isSelected) 60.dp else 44.dp
                val iconBg = if (isSelected) EmeraldPrimary else DarkSurfaceVariant
                val iconBorder = if (isSelected) EmeraldPrimary else Color.White.copy(alpha = 0.15f)
                val emojiSize = if (isSelected) 26.sp else 20.sp
                
                // Position on arc (top-center = 0°, rotate from center button)
                val rad = (currentAngle - 90f) * PI.toFloat() / 180f
                val x = (menuRadius.value * cos(rad)).toInt()
                val y = (menuRadius.value * sin(rad)).toInt()
                
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(
                            x = (x).dp,
                            y = -(navBarHeight / 2).toInt().dp - y.dp
                        )
                        .size(iconSize)
                        .scale(if (isSelected) 1.1f else 1f)
                        .clip(CircleShape)
                        .background(iconBg)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch {
                                // Rotate to this icon, then navigate
                                val targetRotation = -baseAngle
                                rotationAnim.animateTo(
                                    targetValue = targetRotation,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                onNavigate(AppScreen.valueOf(item.route))
                                isExpanded = false
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Outer glow ring when selected
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(1.15f)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            EmeraldPrimary.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                    
                    Text(
                        text = item.emoji,
                        fontSize = emojiSize
                    )
                }
            }
        }
        
        // Center button (trigger)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp)
                .size(centerButtonSize)
                .scale(if (isExpanded) 1.1f else 1f)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = if (isExpanded)
                            listOf(GoldVip, GoldVip.copy(alpha = 0.7f))
                        else
                            listOf(GoldVip, GoldVip.copy(alpha = 0.7f))
                    )
                )
                .pointerInput(isExpanded) {
                    detectDragGestures(
                        onDragStart = {
                            if (!isExpanded) {
                                isExpanded = true
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                rotationAnim.snapTo(rotationAnim.value + dragAmount.x * 0.6f)
                            }
                            val newIndex = rotationToNearestIndex(rotationAnim.value)
                            if (newIndex != selectedIndex) {
                                selectedIndex = newIndex
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        onDragEnd = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            scope.launch {
                                // Snap to nearest icon
                                val targetAngle = indexToAngle(selectedIndex)
                                val currentRotation = rotationAnim.value
                                // Find nearest multiple (mod 360)
                                var bestRotation = currentRotation
                                var bestDist = Float.MAX_VALUE
                                for (k in -2..2) {
                                    val candidate = -targetAngle + k * 360f
                                    val dist = abs(candidate - currentRotation)
                                    if (dist < bestDist) {
                                        bestDist = dist
                                        bestRotation = candidate
                                    }
                                }
                                rotationAnim.animateTo(
                                    targetValue = bestRotation,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                                // Auto-navigate
                                onNavigate(AppScreen.valueOf(items[selectedIndex].route))
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
                                // Tap center when expanded → navigate ke selected
                                onNavigate(AppScreen.valueOf(items[selectedIndex].route))
                                isExpanded = false
                            } else {
                                // Tap center when idle → expand
                                isExpanded = true
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (isExpanded) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Open menu",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        // Normal bottom nav row (only when NOT expanded)
        if (!isExpanded) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(navBarHeight)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isActive = item.route == currentRoute
                    // Skip center (index 2) — sebab center button dah ada
                    val isCenter = index == totalIcons / 2
                    if (isCenter) {
                        // Spacer untuk center button
                        Box(modifier = Modifier.size(centerButtonSize))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigate(AppScreen.valueOf(item.route))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.emoji,
                                fontSize = 22.sp,
                                modifier = Modifier.alpha(if (isActive) 1f else 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}
