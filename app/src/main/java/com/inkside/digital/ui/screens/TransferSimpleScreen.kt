package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip

@Composable
fun TransferSimpleScreen(
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    viewModel: TransferViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val history by viewModel.history.collectAsState()
    val userBalance by viewModel.userBalance.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var lastTransferInfo by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // ============ HEADER ============
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                    Column {
                        Text(
                            text = LanguageManager.translate("transfer_title", currentLanguage, "Transfer RTP"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = LanguageManager.translate("transfer_subtitle", currentLanguage, "Kirim saldo poin ke user lain"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ============ SALDO CARD ============
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EmeraldLight.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                LanguageManager.translate("transfer_balance", currentLanguage, "Saldo Anda"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "$userBalance RTP",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldLight
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            null,
                            tint = EmeraldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // ============ SEARCH PENERIMA ============
            item {
                Text(
                    LanguageManager.translate("transfer_recipient", currentLanguage, "Penerima"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                LanguageManager.translate("transfer_search_hint", currentLanguage, "@username / kode referral"),
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.searchUser(searchQuery) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        enabled = searchQuery.length >= 3 && !isLoading,
                        modifier = Modifier.height(56.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(LanguageManager.translate("transfer_search", currentLanguage, "Cari"))
                        }
                    }
                }
            }

            // ============ SEARCH RESULTS ============
            if (searchResults.isNotEmpty()) {
                items(searchResults) { user ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedUser?.uid == user.uid)
                                GoldVip.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.selectUser(user) }
                            .border(
                                width = if (selectedUser?.uid == user.uid) 2.dp else 0.dp,
                                color = if (selectedUser?.uid == user.uid) GoldVip else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(user.avatar, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    user.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    "@${user.username} • ${user.tier}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (selectedUser?.uid == user.uid) {
                                Icon(
                                    Icons.Default.Person,
                                    null,
                                    tint = GoldVip,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ============ AMOUNT INPUT ============
            if (selectedUser != null) {
                item {
                    Text(
                        LanguageManager.translate("transfer_amount", currentLanguage, "Jumlah Transfer"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { if (it.all { c -> c.isDigit() }) amountText = it },
                        placeholder = { Text("Min. 20 RTP", fontSize = 12.sp) },
                        suffix = { Text("RTP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(20, 50, 100, 250).forEach { qty ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (amountText == qty.toString())
                                        GoldVip.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { amountText = qty.toString() }
                            ) {
                                Text(
                                    "$qty",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (amountText == qty.toString()) GoldVip
                                    else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // ============ NOTE ============
                item {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = {
                            Text(
                                LanguageManager.translate("transfer_note", currentLanguage, "Catatan (opsional)"),
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // ============ SUBMIT ============
                item {
                    Button(
                        onClick = {
                            val amt = amountText.toIntOrNull() ?: 0
                            viewModel.sendTransfer(selectedUser!!.uid, amt, noteText) { success, message ->
                                if (success) {
                                    lastTransferInfo = message
                                    showSuccessDialog = true
                                    amountText = ""
                                    noteText = ""
                                    searchQuery = ""
                                    viewModel.loadHistory()
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldVip,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        enabled = (amountText.toIntOrNull() ?: 0) >= 20 && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                LanguageManager.translate("transfer_send", currentLanguage, "KIRIM"),
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // ============ HISTORY ============
            if (history.isNotEmpty()) {
                item {
                    Text(
                        LanguageManager.translate("transfer_history", currentLanguage, "Riwayat Transfer"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                items(history.take(5)) { item ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (item.type == "SENT") Color(0xFFEF4444).copy(alpha = 0.2f)
                                        else EmeraldLight.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (item.type == "SENT") "↑" else "↓",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.type == "SENT") Color(0xFFEF4444) else EmeraldLight
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (item.type == "SENT")
                                        "→ ${item.counterpartyName}"
                                    else "← ${item.counterpartyName}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (item.note.isNotBlank()) {
                                    Text(
                                        item.note,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                (if (item.type == "SENT") "-" else "+") + "${item.amount} RTP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (item.type == "SENT") Color(0xFFEF4444) else EmeraldLight
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // ============ SUCCESS DIALOG ============
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = { Text("✅", fontSize = 40.sp) },
            title = { Text("Transfer Berhasil!", fontWeight = FontWeight.Bold) },
            text = { Text(lastTransferInfo) },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight, contentColor = Color.White)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
