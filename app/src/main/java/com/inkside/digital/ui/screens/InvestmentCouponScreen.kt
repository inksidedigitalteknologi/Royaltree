package com.inkside.digital.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.R
import com.inkside.digital.data.model.InvestmentCouponEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.util.GlobalPointsManager
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager

@Composable
fun InvestmentCouponScreen(
    currentLanguage: AppLanguage,
    user: UserEntity?,
    coupons: List<InvestmentCouponEntity>,
    onBuyCoupon: (InvestmentCouponEntity) -> Unit,
    onListCoupon: (InvestmentCouponEntity) -> Unit,
    onClaimYield: (InvestmentCouponEntity) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Katalog, 1: Portofolio Saya, 2: Pasar Sekunder

    val myPortfolio = coupons.filter { it.ownedUnits > 0 }
    val secondaryMarketList = coupons.filter { it.isListedOnSecondaryMarket }

    val totalInvested = myPortfolio.sumOf { it.unitPrice * it.ownedUnits }
    val totalAccumYield = myPortfolio.sumOf { it.accumulatedYield }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        // Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.banner_invest_coupon),
                            contentDescription = LanguageManager.translate("cd_investment_coupon", currentLanguage, "Investasi Kupon"),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Portfolio Stats Strip
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(LanguageManager.translate("investment_total_value", currentLanguage, "Total Nilai Investasi:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(GlobalPointsManager.formatBalanceIdr(totalInvested, currentLanguage.code), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(LanguageManager.translate("investment_dividend_return", currentLanguage, "Hasil Return Dividen:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+Rp ${String.format("%,.0f", totalAccumYield)}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = EmeraldLight)
                            }
                        }
                    }
                }
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldLight,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = EmeraldLight
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text(LanguageManager.translate("investment_tab_catalog", currentLanguage, "Katalog Proyek"), fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text(LanguageManager.translate("investment_tab_portfolio", currentLanguage, "Portofolio (") + "${myPortfolio.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text(LanguageManager.translate("investment_tab_secondary", currentLanguage, "Pasar Sekunder (") + "${secondaryMarketList.size})", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        when (selectedTab) {
            0 -> {
                // Catalog List
                items(coupons) { coupon ->
                    CouponCatalogCard(
                        coupon = coupon,
                        currentLanguage = currentLanguage,
                        onBuy = { onBuyCoupon(coupon) }
                    )
                }
            }
            1 -> {
                // My Portfolio
                if (myPortfolio.isEmpty()) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(LanguageManager.translate("investment_empty_coupon_title", currentLanguage, "Belum Ada Kupon Investasi"), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(LanguageManager.translate("investment_empty_coupon_desc", currentLanguage, "Pilih kupon di Katalog untuk mulai mendiversifikasi saldo komisi Anda."), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(myPortfolio) { coupon ->
                        MyCouponPortfolioCard(
                            coupon = coupon,
                            currentLanguage = currentLanguage,
                            onListToMarket = { onListCoupon(coupon) },
                            onClaim = { onClaimYield(coupon) }
                        )
                    }
                }
            }
            2 -> {
                // Secondary Market
                item {
                    Surface(
                        color = PurpleSecondary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Storefront, contentDescription = null, tint = PurpleSecondary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bursa Sekunder Peer-to-Peer: Jual & Beli kupon hasil investasi kapan saja sebelum jatuh tempo.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                if (secondaryMarketList.isEmpty()) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(LanguageManager.translate("investment_empty_secondary_desc", currentLanguage, "Belum ada kupon yang dipasang di pasar sekunder saat ini."), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                } else {
                    items(secondaryMarketList) { coupon ->
                        SecondaryMarketCard(
                            coupon = coupon,
                            currentLanguage = currentLanguage,
                            onBuy = { onBuyCoupon(coupon) }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun CouponCatalogCard(
    coupon: InvestmentCouponEntity,
    currentLanguage: AppLanguage,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(coupon.projectCategory, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(LanguageManager.translate("investment_coupon_risk", currentLanguage, "Risiko: ") + "${coupon.riskRating}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(coupon.projectTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${coupon.expectedApyPercent}% p.a.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(LanguageManager.translate("investment_coupon_unit_price", currentLanguage, "Harga Unit:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(GlobalPointsManager.formatBalanceIdr(coupon.unitPrice, currentLanguage.code), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Column {
                        Text(LanguageManager.translate("investment_coupon_tenor", currentLanguage, "Tenor:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${coupon.durationDays}" + LanguageManager.translate("investment_coupon_days_suffix", currentLanguage, " Hari"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(LanguageManager.translate("investment_coupon_available", currentLanguage, "Tersedia:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${coupon.totalUnitsAvailable}" + LanguageManager.translate("investment_coupon_units_suffix", currentLanguage, " Kupon"), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(LanguageManager.translate("investment_coupon_buy_btn", currentLanguage, "Beli Kupon Investasi"), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MyCouponPortfolioCard(
    coupon: InvestmentCouponEntity,
    currentLanguage: AppLanguage,
    onListToMarket: () -> Unit,
    onClaim: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(coupon.projectTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(LanguageManager.translate("investment_portfolio_ownership", currentLanguage, "Kepemilikan: ") + "${coupon.ownedUnits}" + LanguageManager.translate("investment_portfolio_units_suffix", currentLanguage, " Unit Kupon"), fontSize = 11.sp, color = EmeraldLight, fontWeight = FontWeight.Bold)
                }

                if (coupon.isListedOnSecondaryMarket) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PurpleSecondary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(LanguageManager.translate("investment_portfolio_listed", currentLanguage, "DIPASANG JUAL"), fontSize = 9.sp, fontWeight = FontWeight.Black, color = PurpleSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(LanguageManager.translate("investment_portfolio_total", currentLanguage, "Total Nilai:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(GlobalPointsManager.formatBalanceIdr(coupon.unitPrice * coupon.ownedUnits, currentLanguage.code), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(LanguageManager.translate("investment_portfolio_dividend", currentLanguage, "Dividen Berjalan:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(GlobalPointsManager.formatBalanceIdr(coupon.accumulatedYield, currentLanguage.code), fontWeight = FontWeight.Black, fontSize = 12.sp, color = EmeraldLight)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onListToMarket,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (coupon.isListedOnSecondaryMarket) "Ubah Pasar Sekunder" else "Jual ke Pasar Sekunder", fontSize = 11.sp)
                }

                Button(
                    onClick = onClaim,
                    enabled = coupon.accumulatedYield > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(LanguageManager.translate("investment_portfolio_claim_yield", currentLanguage, "Klaim Yield"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SecondaryMarketCard(
    coupon: InvestmentCouponEntity,
    currentLanguage: AppLanguage,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
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
                    Text(coupon.projectTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(LanguageManager.translate("investment_secondary_apy", currentLanguage, "Return APY: ") + "${coupon.expectedApyPercent}" + LanguageManager.translate("investment_secondary_apy_suffix", currentLanguage, "% p.a. • Sisa: ") + "${coupon.durationDays}" + LanguageManager.translate("investment_coupon_days_suffix", currentLanguage, " Hari"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PurpleSecondary.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(LanguageManager.translate("investment_secondary_badge", currentLanguage, "SEKUNDER"), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PurpleSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(LanguageManager.translate("investment_secondary_sell_price", currentLanguage, "Harga Jual:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = GlobalPointsManager.formatBalanceIdr(coupon.secondaryMarketPrice, currentLanguage.code),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = EmeraldLight
                    )
                }

                Button(
                    onClick = onBuy,
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(LanguageManager.translate("investment_secondary_buy_now", currentLanguage, "Beli Sekarang"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
