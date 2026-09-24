package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.model.WithdrawalEntity
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip

@Composable
fun WithdrawalScreen(
    user: UserEntity?,
    withdrawals: List<WithdrawalEntity>,
    onSubmitWithdrawal: (amount: Double, channelType: String, providerName: String, accountDestination: String, accountHolderName: String) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var channelType by remember { mutableStateOf("E_WALLET") }
    var providerName by remember { mutableStateOf("GoPay") }
    var accountDestination by remember { mutableStateOf("") }
    var accountHolderName by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showChannelDropdown by remember { mutableStateOf(false) }
    var showProviderDropdown by remember { mutableStateOf(false) }

    val balance = user?.balance ?: 0.0
    val minAmount = if (channelType == "E_WALLET") 50000.0 else 100000.0

    val providers = if (channelType == "E_WALLET") {
        listOf("GoPay", "DANA", "OVO", "ShopeePay", "LinkAja")
    } else {
        listOf("BCA", "Mandiri", "BNI", "BRI", "CIMB Niaga")
    }

    val amountValue = amount.toDoubleOrNull() ?: 0.0
    val canSubmit = amountValue >= minAmount && amountValue <= balance &&
                    accountDestination.isNotBlank() && accountHolderName.isNotBlank()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header
        item {
            Column {
                Text("💰 Penarikan", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                Text("Tarik saldo ke rekening atau e-wallet kamu", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Saldo Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldLight.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MonetizationOn, null, tint = EmeraldLight, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Saldo Tersedia", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "Rp " + String.format("%,.0f", balance).replace(",", "."),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = EmeraldLight
                        )
                    }
                }
            }
        }

        // Form Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📝 Form Penarikan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Jumlah
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it.filter { c -> c.isDigit() } },
                        label = { Text("Jumlah (min Rp " + String.format("%,.0f", minAmount).replace(",", ".") + ")") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Channel Dropdown
                    Box {
                        OutlinedTextField(
                            value = if (channelType == "E_WALLET") "E-Wallet" else "Bank Transfer",
                            onValueChange = {},
                            label = { Text("Channel") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(24.dp))
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(onClick = { showChannelDropdown = true }) {
                                Text("", fontSize = 1.sp)
                            }
                        }
                        DropdownMenu(
                            expanded = showChannelDropdown,
                            onDismissRequest = { showChannelDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("E-Wallet (min Rp 50.000)") },
                                onClick = {
                                    channelType = "E_WALLET"
                                    providerName = "GoPay"
                                    showChannelDropdown = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Bank Transfer (min Rp 100.000)") },
                                onClick = {
                                    channelType = "BANK"
                                    providerName = "BCA"
                                    showChannelDropdown = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Provider Dropdown
                    Box {
                        OutlinedTextField(
                            value = providerName,
                            onValueChange = {},
                            label = { Text("Provider") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(24.dp))
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            TextButton(onClick = { showProviderDropdown = true }) {
                                Text("", fontSize = 1.sp)
                            }
                        }
                        DropdownMenu(
                            expanded = showProviderDropdown,
                            onDismissRequest = { showProviderDropdown = false }
                        ) {
                            providers.forEach { provider ->
                                DropdownMenuItem(
                                    text = { Text(provider) },
                                    onClick = {
                                        providerName = provider
                                        showProviderDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // No. Rekening
                    OutlinedTextField(
                        value = accountDestination,
                        onValueChange = { accountDestination = it },
                        label = { Text("Nomor Rekening / HP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Nama Pemilik
                    OutlinedTextField(
                        value = accountHolderName,
                        onValueChange = { accountHolderName = it },
                        label = { Text("Nama Pemilik Rekening") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showConfirmDialog = true },
                        enabled = canSubmit,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldLight,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("💸 AJUKAN PENARIKAN", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Riwayat
        item {
            Text("📜 Riwayat Penarikan", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        }

        if (withdrawals.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Belum ada riwayat penarikan",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(withdrawals.size) { index ->
                val w = withdrawals[index]
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (w.status == "PENDING") Icons.Default.HourglassEmpty else Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = if (w.status == "PENDING") GoldVip else EmeraldLight,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(w.id, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "Rp " + String.format("%,.0f", w.amount).replace(",", ".") + " → " + w.providerName,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            w.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (w.status == "PENDING") GoldVip else EmeraldLight
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Dialog Konfirmasi
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Penarikan") },
            text = {
                Column {
                    Text("Pastikan data sudah benar:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Jumlah: Rp " + String.format("%,.0f", amountValue).replace(",", "."), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Channel: " + (if (channelType == "E_WALLET") "E-Wallet" else "Bank"), fontSize = 12.sp)
                    Text("Provider: $providerName", fontSize = 12.sp)
                    Text("No. Rekening: $accountDestination", fontSize = 12.sp)
                    Text("Nama Pemilik: $accountHolderName", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFFEF4444).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            "⚠️ Kesalahan pengisian data bukan tanggung jawab kami. Pastikan data sudah benar.",
                            fontSize = 10.sp,
                            color = Color(0xFFEF4444),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onSubmitWithdrawal(amountValue, channelType, providerName, accountDestination, accountHolderName)
                    showConfirmDialog = false
                    amount = ""
                    accountDestination = ""
                    accountHolderName = ""
                }) {
                    Text("Ya, Ajukan", color = EmeraldLight, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
