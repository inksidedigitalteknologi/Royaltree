package com.inkside.digital.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.screens.ProfileAvatarWithFrame
import com.inkside.digital.util.GlobalPointsManager
import com.inkside.digital.viewmodel.AppScreen

// ─────────────────────────────────────────────────────────────
// Palette mewah
// ─────────────────────────────────────────────────────────────
private val BgDeep = Color(0xFF0B1220)
private val BgCard = Color(0xFF131C2E)
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldDeep = Color(0xFF059669)
private val GoldAccent = Color(0xFFFFD700)
private val GoldSoft = Color(0xFFFBBF24)
private val TextMuted = Color(0xFF94A3B8)
private val TextFaint = Color(0xFF64748B)
private val DangerRed = Color(0xFFEF4444)
private val DangerDeep = Color(0xFFDC2626)

@Composable
fun AppDrawer(
    user: UserEntity?,
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    roomState: com.inkside.digital.data.model.GameRoomStateEntity? = null,
    onNavigate: (AppScreen) -> Unit,
    onCloseDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (e: Exception) { "1.0" }
    }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp),
        color = BgDeep,
        shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // ═══════════════════════════════════════════
            // HEADER PREMIUM
            // ═══════════════════════════════════════════
            PremiumDrawerHeader(user = user, currentLanguage = currentLanguage, roomState = roomState)

            Spacer(Modifier.height(12.dp))

            // ═══════════════════════════════════════════
            // SECTION: MENU UTAMA
            // ═══════════════════════════════════════════
            SectionTitle(
                text = LanguageManager.translate("menu_utama", currentLanguage, "MENU UTAMA"),
                emoji = "⚡"
            )

            DrawerItem(
                icon = Icons.Default.Home,
                label = LanguageManager.translate("nav_home", currentLanguage, "Beranda"),
                selected = currentScreen == AppScreen.HOME,
                onClick = { onNavigate(AppScreen.HOME); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.TaskAlt,
                label = LanguageManager.translate("nav_missions", currentLanguage, "Misi"),
                selected = currentScreen == AppScreen.PURCHASE,
                onClick = { onNavigate(AppScreen.PURCHASE); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.CardGiftcard,
                label = LanguageManager.translate("daily_title", currentLanguage, "Login Harian"),
                selected = currentScreen == AppScreen.DAILY_CHECK_IN,
                onClick = { onNavigate(AppScreen.DAILY_CHECK_IN); onCloseDrawer() }
            )

            DrawerItem(
                icon = Icons.AutoMirrored.Filled.Send,
                label = LanguageManager.translate("nav_transfer", currentLanguage, "Transfer"),
                selected = currentScreen == AppScreen.TRANSFER,
                onClick = { onNavigate(AppScreen.TRANSFER); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.MonetizationOn,
                label = LanguageManager.translate("nav_withdraw", currentLanguage, "Penarikan"),
                selected = currentScreen == AppScreen.WITHDRAW,
                onClick = { onNavigate(AppScreen.WITHDRAW); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.TrendingUp,
                label = LanguageManager.translate("nav_invest", currentLanguage, "Investasi Kupon"),
                selected = currentScreen == AppScreen.INVEST,
                onClick = { onNavigate(AppScreen.INVEST); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.History,
                label = LanguageManager.translate("nav_history", currentLanguage, "Riwayat"),
                selected = currentScreen == AppScreen.HISTORY,
                onClick = { onNavigate(AppScreen.HISTORY); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.CardGiftcard,
                label = LanguageManager.translate("referral_title", currentLanguage, "Undang Teman"),
                selected = currentScreen == AppScreen.REFERRAL,
                onClick = { onNavigate(AppScreen.REFERRAL); onCloseDrawer() }
            )

            Spacer(Modifier.height(8.dp))

            // ═══════════════════════════════════════════
            // SECTION: KOMUNITAS
            // ═══════════════════════════════════════════
            SectionTitle(
                text = LanguageManager.translate("menu_komunitas", currentLanguage, "KOMUNITAS"),
                emoji = "💬"
            )

            DrawerItem(
                icon = Icons.Default.Chat,
                label = LanguageManager.translate("chat_title", currentLanguage, "Global Chat"),
                selected = currentScreen == AppScreen.GLOBAL_CHAT,
                onClick = { onNavigate(AppScreen.GLOBAL_CHAT); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.MenuBook,
                label = LanguageManager.translate("wp_title", currentLanguage, "Whitepaper"),
                selected = currentScreen == AppScreen.WHITEPAPER,
                onClick = { onNavigate(AppScreen.WHITEPAPER); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.EmojiEvents,
                label = LanguageManager.translate("achievement_title", currentLanguage, "Achievement"),
                selected = currentScreen == AppScreen.ACHIEVEMENT,
                onClick = { onNavigate(AppScreen.ACHIEVEMENT); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                label = LanguageManager.translate("faq_title", currentLanguage, "Bantuan"),
                selected = currentScreen == AppScreen.FAQ,
                onClick = { onNavigate(AppScreen.FAQ); onCloseDrawer() }
            )

            Spacer(Modifier.height(8.dp))

            // ═══════════════════════════════════════════
            // SECTION: AKUN
            // ═══════════════════════════════════════════
            SectionTitle(
                text = LanguageManager.translate("menu_akun", currentLanguage, "AKUN"),
                emoji = "👤"
            )

            DrawerItem(
                icon = Icons.Default.Person,
                label = LanguageManager.translate("nav_profile", currentLanguage, "Profil"),
                selected = currentScreen == AppScreen.PROFILE,
                onClick = { onNavigate(AppScreen.PROFILE); onCloseDrawer() }
            )
            DrawerItem(
                icon = Icons.Default.Settings,
                label = LanguageManager.translate("settings_title", currentLanguage, "Pengaturan"),
                selected = currentScreen == AppScreen.SETTINGS,
                onClick = { onNavigate(AppScreen.SETTINGS); onCloseDrawer() }
            )

            Spacer(Modifier.height(20.dp))

            // ═══════════════════════════════════════════
            // FOOTER: Trust badge + versi
            // ═══════════════════════════════════════════
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "v$versionName · © 2026 Inkside Digital",
                    fontSize = 10.sp,
                    color = TextFaint,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // ═══════════════════════════════════════════
            // LOGOUT BUTTON
            // ═══════════════════════════════════════════
            LogoutButton(
                text = LanguageManager.translate("settings_logout", currentLanguage, "Keluar"),
                onClick = { showLogoutDialog = true }
            )

            Spacer(Modifier.height(16.dp))
        }
    }

    // ═══════════════════════════════════════════
    // LOGOUT DIALOG
    // ═══════════════════════════════════════════
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(LanguageManager.translate("settings_logout", currentLanguage, "Keluar dari Akun?")) },
            text = { Text(LanguageManager.translate("settings_logout_confirm", currentLanguage, "Anda yakin ingin keluar dari Royaltree?")) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onCloseDrawer()
                    onLogout()
                }) {
                    Text(
                        text = LanguageManager.translate("settings_logout", currentLanguage, "Keluar"),
                        color = DangerRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(LanguageManager.translate("common_cancel", currentLanguage, "Batal"))
                }
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────
// PREMIUM HEADER
// ─────────────────────────────────────────────────────────────
@Composable
private fun PremiumDrawerHeader(user: UserEntity?, currentLanguage: AppLanguage, roomState: com.inkside.digital.data.model.GameRoomStateEntity? = null) {
    // Glow pulse avatar
    val infinite = rememberInfiniteTransition(label = "header_glow")
    val glowAlpha by infinite.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GoldAccent.copy(alpha = 0.15f),
                        EmeraldDeep.copy(alpha = 0.35f),
                        BgDeep
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Radial glow di belakang avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            GoldAccent.copy(alpha = 0.15f * glowAlpha),
                            EmeraldLight.copy(alpha = 0.08f * glowAlpha),
                            Color.Transparent
                        ),
                        radius = 400f
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ═══════════════════════════════════════
            // ROW: Avatar (80dp) + Info (nama, tier, email)
            // ═══════════════════════════════════════
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar 80dp + glow
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(96.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        GoldAccent.copy(alpha = 0.35f * glowAlpha),
                                        EmeraldLight.copy(alpha = 0.15f * glowAlpha),
                                        Color.Transparent
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                    ProfileAvatarWithFrame(
                        userName = user?.name,
                        tier = user?.tier ?: "FREE",
                        size = 80.dp,
                        photoUrl = user?.photoUrl
                    )
                }

                Spacer(Modifier.width(14.dp))

                // Info column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user?.name ?: "User",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.3.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    TierBadge(tier = user?.tier ?: "FREE")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = user?.email ?: "-",
                        fontSize = 10.sp,
                        color = TextMuted,
                        letterSpacing = 0.2.sp,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // ═══════════════════════════════════════
            // HORIZONTAL: Saldo | RTP
            // ═══════════════════════════════════════
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                // Saldo
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💰", fontSize = 11.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = LanguageManager.translate("drawer_balance", currentLanguage, "Saldo"),
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = GlobalPointsManager.formatBalanceIdr(
                            user?.balance ?: 0.0,
                            currentLanguage.code
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                    )
                }

                // Divider vertical
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(Color.White.copy(alpha = 0.08f))
                )

                // RTP
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 11.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = LanguageManager.translate("drawer_rtp", currentLanguage, "RTP"),
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = GlobalPointsManager.formatRtp((user?.points ?: 0.0) + (roomState?.unclaimedMiningPoints ?: 0.0)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TIER BADGE
// ─────────────────────────────────────────────────────────────
@Composable
private fun TierBadge(tier: String) {
    val (bgColor, textColor, icon) = when (tier.uppercase()) {
        "ROYAL" -> Triple(GoldAccent, Color.Black, "👑")
        "VIP" -> Triple(Color(0xFFFBBF24), Color.Black, "⭐")
        "PREMIUM" -> Triple(Color(0xFFC0C0C0), Color.Black, "💎")
        "STARTER" -> Triple(Color(0xFFCD7F32), Color.White, "🌱")
        else -> Triple(Color.White.copy(alpha = 0.15f), Color.White, "🌿")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 11.sp)
            Spacer(Modifier.width(4.dp))
            Text(
                text = tier.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 1.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// INFO ROW (horizontal: label kiri, value kanan)
// ─────────────────────────────────────────────────────────────
@Composable
private fun InfoRow(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 13.sp)
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

// ─────────────────────────────────────────────────────────────
// SECTION TITLE
// ─────────────────────────────────────────────────────────────
@Composable
private fun SectionTitle(text: String, emoji: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GoldAccent.copy(alpha = 0.85f),
            letterSpacing = 1.5.sp
        )
    }
}

// ─────────────────────────────────────────────────────────────
// DRAWER ITEM (premium, animated)
// ─────────────────────────────────────────────────────────────
@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    tint: Color = EmeraldLight
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            GoldAccent.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.Transparent)
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gold bar kiri (kalau selected)
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) GoldAccent else Color.Transparent)
        )

        Spacer(Modifier.width(13.dp))

        // Icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (selected) tint.copy(alpha = 0.15f)
                    else Color.White.copy(alpha = 0.04f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) tint else tint.copy(alpha = 0.75f),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        // Label
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else Color.White.copy(alpha = 0.75f),
            modifier = Modifier.weight(1f)
        )

        // Active dot
        if (selected) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(GoldAccent)
            )
            Spacer(Modifier.width(12.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────
// LOGOUT BUTTON (gradient merah)
// ─────────────────────────────────────────────────────────────
@Composable
private fun LogoutButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = tween(120),
        label = "logoutScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        DangerRed.copy(alpha = 0.15f),
                        DangerDeep.copy(alpha = 0.1f)
                    )
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
            contentDescription = text,
            tint = DangerRed,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = DangerRed,
            letterSpacing = 0.3.sp
        )
    }
}
