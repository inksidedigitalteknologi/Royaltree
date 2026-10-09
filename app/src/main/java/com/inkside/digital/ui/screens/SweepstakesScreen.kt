package com.inkside.digital.ui.screens

import androidx.compose.ui.unit.Dp
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
import com.inkside.digital.ui.components.BannerAdView
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.util.GlobalPointsManager
import com.inkside.digital.ui.components.RtpAmount

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
    val maxTicketsPerUser by viewModel.maxTicketsPerUser.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
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

            // ============ ROYAL JACKPOT CARD ============
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.verticalGradient(
                                listOf(GoldVip, Color(0xFFD97706))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("\uD83D\uDC51", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "ROYAL JACKPOT",
                            fontSize = 12.sp,
                            color = GoldVip,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        RtpAmount(
                            amount = "${GlobalPointsManager.formatRtp(totalPool)} RTP",
                            fontSize = 36.sp,
                            iconSize = 32.dp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, null, tint = GoldVip, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Berakhir: ${formatCountdown(endTime)}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
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
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🎟️ Kupon Anda", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                            Text("$userTickets / $maxTicketsPerUser", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EmeraldLight)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (userTickets.toFloat() / maxTicketsPerUser.coerceAtLeast(1)).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = EmeraldLight,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
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
                        "BELI KUPON — ${GlobalPointsManager.formatRtp(ticketPrice)} RTP",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }

            // ============ PODIUM WINNERS (M49) ============
            val lastWinners = history.firstOrNull()?.topWinners ?: emptyList()
            if (lastWinners.isNotEmpty()) {
                item {
                    Text(
                        "🏆 Pemenang Periode Lalu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                item {
                    PodiumRow(winners = lastWinners)
                }
            } else {
                item {
                    Text(
                        "🏆 Pemenang Periode Lalu",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📭", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Belum ada pemenang", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Periode pertama sedang berjalan",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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

            // ============ BANNER AD ============
            item {
                BannerAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Total: ", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    RtpAmount(
                        amount = "${GlobalPointsManager.formatRtp(totalPrice)} RTP",
                        fontSize = 14.sp,
                        iconSize = 14.dp,
                        fontWeight = FontWeight.Black
                    )
                }
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

// ============ HELPER: Podium Winners ============
@Composable
fun PodiumRow(winners: List<SweepstakesViewModel.TopWinner>) {
    val rank1 = winners.firstOrNull { it.rank == 1 }
    val rank2 = winners.firstOrNull { it.rank == 2 }
    val rank3 = winners.firstOrNull { it.rank == 3 }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Juara 2 (kiri, rendah)
        if (rank2 != null) {
            PodiumItem(
                winner = rank2,
                medal = "\uD83E\uDD48",
                color = Color(0xFFC0C0C0),
                height = 100.dp,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Juara 1 (tengah, tinggi)
        if (rank1 != null) {
            PodiumItem(
                winner = rank1,
                medal = "\uD83E\uDD47",
                color = GoldVip,
                height = 130.dp,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Juara 3 (kanan, paling rendah)
        if (rank3 != null) {
            PodiumItem(
                winner = rank3,
                medal = "\uD83E\uDD49",
                color = Color(0xFFCD7F32),
                height = 80.dp,
                modifier = Modifier.weight(1f)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PodiumItem(
    winner: SweepstakesViewModel.TopWinner,
    medal: String,
    color: Color,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(medal, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            color.copy(alpha = 0.25f),
                            color.copy(alpha = 0.10f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = color.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = winner.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${winner.amount}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = color,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "RTP",
                    fontSize = 8.sp,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
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
