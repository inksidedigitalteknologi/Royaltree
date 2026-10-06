package com.inkside.digital.ui.screens

import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.ui.unit.TextUnit
import com.inkside.digital.ui.components.FramedCard
import com.inkside.digital.R
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.inkside.digital.data.model.GameRoomStateEntity
import java.util.Locale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import com.inkside.digital.ui.components.BannerAdView
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.ui.components.AffiliateBalanceCard
import com.inkside.digital.viewmodel.AffiliateViewModel
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip

@Composable
fun ProfileScreen(
    user: UserEntity?,
    currentLanguage: AppLanguage,
    onOpenLanguage: () -> Unit,
    onSaveProfile: (name: String, email: String, phone: String) -> Unit,
    onLogout: () -> Unit,
    affiliateBalance: AffiliateViewModel.AffiliateBalanceData = AffiliateViewModel.AffiliateBalanceData(),
    onOpenWithdraw: () -> Unit = {},
    onOpenWithdrawHistory: () -> Unit = {},
    // Quick Actions
    onOpenUpgrade: () -> Unit = {},
    onNavigateToDailyCheckIn: () -> Unit = {},
    onNavigateToMining: () -> Unit = {},
    onNavigateToCoinRush: () -> Unit = {},
    onNavigateToMissions: () -> Unit = {},
    // Mining & Referral data
    minerSlotsUsed: Int = 0,
    minerSlotsMax: Int = 6,
    minerPowerGhs: Double = 0.0,
    minerTokens: Int = 0,
    roomState: GameRoomStateEntity? = null  // M46: Sinkron data dengan Mining Room
) {
    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header
        item {
            Column {
                Text("👤 " + LanguageManager.translate("profile_title", currentLanguage, "Profil"), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                Text(LanguageManager.translate("profile_subtitle", currentLanguage, "Kelola akun & informasi pribadi"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Avatar + Tier
        item {
            FramedCard(
                frameRes = R.drawable.card_frame_profile,
                contentPadding = 40.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // M47: Avatar dengan frame tier
                    ProfileAvatarWithFrame(
                        userName = user?.name,
                        tier = user?.tier ?: "FREE",
                        size = 100.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user?.name ?: "User", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "-", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (user?.tier) {
                                        "STARTER" -> Color(0xFFCD7F32)
                                        "PREMIUM" -> Color(0xFFC0C0C0)
                                        "VIP" -> GoldVip
                                        "ROYAL" -> Color(0xFFB9F2FF)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                when (user?.tier) {
                                    "STARTER" -> LanguageManager.translate("tier_starter", currentLanguage, "Pemula")
                                    "PREMIUM" -> LanguageManager.translate("tier_premium", currentLanguage, "Premium")
                                    "VIP" -> LanguageManager.translate("tier_vip", currentLanguage, "VIP")
                                    "ROYAL" -> LanguageManager.translate("tier_royal", currentLanguage, "Royal")
                                    else -> LanguageManager.translate("tier_free", currentLanguage, "Free")
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user?.tier in listOf("PREMIUM", "VIP", "ROYAL")) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Saldo & Poin
        item {
            FramedCard(
                frameRes = R.drawable.card_frame_stats,
                contentPadding = 40.dp
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(LanguageManager.translate("profile_balance", currentLanguage, "Saldo"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "Rp " + String.format("%,.0f", user?.balance ?: 0.0).replace(",", "."),
                            fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmeraldLight
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("RTP", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${user?.points ?: 0}",
                            fontSize = 16.sp, fontWeight = FontWeight.Black, color = ElectricBlue
                        )
                    }
                }
            }
        }

        // Edit Profil
        item {
            FramedCard(
                frameRes = R.drawable.card_frame_form,
                contentPadding = 40.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(LanguageManager.translate("profile_edit", currentLanguage, "✏️ Edit Profil"), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text(LanguageManager.translate("profile_name", currentLanguage, "Nama Lengkap")) }, singleLine = true,
                        shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text(LanguageManager.translate("profile_email_label", currentLanguage, "Email")) }, singleLine = true,
                        shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        label = { Text(LanguageManager.translate("profile_phone", currentLanguage, "Nomor HP")) }, singleLine = true,
                        shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onSaveProfile(name, email, phone) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(LanguageManager.translate("profile_save", currentLanguage, "Simpan Perubahan"), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ==== Quick Actions ====
        item {
            QuickActionsRow(
                currentLanguage = currentLanguage,
                onDailyCheckIn = onNavigateToDailyCheckIn,
                onMining = onNavigateToMining,
                onCoinRush = onNavigateToCoinRush,
                onMissions = onNavigateToMissions
            )
        }

        // ==== Upgrade Tier ====
        item {
            UpgradeTierCard(
                currentTier = user?.tier ?: "FREE",
                currentLanguage = currentLanguage,
                onClick = onOpenUpgrade
            )
        }

        // ==== Referral Stats ====
        item {
            ReferralStatsCard(
                referredCount = user?.referredCount ?: 0,
                commissionIdr = (user?.referredCount ?: 0) * 0.0,  // TODO: guna komisi dari backend nanti
                currentLanguage = currentLanguage
            )
        }

        // ==== Mining Stats ====
        item {
            MiningStatsCard(
                currentLanguage = currentLanguage,
                slotsUsed = minerSlotsUsed,
                slotsMax = minerSlotsMax,
                powerGhs = minerPowerGhs,
                tokens = minerTokens,
                roomState = roomState  // M46: Pass roomState untuk sinkron
            )
        }

        // ==== Affiliate Balance ====
        item {
            AffiliateBalanceCard(
                balance = affiliateBalance,
                currentLanguage = currentLanguage,
                onWithdraw = onOpenWithdraw,
                onHistory = onOpenWithdrawHistory
            )
        }

        // ==== Banner AdMob ====
        item {
            BannerAdView()
        }

        // Bahasa
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                        .clickable { onOpenLanguage() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Language, null, tint = EmeraldLight)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(LanguageManager.translate("profile_language", currentLanguage, "Bahasa"), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${currentLanguage.flag} ${currentLanguage.displayName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Logout
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().clickable { onLogout() }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(LanguageManager.translate("profile_logout", currentLanguage, "Keluar"), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ==========================================================
// QUICK ACTIONS — 4 tombol
// ==========================================================
@Composable
private fun QuickActionsRow(
    currentLanguage: AppLanguage,
    onDailyCheckIn: () -> Unit,
    onMining: () -> Unit,
    onCoinRush: () -> Unit,
    onMissions: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                LanguageManager.translate("profile_quick_actions", currentLanguage, "⚡ Aksi Cepat"),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton("🎁", LanguageManager.translate("profile_action_checkin", currentLanguage, "Check-in"), EmeraldLight, onDailyCheckIn)
                QuickActionButton("⛏️", LanguageManager.translate("profile_action_mining", currentLanguage, "Mining"), ElectricBlue, onMining)
                QuickActionButton("🎰", LanguageManager.translate("profile_action_coinrush", currentLanguage, "Coin Rush"), GoldVip, onCoinRush)
                QuickActionButton("🎯", LanguageManager.translate("profile_action_missions", currentLanguage, "Misi"), Color(0xFFEC4899), onMissions)
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    emoji: String,
    label: String,
    accent: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

// ==========================================================
// UPGRADE TIER — card dengan tier semasa + button
// ==========================================================
@Composable
private fun UpgradeTierCard(
    currentTier: String,
    currentLanguage: AppLanguage,
    onClick: () -> Unit
) {
    val tierInfo = when (currentTier.uppercase()) {
        "ROYAL" -> Triple("💎", LanguageManager.translate("tier_royal", currentLanguage, "Royal"), Color(0xFFB9F2FF))
        "VIP" -> Triple("🥇", LanguageManager.translate("tier_vip", currentLanguage, "VIP"), GoldVip)
        "PREMIUM" -> Triple("🥈", LanguageManager.translate("tier_premium", currentLanguage, "Premium"), Color(0xFFC0C0C0))
        "STARTER" -> Triple("🥉", LanguageManager.translate("tier_starter", currentLanguage, "Pemula"), Color(0xFFCD7F32))
        else -> Triple("🌱", LanguageManager.translate("tier_free", currentLanguage, "Gratis"), EmeraldLight)
    }

    FramedCard(
        frameRes = R.drawable.card_frame_tier,
        contentPadding = 40.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tierInfo.third.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(tierInfo.first, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    String.format(LanguageManager.translate("profile_tier_you", currentLanguage, "Tingkat Anda: %s"), tierInfo.second),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (currentTier == "ROYAL") LanguageManager.translate("profile_tier_max_msg", currentLanguage, "Tingkat tertinggi tercapai! 🎉") else LanguageManager.translate("profile_tier_desc", currentLanguage, "Upgrade untuk slot miner & komisi lebih besar"),
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = tierInfo.third,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    if (currentTier == "ROYAL") LanguageManager.translate("profile_tier_max", currentLanguage, "MAX") else LanguageManager.translate("profile_tier_upgrade", currentLanguage, "Upgrade"),
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// ==========================================================
// REFERRAL STATS — 2 kolum
// ==========================================================
@Composable
private fun ReferralStatsCard(
    referredCount: Int,
    commissionIdr: Double,
    currentLanguage: AppLanguage
) {
    FramedCard(
        frameRes = R.drawable.card_frame_referral,
        contentPadding = 40.dp
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Teman diundang
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Text(LanguageManager.translate("profile_referral_friends", currentLanguage, "👥 Teman"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "$referredCount",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldLight
                )
                Text(LanguageManager.translate("profile_referral_invited", currentLanguage, "Diundang"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )
            // Komisi
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Text(LanguageManager.translate("profile_commission", currentLanguage, "💰 Komisi"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Rp ${String.format("%,.0f", commissionIdr).replace(",", ".")}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldVip
                )
                Text(LanguageManager.translate("profile_total", currentLanguage, "Total"), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ==========================================================
// MINING STATS — slot, power, token
// ==========================================================
    }
}

// ============================================================
// M47: Avatar dengan Frame Tier (dragon, lion, star, dll)
// ============================================================
@Composable
private fun ProfileAvatarWithFrame(
    userName: String?,
    tier: String,
    size: Dp = 120.dp
) {
    // Frame resource per tier (fallback null kalau tiada)
    val frameRes: Int = when (tier) {
        "ROYAL" -> R.drawable.frame_dragon_royal
        "VIP" -> R.drawable.frame_lion_vip
        "PREMIUM" -> R.drawable.frame_star_premium
        "STARTER" -> R.drawable.frame_bronze_starter
        else -> R.drawable.frame_leaf_free  // FREE default
    }
    
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Avatar bulat di tengah (68% dari frame)
        Box(
            modifier = Modifier
                .size(size * 0.68f)
                .clip(CircleShape)
                .background(
                    when (tier) {
                        "ROYAL" -> ElectricBlue
                        "VIP" -> GoldVip
                        "PREMIUM" -> Color(0xFFC0C0C0)
                        "STARTER" -> Color(0xFFCD7F32)
                        else -> EmeraldLight
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName?.firstOrNull()?.uppercase() ?: "U",
                fontSize = (size.value / 4).sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
        
        // Frame overlay (kalau ada)
        Image(
            painter = painterResource(frameRes),
            contentDescription = "Frame $tier",
            modifier = Modifier.fillMaxSize()
        )
    }
}

// ============================================================
// M48: RTP Amount dengan Coin Icon
// Guna di mana-mana untuk konsisten display RTP
// ============================================================
@Composable
fun RtpAmount(
    amount: String,
    fontSize: TextUnit = 14.sp,
    iconSize: Dp = 16.dp,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_rtp),
            contentDescription = "RTP",
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = amount,
            fontSize = fontSize,
            color = color,
            fontWeight = fontWeight
        )
    }
}

@Composable
private fun MiniStatBox(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = accent)
        }
    }
}
