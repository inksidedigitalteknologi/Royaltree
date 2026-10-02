package com.inkside.digital.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.viewmodel.AffiliateViewModel
import com.inkside.digital.localization.LanguageManager

private val AffiliateGold = Color(0xFFFFD700)
private val AffiliateAmber = Color(0xFFF59E0B)
private val AffiliateEmerald = Color(0xFF10B981)
private val AffiliateBlue = Color(0xFF3B82F6)

/**
 * AffiliateBalanceCard — papar balance affiliate user.
 *
 * @param balance state dari ViewModel
 * @param onWithdraw callback bila user tekan butang withdraw
 * @param onHistory callback bila user tekan butang history
 */
@Composable
fun AffiliateBalanceCard(
    balance: AffiliateViewModel.AffiliateBalanceData,
    currentLanguage: com.inkside.digital.localization.AppLanguage = com.inkside.digital.localization.AppLanguage.INDONESIAN,
    onWithdraw: () -> Unit = {},
    onHistory: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(AffiliateGold, AffiliateAmber)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💰", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        LanguageManager.translate("profile_affiliate_balance", currentLanguage, "Affiliate Balance"),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        LanguageManager.translate("profile_affiliate_subtitle", currentLanguage, "Komisi dari teman yang upgrade"),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Total Balance
            Text(
                text = LanguageManager.translate("profile_total_balance", currentLanguage, "Total Balance"),
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$${String.format("%.2f", balance.balance)}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = AffiliateGold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Split: Available + Pending
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Available
                Surface(
                    color = AffiliateEmerald.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(LanguageManager.translate("profile_available", currentLanguage, "✅ Available"), fontSize = 9.sp, color = AffiliateEmerald, fontWeight = FontWeight.Bold)
                        Text(
                            "$${String.format("%.2f", balance.available)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = AffiliateEmerald
                        )
                        Text(LanguageManager.translate("profile_ready_withdraw", currentLanguage, "Siap withdraw"), fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Pending
                Surface(
                    color = AffiliateAmber.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(LanguageManager.translate("profile_pending", currentLanguage, "⏳ Pending"), fontSize = 9.sp, color = AffiliateAmber, fontWeight = FontWeight.Bold)
                        Text(
                            "$${String.format("%.2f", balance.pending)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = AffiliateAmber
                        )
                        Text(LanguageManager.translate("profile_hold_24h", currentLanguage, "Hold 24 jam"), fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Butang Withdraw + History
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = if (balance.available >= balance.minWithdraw) AffiliateGold else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = balance.available >= balance.minWithdraw) { onWithdraw() }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (balance.available >= balance.minWithdraw)
                                LanguageManager.translate("profile_withdraw", currentLanguage, "Withdraw")
                            else
                                "Min $${String.format("%.0f", balance.minWithdraw)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (balance.available >= balance.minWithdraw) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(0.6f)
                        .clickable { onHistory() }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            LanguageManager.translate("profile_history", currentLanguage, "History"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Info total earned
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = LanguageManager.translate("profile_total_earned", currentLanguage, "Total seumur hidup") + ": $${String.format("%.2f", balance.totalEarned)}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
