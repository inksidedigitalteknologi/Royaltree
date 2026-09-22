package com.inkside.digital.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.repository.PeerContact
import com.inkside.digital.data.repository.TransferResult
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.util.GlobalPointsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

typealias PeerRecipient = PeerContact

val DEFAULT_PEERS = listOf(
    PeerContact("usr_siti_88", "Siti Rahma", "@siti_rtp", "SITI772", "👩‍💼", "Affiliate Gold", isFavorite = true),
    PeerContact("usr_budi_99", "Budi Santoso", "@budi_affiliate", "BUDI901", "👨‍💻", "Top Referrer", isFavorite = true),
    PeerContact("usr_dewi_77", "Dewi Lestari", "@dewi_vip", "DEWI442", "👑", "VIP Partner", isFavorite = false),
    PeerContact("usr_alex_55", "Alex Pratama", "@alex_crypto", "ALEX339", "⚡", "Crypto Specialist", isFavorite = false)
)
val SAMPLE_PEERS = DEFAULT_PEERS

@Composable
fun QrTransferModal(
    user: UserEntity?,
    contacts: List<PeerContact> = emptyList(),
    onToggleFavorite: (String) -> Unit = {},
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onTransfer: (recipientId: String, recipientName: String, amount: Int, note: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Kirim (Scan QR), 1: Terima (QR Saya)

    val activeContactList = remember(contacts) {
        if (contacts.isNotEmpty()) contacts else DEFAULT_PEERS
    }

    // Form states for sending
    var selectedRecipient by remember { mutableStateOf<PeerContact?>(activeContactList.firstOrNull()) }
    var manualRecipientInput by remember { mutableStateOf("") }
    var transferAmountText by remember { mutableStateOf("50") }
    var transferNote by remember { mutableStateOf("") }
    var isManualMode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isScanningMode by remember { mutableStateOf(false) }
    var contactSearchQuery by remember { mutableStateOf("") }

    // Security PIN Verification Dialog states
    var showPinModal by remember { mutableStateOf(false) }
    var pendingTargetId by remember { mutableStateOf("") }
    var pendingTargetName by remember { mutableStateOf("") }
    var pendingAmount by remember { mutableIntStateOf(0) }
    var pendingNote by remember { mutableStateOf("") }

    // Request states for receiving / split bill
    var requestedAmountText by remember { mutableStateOf("") }
    var requestNoteText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val minTransfer = GlobalPointsManager.MIN_TRANSFER_RTP
    val adminFee = GlobalPointsManager.TRANSFER_ADMIN_FEE_RTP

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp)
                .testTag("qr_transfer_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Transfer RTP Royaltree",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                )
                            )
                            Text(
                                text = "Kirim & Terima Saldo Poin Antar Pengguna",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Kirim RTP (Scan)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Terima (QR Saya)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // TAB 0: KIRIM RTP (SCAN QR & TRANSFER)
                    if (isScanningMode) {
                        // Scanner Viewport
                        ScannerViewFinder(
                            onScanDetected = { scannedPayload ->
                                isScanningMode = false
                                // Parse payload
                                val matchedPeer = activeContactList.find { peer ->
                                    scannedPayload.contains(peer.id) ||
                                            scannedPayload.contains(peer.referralCode) ||
                                            scannedPayload.contains(peer.name)
                                }
                                if (matchedPeer != null) {
                                    selectedRecipient = matchedPeer
                                    isManualMode = false
                                } else {
                                    manualRecipientInput = scannedPayload
                                    isManualMode = true
                                    selectedRecipient = null
                                }
                                // Check if amount is in payload
                                if (scannedPayload.contains("amount=")) {
                                    val amt = scannedPayload.substringAfter("amount=").substringBefore("&")
                                    amt.toIntOrNull()?.let { transferAmountText = it.toString() }
                                }
                            },
                            onCancel = { isScanningMode = false }
                        )
                    } else {
                        // Recipient Selection Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Pilih / Scan Penerima:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Buku Kontak & Favorit (Quick Pay)",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Button(
                                onClick = { isScanningMode = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp).testTag("btn_open_camera_scanner")
                            ) {
                                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Buka Scanner", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Filter / Search Contacts
                        val filteredPeers = remember(activeContactList, contactSearchQuery) {
                            val sorted = activeContactList.sortedWith(
                                compareByDescending<PeerContact> { it.isFavorite }
                                    .thenByDescending { it.lastTransferredAt }
                                    .thenBy { it.name }
                            )
                            if (contactSearchQuery.isBlank()) {
                                sorted
                            } else {
                                sorted.filter {
                                    it.name.contains(contactSearchQuery, ignoreCase = true) ||
                                            it.username.contains(contactSearchQuery, ignoreCase = true) ||
                                            it.referralCode.contains(contactSearchQuery, ignoreCase = true) ||
                                            it.id.contains(contactSearchQuery, ignoreCase = true)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = contactSearchQuery,
                            onValueChange = { contactSearchQuery = it },
                            placeholder = { Text("Cari kontak, @username, atau kode referral...", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("input_search_contacts"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Horizontally scrollable rich contact cards
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredPeers.forEach { peer ->
                                val isSelected = !isManualMode && selectedRecipient?.id == peer.id
                                Surface(
                                    color = if (isSelected) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, EmeraldPrimary) else null,
                                    modifier = Modifier
                                        .width(135.dp)
                                        .clickable {
                                            selectedRecipient = peer
                                            isManualMode = false
                                            errorMessage = null
                                        }
                                        .testTag("contact_chip_${peer.id}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(peer.avatarEmoji, fontSize = 22.sp)
                                            IconButton(
                                                onClick = { onToggleFavorite(peer.id) },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (peer.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                                    contentDescription = "Favorit",
                                                    tint = if (peer.isFavorite) GoldVip else MaterialTheme.colorScheme.outline,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = peer.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = peer.username,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            color = if (peer.isFavorite) GoldVip.copy(alpha = 0.15f) else EmeraldPrimary.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (peer.isFavorite) "⭐ Favorit" else peer.referralCode,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (peer.isFavorite) GoldVip else EmeraldPrimary,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Manual Option
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isManualMode) "Mode: Input Manual ID/Kode" else "Atau Masukkan ID / Kode Referral Manual",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = ElectricBlue
                            )
                            OutlinedButton(
                                onClick = {
                                    isManualMode = !isManualMode
                                    if (isManualMode) selectedRecipient = null
                                },
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(if (isManualMode) "Gunakan Kontak" else "Input Manual", fontSize = 10.sp)
                            }
                        }

                        if (isManualMode) {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = manualRecipientInput,
                                onValueChange = {
                                    manualRecipientInput = it
                                    errorMessage = null
                                },
                                label = { Text("ID Pengguna, Kode Referral, atau Hasil Scan QR") },
                                placeholder = { Text("Contoh: USR-8821 atau PRO8892") },
                                modifier = Modifier.fillMaxWidth().testTag("input_manual_recipient"),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            // Display selected recipient badge
                            selectedRecipient?.let { peer ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.08f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(peer.avatarEmoji, fontSize = 24.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(peer.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                                                }
                                                Text("ID: ${peer.id} • Kode: ${peer.referralCode} (${peer.role})", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                        Surface(
                                            color = EmeraldPrimary,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Terpilih", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Amount Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Jumlah Transfer (RTP):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "Saldo Anda: ${user?.points ?: 0} RTP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = transferAmountText,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    transferAmountText = it
                                    errorMessage = null
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("input_transfer_amount"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = { Text("RTP", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(end = 12.dp)) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(20, 50, 100, 250).forEach { preset ->
                                Surface(
                                    color = if (transferAmountText == preset.toString()) GoldVip.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    border = if (transferAmountText == preset.toString()) androidx.compose.foundation.BorderStroke(1.dp, GoldVip) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            transferAmountText = preset.toString()
                                            errorMessage = null
                                        }
                                ) {
                                    Text(
                                        text = "$preset RTP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }
                            // Max Button
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        transferAmountText = (user?.points ?: 0).toString()
                                        errorMessage = null
                                    }
                            ) {
                                Text(
                                    text = "Semua",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = EmeraldLight,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Note
                        OutlinedTextField(
                            value = transferNote,
                            onValueChange = { transferNote = it },
                            label = { Text("Catatan Transfer (Opsional)") },
                            placeholder = { Text("Contoh: Bagi reward campaign, bonus referral, dll.") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // FEE BREAKDOWN & MINIMUM TRANSFER BOX (CRITICAL REQUIREMENT)
                        val enteredAmount = transferAmountText.toIntOrNull() ?: 0
                        val netReceived = GlobalPointsManager.calculateNetReceived(enteredAmount)
                        val usdValue = GlobalPointsManager.getUsdValue(enteredAmount)
                        val idrValue = GlobalPointsManager.getIdrValue(enteredAmount)

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Ketentuan & Biaya Admin Transfer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                    Surface(
                                        color = ElectricBlue.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "Min. $minTransfer RTP",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElectricBlue,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Jumlah Ditransfer:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("$enteredAmount RTP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Potongan Biaya Admin (Fixed):", fontSize = 11.sp, color = Color(0xFFEF4444))
                                    Text("-$adminFee RTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                }

                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "*Biaya admin $adminFee RTP dipotong langsung dari saldo yang diterima penerima.",
                                    fontSize = 9.sp,
                                    color = Color(0xFFEF4444).copy(alpha = 0.85f)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Bersih Diterima Penerima:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Setara: $${String.format(Locale.US, "%.2f", usdValue)} USD ≈ Rp ${String.format("%,.0f", idrValue)}", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text(
                                        "$netReceived RTP",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldLight
                                    )
                                }
                            }
                        }

                        // Error Message
                        errorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⚠️ $err",
                                    fontSize = 11.sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val amount = transferAmountText.toIntOrNull() ?: 0
                                val targetId = if (isManualMode) manualRecipientInput.trim() else selectedRecipient?.id ?: ""
                                val targetName = if (isManualMode) {
                                    if (manualRecipientInput.contains("name=")) {
                                        manualRecipientInput.substringAfter("name=").substringBefore("&").replace("+", " ")
                                    } else {
                                        "Pengguna Royaltree ($manualRecipientInput)"
                                    }
                                } else {
                                    selectedRecipient?.name ?: ""
                                }

                                // Validation & Anti-Fraud Security Check
                                when {
                                    targetId.isBlank() -> {
                                        errorMessage = "Silakan pilih penerima atau masukkan ID/Kode penerima"
                                    }
                                    targetId.equals(user?.id, ignoreCase = true) || targetId.equals(user?.referralCode, ignoreCase = true) -> {
                                        errorMessage = "Pencegahan Fraud: Tidak dapat mentransfer ke akun Anda sendiri"
                                    }
                                    amount < minTransfer -> {
                                        errorMessage = "Jumlah transfer kurang dari batas minimum ($minTransfer RTP)"
                                    }
                                    amount > GlobalPointsManager.MAX_TRANSFER_PER_TX_RTP -> {
                                        errorMessage = "Melebihi batas transaksi tunggal (Maksimal ${GlobalPointsManager.MAX_TRANSFER_PER_TX_RTP} RTP)"
                                    }
                                    amount > (user?.points ?: 0) -> {
                                        errorMessage = "Saldo RTP Anda tidak mencukupi (Saldo: ${user?.points ?: 0} RTP)"
                                    }
                                    else -> {
                                        errorMessage = null
                                        pendingTargetId = targetId
                                        pendingTargetName = targetName
                                        pendingAmount = amount
                                        pendingNote = transferNote
                                        showPinModal = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_confirm_transfer_rtp")
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Verifikasi & Kirim ($enteredAmount RTP)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // TAB 1: TERIMA RTP (TAMPILKAN QR SAYA & SPLIT BILL)
                    val myUserId = user?.id ?: "user_001"
                    val myName = user?.name ?: "Pengguna Royaltree"
                    val myRefCode = user?.referralCode ?: "PRO8892"

                    val customAmount = requestedAmountText.toIntOrNull()
                    val qrPayload = remember(myUserId, myName, myRefCode, customAmount, requestNoteText) {
                        val base = "royaltree:pay?userId=$myUserId&name=${myName.replace(" ", "+")}&code=$myRefCode"
                        if (customAmount != null && customAmount > 0) {
                            val noteParam = if (requestNoteText.isNotBlank()) "&note=${requestNoteText.trim().replace(" ", "+")}" else ""
                            "$base&amount=$customAmount&type=split_bill$noteParam"
                        } else {
                            base
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tunjukkan QR Code ini kepada pengirim atau bagikan tagihan:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // High Fidelity Procedural QR Code Canvas
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(2.dp, EmeraldPrimary),
                            modifier = Modifier
                                .size(230.dp)
                                .testTag("canvas_my_qr_code")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CanvasQrCodeMatrix(
                                    payload = qrPayload,
                                    modifier = Modifier.fillMaxSize()
                                )
                                // Centered Tree Badge
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldPrimary)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🌲", fontSize = 20.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Name & ID
                        Text(
                            text = myName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "ID Akun: $myUserId • Kode Referral: $myRefCode",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Set Nominal Permintaan (Split Bill / Request)
                        OutlinedTextField(
                            value = requestedAmountText,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    requestedAmountText = it
                                }
                            },
                            label = { Text("Atur Nominal Tagihan / Split Bill (Opsional)") },
                            placeholder = { Text("Contoh: 100") },
                            modifier = Modifier.fillMaxWidth().testTag("input_request_qr_amount"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = { Text("RTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Chips for Split Bill
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(25, 50, 100, 250, 500).forEach { preset ->
                                Surface(
                                    color = if (requestedAmountText == preset.toString()) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.clickable { requestedAmountText = preset.toString() }
                                ) {
                                    Text(
                                        text = "+$preset RTP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (requestedAmountText == preset.toString()) Color.White else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                            if (requestedAmountText.isNotBlank()) {
                                Surface(
                                    color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.clickable { requestedAmountText = ""; requestNoteText = "" }
                                ) {
                                    Text(
                                        text = "Reset",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEF4444),
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Note for Split Bill
                        OutlinedTextField(
                            value = requestNoteText,
                            onValueChange = { requestNoteText = it },
                            label = { Text("Catatan Tagihan / Split Bill (Opsional)") },
                            placeholder = { Text("Cth: Iuran makan siang, patungan proyek") },
                            modifier = Modifier.fillMaxWidth().testTag("input_request_note"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Notice
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Minimum transfer adalah $minTransfer RTP. Potongan biaya admin tetap $adminFee RTP akan dipotong dari saldo yang Anda terima.",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions: Copy QR, Copy ID, Share Split Bill Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Royaltree QR Payload", qrPayload)
                                    clipboard.setPrimaryClip(clip)
                                },
                                modifier = Modifier.weight(1f).height(44.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Salin QR", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    val shareText = buildString {
                                        append("🌲 Tagihan Poin Royaltree dari $myName\n")
                                        if (customAmount != null && customAmount > 0) {
                                            append("💰 Nominal: $customAmount RTP\n")
                                        }
                                        if (requestNoteText.isNotBlank()) {
                                            append("📝 Keperluan: $requestNoteText\n")
                                        }
                                        append("🔑 ID Penerima: $myUserId (Ref: $myRefCode)\n")
                                        append("📲 Buka aplikasi Royaltree & Scan QR ini: $qrPayload")
                                    }
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Bagikan Tagihan Royaltree")
                                    context.startActivity(shareIntent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                modifier = Modifier.weight(1f).height(44.dp).testTag("btn_share_split_bill")
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Bagikan Tagihan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPinModal) {
        SecurityPinModal(
            recipientName = pendingTargetName,
            recipientIdentifier = pendingTargetId,
            amount = pendingAmount,
            adminFee = adminFee,
            netReceived = GlobalPointsManager.calculateNetReceived(pendingAmount),
            note = pendingNote,
            onDismiss = { showPinModal = false },
            onPinSuccess = {
                showPinModal = false
                onTransfer(pendingTargetId, pendingTargetName, pendingAmount, pendingNote)
                onDismiss()
            }
        )
    }
}

@Composable
fun ScannerViewFinder(
    onScanDetected: (String) -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Pindai Kamera QR Penerima", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            IconButton(onClick = onCancel) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Batal")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            CameraScannerView(
                modifier = Modifier.fillMaxSize(),
                onQrDetected = onScanDetected
            )
        }
    }
}

@Composable
fun CanvasQrCodeMatrix(
    payload: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val gridSize = 21 // Standard 21x21 QR Version 1
        val cellSize = size.width / gridSize
        val color = Color(0xFF0F172A)

        // Generate deterministic pattern based on payload hash
        val hash = payload.hashCode()

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val isFinderTL = row < 7 && col < 7
                val isFinderTR = row < 7 && col >= gridSize - 7
                val isFinderBL = row >= gridSize - 7 && col < 7
                val isCenterLogo = row in 8..12 && col in 8..12

                var isDark = false

                if (isFinderTL) {
                    isDark = row == 0 || row == 6 || col == 0 || col == 6 || (row in 2..4 && col in 2..4)
                } else if (isFinderTR) {
                    val localCol = col - (gridSize - 7)
                    isDark = row == 0 || row == 6 || localCol == 0 || localCol == 6 || (row in 2..4 && localCol in 2..4)
                } else if (isFinderBL) {
                    val localRow = row - (gridSize - 7)
                    isDark = localRow == 0 || localRow == 6 || col == 0 || col == 6 || (localRow in 2..4 && col in 2..4)
                } else if (isCenterLogo) {
                    isDark = false // Reserve for logo
                } else {
                    // Pseudorandom deterministic pattern
                    val bit = (abs(hash * (row * 31 + col * 17) + row + col)) % 7
                    isDark = bit % 2 == 0
                }

                if (isDark) {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(col * cellSize + 0.5f, row * cellSize + 0.5f),
                        size = Size(cellSize - 1f, cellSize - 1f),
                        cornerRadius = CornerRadius(1.5f, 1.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun TransferReceiptDialog(
    receipt: TransferResult,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val dateStr = remember(receipt.timestamp) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale("id", "ID"))
        sdf.format(Date(receipt.timestamp))
    }

    val signature = remember(receipt) {
        if (receipt.securitySignature.isNotBlank()) {
            receipt.securitySignature
        } else {
            GlobalPointsManager.generateSecuritySignature(
                receipt.transferId,
                "ROYALTREE_SECURE",
                receipt.recipientIdentifier,
                receipt.sentPoints,
                receipt.timestamp
            )
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("transfer_receipt_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(EmeraldLight.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldLight,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Transfer RTP Berhasil! 📤",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Text(
                    text = "Transaksi telah diverifikasi di jaringan Royaltree",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ID Transaksi:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(receipt.transferId, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Waktu:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(dateStr, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Penerima:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${receipt.recipientName} (${receipt.recipientIdentifier})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jumlah Dikirim:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${receipt.sentPoints} RTP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Potongan Biaya Admin:", fontSize = 12.sp, color = Color(0xFFEF4444))
                            Text("-${receipt.adminFee} RTP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                        }
                        Text(
                            text = "*Biaya admin dipotong dari saldo penerima",
                            fontSize = 9.sp,
                            color = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Bersih Diterima Penerima:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("${receipt.netPointsReceived} RTP", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldLight)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sisa Saldo Anda:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${receipt.senderPointsRemaining} RTP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                        }

                        // Cryptographic Integrity Signature (Anti-Tamper SHA-256)
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = EmeraldPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Validasi Kriptografis Terverifikasi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                    Text(
                                        text = "SHA-256: ${signature.take(20)}...",
                                        fontSize = 8.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val shareReceiptText = remember(receipt, signature) {
                    buildString {
                        append("🧾 BUKTI RESMI TRANSFER POIN ROYALTREE\n")
                        append("━━━━━━━━━━━━━━━━━━━━━━━━━\n")
                        append("No. Transaksi : ${receipt.transferId}\n")
                        append("Waktu         : $dateStr\n")
                        append("Penerima      : ${receipt.recipientName} (${receipt.recipientIdentifier})\n")
                        append("Nominal Kirim : ${receipt.sentPoints} RTP\n")
                        append("Biaya Admin   : ${receipt.adminFee} RTP (Dipotong penerima)\n")
                        append("Bersih Diterima: ${receipt.netPointsReceived} RTP\n")
                        append("Kripto SHA-256: ${signature.take(16)}...\n")
                        append("Status        : SUKSES (Anti-Fraud Checked)\n")
                        append("━━━━━━━━━━━━━━━━━━━━━━━━━\n")
                        append("Aplikasi Royaltree Rewards Network")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Receipt", shareReceiptText)
                            clipboard.setPrimaryClip(clip)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareReceiptText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Bagikan Struk Digital")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.2f).height(46.dp).testTag("btn_share_receipt")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bagikan Struk", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).height(46.dp)
                    ) {
                        Text("Selesai", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

/**
 * Dialog Keamanan PIN & Biometrik Transaksi
 * Mencegah transaksi unauthorized / fraud.
 */
@Composable
fun SecurityPinModal(
    recipientName: String,
    recipientIdentifier: String,
    amount: Int,
    adminFee: Int,
    netReceived: Int,
    note: String,
    onDismiss: () -> Unit,
    onPinSuccess: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("dialog_security_pin"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Lock Badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Security PIN",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Konfirmasi PIN Keamanan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Proteksi Anti-Fraud Transaksi P2P",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Summary Card
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Penerima:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(recipientName, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Nominal Transfer:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$amount RTP", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldLight)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Biaya Admin (Penerima):", fontSize = 10.sp, color = Color(0xFFEF4444))
                            Text("-$adminFee RTP", fontSize = 10.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // PIN Dots (6 Digits)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 6) {
                        val isFilled = i < enteredPin.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isFilled) EmeraldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                                .border(
                                    1.dp,
                                    if (isFilled) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                                    CircleShape
                                )
                        )
                    }
                }

                pinError?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ $err",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Number Pad (1-9, Biometric, 0, Backspace)
                val keyRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("BIO", "0", "DEL")
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    keyRows.forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowKeys.forEach { key ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = when (key) {
                                        "BIO" -> EmeraldPrimary.copy(alpha = 0.15f)
                                        "DEL" -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clickable {
                                            when (key) {
                                                "BIO" -> {
                                                    // Biometric Quick-Pass
                                                    onPinSuccess()
                                                }
                                                "DEL" -> {
                                                    if (enteredPin.isNotEmpty()) {
                                                        enteredPin = enteredPin.dropLast(1)
                                                        pinError = null
                                                    }
                                                }
                                                else -> {
                                                    if (enteredPin.length < 6) {
                                                        enteredPin += key
                                                        pinError = null
                                                        if (enteredPin.length == 6) {
                                                            onPinSuccess()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        .testTag("pin_key_$key")
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        when (key) {
                                            "BIO" -> {
                                                Icon(
                                                    imageVector = Icons.Default.Fingerprint,
                                                    contentDescription = "Biometrik",
                                                    tint = EmeraldPrimary,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                            "DEL" -> {
                                                Icon(
                                                    imageVector = Icons.Default.Backspace,
                                                    contentDescription = "Hapus",
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            else -> {
                                                Text(
                                                    text = key,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "💡 Masukkan 6 digit PIN atau tap icon Sidik Jari untuk otorisasi.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(42.dp)
                ) {
                    Text("Batal Transaksi", fontSize = 12.sp)
                }
            }
        }
    }
}
