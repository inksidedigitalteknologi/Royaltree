package com.inkside.digital.ui.screens

import com.inkside.digital.util.GlobalPointsManager
import com.inkside.digital.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.components.FramedCard
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.viewmodel.AppScreen
import com.inkside.digital.viewmodel.AdButtonState
import com.inkside.digital.ui.components.BannerAdView
import androidx.compose.ui.text.style.TextOverflow

// ==== Accent Colors (tetap sama light/dark) ====
private val GoldAccent = Color(0xFFFFD700)
private val EmeraldLight = Color(0xFF10B981)
private val ElectricBlue = Color(0xFF3B82F6)

@Composable
fun HomeScreen(
    user: UserEntity?,
    links: List<AffiliateLinkEntity>,
    recentTransactions: List<TransactionEntity>,
    currentLanguage: AppLanguage,
    onNavigate: (AppScreen) -> Unit,
    onOpenWithdraw: () -> Unit,
    onOpenNewLink: () -> Unit,
    onOpenUpgrade: () -> Unit,
    onOpenRedeemPoints: () -> Unit,
    onOpenTransferQr: () -> Unit = {},
    onSimulateConversion: (AffiliateLinkEntity) -> Unit,
    onOpenAdReward: () -> Unit,
    adButtonState: AdButtonState = AdButtonState.LOADING,
    onRetryAdLoad: () -> Unit = {}
) {
    val isPremium = user?.tier in listOf("PREMIUM", "VIP", "ROYAL")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // ==== 1. Header: Nama + Tier ====
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = LanguageManager.translate("home_welcome", currentLanguage, "Selamat Datang"),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user?.name ?: "User",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Tier Badge
                Surface(
                    color = if (isPremium) GoldAccent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPremium) Icons.Filled.WorkspacePremium else Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (isPremium) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when (user?.tier) {
                                "STARTER" -> LanguageManager.translate("tier_starter", currentLanguage, "Pemula")
                                "PREMIUM" -> LanguageManager.translate("tier_premium", currentLanguage, "Premium")
                                "VIP" -> LanguageManager.translate("tier_vip", currentLanguage, "VIP")
                                "ROYAL" -> LanguageManager.translate("tier_royal", currentLanguage, "Royal")
                                else -> LanguageManager.translate("tier_free", currentLanguage, "Free")
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPremium) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ==== 2. Balance Hero ====
        item {
            FramedCard(
                frameRes = R.drawable.card_frame_balance,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                paddingPercentHorizontal = 0.12f,
                paddingPercentVertical = 0.26f
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = LanguageManager.translate("total_commission", currentLanguage, "Saldo Tersedia"),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = GlobalPointsManager.formatBalanceIdr(user?.balance ?: 0.0, currentLanguage.code),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = EmeraldLight,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenWithdraw() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = LanguageManager.translate("btn_withdraw", currentLanguage, "Tarik Dana"),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==== 2b. Banner AdMob ====
        item {
            BannerAdView()
        }

        // ==== 3. Stats Row ====
        item {
            Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth()
) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        icon = Icons.Filled.Star,
                        iconColor = GoldAccent,
                        value = "${user?.points ?: 0}",
                        label = "RTP",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Filled.LocalFireDepartment,
                        iconColor = Color(0xFFF97316),
                        value = "${user?.checkInStreak ?: 0}",
                        label = LanguageManager.translate("daily_streak", currentLanguage, "Streak"),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                        iconColor = ElectricBlue,
                        value = "${user?.todaySteps ?: 0}",
                        label = LanguageManager.translate("home_steps_today", currentLanguage, "Langkah"),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ==== 4. Quick Actions ====
        item {
            Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth()
) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Filled.TaskAlt,
                        label = LanguageManager.translate("nav_missions", currentLanguage, "Misi"),
                        color = Color(0xFF8B5CF6),
                        onClick = { onNavigate(AppScreen.MISSIONS) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Filled.CheckCircle,
                        label = LanguageManager.translate("daily_title", currentLanguage, "Check-In"),
                        color = EmeraldLight,
                        onClick = { onNavigate(AppScreen.DAILY_CHECK_IN) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Filled.ArrowUpward,
                        label = LanguageManager.translate("nav_withdraw", currentLanguage, "Tarik"),
                        color = ElectricBlue,
                        onClick = { onOpenWithdraw() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ==== 5. Ad Banner (Dinamis) ====
        item {
            val (label, subtitle, accentColor, isEnabled) = when (adButtonState) {
                AdButtonState.READY -> Quadruple(
                    LanguageManager.translate("home_watch_ad", currentLanguage, "Tonton Iklan Sponsor"),
                    LanguageManager.translate("home_watch_ad_desc", currentLanguage, "Dapat +2-5 RTP • 30 detik"),
                    GoldAccent,
                    true
                )
                AdButtonState.LOADING_AD -> Quadruple(
                    LanguageManager.translate("home_loading_ad", currentLanguage, "Memuat iklan..."),
                    LanguageManager.translate("home_please_wait", currentLanguage, "Mohon tunggu sebentar"),
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    false
                )
                AdButtonState.NO_AD -> Quadruple(
                    LanguageManager.translate("home_ad_not_available", currentLanguage, "Iklan tidak tersedia"),
                    LanguageManager.translate("home_try_later", currentLanguage, "Coba lagi nanti"),
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    false
                )
                AdButtonState.LIMIT -> Quadruple(
                    LanguageManager.translate("home_limit_reached", currentLanguage, "Limit harian tercapai"),
                    LanguageManager.translate("home_come_back_tomorrow", currentLanguage, "Kembali besok untuk nonton lagi"),
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    false
                )
                AdButtonState.COOLDOWN -> Quadruple(
                    LanguageManager.translate("home_wait_moment", currentLanguage, "Tunggu sebentar..."),
                    LanguageManager.translate("home_wait_cooldown", currentLanguage, "Iklan sedang dimuat"),
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    false
                )
                AdButtonState.LOADING -> Quadruple(
                    LanguageManager.translate("home_checking", currentLanguage, "Memeriksa..."),
                    LanguageManager.translate("home_checking_status", currentLanguage, "Sedang memuat status"),
                    MaterialTheme.colorScheme.onSurfaceVariant,
                    false
                )
            }

            Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = Modifier.fillMaxWidth()
) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .then(
                            if (isEnabled) Modifier.clickable { onOpenAdReward() }
                            else Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = accentColor.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (adButtonState == AdButtonState.NO_AD) {
                        Text(
                            text = LanguageManager.translate("common_try_again", currentLanguage, "Coba Lagi"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            modifier = Modifier
                                .clickable { onRetryAdLoad() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

// ==== Komponen Stat Card ====
@Composable
private fun StatCard(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            iconColor.copy(alpha = 0.18f),
                            iconColor.copy(alpha = 0.04f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = iconColor.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon dalam circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = iconColor,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==== Komponen Quick Action ====
@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


// Helper: Quadruple untuk destructuring 4 nilai
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
