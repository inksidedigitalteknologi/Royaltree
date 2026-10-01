package com.inkside.digital.ui.screens

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
    minerTokens: Int = 0
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
                Text("👤 Profil", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                Text("Kelola akun & informasi pribadi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Avatar + Tier
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier.size(60.dp).clip(CircleShape)
                            .background(ElectricBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (user?.name?.firstOrNull()?.uppercase() ?: "U"),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user?.name ?: "User", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "-", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.clip(RoundedCornerShape(6.dp))
                                .background(if (user?.tier == "PREMIUM") GoldVip else MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                if (user?.tier == "PREMIUM") "VIP" else "FREE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user?.tier == "PREMIUM") Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Saldo & Poin
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldLight.copy(alpha = 0.1f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Text("Saldo", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "Rp " + String.format("%,.0f", user?.balance ?: 0.0).replace(",", "."),
                            fontSize = 16.sp, fontWeight = FontWeight.Black, color = EmeraldLight
                        )
                    }
                }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ElectricBlue.copy(alpha = 0.1f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Text("Poin", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("✏️ Edit Profil", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Nama Lengkap") }, singleLine = true,
                        shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Email") }, singleLine = true,
                        shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phone, onValueChange = { phone = it },
                        label = { Text("Nomor HP") }, singleLine = true,
                        shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { onSaveProfile(name, email, phone) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // ==== Quick Actions ====
        item {
            QuickActionsRow(
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
                onClick = onOpenUpgrade
            )
        }

        // ==== Referral Stats ====
        item {
            ReferralStatsCard(
                referredCount = user?.referredCount ?: 0,
                commissionIdr = (user?.referredCount ?: 0) * 0.0  // TODO: guna komisi dari backend nanti
            )
        }

        // ==== Mining Stats ====
        item {
            MiningStatsCard(
                slotsUsed = minerSlotsUsed,
                slotsMax = minerSlotsMax,
                powerGhs = minerPowerGhs,
                tokens = minerTokens
            )
        }

        // ==== Affiliate Balance ====
        item {
            AffiliateBalanceCard(
                balance = affiliateBalance,
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
                        Text("Bahasa", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
                    Text("Keluar", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.error)
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
                "⚡ Aksi Cepat",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton("🎁", "Check-in", EmeraldLight, onDailyCheckIn)
                QuickActionButton("⛏️", "Mining", ElectricBlue, onMining)
                QuickActionButton("🎰", "Coin Rush", GoldVip, onCoinRush)
                QuickActionButton("🎯", "Misi", Color(0xFFEC4899), onMissions)
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
    onClick: () -> Unit
) {
    val tierInfo = when (currentTier.uppercase()) {
        "ROYAL" -> Triple("💎", "Royal", Color(0xFFB9F2FF))
        "VIP" -> Triple("🥇", "VIP", GoldVip)
        "PREMIUM" -> Triple("🥈", "Premium", Color(0xFFC0C0C0))
        "STARTER" -> Triple("🥉", "Pemula", Color(0xFFCD7F32))
        else -> Triple("🌱", "Gratis", EmeraldLight)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
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
                    "Tingkat Anda: ${tierInfo.second}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    if (currentTier == "ROYAL") "Tingkat tertinggi tercapai! 🎉" else "Upgrade untuk slot miner & komisi lebih besar",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                color = tierInfo.third,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    if (currentTier == "ROYAL") "MAX" else "Upgrade",
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
    commissionIdr: Double
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Teman diundang
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Text("👥 Teman", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "$referredCount",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldLight
                )
                Text("Diundang", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text("💰 Komisi", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Rp ${String.format("%,.0f", commissionIdr).replace(",", ".")}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldVip
                )
                Text("Total", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ==========================================================
// MINING STATS — slot, power, token
// ==========================================================
@Composable
private fun MiningStatsCard(
    slotsUsed: Int,
    slotsMax: Int,
    powerGhs: Double,
    tokens: Int
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                "⛏️ Status Mining",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniStatBox("Slot", "$slotsUsed/$slotsMax", EmeraldLight, Modifier.weight(1f))
                MiniStatBox("Power", "${powerGhs.toInt()} GH/s", ElectricBlue, Modifier.weight(1f))
                MiniStatBox("Token", "$tokens", GoldVip, Modifier.weight(1f))
            }
        }
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
