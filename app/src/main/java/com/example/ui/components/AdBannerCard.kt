package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdBannerItem

val SAMPLE_BANNER_ADS = listOf(
    AdBannerItem(
        id = "banner_tokopedia",
        sponsorName = "Tokopedia",
        title = "Promo Waktu Indonesia Belanja (WIB)",
        subtitle = "Diskon hingga 90% & Bebas Ongkir ke seluruh kota!",
        ctaText = "Klaim +25 Koin",
        bannerEmoji = "🟢",
        gradientColors = listOf(0xFF03AC0E, 0xFF007A09),
        rewardCoins = 25
    ),
    AdBannerItem(
        id = "banner_shopee",
        sponsorName = "Shopee",
        title = "Shopee Garansi Tepat Waktu",
        subtitle = "Pasti sampai tepat waktu atau dapat voucher kompensasi!",
        ctaText = "Klaim +20 Koin",
        bannerEmoji = "🛍️",
        gradientColors = listOf(0xFFEE4D2D, 0xFFC7280A),
        rewardCoins = 20
    ),
    AdBannerItem(
        id = "banner_dana",
        sponsorName = "DANA Kaget",
        title = "Bagi-Bagi Saldo DANA Kaget Harian",
        subtitle = "Tap banner dan buka amplop keberuntungan kamu hari ini!",
        ctaText = "Klaim +30 Koin",
        bannerEmoji = "💙",
        gradientColors = listOf(0xFF118EEA, 0xFF0860A8),
        rewardCoins = 30
    ),
    AdBannerItem(
        id = "banner_jago",
        sponsorName = "Bank Jago",
        title = "Nabung Kantong Bunga 5% P.A",
        subtitle = "Buka rekening bebas biaya admin selamanya dalam 3 menit!",
        ctaText = "Klaim +50 Koin",
        bannerEmoji = "⭐",
        gradientColors = listOf(0xFFFF7A00, 0xFFC95B00),
        rewardCoins = 50
    )
)

@Composable
fun AdBannerCard(
    banner: AdBannerItem,
    onClaimReward: (AdBannerItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isClaimed by remember { mutableStateOf(false) }

    val gradientBrush = remember(banner.gradientColors) {
        Brush.horizontalGradient(
            colors = banner.gradientColors.map { Color(it) }
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .clickable {
                if (!isClaimed) {
                    isClaimed = true
                    onClaimReward(banner)
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sponsor tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.25f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "IKLAN SPONSOR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.9f),
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "• ${banner.sponsorName}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }

                // Reward badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFD700)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = null,
                            tint = Color(0xFF78350F),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "+${banner.rewardCoins} Koin",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = banner.bannerEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = banner.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = banner.subtitle,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isClaimed) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "✓ Bonus Sudah Diklaim",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                } else {
                    ElevatedButton(
                        onClick = {
                            isClaimed = true
                            onClaimReward(banner)
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(banner.gradientColors.first())
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = ButtonDefaults.ContentPadding
                    ) {
                        Text(
                            text = banner.ctaText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
