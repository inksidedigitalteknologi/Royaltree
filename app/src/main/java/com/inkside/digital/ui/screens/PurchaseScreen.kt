package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.PurchaseTier
import com.inkside.digital.util.IapManager
import com.inkside.digital.viewmodel.AffiliateViewModel
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseScreen(
    viewModel: AffiliateViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val tiers by viewModel.purchaseTiers.collectAsState()
    val loading by viewModel.purchaseLoading.collectAsState()
    val message by viewModel.purchaseMessage.collectAsState()
    val isMock by IapManager.isMockMode.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadPurchaseTiers()
    }

    // Auto-clear message
    LaunchedEffect(message) {
        if (message.isNotBlank()) {
            kotlinx.coroutines.delay(4000)
            viewModel.clearPurchaseMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(LanguageManager.translate("purchase_upgrade_tier", currentLanguage, "👑 Upgrade Tier"), fontWeight = FontWeight.Bold)
                        Text(
                            "Buka slot miner & benefit eksklusif",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ============ MOCK MODE BANNER ============
            if (isMock) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🧪", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Mode Testing — pembelian simulasi",
                            fontSize = 12.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ============ MESSAGE ============
            if (message.isNotBlank()) {
                Surface(
                    color = when {
                        message.startsWith("✅") -> Color(0xFFD1FAE5)
                        message.startsWith("❌") -> Color(0xFFFEE2E2)
                        else -> Color(0xFFDBEAFE)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        message,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (loading && tiers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    item {
                        Text(
                            "Pilih tier yang sesuai kebutuhanmu:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(tiers) { tier ->
                        TierCard(
                            tier = tier,
                            isMock = isMock,
                            onBuy = { IapManager.purchase(tier.productId) }
                        )
                    }

                    if (tiers.isEmpty() && !loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 60.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🛒", fontSize = 48.sp)
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        "Produk tidak tersedia",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Coba refresh atau hubungi support",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "💡 Info",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "• Pembelian bersifat lifetime (sekali bayar)\n" +
                                    "• Hanya bisa upgrade, tidak bisa downgrade\n" +
                                    "• Komisi affiliate naik sesuai tier\n" +
                                    "• Slot miner bertambah otomatis",
                                    fontSize = 11.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TierCard(
    tier: PurchaseTier,
    isMock: Boolean,
    onBuy: () -> Unit
) {
    val tierColor = when (tier.tier.uppercase()) {
        "STARTER" -> Color(0xFF3B82F6)
        "PREMIUM" -> Color(0xFF8B5CF6)
        "VIP" -> Color(0xFFF59E0B)
        "ROYAL" -> Color(0xFFEC4899)
        else -> Color.Gray
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, tierColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(tierColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = tierColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        tier.displayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = tierColor
                    )
                    Text(
                        "Tier ${tier.tier}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    "$${tier.priceUsd}",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = tierColor
                )
            }

            Spacer(Modifier.height(12.dp))

            tier.benefits.forEach { benefit ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(benefit, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = tierColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    if (isMock) "🧪 Beli (Mock)" else "Beli Sekarang",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
