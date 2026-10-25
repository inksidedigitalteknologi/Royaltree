package com.inkside.digital.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.R
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.viewmodel.AppScreen

// ─────────────────────────────────────────────────────────────
// Palette
// ─────────────────────────────────────────────────────────────
private val BgDeep = Color(0xFF0B1220)
private val BgPill = Color(0xFF0F172A)
private val EmeraldPrimary = Color(0xFF10B981)
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldDeep = Color(0xFF064E3B)
private val GoldVip = Color(0xFFFBBF24)
private val GoldBright = Color(0xFFFFD700)
private val GoldDeep = Color(0xFFB45309)
private val TextMuted = Color(0xFF94A3B8)

data class CommandItem(
    val screen: AppScreen,
    val labelKey: String,
    val fallbackLabel: String,
    val icon: ImageVector
)

@Composable
fun RevolverBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    currentLanguage: AppLanguage = AppLanguage.INDONESIAN,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Menu items (4 tab + coin center)
    val items = remember {
        listOf(
            CommandItem(AppScreen.GAME_ROOM, "nav_game", "Game", Icons.Filled.SportsEsports),
            CommandItem(AppScreen.REFERRAL, "nav_referral", "Referral", Icons.Filled.People),
            CommandItem(AppScreen.PROFILE, "nav_profile", "Profil", Icons.Filled.AccountCircle),
            CommandItem(AppScreen.PURCHASE, "nav_purchase", "Beli", Icons.Filled.ShoppingBag)
        )
    }

    // Pulse animation untuk coin center
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val coinScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coinScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // ═══════════════════════════════════════════
        // FLOATING PILL
        // ═══════════════════════════════════════════
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BgPill.copy(alpha = 0.96f),
                            BgDeep.copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            EmeraldPrimary.copy(alpha = 0.35f),
                            GoldVip.copy(alpha = 0.25f),
                            EmeraldPrimary.copy(alpha = 0.35f)
                        )
                    ),
                    shape = RoundedCornerShape(36.dp)
                )
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // USER MODE: 2 kiri + coin + 2 kanan
            items.take(2).forEach { item ->
                    PillTab(
                        item = item,
                        isSelected = currentScreen == item.screen,
                        currentLanguage = currentLanguage,
                        haptic = haptic,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigate(item.screen)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                // ═══════════════════════════════════════════
                // RTP COIN CENTER (tap → HOME)
                // ═══════════════════════════════════════════
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.width(80.dp)
                ) {
                // Glow pulse
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GoldVip.copy(alpha = 0.5f * pulseAlpha),
                                    GoldVip.copy(alpha = 0.15f * pulseAlpha),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // RTP Coin
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .scale(coinScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFEF3C7),
                                    GoldBright,
                                    GoldVip,
                                    Color(0xFFF59E0B)
                                )
                            )
                        )
                        .border(2.dp, GoldDeep, CircleShape)
                        .clickable {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onNavigate(AppScreen.HOME)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rtp_coin),
                        contentDescription = "RTP",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // 2 tab kanan
            items.drop(2).take(2).forEach { item ->
                PillTab(
                    item = item,
                    isSelected = currentScreen == item.screen,
                    currentLanguage = currentLanguage,
                    haptic = haptic,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigate(item.screen)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PILL TAB — item premium
// ─────────────────────────────────────────────────────────────
@Composable
private fun PillTab(
    item: CommandItem,
    isSelected: Boolean,
    currentLanguage: AppLanguage,
    haptic: androidx.compose.ui.hapticfeedback.HapticFeedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.9f else if (isSelected) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "tabScale"
    )


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.5f)
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp)
    ) {
        // Icon circle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        Brush.linearGradient(
                            colors = listOf(
                                GoldBright.copy(alpha = 0.9f),
                                GoldVip.copy(alpha = 0.85f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.06f),
                                Color.White.copy(alpha = 0.04f)
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.fallbackLabel,
                tint = if (isSelected) Color(0xFF1F2937) else TextMuted,
                modifier = Modifier.size(if (isSelected) 18.dp else 16.dp)
            )
        }

        Spacer(Modifier.height(3.dp))

        // Label
        Text(
            text = LanguageManager.translate(item.labelKey, currentLanguage, item.fallbackLabel),
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) GoldBright else TextMuted,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            letterSpacing = 0.3.sp
        )
    }
}
