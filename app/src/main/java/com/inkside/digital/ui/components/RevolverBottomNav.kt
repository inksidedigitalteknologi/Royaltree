package com.inkside.digital.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.viewmodel.AppScreen

// ==== DESIGN SYSTEM ====
private val SurfaceDark = Color(0xFF0F172A)
private val SurfaceDeep = Color(0xFF020617)
private val EmeraldPrimary = Color(0xFF10B981)
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldDeep = Color(0xFF064E3B)
private val GoldVip = Color(0xFFFBBF24)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFCBD5E1)
private val TextMuted = Color(0xFF64748B)

data class CommandItem(
    val screen: AppScreen,
    val label: String,
    val icon: ImageVector
)

@Composable
fun RevolverBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    currentLanguage: AppLanguage = AppLanguage.INDONESIAN,
    modifier: Modifier = Modifier,
    isAdminMode: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    var isExpanded by remember { mutableStateOf(false) }

    // Pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // 5 menu
    val items = remember(isAdminMode) {
        if (isAdminMode) {
            listOf(
                CommandItem(AppScreen.ADMIN, "Admin", Icons.Filled.Home),
                CommandItem(AppScreen.MISSIONS, "Misi", Icons.Filled.Flag),
                CommandItem(AppScreen.GAME_ROOM, "Game", Icons.Filled.SportsEsports),
                CommandItem(AppScreen.PROFILE, "Profil", Icons.Filled.AccountCircle)
            )
        } else {
            listOf(
                CommandItem(AppScreen.HOME, "Home", Icons.Filled.Home),
                CommandItem(AppScreen.MISSIONS, "Misi", Icons.Filled.Flag),
                CommandItem(AppScreen.GAME_ROOM, "Game", Icons.Filled.SportsEsports),
                CommandItem(AppScreen.PROFILE, "Profil", Icons.Filled.AccountCircle)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ==== OVERLAY GELAP ====
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
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

        // ==== BOTTOM BAR ====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            if (!isExpanded) {
                // ==== COLLAPSED: Coin RTP ====
                Box(
                    modifier = Modifier.align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    // Glow
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        GoldVip.copy(alpha = 0.5f),
                                        GoldVip.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    )
                    // Coin
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        Color(0xFFFDE68A),
                                        GoldVip,
                                        Color(0xFFF59E0B)
                                    )
                                )
                            )
                            .border(2.dp, Color(0xFFB45309), CircleShape)
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isExpanded = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(
                                id = com.inkside.digital.R.drawable.rtp_coin
                            ),
                            contentDescription = LanguageManager.translate("cd_rtp", currentLanguage, "RTP"),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
            } else {
                // ==== EXPANDED: Command Bar ====
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F172A).copy(alpha = 0.98f),
                                    Color(0xFF020617).copy(alpha = 0.98f)
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            color = EmeraldPrimary.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    // ==== CORNER BRACKETS + HEADER ====
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "┌─",
                            fontSize = 10.sp,
                            color = EmeraldPrimary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ROYALTREE COMMAND",
                            fontSize = 8.sp,
                            color = TextMuted,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "─┐",
                            fontSize = 10.sp,
                            color = EmeraldPrimary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // ==== ROW ICON ====
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items.forEachIndexed { index, item ->
                            // Insert coin di tengah (index 2)
                            if (index == 2) {
                                // ==== CENTER COIN ====
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    Color(0xFFFDE68A),
                                                    GoldVip,
                                                    Color(0xFFF59E0B)
                                                )
                                            )
                                        )
                                        .border(2.dp, Color(0xFFB45309), CircleShape)
                                        .clickable {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            isExpanded = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(
                                            id = com.inkside.digital.R.drawable.rtp_coin
                                        ),
                                        contentDescription = LanguageManager.translate("cd_rtp", currentLanguage, "RTP"),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            // ==== ICON ITEM ====
                            val isSelected = currentScreen == item.screen
                            CommandIcon(
                                item = item,
                                isSelected = isSelected,
                                pulseAlpha = pulseAlpha,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onNavigate(item.screen)
                                    isExpanded = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // ==== DIVIDER MEKANIKAL ====
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "└",
                            fontSize = 10.sp,
                            color = EmeraldPrimary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(0.5.dp)
                                .background(EmeraldPrimary.copy(alpha = 0.3f))
                        )
                        Text(
                            text = " 0x4E 0x4F 0x44 0x45 0x53 ",
                            fontSize = 7.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(0.5.dp)
                                .background(EmeraldPrimary.copy(alpha = 0.3f))
                        )
                        Text(
                            text = "┘",
                            fontSize = 10.sp,
                            color = EmeraldPrimary.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandIcon(
    item: CommandItem,
    isSelected: Boolean,
    pulseAlpha: Float,
    onClick: () -> Unit
) {
    val iconTint = if (isSelected) EmeraldPrimary else TextMuted
    val labelColor = if (isSelected) EmeraldPrimary else TextMuted

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(54.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier.size(38.dp),
            contentAlignment = Alignment.Center
        ) {
            // Glow untuk selected
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    EmeraldPrimary.copy(alpha = 0.3f * pulseAlpha),
                                    Color.Transparent
                                )
                            ),
                            CircleShape
                        )
                )
            }

            // Border circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) EmeraldDeep.copy(alpha = 0.5f)
                        else SurfaceDark.copy(alpha = 0.6f)
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) EmeraldPrimary else Color(0xFF334155),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = iconTint,
                    modifier = Modifier.size(if (isSelected) 18.dp else 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Label
        Text(
            text = item.label,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = labelColor,
            maxLines = 1
        )
    }
}
