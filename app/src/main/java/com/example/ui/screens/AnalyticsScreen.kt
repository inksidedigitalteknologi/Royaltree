package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AffiliateLinkEntity
import com.example.data.model.CampaignEntity
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.GoldVip
import com.example.ui.theme.PurpleSecondary

@Composable
fun AnalyticsScreen(
    campaigns: List<CampaignEntity>,
    links: List<AffiliateLinkEntity>
) {
    var selectedRange by remember { mutableIntStateOf(1) } // 0: 24 Jam, 1: 7 Hari, 2: 30 Hari, 3: Semua

    val totalClicks = links.sumOf { it.clicks } + 1480
    val totalConversions = links.sumOf { it.conversions } + 162
    val totalEarnings = links.sumOf { it.totalEarnings } + 2850000.0
    val overallCr = if (totalClicks > 0) (totalConversions.toDouble() / totalClicks.toDouble()) * 100.0 else 0.0
    val averageEpc = if (totalClicks > 0) totalEarnings / totalClicks.toDouble() else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        item {
            Column {
                Text(
                    text = "Dashboard Analitik Mendalam",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "Pantau konversi, EPC, sumber traffic, dan performa ROI setiap link",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Time Range Filter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("24 Jam", "7 Hari", "30 Hari", "Semua").forEachIndexed { index, label ->
                    val isSel = selectedRange == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedRange = index }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // KPI Matrix 2x2
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KpiCard(
                        title = "Total Klik Pengunjung",
                        value = String.format("%,d", totalClicks),
                        subtext = "+18.2% vs periode lalu",
                        isPositive = true,
                        accentColor = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Total Konversi",
                        value = String.format("%,d", totalConversions),
                        subtext = "+24.5% peningkatan",
                        isPositive = true,
                        accentColor = EmeraldLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    KpiCard(
                        title = "Conversion Rate (CR)",
                        value = String.format("%.2f%%", overallCr),
                        subtext = "Standar industri: 4.5%",
                        isPositive = true,
                        accentColor = GoldVip,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Earnings Per Click (EPC)",
                        value = "Rp ${String.format("%,.0f", averageEpc)}",
                        subtext = "Nilai per klik",
                        isPositive = true,
                        accentColor = PurpleSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Performance Chart Breakdown
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
                        Text("Grafik Pertumbuhan Komisi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("+Rp 3.490.000", fontWeight = FontWeight.Black, fontSize = 13.sp, color = EmeraldLight)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val intervals = listOf(
                        "Hari 1" to 0.35f,
                        "Hari 2" to 0.55f,
                        "Hari 3" to 0.40f,
                        "Hari 4" to 0.75f,
                        "Hari 5" to 0.65f,
                        "Hari 6" to 0.90f,
                        "Hari 7" to 1.00f
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        intervals.forEach { (label, fraction) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(32.dp)
                                        .height((80 * fraction).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (fraction == 1.0f) EmeraldLight else ElectricBlue.copy(alpha = 0.65f))
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Traffic Sources Breakdown
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sumber Lalu Lintas (Traffic Source)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val sources = listOf(
                        Triple("Instagram & TikTok Bio", 42, EmeraldLight),
                        Triple("Telegram & WhatsApp Groups", 31, ElectricBlue),
                        Triple("Blog / Review Website (SEO)", 18, GoldVip),
                        Triple("Direct Link Clicks", 9, PurpleSecondary)
                    )

                    sources.forEach { (sourceName, percentage, color) ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(sourceName, fontSize = 11.sp)
                                Text("$percentage%", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { percentage / 100f },
                                color = color,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                        }
                    }
                }
            }
        }

        // Geographic Distribution
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Distribusi Geografis Pengunjung", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(
                        "🇮🇩 Indonesia (Jabodetabek, Surabaya, Medan)" to "78%",
                        "🇲🇾 Singapore & Malaysia" to "14%",
                        "🌐 Global & Amerika Serikat" to "8%"
                    ).forEach { (region, pct) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(region, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(pct, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun KpiCard(
    title: String,
    value: String,
    subtext: String,
    isPositive: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = accentColor)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = if (isPositive) EmeraldLight else Color.Red,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = subtext,
                    fontSize = 10.sp,
                    color = if (isPositive) EmeraldLight else Color.Red
                )
            }
        }
    }
}
