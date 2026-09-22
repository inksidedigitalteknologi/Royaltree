package com.inkside.digital.ui.screens

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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.TransactionEntity
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    transactions: List<TransactionEntity>
) {
    var filterType by remember { mutableStateOf("ALL") } // ALL, COMMISSION, WITHDRAWAL, COUPON, REWARD
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = transactions.filter { tx ->
        val matchesType = when (filterType) {
            "COMMISSION" -> tx.type == "COMMISSION"
            "WITHDRAWAL" -> tx.type == "WITHDRAWAL"
            "COUPON" -> tx.type.contains("COUPON")
            "REWARD" -> tx.type.contains("POINT") || tx.type.contains("REWARD")
            else -> true
        }
        val matchesQuery = searchQuery.isBlank() || tx.title.contains(searchQuery, ignoreCase = true) || tx.description.contains(searchQuery, ignoreCase = true) || tx.referenceId.contains(searchQuery, ignoreCase = true)
        matchesType && matchesQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(10.dp)) }

        item {
            Column {
                Text(
                    text = "Riwayat Buku Besar & Transaksi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Text(
                    text = "Audit lengkap penerimaan komisi, penarikan dana, dan investasi",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari berdasarkan judul, ref ID, atau nama kampanye...", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        // Filter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "ALL" to "Semua",
                    "COMMISSION" to "Komisi",
                    "WITHDRAWAL" to "Tarik Dana",
                    "COUPON" to "Kupon",
                    "REWARD" to "Poin"
                ).forEach { (type, label) ->
                    val isSel = filterType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) EmeraldLight else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { filterType = type }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (filteredList.isEmpty()) {
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
                        Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Tidak Ada Catatan Transaksi", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(filteredList) { tx ->
                HistoryDetailCard(tx = tx)
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun HistoryDetailCard(tx: TransactionEntity) {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(tx.timestamp))
    val isTransfer = tx.type.contains("TRANSFER")

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val iconBg = when {
                isTransfer -> ElectricBlue.copy(alpha = 0.15f)
                tx.isCredit -> EmeraldLight.copy(alpha = 0.15f)
                else -> Color(0xFFEF4444).copy(alpha = 0.15f)
            }
            val iconTint = when {
                isTransfer -> ElectricBlue
                tx.isCredit -> EmeraldLight
                else -> Color(0xFFEF4444)
            }
            val iconVec = when {
                isTransfer -> Icons.Default.QrCodeScanner
                tx.isCredit -> Icons.Default.ArrowDownward
                else -> Icons.Default.ArrowUpward
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVec,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tx.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (isTransfer) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = ElectricBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Transfer QR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Text(tx.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Ref: ${tx.referenceId} • $dateStr", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${if (tx.isCredit) "+" else "-"}Rp ${String.format("%,.0f", tx.amount)}",
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = if (tx.isCredit) EmeraldLight else Color(0xFFEF4444)
            )
        }
    }
}
