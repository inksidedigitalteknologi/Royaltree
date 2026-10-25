package com.inkside.digital.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.util.GlobalPointsManager

@Composable
fun BusinessHomeScreen(
    user: UserEntity?,
    recentTransactions: List<TransactionEntity>,
    currentLanguage: AppLanguage,
    affiliateBalance: Double = 0.0,
    referralCount: Int = 0,
    referralActive: Int = 0,
    referralPending: Int = 0,
    checkInStreak: Int = 0,
    onNavigate: (String) -> Unit,
    onOpenWithdraw: () -> Unit = {},
    onOpenTransfer: () -> Unit = {},
    onOpenDaily: () -> Unit = {},
    onOpenAds: () -> Unit = {},
    onOpenReferral: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val referralCode = user?.referralCode ?: ""
    val referralLink = "https://royaltree.app/invite/$referralCode"
    var showCopied by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "\uD83D\uDC4B Halo, ${user?.name ?: "User"}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Tier: ${user?.tier ?: "FREE"}",
                        fontSize = 12.sp,
                        color = GoldVip
                    )
                }
                if (checkInStreak > 0) {
                    Surface(
                        color = GoldVip.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("\uD83D\uDD25", fontSize = 14.sp)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "$checkInStreak hari",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldVip
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SaldoCard(
                    title = "RTP",
                    value = GlobalPointsManager.formatRtp(user?.points ?: 0.0),
                    icon = Icons.Filled.Bolt,
                    color = EmeraldLight,
                    modifier = Modifier.weight(1f)
                )
                SaldoCard(
                    title = "Komisi",
                    value = GlobalPointsManager.formatBalanceIdr(affiliateBalance, "id"),
                    icon = Icons.Filled.Paid,
                    color = GoldVip,
                    modifier = Modifier.weight(1f),
                    onClick = onOpenWithdraw
                )
            }
        }

        item {
            Text(
                "\uD83C\uDFAF Aksi Cepat",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickAction(
                    icon = Icons.Filled.Bolt,
                    label = "Produksi",
                    color = EmeraldPrimary
                ) { onNavigate("GAME_ROOM") }
                QuickAction(
                    icon = Icons.Filled.CardGiftcard,
                    label = "Daily",
                    color = Color(0xFFF59E0B)
                ) { onOpenDaily() }
                QuickAction(
                    icon = Icons.Filled.PlayArrow,
                    label = "Ads",
                    color = Color(0xFF3B82F6)
                ) { onOpenAds() }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickAction(
                    icon = Icons.Filled.People,
                    label = "Referral",
                    color = Color(0xFF8B5CF6)
                ) { onOpenReferral() }
                QuickAction(
                    icon = Icons.AutoMirrored.Filled.Send,
                    label = "Transfer",
                    color = Color(0xFF10B981)
                ) { onOpenTransfer() }
                QuickAction(
                    icon = Icons.Filled.Person,
                    label = "Profile",
                    color = Color(0xFF94A3B8)
                ) { onOpenProfile() }
            }
        }

        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "\uD83D\uDD17 Link Referral",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        referralLink,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Referral", referralLink))
                                showCopied = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text(if (showCopied) "\u2705 Copied" else "\uD83D\uDCCB Copy", fontSize = 11.sp)
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Ayo gabung Royaltree! $referralLink")
                                }
                                context.startActivity(Intent.createChooser(intent, "Share"))
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                        ) {
                            Text("\uD83D\uDCE4 Share", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "\uD83D\uDCCA Progres Referral",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total: $referralCount", fontSize = 11.sp, color = Color.White)
                        Text("Aktif: $referralActive", fontSize = 11.sp, color = EmeraldLight)
                        Text("Pending: $referralPending", fontSize = 11.sp, color = GoldVip)
                    }
                    Spacer(Modifier.height(8.dp))
                    val progress = (referralCount / 10.0).coerceIn(0.0, 1.0)
                    LinearProgressIndicator(
                        progress = { progress.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = EmeraldPrimary,
                        trackColor = Color(0xFF334155)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Menuju Silver: $referralCount/10 referral",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "\uD83D\uDCC8 Aktivitas Terbaru",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                TextButton(onClick = { onNavigate("HISTORY") }) {
                    Text("Lihat Semua", fontSize = 10.sp, color = EmeraldLight)
                }
            }
        }
        items(recentTransactions.take(5)) { tx ->
            TransactionItem(tx)
        }

        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "\uD83D\uDCA1 Tips",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldVip
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Ajak 5 teman \u2192 dapat bonus RTP. Semakin banyak referral, semakin tinggi komisi!",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun SaldoCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, fontSize = 10.sp, color = Color(0xFF94A3B8))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Surface(
            color = color.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 9.sp, color = Color(0xFF94A3B8))
    }
}

@Composable
private fun TransactionItem(tx: TransactionEntity) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    tx.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    tx.description ?: "",
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1
                )
            }
            Text(
                text = if (tx.isCredit) "+${tx.amount}" else "-${tx.amount}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (tx.isCredit) EmeraldLight else Color(0xFFEF4444)
            )
        }
    }
}
