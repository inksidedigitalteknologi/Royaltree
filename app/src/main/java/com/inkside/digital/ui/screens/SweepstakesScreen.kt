package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip

@Composable
fun SweepstakesScreen(
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    viewModel: SweepstakesViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val ticketPrice by viewModel.ticketPrice.collectAsState()
    val totalTickets by viewModel.totalTickets.collectAsState()
    val totalPool by viewModel.totalPool.collectAsState()
    val userTickets by viewModel.userTickets.collectAsState()
    val userChance by viewModel.userChance.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val topHolders by viewModel.topHolders.collectAsState()
    val history by viewModel.history.collectAsState()

    var showBuyModal by remember { mutableStateOf(false) }
    var selectedQty by remember { mutableStateOf(1) }

    // Snackbar
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
                            text = LanguageManager.translate("sweepstakes_title", currentLanguage, "🎟️ Sweepstakes"),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = LanguageManager.translate("sweepstakes_subtitle", currentLanguage, "Menang besar!"),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ============ POOL CARD ============
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        ElectricBlue,
                                        ElectricBlue.copy(alpha = 0.85f),
                                        Color(0xFF7C3AED)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Casino, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "TOTAL POOL",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "$totalPool RTP",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Kupon Terjual", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                    Text("$totalTickets", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Peserta", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                                    Text("${topHolders.size}+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            // Timer
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Berakhir: ${formatCountdown(endTime)}",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }
            }

            // ============ USER STATS ============
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Kupon Anda", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$userTickets", fontSize = 22.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Peluang Menang", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(userChance, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                        }
                    }
                }
            }

            // ============ BUY BUTTON ============
            item {
                Button(
                    onClick = { showBuyModal = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldVip, contentColor = Color.Black),
                    modifier = Modifier.fillMaxWidth().height(54.dp)
                ) {
                    Icon(Icons.Default.Casino, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "BELI KUPON — $ticketPrice RTP",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }

            // ============ REWARDS ============
            if (rewards.isNotEmpty()) {
                item {
                    Text(
                        "🎁 Hadiah",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(rewards) { reward ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val medal = when (reward.rank) {
                                1 -> "🥇"
                                2 -> "🥈"
                                3 -> "🥉"
                                else -> "🎁"
                            }
                            Text(medal, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                if (reward.count > 1) "Juara ${reward.rank} (${reward.count}x)"
                                else "Juara ${reward.rank}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${reward.amount} RTP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldVip
                            )
                        }
                    }
                }
            }

            // ============ TOP HOLDERS ============
            if (topHolders.isNotEmpty()) {
                item {
                    Text(
                        "👑 Top Peserta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(topHolders) { holder ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rank = topHolders.indexOf(holder) + 1
                            Text(
                                "#$rank",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (rank <= 3) GoldVip else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(36.dp)
                            )
                            Text(
                                holder.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${holder.tickets} kupon",
                                fontSize = 12.sp,
                                color = EmeraldLight
                            )
                        }
                    }
                }
            }

            // ============ HISTORY ============
            if (history.isNotEmpty()) {
                item {
                    Text(
                        "🏆 Pemenang Lalu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(history.take(5)) { h ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (h.userWon) EmeraldLight.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.EmojiEvents, null, tint = GoldVip, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    if (h.userWon) "🎉 Anda menang!" else "Selesai",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${h.totalTickets} kupon • ${h.winnerCount} pemenang",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${h.totalPool} RTP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldVip
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // ============ BUY MODAL ============
    if (showBuyModal) {
        BuyTicketModal(
            ticketPrice = ticketPrice,
            userTickets = userTickets,
            maxTickets = 200,
            selectedQty = selectedQty,
            onQtyChange = { selectedQty = it },
            onConfirm = {
                viewModel.buyTickets(selectedQty) { total, bonus ->
                    showBuyModal = false
                }
            },
            onDismiss = { showBuyModal = false },
            currentLanguage = currentLanguage
        )
    }
}

// ============ BUY MODAL ============
@Composable
fun BuyTicketModal(
    ticketPrice: Int,
    userTickets: Int,
    maxTickets: Int,
    selectedQty: Int,
    onQtyChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    currentLanguage: AppLanguage
) {
    val packages = listOf(
        Triple(1, 0, 0),
        Triple(5, 1, 50),
        Triple(10, 3, 50),
        Triple(20, 8, 50),
        Triple(50, 25, 50)
    )
    val totalPrice = selectedQty * ticketPrice

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("🎟️ Beli Kupon", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                packages.forEach { (qty, bonus, _) ->
                    val selected = selectedQty == qty
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) GoldVip.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onQtyChange(qty) }
                            .border(
                                width = if (selected) 2.dp else 0.dp,
                                color = if (selected) GoldVip else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "$qty kupon" + if (bonus > 0) " + $bonus bonus" else "",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                "${qty * ticketPrice} RTP",
                                fontSize = 13.sp,
                                color = GoldVip,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Total: $totalPrice RTP",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
                Text(
                    "Anda punya $userTickets/$maxTickets kupon",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = GoldVip, contentColor = Color.Black)
            ) {
                Text("BELI", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

// ============ HELPER: Countdown ============
fun formatCountdown(endTime: Long): String {
    if (endTime == 0L) return "—"
    val now = System.currentTimeMillis()
    val diff = endTime - now
    if (diff <= 0) return "Berakhir"

    val days = diff / (24 * 60 * 60 * 1000)
    val hours = (diff % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
    val mins = (diff % (60 * 60 * 1000)) / (60 * 1000)

    return "$days hari $hours jam $mins menit"
}
