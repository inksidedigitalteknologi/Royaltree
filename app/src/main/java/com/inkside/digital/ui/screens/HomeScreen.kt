package com.inkside.digital.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.R
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.components.AdBannerCard
import com.inkside.digital.ui.components.SAMPLE_BANNER_ADS
import com.inkside.digital.ui.theme.BrandNavyCard
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    val context = LocalContext.current
    val isPremium = user?.tier == "PREMIUM"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Hero Commission Ticker Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E293B)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = LanguageManager.getString("total_commission", currentLanguage),
                                    fontSize = 12.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "Rp ${String.format("%,.0f", user?.balance ?: 0.0)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldLight
                                )
                            }
                            // Tier Indicator
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isPremium) GoldVip.copy(alpha = 0.2f) else Color(0xFF334155))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isPremium) Icons.Default.Star else Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = if (isPremium) GoldVip else EmeraldLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPremium) "VIP (30%)" else "FREE (12%)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPremium) GoldVip else Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Strip: Pending & Paid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = LanguageManager.getString("pending_balance", currentLanguage),
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "Rp ${String.format("%,.0f", user?.pendingBalance ?: 0.0)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldVip
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFF1E293B).copy(alpha = 0.7f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = LanguageManager.getString("total_paid", currentLanguage),
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "Rp ${String.format("%,.0f", user?.totalPaidOut ?: 0.0)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricBlue
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFF1E293B).copy(alpha = 0.7f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = LanguageManager.getString("activity_points", currentLanguage),
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                    Text(
                                        text = "${user?.points ?: 0} RTP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                    Text(
                                        text = "≈ $${String.format(java.util.Locale.US, "%.2f", (user?.points ?: 0) * 0.01)} USD",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Slot Iklan Banner Sponsor (Indonesia)
        item {
            val bannerAd = SAMPLE_BANNER_ADS.first()
            AdBannerCard(
                banner = bannerAd,
                onClaimReward = { onOpenAdReward() }
            )
        }



        // Daily Login & Task Missions Highlight Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(AppScreen.MISSIONS) }
                    .testTag("home_missions_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF064E3B),
                                    Color(0xFF047857)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(GoldVip.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎁", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Login Harian & Misi RTP",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = GoldVip,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Streak H-${user?.checkInStreak ?: 1}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Klaim bonus login harian s/d +150 RTP dan selesaikan tugas!",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "Buka Misi >",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Row 1: Tarik Dana, Transfer QR, Tukar RTP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Withdraw Button
                    Surface(
                        color = EmeraldPrimary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenWithdraw() }
                            .testTag("action_withdraw")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = LanguageManager.getString("btn_withdraw", currentLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Transfer QR Button
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenTransferQr() }
                            .testTag("action_transfer_qr")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Transfer QR",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Redeem Points
                    Surface(
                        color = PurpleSecondary,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenRedeemPoints() }
                            .testTag("action_redeem_points")
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Redeem, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tukar RTP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Row 2: Langkah Cuan & Unduh App Berhadiah
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Langkah Cuan (Pedometer)
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(AppScreen.STEP_COUNTER) }
                            .testTag("action_step_counter")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Filled.DirectionsWalk, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Langkah Kaki",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Unduh App & Iklan
                    Surface(
                        color = Color(0xFFEA580C),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(AppScreen.APP_OFFERS) }
                            .testTag("action_app_offers")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Filled.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Unduh App",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Row 3: Buat Link & Upgrade VIP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // New Link Button
                    Surface(
                        color = ElectricBlue,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenNewLink() }
                            .testTag("action_new_link")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.AddLink, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = LanguageManager.getString("btn_new_link", currentLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Upgrade VIP
                    Surface(
                        color = GoldVip,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenUpgrade() }
                            .testTag("action_upgrade_vip")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VIP Tier (2.5x)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Live Real-Time Conversion Simulator Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Simulasi Konversi Real-Time",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Uji coba pelacakan komisi instan & push notifikasi saat ada pengunjung bertransaksi.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val activeLink = links.firstOrNull()
                            if (activeLink != null) {
                                onSimulateConversion(activeLink)
                            } else {
                                Toast.makeText(context, "Buat link affiliasi terlebih dahulu", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_trigger_conversion")
                    ) {
                        Text("Simulasikan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 7-Day Performance Sparkline / Mini Trend Chart
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Performa 7 Hari Terakhir", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = "+24.8% Konversi dibanding minggu lalu", fontSize = 11.sp, color = EmeraldLight)
                        }
                        TextButtonNav(text = "Analitik", onClick = { onNavigate(AppScreen.ANALYTICS) })
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Visual Bar Chart
                    val dailyStats = listOf(
                        "Sen" to 0.45f,
                        "Sel" to 0.60f,
                        "Rab" to 0.35f,
                        "Kam" to 0.85f,
                        "Jum" to 0.70f,
                        "Sab" to 0.95f,
                        "Min" to 0.80f
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        dailyStats.forEach { (day, fraction) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height((60 * fraction).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (fraction > 0.8f) EmeraldLight else ElectricBlue.copy(alpha = 0.7f)
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Monetization Ad Integration Banner
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAdReward() }
                    .testTag("ad_monetization_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(GoldVip.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = GoldVip, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Iklan Sponsor Pengembang",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GoldVip)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("REWARD", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                        }
                        Text(
                            text = "Tonton iklan 5 detik untuk klaim +50 Poin bonus komisi!",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Active Promo Links Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tautan Promosi Teratas", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                TextButtonNav(text = "Kelola Semua", onClick = { onNavigate(AppScreen.CAMPAIGNS) })
            }
        }

        // Top 2 Active Links preview
        items(links.take(2)) { link ->
            AffiliateLinkItemCard(
                link = link,
                onCopy = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Affiliate Link", link.shortUrl)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Tautan disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                },
                onSimulate = { onSimulateConversion(link) }
            )
        }

        // Recent Transactions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Riwayat Transaksi Terbaru", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                TextButtonNav(text = "Lihat Semua", onClick = { onNavigate(AppScreen.HISTORY) })
            }
        }

        items(recentTransactions.take(3)) { tx ->
            TransactionItemRow(tx = tx)
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun TextButtonNav(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = EmeraldLight,
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun AffiliateLinkItemCard(
    link: AffiliateLinkEntity,
    onCopy: () -> Unit,
    onSimulate: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = link.campaignTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    Text(
                        text = link.shortUrl,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ElectricBlue
                    )
                }

                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = EmeraldLight, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onSimulate, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = "Simulate", tint = GoldVip, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Klik: ${link.clicks}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Konversi: ${link.conversions}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = "Total: Rp ${String.format("%,.0f", link.totalEarnings)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
            }
        }
    }
}

@Composable
fun TransactionItemRow(tx: TransactionEntity) {
    val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(tx.timestamp))

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = tx.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                Text(text = "${tx.description} • $dateStr", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }

            Text(
                text = "${if (tx.isCredit) "+" else "-"}Rp ${String.format("%,.0f", tx.amount)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = if (tx.isCredit) EmeraldLight else Color(0xFFEF4444)
            )
        }
    }
}
