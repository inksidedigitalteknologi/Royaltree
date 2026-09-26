package com.inkside.digital.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsWalk
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
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.viewmodel.AppScreen

// ==== Color Palette (Mewah & Elegan) ====
private val DarkNavy = Color(0xFF0A0E1A)
private val CardSlate = Color(0xFF1E293B)
private val GoldAccent = Color(0xFFFFD700)
private val EmeraldLight = Color(0xFF10B981)
private val TextSecondary = Color(0xFF94A3B8)
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
    onOpenAdReward: () -> Unit
) {
    val isPremium = user?.tier == "PREMIUM"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
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
                        color = TextSecondary
                    )
                    Text(
                        text = user?.name ?: "User",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Tier Badge
                Surface(
                    color = if (isPremium) GoldAccent.copy(alpha = 0.2f) else CardSlate,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isPremium) Icons.Filled.WorkspacePremium else Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (isPremium) GoldAccent else TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = user?.tier ?: "FREE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPremium) GoldAccent else TextSecondary
                        )
                    }
                }
            }
        }

        // ==== 2. Balance Hero ====
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = LanguageManager.translate("total_commission", currentLanguage, "Saldo Tersedia"),
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Rp ${String.format("%,.0f", user?.balance ?: 0.0)}",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = EmeraldLight,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenWithdraw() }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 14.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = LanguageManager.translate("btn_withdraw", currentLanguage, "Tarik Dana"),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==== 3. Stats Row ====
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    icon = Icons.Filled.Star,
                    iconColor = GoldAccent,
                    value = "${user?.points ?: 0}",
                    label = LanguageManager.translate("home_points", currentLanguage, "Poin"),
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
                    icon = Icons.Filled.DirectionsWalk,
                    iconColor = ElectricBlue,
                    value = "${user?.todaySteps ?: 0}",
                    label = LanguageManager.translate("home_steps_today", currentLanguage, "Langkah"),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ==== 4. Quick Actions ====
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

        // ==== 5. Ad Banner (Placeholder) ====
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardSlate),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAdReward() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = GoldAccent.copy(alpha = 0.15f),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageManager.translate("sponsor_ad_title", currentLanguage, "Tonton Iklan Sponsor"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Dapat +50 RTP • 30 detik",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
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
        colors = CardDefaults.cardColors(containerColor = CardSlate),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
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
                color = Color.White
            )
        }
    }
}
