package com.inkside.digital.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.viewmodel.AppScreen
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.filled.Email

// ============================================================
// M52: AnimatedRoyaltreeTitle — text dengan gradient bergerak
// ============================================================
@Composable
fun AnimatedRoyaltreeTitle() {
    val transition = androidx.compose.animation.core.rememberInfiniteTransition(label = "title")

    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = androidx.compose.animation.core.infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(
                durationMillis = 3500,
                easing = androidx.compose.animation.core.LinearEasing
            ),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "offset"
    )

    Text(
        text = "Royaltree",
        style = TextStyle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF10B981),
                    Color(0xFFFFB800),
                    Color(0xFF10B981)
                ),
                start = Offset(offset - 500f, 0f),
                end = Offset(offset, 0f)
            ),
            fontWeight = FontWeight.Black,
            fontSize = 17.sp
        )
    )
}

@Composable
fun AppTopBar(
    user: UserEntity?,
    unreadNotifs: Int,
    currentLanguage: AppLanguage,
    onOpenDrawer: () -> Unit,
    onOpenLanguage: () -> Unit,
    onOpenInbox: () -> Unit,
    onOpenNotifs: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT: Hamburger + Logo + Tier
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("hamburger_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = LanguageManager.translate("cd_menu", currentLanguage, "Menu"),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // M52: Logo Royaltree — coin RTP
                Image(
                    painter = androidx.compose.ui.res.painterResource(com.inkside.digital.R.drawable.rtp_coin),
                    contentDescription = LanguageManager.translate("cd_royaltree_logo", currentLanguage, "Royaltree Logo"),
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(9.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // M52: Animated gradient "Royaltree"
                        AnimatedRoyaltreeTitle()
                        Spacer(modifier = Modifier.width(5.dp))
                        if (user?.tier in listOf("PREMIUM", "VIP", "ROYAL")) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(GoldVip)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "VIP",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = LanguageManager.translate("tier_free", currentLanguage, "Free"),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // RIGHT: Language + Inbox + Notif
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Language
                IconButton(
                    onClick = onOpenLanguage,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("language_button")
                ) {
                    Text(text = currentLanguage.flag, fontSize = 18.sp)
                }

                // Inbox
                IconButton(
                    onClick = onOpenInbox,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("inbox_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = LanguageManager.translate("cd_inbox", currentLanguage, "Inbox"),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Notifications
                IconButton(
                    onClick = onOpenNotifs,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadNotifs > 0) {
                                Badge(
                                    containerColor = Color(0xFFEF4444),
                                    contentColor = Color.White
                                ) {
                                    Text(text = if (unreadNotifs > 9) "9+" else unreadNotifs.toString())
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = LanguageManager.translate("cd_notifications", currentLanguage, "Notifications"),
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

