package com.inkside.digital.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CurrencyBitcoin
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.inkside.digital.R
import com.inkside.digital.data.model.CampaignEntity
import com.inkside.digital.data.model.InvestmentCouponEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.model.WithdrawalEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.security.SecurityManager
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.util.GlobalPointsManager
import kotlinx.coroutines.delay

@Composable
fun WithdrawDialog(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, currency: String, channel: String, provider: String, accountDest: String, accountHolder: String) -> Unit
) {
    var selectedChannel by remember { mutableStateOf("BANK") } // BANK, CRYPTO, E_WALLET
    var selectedRegion by remember { mutableStateOf(user?.regionZone ?: "ID") } // ID, GLOBAL, US, EU
    var provider by remember { mutableStateOf("Bank Central Asia (BCA)") }
    var accountDestination by remember { mutableStateOf("") }
    var accountHolderName by remember { mutableStateOf(user?.name ?: "") }
    var amountText by remember { mutableStateOf("500000") }

    val minLimit = when (selectedChannel) {
        "E_WALLET" -> 50000.0
        "BANK" -> 100000.0
        "CRYPTO" -> 250000.0
        else -> 100000.0
    }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val fee = if (user?.tier == "PREMIUM") 0.0 else when (selectedChannel) {
        "CRYPTO" -> 15000.0
        "BANK" -> 4500.0
        else -> 2000.0
    }
    val netReceived = (amount - fee).coerceAtLeast(0.0)

    val bankOptions = if (selectedRegion == "ID") {
        listOf("Bank Central Asia (BCA)", "Bank Mandiri", "Bank Rakyat Indonesia (BRI)", "Bank Negara Indonesia (BNI)", "CIMB Niaga")
    } else {
        listOf("SEPA Direct Bank Transfer", "SWIFT International Wire", "Chase J.P. Morgan", "HSBC Premier")
    }

    val cryptoOptions = listOf("USDT (TRC-20 Tron Network)", "USDT (BEP-20 BNB Chain)", "Bitcoin (BTC On-Chain)", "Ethereum (ERC-20)", "Solana (SOL)")
    val ewalletOptions = if (selectedRegion == "ID") {
        listOf("GoPay", "Dana", "OVO", "ShopeePay", "LinkAja")
    } else {
        listOf("PayPal Global", "Wise Multi-Currency", "Binance Pay", "Revolut")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tarik Saldo Komisi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = "Tersedia: Rp ${String.format("%,.0f", user?.balance ?: 0.0)}",
                            fontSize = 12.sp,
                            color = EmeraldLight
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Regional Zone Selector
                Text(
                    text = "Zona Wilayah Pembayaran",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ID" to "🇮🇩 Indonesia", "GLOBAL" to "🌐 Global").forEach { (code, label) ->
                        val isSel = selectedRegion == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSel) EmeraldPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedRegion = code
                                    provider = if (code == "ID") "Bank Central Asia (BCA)" else "USDT (TRC-20 Tron Network)"
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) EmeraldLight else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Channel Selector
                Text(
                    text = "Metode Penarikan",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("BANK", "Bank", Icons.Default.AccountBalance),
                        Triple("CRYPTO", "Kripto", Icons.Default.CurrencyBitcoin),
                        Triple("E_WALLET", "E-Wallet", Icons.Default.AccountBalanceWallet)
                    ).forEach { (channel, label, icon) ->
                        val isSel = selectedChannel == channel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) ElectricBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSel) ElectricBlue else Color.Transparent, RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedChannel = channel
                                    provider = when (channel) {
                                        "BANK" -> bankOptions.first()
                                        "CRYPTO" -> cryptoOptions.first()
                                        else -> ewalletOptions.first()
                                    }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSel) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) ElectricBlue else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Provider selection chips
                val currentOptions = when (selectedChannel) {
                    "BANK" -> bankOptions
                    "CRYPTO" -> cryptoOptions
                    else -> ewalletOptions
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    currentOptions.take(3).forEach { opt ->
                        val isSel = provider == opt
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { provider = opt }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isSel) Icons.Default.CheckCircle else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isSel) EmeraldLight else Color.Transparent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = opt, fontSize = 12.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = accountDestination,
                    onValueChange = { accountDestination = it },
                    label = {
                        Text(
                            text = if (selectedChannel == "CRYPTO") "Alamat Wallet (Address)" else "Nomor Rekening / No. HP E-Wallet",
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_account_dest"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accountHolderName,
                    onValueChange = { accountHolderName = it },
                    label = { Text("Nama Pemilik Rekening / Akun", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_account_holder"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Jumlah Penarikan (Rp)", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_withdraw_amount"),
                    singleLine = true
                )

                // Quick buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(100000.0, 500000.0, 1000000.0, user?.balance ?: 0.0).forEach { quick ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { amountText = quick.toLong().toString() }
                                .padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (quick == user?.balance) "Maks" else "${quick.toLong() / 1000}k",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Minimum threshold notice & Breakdown
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Batas Minimum Sistem:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Rp ${String.format("%,.0f", minLimit)}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Biaya Admin Transfer:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (fee == 0.0) "GRATIS (VIP)" else "Rp ${String.format("%,.0f", fee)}",
                                fontSize = 11.sp,
                                color = if (fee == 0.0) EmeraldLight else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Bersih Diterima:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Rp ${String.format("%,.0f", netReceived)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val isAmountValid = amount >= minLimit && amount <= (user?.balance ?: 0.0) && accountDestination.isNotBlank()

                Button(
                    onClick = {
                        onSubmit(
                            amount,
                            if (selectedChannel == "CRYPTO") "USDT" else "IDR",
                            selectedChannel,
                            provider,
                            accountDestination,
                            accountHolderName
                        )
                    },
                    enabled = isAmountValid,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_submit_withdrawal")
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Kirim Pengajuan Penarikan", fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "🔒 Penarikan diproses manual oleh admin untuk mencegah kecurangan & divalidasi ke rekening tujuan.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PaymentGatewaySimulatorModal(
    withdrawal: WithdrawalEntity,
    onDismiss: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    var signature by remember { mutableStateOf("") }

    LaunchedEffect(withdrawal.id) {
        signature = SecurityManager.signPaymentGatewayPayload(withdrawal.id, withdrawal.amount)
        delay(1200)
        step = 2
        delay(1500)
        step = 3
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (step == 3) EmeraldPrimary.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (step < 3) {
                        CircularProgressIndicator(
                            color = ElectricBlue,
                            modifier = Modifier.size(30.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = EmeraldLight,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (step < 3) "Enkripsi Gateway Pembayaran..." else "Pengajuan Terverifikasi!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (step) {
                        1 -> "Menghubungkan ke API Gateway & Enkripsi AES-256 GCM..."
                        2 -> "Memvalidasi tanda tangan kriptografis HMAC-SHA256..."
                        else -> "Pengajuan penarikan dana berhasil diverifikasi dan masuk antrean persetujuan manual admin."
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ref ID:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(withdrawal.txRef, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tujuan:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${withdrawal.providerName} (${withdrawal.accountDestination})", fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nominal Net:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Rp ${String.format("%,.0f", withdrawal.netAmount)}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("HMAC Sig:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(signature, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Selesai & Tutup")
                }
            }
        }
    }
}

@Composable
fun NewLinkModal(
    campaigns: List<CampaignEntity>,
    preselectedCampaign: CampaignEntity?,
    onDismiss: () -> Unit,
    onCreateLink: (campaign: CampaignEntity, slug: String, subId: String) -> Unit
) {
    var selectedCampaign by remember { mutableStateOf(preselectedCampaign ?: campaigns.firstOrNull()) }
    var customSlug by remember { mutableStateOf(selectedCampaign?.title?.take(10)?.replace(" ", "-")?.lowercase() ?: "promo-special") }
    var subId by remember { mutableStateOf("social_media") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Buat Tautan Promosi Afiliasi",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = "Pilih Kampanye:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    campaigns.take(3).forEach { camp ->
                        val isSel = selectedCampaign?.id == camp.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSel) EmeraldPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedCampaign = camp
                                    customSlug = camp.title.take(12).replace(" ", "-").lowercase()
                                }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(camp.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(camp.commissionDisplay, fontSize = 10.sp, color = EmeraldLight)
                                }
                                if (isSel) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = customSlug,
                    onValueChange = { customSlug = it.replace(" ", "-").lowercase() },
                    label = { Text("Custom Slug URL", fontSize = 12.sp) },
                    prefix = { Text("affl.pro/s/", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = subId,
                    onValueChange = { subId = it },
                    label = { Text("SubID Tracking (opsional: ig, tiktok, wa)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        selectedCampaign?.let { camp ->
                            onCreateLink(camp, customSlug, subId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Buat Tautan & Mulai Melacak", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun UpgradeVipModal(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onUpgrade: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Banner Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_premium_upgrade),
                        contentDescription = "Upgrade VIP",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth()
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Upgrade ke Level Premium VIP",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dapatkan komisi hingga 2.5x lebih tinggi, prioritas pencairan, dan dasbor analitik mendalam.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Comparison
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "Tingkat Komisi" to ("12% (Standar)" to "30% (2.5x Ekstra)"),
                            "Biaya Penarikan" to ("Rp 4.500/trx" to "0% Bebas Biaya"),
                            "Waktu Pencairan" to ("1-2 Hari Kerja" to "Prioritas Instan"),
                            "Pasar Sekunder" to ("Fee 5%" to "Fee 0% Bebas"),
                            "Akses Analitik" to ("Dasar" to "Real-time Mendalam")
                        ).forEach { (feat, values) ->
                            val (free, vip) = values
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = feat, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row {
                                    Text(text = free, fontSize = 11.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = vip, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = GoldVip.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Biaya Upgrade:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Rp 250.000 / Sekali Seumur Hidup", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldVip)
                            }
                            Text("+500 Poin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onUpgrade,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldVip),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_confirm_upgrade_vip")
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upgrade Sekarang (Rp 250.000)", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RedeemPointsModal(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onRedeem: (points: Int, wallet: String, phoneOrDest: String, currency: String) -> Unit,
    onOpenTransferQr: (() -> Unit)? = null
) {
    var pointsToRedeem by remember { mutableIntStateOf(1000.coerceAtMost(user?.points ?: 0)) }
    var selectedCurrency by remember { mutableStateOf("USD") } // USD, USDT, IDR, EUR
    var targetWallet by remember { mutableStateOf("PayPal Global") }
    var destinationInput by remember { mutableStateOf(user?.email ?: "") }

    val usdVal = GlobalPointsManager.getUsdValue(pointsToRedeem)
    val convertedVal = GlobalPointsManager.getValueInCurrency(pointsToRedeem, selectedCurrency)
    val formattedConverted = GlobalPointsManager.formatCurrencyDisplay(convertedVal, selectedCurrency)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tukar RTP (Royaltree Point) Global", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp))
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                // Global Standard Rate Explainer Box
                Surface(
                    color = ElectricBlue.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌍 Standar Nilai RTP Global", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ElectricBlue)
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Universal Rate: 100 RTP = $1.00 USD / 1.00 USDT (Rp 16.000). Nilai RTP identik dan adil untuk seluruh pengguna di seluruh dunia.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                onOpenTransferQr?.let { openQr ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = EmeraldPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onDismiss()
                                openQr()
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Mau kirim poin ke pengguna lain?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                                    Text("Gunakan Transfer RTP via Scan QR", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Text("Transfer >", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Saldo RTP Anda: ${user?.points ?: 0} RTP (Setara $${String.format(java.util.Locale.US, "%.2f", GlobalPointsManager.getUsdValue(user?.points ?: 0))} USD)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EmeraldLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Select Currency / Region
                Text("Pilih Mata Uang Pencairan Poin:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        Triple("USD", "$ USD", "PayPal"),
                        Triple("USDT", "₮ USDT", "Crypto TRC-20"),
                        Triple("IDR", "Rp IDR", "GoPay/Dana"),
                        Triple("EUR", "€ EUR", "Wise/SEPA")
                    ).forEach { (code, label, defaultProvider) ->
                        val isSel = selectedCurrency == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    selectedCurrency = code
                                    targetWallet = defaultProvider
                                    if (code == "USDT") {
                                        destinationInput = "T" + (user?.id ?: "001").take(6) + "x99zKcryptowallet"
                                    } else if (code == "USD") {
                                        destinationInput = user?.email ?: "user@paypal.com"
                                    } else if (code == "IDR") {
                                        destinationInput = user?.phone ?: "+62 812-3456-7890"
                                    }
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                                color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Provider selection
                Text("Saluran Pembayaran Poin:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                val providerOptions = when (selectedCurrency) {
                    "USD" -> listOf("PayPal Global", "Payoneer", "Wire Transfer USD")
                    "USDT" -> listOf("USDT TRC-20", "USDT BEP-20", "Binance Pay")
                    "EUR" -> listOf("Wise EUR", "Revolut", "SEPA Transfer")
                    else -> listOf("GoPay", "Dana", "OVO", "BCA IDR")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    providerOptions.forEach { p ->
                        val isSel = targetWallet == p
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) ElectricBlue.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { targetWallet = p }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = p, fontSize = 9.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Points Presets
                Text("Jumlah Poin yang Ditukar:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(250, 500, 1000, user?.points ?: 0).forEach { pts ->
                        val isSel = pointsToRedeem == pts
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) GoldVip.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { pointsToRedeem = pts }
                            .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (pts == (user?.points ?: 0)) "Semua ($pts RTP)" else "$pts RTP",
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) GoldVip else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = destinationInput,
                    onValueChange = { destinationInput = it },
                    label = {
                        Text(
                            when (selectedCurrency) {
                                "USD" -> "Email Akun PayPal"
                                "USDT" -> "Alamat Wallet USDT"
                                "EUR" -> "IBAN / Email Wise"
                                else -> "Nomor HP E-Wallet / No. Rekening"
                            },
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Calculated Output Box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total yang Diterima:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formattedConverted, fontSize = 15.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Nilai Acuan Global (USD):", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$${String.format(java.util.Locale.US, "%.2f", usdVal)} USD (Universal Peg)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onRedeem(pointsToRedeem, targetWallet, destinationInput, selectedCurrency) },
                    enabled = (user?.points ?: 0) >= pointsToRedeem && pointsToRedeem > 0 && destinationInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Konfirmasi Penukaran Poin Global", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BuyCouponModal(
    coupon: InvestmentCouponEntity,
    user: UserEntity?,
    onDismiss: () -> Unit,
    onBuy: (quantity: Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    val totalCost = coupon.unitPrice * quantity

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Beli Kupon Investasi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Text(text = coupon.projectTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = "Kategori: ${coupon.projectCategory} • Risiko: ${coupon.riskRating}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Estimasi Return (APY):", fontSize = 11.sp)
                            Text("${coupon.expectedApyPercent}% p.a.", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Harga per Kupon:", fontSize = 11.sp)
                            Text("Rp ${String.format("%,.0f", coupon.unitPrice)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Durasi Jatuh Tempo:", fontSize = 11.sp)
                            Text("${coupon.durationDays} Hari", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Jumlah Kupon:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Button(
                            onClick = { if (quantity > 1) quantity-- },
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("-", fontSize = 16.sp)
                        }
                        Text(
                            text = quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        )
                        Button(
                            onClick = { if (quantity < coupon.totalUnitsAvailable) quantity++ },
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                        ) {
                            Text("+", fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Biaya:", fontWeight = FontWeight.Bold)
                    Text(
                        "Rp ${String.format("%,.0f", totalCost)}",
                        fontWeight = FontWeight.Black,
                        color = EmeraldLight,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onBuy(quantity) },
                    enabled = (user?.balance ?: 0.0) >= totalCost,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Beli & Tambah ke Portofolio", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ListCouponModal(
    coupon: InvestmentCouponEntity,
    onDismiss: () -> Unit,
    onConfirm: (price: Double, isListing: Boolean) -> Unit
) {
    var priceText by remember { mutableStateOf(coupon.unitPrice.toLong().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Jual ke Pasar Sekunder", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Text(
                    text = "Daftarkan kupon '${coupon.projectTitle}' Anda di bursa sekunder agar dapat dibeli pengguna lain secara transparan.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Harga Jual di Pasar Sekunder (Rp)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (coupon.isListedOnSecondaryMarket) {
                        OutlinedButton(
                            onClick = { onConfirm(0.0, false) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal Jual")
                        }
                    }
                    Button(
                        onClick = {
                            val pr = priceText.toDoubleOrNull() ?: coupon.unitPrice
                            onConfirm(pr, true)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (coupon.isListedOnSecondaryMarket) "Update Harga" else "Pasang Jual")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReviewModal(
    withdrawal: WithdrawalEntity,
    onDismiss: () -> Unit,
    onProcess: (isApprove: Boolean, note: String) -> Unit
) {
    var adminNote by remember { mutableStateOf("Tervalidasi transfer manual via Bank/Kripto Gateway") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PurpleSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verifikasi Admin Manual", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("ID Penarikan: ${withdrawal.id}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("Pemohon: ${withdrawal.accountHolderName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Metode: ${withdrawal.providerName}", fontSize = 12.sp)
                        Text("Rekening/Address: ${withdrawal.accountDestination}", fontSize = 12.sp, color = ElectricBlue)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Nominal Kotor:", fontSize = 11.sp)
                            Text("Rp ${String.format("%,.0f", withdrawal.amount)}", fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Biaya:", fontSize = 11.sp)
                            Text("Rp ${String.format("%,.0f", withdrawal.fee)}", fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bersih Dibayarkan:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Rp ${String.format("%,.0f", withdrawal.netAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = adminNote,
                    onValueChange = { adminNote = it },
                    label = { Text("Catatan Validasi Admin", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onProcess(false, adminNote) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tolak & Revert")
                    }
                    Button(
                        onClick = { onProcess(true, adminNote) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Setujui & Bayar")
                    }
                }
            }
        }
    }
}

@Composable
fun TwoFactorModal(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onVerifyToggle: (enable: Boolean) -> Unit
) {
    var inputOtp by remember { mutableStateOf("") }
    val currentSimulatedCode = remember { SecurityManager.generateCurrentTotpCode() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (user?.is2FAEnabled == true) "Kelola Autentikasi 2FA" else "Aktifkan 2-Factor Authentication",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "Gunakan aplikasi Google Authenticator untuk keamanan penarikan dana dan data komisi.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Secret Key: ${user?.twoFactorSecret ?: "JBSWY3DPEHPK3PXP"}", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        Text("(Demo Master Code: 123456 atau $currentSimulatedCode)", fontSize = 10.sp, color = EmeraldLight)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = inputOtp,
                    onValueChange = { inputOtp = it.filter { c -> c.isDigit() }.take(6) },
                    label = { Text("Masukkan 6 Digit OTP", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (user?.is2FAEnabled == true) {
                        OutlinedButton(
                            onClick = { onVerifyToggle(false) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Matikan 2FA")
                        }
                    }
                    Button(
                        onClick = {
                            if (SecurityManager.verifyTotpCode(inputOtp)) {
                                onVerifyToggle(true)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Verifikasi & Simpan")
                    }
                }
            }
        }
    }
}

data class EligibleAdVariation(
    val id: String,
    val sponsorName: String,
    val category: String,
    val headline: String,
    val tagLine: String,
    val description: String,
    val rating: String,
    val userCount: String,
    val verifiedBadge: String,
    val ctaText: String,
    val brandColors: List<Color>,
    val iconVector: androidx.compose.ui.graphics.vector.ImageVector,
    val features: List<String>,
    val complianceTag: String
)

@Composable
fun AdRewardModal(
    onDismiss: () -> Unit,
    onRewardEarned: () -> Unit
) {
    val adVariations = remember {
        listOf(
            EligibleAdVariation(
                id = "ad_google_cloud",
                sponsorName = "Google Cloud & Vertex AI",
                category = "Cloud & AI Infrastructure",
                headline = "Bangun Aplikasi Skala Global dengan AI Generatif",
                tagLine = "Kredit Cloud $300 Gratis untuk Pengembang Baru",
                description = "Infrastruktur cloud enterprise dengan latensi rendah, model Gemini siap pakai, dan perlindungan keamanan tier-1.",
                rating = "4.9 ★ (180rb ulasan)",
                userCount = "5Jt+ Developer",
                verifiedBadge = "Google Partner Resmi",
                ctaText = "Buka Akun Google Cloud",
                brandColors = listOf(Color(0xFF1E3A8A), Color(0xFF0284C7)),
                iconVector = Icons.Default.Cloud,
                features = listOf("Uptime SLA 99.99%", "Integrasi Gemini Pro & Flash", "Zero Setup Fee"),
                complianceTag = "Standar Kelayakan: Layanan Cloud Terverifikasi Global"
            ),
            EligibleAdVariation(
                id = "ad_dicoding",
                sponsorName = "Dicoding Academy Indonesia",
                category = "Sertifikasi & Edukasi Digital",
                headline = "Kuasai Android Jetpack Compose & Kotlin Modern",
                tagLine = "Google Authorized Training Partner di Asia Tenggara",
                description = "Pelajari arsitektur Clean Architecture, Room Database, dan StateFlow dengan bimbingan code review 1-on-1 para Google Developer Experts.",
                rating = "4.8 ★ (95rb alumni)",
                userCount = "1.5Jt+ Siswa Terdaftar",
                verifiedBadge = "Mitra Resmi Google Developers",
                ctaText = "Lihat Kelas & Beasiswa",
                brandColors = listOf(Color(0xFF064E3B), Color(0xFF059669)),
                iconVector = Icons.Default.School,
                features = listOf("Review Kode Profesional", "Sertifikat Berstandar Global", "Kurikulum Terkini"),
                complianceTag = "Standar Kelayakan: Edukasi Resmi Tanpa Klaim Menyesatkan"
            ),
            EligibleAdVariation(
                id = "ad_bibit_wealth",
                sponsorName = "Bibit - Investasi Reksadana & SBN",
                category = "Fintech & Manajemen Portofolio",
                headline = "Investasi Otomatis & Aman dengan Bantuan Robo-Advisor",
                tagLine = "Berizin & Diawasi Resmi oleh Otoritas Jasa Keuangan (OJK)",
                description = "Diversifikasi dana komisi affiliasi Anda ke produk reksadana pasar uang dan obligasi negara dengan imbal hasil terukur dan pencairan instan.",
                rating = "4.7 ★ (310rb ulasan)",
                userCount = "10Jt+ Unduhan",
                verifiedBadge = "Berizin & Diawasi OJK",
                ctaText = "Mulai Portofolio Investasi",
                brandColors = listOf(Color(0xFF14532D), Color(0xFF16A34A)),
                iconVector = Icons.Default.TrendingUp,
                features = listOf("Bebas Biaya Komisi Beli/Jual", "Mulai Rp 10.000", "Pencairan Instan 24/7"),
                complianceTag = "Standar Kelayakan: Lembaga Keuangan Berlisensi OJK"
            ),
            EligibleAdVariation(
                id = "ad_tokopedia_official",
                sponsorName = "Sponsor",
                category = "E-Commerce & Gadget Resmi",
                headline = "Promo Super Gadget Smartphone & Tablet Kerja",
                tagLine = "Garansi Resmi Distributor 100% Original",
                description = "Upgrade perangkat kerja affiliasi Anda dengan penawaran cashback spesial s.d Rp 1.500.000 dan proteksi asuransi pengiriman gratis.",
                rating = "4.8 ★ (2.4Jt ulasan)",
                userCount = "100Jt+ Unduhan",
                verifiedBadge = "Official Store Terverifikasi",
                ctaText = "Klaim Voucher Diskon",
                brandColors = listOf(Color(0xFF0F766E), Color(0xFF14B8A6)),
                iconVector = Icons.Default.ShoppingBag,
                features = listOf("Jaminan Asli 100%", "Bebas Ongkir se-Indonesia", "Cicilan 0%"),
                complianceTag = "Standar Kelayakan: Marketplace Resmi & Perlindungan Konsumen"
            ),
            EligibleAdVariation(
                id = "ad_notion_workspace",
                sponsorName = "Notion Workspaces & AI",
                category = "Produktivitas & Manajemen Proyek",
                headline = "Satu Ruang Kerja untuk Seluruh Dokumen, Rencana, & Tim",
                tagLine = "Platform Kolaborasi Kerja Favorit Lebih dari 35 Juta Pengguna",
                description = "Pantau tautan affiliasi, jadwal konten pemasaran, dan pipeline komisi dengan database fleksibel serta asisten AI bawaan.",
                rating = "4.8 ★ (150rb ulasan)",
                userCount = "35Jt+ Pengguna",
                verifiedBadge = "Keamanan Data Standar SOC-2",
                ctaText = "Coba Gratis di Desktop & Mobile",
                brandColors = listOf(Color(0xFF18181B), Color(0xFF3F3F46)),
                iconVector = Icons.Default.Verified,
                features = listOf("Sinkronisasi Real-time", "Template Pelacak Affiliasi", "AI Summary Cepat"),
                complianceTag = "Standar Kelayakan: Software Produktivitas Berstandar Internasional"
            )
        )
    }

    var selectedAdIndex by remember { mutableIntStateOf(0) }
    val currentAd = adVariations[selectedAdIndex % adVariations.size]

    var countdown by remember { mutableIntStateOf(5) }
    var isFinished by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(true) }
    var showComplianceInfo by remember { mutableStateOf(false) }
    var adActionFeedback by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedAdIndex, isPlaying) {
        if (!isPlaying || isFinished) return@LaunchedEffect
        while (countdown > 0 && isPlaying) {
            delay(1000)
            countdown--
        }
        if (countdown <= 0) {
            isFinished = true
        }
    }

    val progress = ((5 - countdown) / 5f).coerceIn(0f, 1f)

    Dialog(onDismissRequest = {
        if (isFinished) onDismiss()
    }) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .testTag("ad_reward_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header: Sponsor Badge & Countdown Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = EmeraldLight.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = EmeraldLight,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "IKLAN BERSPONSOR",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = { showComplianceInfo = !showComplianceInfo },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Informasi Kelayakan",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFinished) EmeraldLight.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.75f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isFinished) "✓ Reward Siap!" else "00:0$countdown",
                                color = if (isFinished) EmeraldLight else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (isFinished) {
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Linear Playback Progress Bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = EmeraldLight,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Ad Variation Selector Pills (Standar Kelayakan: Memperlihatkan ragam variasi kampanye resmi)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Variasi Iklan Mitra:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        adVariations.forEachIndexed { index, ad ->
                            val isSelected = index == selectedAdIndex
                            Surface(
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .clickable {
                                        selectedAdIndex = index
                                        adActionFeedback = null
                                    }
                                    .testTag("ad_variation_pill_$index")
                            ) {
                                Text(
                                    text = "#${index + 1}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Simulated High-Definition Video / Creative Frame
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(175.dp)
                        .testTag("ad_creative_card")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(colors = currentAd.brandColors))
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Top Row of Video Simulation: Category & Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = currentAd.category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = CircleShape,
                                        modifier = Modifier.clickable { isMuted = !isMuted }
                                    ) {
                                        Icon(
                                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                            contentDescription = "Mute",
                                            tint = Color.White,
                                            modifier = Modifier.padding(5.dp).size(14.dp)
                                        )
                                    }
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.4f),
                                        shape = CircleShape,
                                        modifier = Modifier.clickable { isPlaying = !isPlaying }
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Pause",
                                            tint = Color.White,
                                            modifier = Modifier.padding(5.dp).size(14.dp)
                                        )
                                    }
                                }
                            }

                            // Middle: Brand Identity & Headline
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = currentAd.iconVector,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentAd.sponsorName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = currentAd.headline,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White,
                                        maxLines = 2
                                    )
                                }
                            }

                            // Bottom Strip of Creative: Rating & Live Streaming simulation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldVip,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = currentAd.rating,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Surface(
                                    color = Color.Black.copy(alpha = 0.45f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "HD • 1080p",
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ad Description & Highlights Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentAd.tagLine,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                            Surface(
                                color = EmeraldLight.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = currentAd.verifiedBadge,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentAd.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Features List
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currentAd.features.forEach { feat ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "✓ $feat",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive CTA to visit / install sponsor
                OutlinedButton(
                    onClick = {
                        adActionFeedback = "Membuka halaman resmi ${currentAd.sponsorName}..."
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${currentAd.ctaText} (${currentAd.userCount})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                adActionFeedback?.let { feedback ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = feedback,
                        fontSize = 10.sp,
                        color = EmeraldLight,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Expandable Compliance Info Box (Standar Kelayakan)
                if (showComplianceInfo) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = EmeraldLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Standar Kelayakan Kebijakan Google Play",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Iklan berbasis opt-in: Pengguna dengan sadar memilih menonton untuk mendapatkan reward.\n" +
                                        "• Anti-Deceptive: Tidak ada tombol jebakan atau klik paksa.\n" +
                                        "• Verifikasi Pengiklan: Menampilkan pengiklan kredibel dari kategori Cloud, Edukasi, Fintech OJK, & E-Commerce Resmi.\n" +
                                        "• Perlindungan Privasi: Sesuai pedoman Google Play Developer Policy.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Reward Claim Button
                Button(
                    onClick = {
                        onRewardEarned()
                        onDismiss()
                    },
                    enabled = isFinished,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFinished) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_claim_reward")
                ) {
                    if (isFinished) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Klaim +50 RTP Poin Aktivitas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = EmeraldLight
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tonton video sampai selesai (${countdown}d)...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageModal(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pilih Bahasa Internasional", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                AppLanguage.values().forEach { lang ->
                    val isSel = currentLanguage == lang
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSel) EmeraldPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSel) EmeraldPrimary else Color.Transparent, RoundedCornerShape(10.dp))
                            .clickable {
                                onSelectLanguage(lang)
                                onDismiss()
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(lang.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(lang.displayName, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                            }
                            if (isSel) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyCheckInModal(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onClaim: () -> Unit
) {
    val streakDay = user?.checkInStreak ?: 1
    val isCheckedInToday = remember(user?.lastCheckInDate) {
        val lastDay = (user?.lastCheckInDate ?: 0L) / 86400000L
        val today = System.currentTimeMillis() / 86400000L
        (user?.lastCheckInDate ?: 0L) > 0 && lastDay == today
    }
    val currentReward = GlobalPointsManager.getLoginRewardForStreak(streakDay)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Check-in Login Harian 🌟",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(GoldVip.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌲", fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Royaltree Daily Streak Tracker",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Bangun streak 7 hari berturut-turut untuk meraih Mega Jackpot +150 RTP!",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 7-day grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..7).forEach { day ->
                        val reward = GlobalPointsManager.getLoginRewardForStreak(day)
                        val isPastOrCurrent = day <= streakDay && isCheckedInToday
                        val isCurrentTarget = day == streakDay && !isCheckedInToday

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isPastOrCurrent -> EmeraldLight.copy(alpha = 0.2f)
                                        isCurrentTarget -> GoldVip.copy(alpha = 0.25f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isCurrentTarget) GoldVip else if (isPastOrCurrent) EmeraldLight else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 6.dp)
                        ) {
                            Text("H-$day", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(if (day == 7) "🎁" else "⭐", fontSize = 12.sp)
                            Text(
                                "+$reward",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPastOrCurrent) EmeraldLight else if (isCurrentTarget) GoldVip else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onClaim,
                    enabled = !isCheckedInToday,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isCheckedInToday) Color.Gray else EmeraldPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("modal_claim_checkin_button")
                ) {
                    Icon(
                        imageVector = if (isCheckedInToday) Icons.Default.CheckCircle else Icons.Default.CardGiftcard,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCheckedInToday) "Sudah Check-in Hari Ini (H-$streakDay)" else "Klaim Bonus Login (+$currentReward RTP)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
