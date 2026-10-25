@file:Suppress("DEPRECATION")

package com.inkside.digital.ui.screens

import android.content.ClipData
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.content.Intent
import android.widget.Toast
import com.inkside.digital.data.preferences.AppThemePreferences
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.AffiliateLinkEntity
import com.inkside.digital.data.model.CampaignEntity
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.util.GlobalPointsManager
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager

@Composable
fun CampaignsScreen(
    campaigns: List<CampaignEntity>,
    currentLanguage: AppLanguage,
    links: List<AffiliateLinkEntity>,
    onSelectCampaignForLink: (CampaignEntity) -> Unit,
    onSimulateConversion: (AffiliateLinkEntity) -> Unit,
    onOpenNewLinkModal: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Kampanye Mitra, 1: Tautan Promosi Saya

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Kampanye & Tautan Promosi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "Kelola link affiliasi dan lacak komisi per klik secara real-time",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onOpenNewLinkModal,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("btn_create_link_header")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(LanguageManager.translate("campaigns_create_link_btn", AppThemePreferences.currentLanguage, "Buat Link"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs
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
                text = { Text(LanguageManager.translate("campaigns_tab_catalog", AppThemePreferences.currentLanguage, "Katalog Kampanye (") + "${campaigns.size})", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(LanguageManager.translate("campaigns_tab_my_links", AppThemePreferences.currentLanguage, "Link Aktif Saya (") + "${links.size})", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Campaigns List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(campaigns) { campaign ->
                    CampaignCard(
                        campaign = campaign,
                        onCreateLink = { onSelectCampaignForLink(campaign) }
                    )
                }
                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        } else {
            // Active Links List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (links.isEmpty()) {
                    item {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(44.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(LanguageManager.translate("campaigns_empty_title", AppThemePreferences.currentLanguage, "Belum Ada Tautan Promosi"), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(LanguageManager.translate("campaigns_empty_desc", AppThemePreferences.currentLanguage, "Pilih kampanye di atas untuk mulai membuat tautan promosi pertama Anda."), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = onOpenNewLinkModal) {
                                    Text(LanguageManager.translate("campaigns_create_now_btn", AppThemePreferences.currentLanguage, "Buat Tautan Sekarang"))
                                }
                            }
                        }
                    }
                } else {
                    items(links) { link ->
                        ActiveLinkCard(
                            link = link,
                            currentLanguage = currentLanguage,
                            onCopy = {
                                scope.launch {
                                    clipboard.setClipEntry(
                                        ClipEntry(ClipData.newPlainText(LanguageManager.translate("campaigns_clip_label", AppThemePreferences.currentLanguage, "Link"), link.shortUrl))
                                    )
                                }
                                Toast.makeText(context, LanguageManager.translate("toast_promo_link_copied", AppThemePreferences.currentLanguage, "Tautan promosi disalin!"), Toast.LENGTH_SHORT).show()
                            },
                            onShare = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Dapatkan promo eksklusif: ${link.shortUrl}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Link"))
                            },
                            onSimulate = { onSimulateConversion(link) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }
}

@Composable
fun CampaignCard(
    campaign: CampaignEntity,
    onCreateLink: () -> Unit
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
                        Text(
                            text = campaign.merchantName,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (campaign.isHighTicket) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GoldVip.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(LanguageManager.translate("campaigns_high_ticket", AppThemePreferences.currentLanguage, "HIGH TICKET"), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = campaign.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = campaign.payoutType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(LanguageManager.translate("campaigns_commission_amount", AppThemePreferences.currentLanguage, "Besaran Komisi:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = campaign.commissionDisplay,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = EmeraldLight
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(LanguageManager.translate("campaigns_global_conversion", AppThemePreferences.currentLanguage, "Konversi Global:"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${campaign.conversionsCount}" + LanguageManager.translate("campaigns_transactions_suffix", AppThemePreferences.currentLanguage, " Transaksi"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onCreateLink,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Link, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(LanguageManager.translate("campaigns_create_tracking_btn", AppThemePreferences.currentLanguage, "Buat Link Tracking"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ActiveLinkCard(
    link: AffiliateLinkEntity,
    currentLanguage: AppLanguage,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onSimulate: () -> Unit
) {
    val cr = if (link.clicks > 0) (link.conversions.toDouble() / link.clicks.toDouble()) * 100.0 else 0.0
    val epc = if (link.clicks > 0) link.totalEarnings / link.clicks.toDouble() else 0.0

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
                    Text(
                        text = link.campaignTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = link.shortUrl,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = ElectricBlue
                    )
                    Text(
                        text = "SubID: ${link.subId} • Target: ${link.customSlug}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    IconButton(onClick = onCopy, modifier = Modifier.size(34.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = LanguageManager.translate("cd_copy", AppThemePreferences.currentLanguage, "Salin"), tint = EmeraldLight, modifier = Modifier.size(17.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(34.dp)) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = LanguageManager.translate("cd_share", AppThemePreferences.currentLanguage, "Bagikan"), tint = ElectricBlue, modifier = Modifier.size(17.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-time metrics strip
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(LanguageManager.translate("campaigns_stat_clicks", AppThemePreferences.currentLanguage, "Klik"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(link.clicks.toString(), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(LanguageManager.translate("campaigns_stat_conversions", AppThemePreferences.currentLanguage, "Konversi"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(link.conversions.toString(), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(LanguageManager.translate("campaigns_stat_cr", AppThemePreferences.currentLanguage, "CR %"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format("%.1f%%", cr), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EmeraldLight)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(LanguageManager.translate("campaigns_stat_epc", AppThemePreferences.currentLanguage, "EPC"), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(GlobalPointsManager.formatBalanceIdr(epc, currentLanguage.code), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Komisi: Rp ${String.format("%,.0f", link.totalEarnings)}",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = EmeraldLight
                )

                Button(
                    onClick = onSimulate,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldVip),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(LanguageManager.translate("campaigns_test_conversion", AppThemePreferences.currentLanguage, "Uji Konversi"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
