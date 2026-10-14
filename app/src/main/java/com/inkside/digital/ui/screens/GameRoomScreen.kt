package com.inkside.digital.ui.screens

import com.inkside.digital.R
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.components.FramedCard
import com.inkside.digital.data.model.GameMinerItemEntity
import com.inkside.digital.data.model.GameRoomStateEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.components.BannerAdView
import com.inkside.digital.ui.components.MiningClaimDialog
import com.inkside.digital.ui.components.ThermalStatusCard
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.viewmodel.AffiliateViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random
import com.inkside.digital.util.HashrateFormatter
import com.inkside.digital.util.GlobalPointsManager
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.inkside.digital.ui.components.RtpAmount
import androidx.compose.ui.draw.scale
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider

@OptIn(ExperimentalMaterial3Api::class)
// ==== Retro Pixel Palette ====
private val RetroBgDark = Color(0xFF1A1008)
private val RetroGold = Color(0xFFFFD700)
private val RetroAmber = Color(0xFFF59E0B)
private val RetroBrown = Color(0xFF78350F)
private val RetroCard = Color(0xFF2A1810)
private val RetroBorder = Color(0xFFD97706)

@Composable
fun GameRoomScreen(
    viewModel: AffiliateViewModel,
    user: UserEntity?,
    currentLanguage: AppLanguage,
    onNavigateToMissions: () -> Unit,
    onBack: () -> Unit
) {
    val minerItems by viewModel.gameMinerItems.collectAsState()
    val placedMiners by viewModel.placedMinerItems.collectAsState()
    val roomState by viewModel.gameRoomState.collectAsState()

    // M46 FIX: Reload catalog bila data kosong (first launch race condition)
    androidx.compose.runtime.LaunchedEffect(minerItems.size) {
        if (minerItems.isEmpty()) {
            viewModel.loadMinerCatalog()
            viewModel.loadGameState()
        }
    }

    // ============ AUTO-CLAIM TIMER (60s) ============
    // Start bila Game Room dibuka, stop bila keluar
    DisposableEffect(Unit) {
        viewModel.startAutoClaim()
        onDispose {
            viewModel.stopAutoClaim()
            viewModel.stopMinerCountdownTicker()
        }
    }

    val minerActiveUntil by viewModel.minerActiveUntil.collectAsState()
    val minerActiveRemainingMs by viewModel.minerActiveRemainingMs.collectAsState()

    // ✅ DEBUG: Log setiap perubahan
    LaunchedEffect(minerActiveUntil) {
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadGameStateThenRefreshMiner()
    }

    // ============ THERMAL + LOCATION MONITORING ============
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.startThermalMonitoring()
        viewModel.fetchUserRegion()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopThermalMonitoring()
        }
    }

    GameRoomScreenContent(
        viewModel = viewModel,
        user = user,
        minerItems = minerItems,
        placedMiners = placedMiners,
        roomState = roomState,
        minerActiveUntil = minerActiveUntil,
        minerActiveRemainingMs = minerActiveRemainingMs,
        onActivateMiner = { 
            viewModel.setPendingMinerActivate(true)
            viewModel.showAdRewardModal.value = true
        },
        onClaimMining = {
            if (viewModel.miningPaused.value) {
                // HP terlalu panas — tolak claim
                viewModel.showThermalPauseAlert.value = true
            } else {
                viewModel.showAdRewardModal.value = true
                viewModel.setPendingMiningClaim(true)
            }
        },
        onToggleMinerSlot = { id, slot -> viewModel.toggleMinerSlot(id, slot) },
        onBuyGameMinerItem = { id -> viewModel.buyGameMinerItem(id) },
        onBuyAndPlaceGameMinerItem = { id -> viewModel.buyAndPlaceGameMinerItem(id) },
        onFinishGame = { score, duration, nonce -> viewModel.finishMiniGame(score, duration, nonce) },
        onNavigateToMissions = onNavigateToMissions,
        onBack = onBack,
        currentLanguage = currentLanguage,
        onUnlockSlot = { slotIndex -> viewModel.unlockSlot(slotIndex) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRoomScreenContent(
    viewModel: AffiliateViewModel,
    user: UserEntity?,
    minerItems: List<GameMinerItemEntity>,
    placedMiners: List<GameMinerItemEntity>,
    roomState: GameRoomStateEntity?,
    minerActiveUntil: Long = 0L,
    minerActiveRemainingMs: Long = 0L,
    onActivateMiner: () -> Unit = {},
    onClaimMining: () -> Unit,
    onToggleMinerSlot: (String, Int) -> Unit,
    onBuyGameMinerItem: (String) -> Unit,
    onBuyAndPlaceGameMinerItem: (String) -> Unit = {},
    onFinishGame: (score: Int, durationMs: Long, gameNonce: String) -> Unit,
    onNavigateToMissions: () -> Unit,
    onBack: () -> Unit,
    currentLanguage: AppLanguage,
    onClaimMinerToken: (String) -> Unit = {},
    onUnlockMiner: (String) -> Unit = {},
    onUnlockSlot: (Int) -> Unit = {}
) {
    var activeTab by remember { mutableIntStateOf(0) } // 0: Ruang Rak, 1: Toko Item, 2: Mini-Game
    var selectedItemToBuy by remember { mutableStateOf<GameMinerItemEntity?>(null) }
    var slotToAssignItem by remember { mutableStateOf<Int?>(null) }

    // Live point ticker for smooth real-time generation feedback
    var liveUnclaimedPoints by remember { mutableDoubleStateOf(0.0) }

    val basePowerGhs = remember(placedMiners) {
        placedMiners.sumOf { it.powerGhs }
    }
    val bonusPowerGhs = roomState?.tempPowerBonusGhs ?: 0.0
    val totalPowerGhs = basePowerGhs + bonusPowerGhs

    // ✅ DINAMIS: Sum pointsPerMinute dari semua placed miners (client-side, real-time)
    // Server rate jadi fallback kalau client kosong
    val generationRatePerMin = remember(roomState?.ratePerMin, placedMiners) {
        // Prioritas 1: sum client-side (real-time, akurat)
        val clientRate = placedMiners.sumOf { it.pointsPerMinute }
        if (clientRate > 0.0) return@remember clientRate
        
        // Prioritas 2: fallback ke server rate
        roomState?.ratePerMin ?: 0.0
    }

    // (minerBreakdown dipindah ke MiningRoomTab)

    LaunchedEffect(roomState, placedMiners, minerActiveUntil) {
        while (true) {
            val now = System.currentTimeMillis()
            
            // ✅ CHECK: Kalau miner tak aktif, RTP = 0 (freeze)
            if (minerActiveUntil <= now) {
                liveUnclaimedPoints = 0.0
                delay(1000L)
                continue
            }
            
            val lastClaim = roomState?.lastClaimTimestamp ?: now
            val elapsedMinutes = ((now - lastClaim) / 60000.0).coerceAtLeast(0.0)
            val generated = elapsedMinutes * generationRatePerMin
            liveUnclaimedPoints = (roomState?.unclaimedMiningPoints ?: 0.0) + generated
            delay(1000L)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = LanguageManager.translate("game_room_title", currentLanguage, "Royaltree Mining Room"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EmeraldLight.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = LanguageManager.translate("badge_passive", currentLanguage, "PASSIVE"),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = LanguageManager.translate("game_room_subtitle", currentLanguage, "Buy miners with RTP & earn passive income"),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("game_room_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                // Chip kuning RTP dah dibuang — sebab ada di stats row bawah
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ============ THERMAL STATUS CARD ============
            ThermalStatusCard(viewModel)
            Spacer(modifier = Modifier.height(8.dp))

            // Tabs: Ruang Rak (Virtual Room), Toko Item (Shop), Mini-Game (Arcade)
            val tabs = listOf("🖥️ Ruang Rak Mining", "🛒 Toko Item", "🎮 Mini-Game Arcade")
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldLight,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = EmeraldLight
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            when (activeTab) {
                0 -> {
                    // TAB 0: RUANG RAK MINING & POWER STATS
                    if (minerItems.isEmpty()) {
                        LoadingState(currentLanguage)
                    } else {
                        MiningRoomTab(
                            currentLanguage = currentLanguage,
                            placedMiners = placedMiners,
                            allMiners = minerItems,
                            totalPowerGhs = totalPowerGhs,
                            basePowerGhs = basePowerGhs,
                            bonusPowerGhs = bonusPowerGhs,
                            generationRatePerMin = generationRatePerMin,
                            liveUnclaimedPoints = liveUnclaimedPoints,
                            roomState = roomState,
                            userPoints = user?.points ?: 0.0,
                            minerActiveUntil = minerActiveUntil,
                            minerActiveRemainingMs = minerActiveRemainingMs,
                            onActivateMiner = onActivateMiner,
                            onClaimMining = onClaimMining,
                            onClaimMinerToken = onClaimMinerToken,
                            onUnlockMiner = onUnlockMiner,
                            onUnplaceMiner = { minerId, slot -> onToggleMinerSlot(minerId, slot) },
                            onOpenShop = { activeTab = 1 },
                            onOpenAssignSlotModal = { slot -> slotToAssignItem = slot },
                            onOpenMiniGame = { activeTab = 2 },
                            onUnlockSlot = onUnlockSlot
                        )
                    }
                }
                1 -> {
                    // TAB 1: TOKO ITEM (SHOP)
                    if (minerItems.isEmpty()) {
                        LoadingState(currentLanguage)
                    } else {
                        ItemShopTab(
                            currentLanguage = currentLanguage,
                            minerItems = minerItems,
                            userPoints = user?.points ?: 0.0,
                            onBuyItem = { item -> selectedItemToBuy = item },
                            onUnlockMiner = onUnlockMiner,
                            onNavigateToMissions = onNavigateToMissions
                        )
                    }
                }
                2 -> {
                    // TAB 2: MINI-GAME ARCADE
                    MiniGameArcadeTab(
                        currentLanguage = currentLanguage,
                        onFinishGame = { score, duration, nonce -> onFinishGame(score, duration, nonce) },
                        highScore = roomState?.miniGameHighScore ?: 0
                    )
                }
            }
        }
    }

    // Modal: Confirmation Beli Item — Framed Style
    selectedItemToBuy?.let { item ->
        val userPoints = user?.points ?: 0.0
        val canAfford = userPoints >= item.tokenCost
        // Debug log untuk trace
        android.util.Log.d("GameRoomBuy", "canAfford check: userPoints=$userPoints, tokenCost=${item.tokenCost}, canAfford=$canAfford")
        val unlockedSlots = roomState?.unlockedSlots ?: 2
        val usedSlots = minerItems
            .filter { it.isPlacedInRoom }
            .map { it.placedSlotIndex }
            .toSet()
        val hasEmptySlot = (0 until unlockedSlots).any { it !in usedSlots }

        Dialog(onDismissRequest = { selectedItemToBuy = null }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(EmeraldPrimary, GoldVip)
                    )
                ),
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.micro_chip),
                            contentDescription = item.name,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Beli ${item.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color.White
                            )
                            Text(
                                text = item.description,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BuyStatRow("Harga", "${item.tokenCost} RTP", GoldVip)
                            BuyStatRow("Mining Power", "+${HashrateFormatter.format(item.powerGhs)}", EmeraldLight)
                            BuyStatRow("Rate RTP", "+${String.format(Locale.US, "%.4f", item.pointsPerMinute)}/mnt", EmeraldLight)
                            BuyStatRow("Saldo Anda", "${user?.points ?: 0} RTP", Color.White)
                        }
                    }

                    if (!canAfford) {
                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "RTP tidak mencukupi - kurang ${String.format(Locale.US, "%.2f", item.tokenCost - (user?.points ?: 0.0))} RTP",
                                fontSize = 11.sp,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedItemToBuy = null },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, Color(0xFF475569))
                        ) {
                            Text("Batal", color = Color(0xFF94A3B8))
                        }
                        Button(
                            onClick = {
                                android.util.Log.d("GameRoomBuy", "🔘 Button diklik: item=${item.id}, hasEmptySlot=$hasEmptySlot, canAfford=$canAfford")
                                try {
                                    if (hasEmptySlot) {
                                        android.util.Log.d("GameRoomBuy", "→ onBuyAndPlaceGameMinerItem")
                                        onBuyAndPlaceGameMinerItem(item.id)
                                    } else {
                                        android.util.Log.d("GameRoomBuy", "→ onBuyGameMinerItem")
                                        onBuyGameMinerItem(item.id)
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("GameRoomBuy", "❌ Error onClick: ${e.message}", e)
                                }
                                selectedItemToBuy = null
                            },
                            enabled = canAfford,
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                        ) {
                            Text(
                                text = if (hasEmptySlot) "Beli & Pasang" else "Beli Sahaja",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal: Pilih Item dari Inventaris untuk dipasang di Slot Kosong
    slotToAssignItem?.let { targetSlot ->
        val unplacedOwnedMiners = minerItems.filter { it.isOwned && !it.isPlacedInRoom }

        AlertDialog(
            onDismissRequest = { slotToAssignItem = null },
            title = {
                Text(text = "Pasang Item ke Slot ${targetSlot + 1}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                if (unplacedOwnedMiners.isEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Text("📦", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada item miner di inventaris.",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Beli item penambang baru di Toko Item menggunakan poin Anda.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(unplacedOwnedMiners) { miner ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onToggleMinerSlot(miner.id, targetSlot)
                                        slotToAssignItem = null
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.micro_chip),
                                        contentDescription = miner.name,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(miner.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("+${HashrateFormatter.format(miner.powerGhs)} • +${String.format(Locale.US, "%.4f", miner.pointsPerMinute)} RTP/mnt", fontSize = 10.sp, color = EmeraldLight)
                                    }
                                    Button(
                                        onClick = {
                                            onToggleMinerSlot(miner.id, targetSlot)
                                            slotToAssignItem = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text(LanguageManager.translate("game_install", currentLanguage, "Pasang"), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (unplacedOwnedMiners.isEmpty()) {
                    Button(
                        onClick = {
                            slotToAssignItem = null
                            activeTab = 1 // Open Shop
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(LanguageManager.translate("game_open_shop", currentLanguage, "Buka Toko Item"))
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { slotToAssignItem = null }) {
                    Text(LanguageManager.translate("common_close", currentLanguage, "Tutup"))
                }
            }
        )
    }

    // ============================================================
    // THERMAL PAUSE ALERT — HP terlalu panas, mining dijeda
    // ============================================================
    if (viewModel.showThermalPauseAlert.value) {
        val pauseReason by viewModel.pauseReason.collectAsState()
        val isBattery = pauseReason == "battery_critical"

        AlertDialog(
            onDismissRequest = { viewModel.showThermalPauseAlert.value = false },
            icon = { Text(if (isBattery) "🔋" else "⛔", fontSize = 40.sp) },
            title = {
                Text(
                    text = if (isBattery) "Baterai Kritis" else "Mining Dijeda",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isBattery)
                        "Baterai HP kritis. Mining dijeda sampai HP dicharge.\n\nCharge HP untuk lanjut mining."
                    else
                        "HP terlalu panas. Mining dijeda otomatis sampai suhu turun (< 55°C).\n\nDinginkan HP atau cabut charger untuk lanjut mining."
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.showThermalPauseAlert.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("OK, Mengerti")
                }
            }
        )
    }

}


// -------------------------------------------------------------
// TAB 0: RUANG RAK MINING (ROLLERCOIN RACK ROOM)
// -------------------------------------------------------------
@Composable
private fun RetroStatBox(
    emoji: String,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier,
    useRtpIcon: Boolean = false
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            if (useRtpIcon) {
                Image(
                    painter = painterResource(R.drawable.rtp_coin),
                    contentDescription = "RTP",
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(emoji, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = color,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun MiningRoomTab(
    currentLanguage: AppLanguage,
    placedMiners: List<GameMinerItemEntity>,
    allMiners: List<GameMinerItemEntity>,
    totalPowerGhs: Double,
    basePowerGhs: Double,
    bonusPowerGhs: Double,
    generationRatePerMin: Double,
    liveUnclaimedPoints: Double,
    roomState: GameRoomStateEntity?,
    userPoints: Double = 0.0,
    minerActiveUntil: Long = 0L,
    minerActiveRemainingMs: Long = 0L,
    onActivateMiner: () -> Unit = {},
    onClaimMining: () -> Unit,
    onClaimMinerToken: (String) -> Unit = {},
    onUnlockMiner: (String) -> Unit = {},
    onUnplaceMiner: (minerId: String, slot: Int) -> Unit,
    onOpenShop: () -> Unit,
    onOpenAssignSlotModal: (slotIndex: Int) -> Unit,
    onOpenMiniGame: () -> Unit,
    onUnlockSlot: (slotIndex: Int) -> Unit = {}
) {
    val unlockedSlots = roomState?.unlockedSlots ?: 2

    // ✅ Breakdown per miner (untuk display)
    val minerBreakdown = remember(placedMiners) {
        placedMiners
            .sortedByDescending { it.pointsPerMinute }
            .map { miner ->
                Triple(miner.name, miner.pointsPerMinute, miner.powerGhs)
            }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {

        // Hero Room Power & Harvest Dashboard (lama)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // ==== PNG FRAME ====
                    Image(
                        painter = painterResource(id = com.inkside.digital.R.drawable.card_frame_mining),
                        contentDescription = null,
                        modifier = Modifier.matchParentSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp, vertical = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // ==== HEADER: Title + Chip ====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = LanguageManager.translate("game_mining_tycoon", currentLanguage, "MINING TYCOON"),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldLight,
                                    letterSpacing = 2.sp
                                )
                                Text(
                                    text = LanguageManager.translate("game_points_miner", currentLanguage, "Royaltree Points Miner"),
                                    fontSize = 10.sp,
                                    color = RetroAmber,
                                    letterSpacing = 1.sp
                                )
                            }
                            Surface(
                                color = Color(0xFF1E293B).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = String.format(LanguageManager.translate("game_rack_filled", currentLanguage, "%s Rak Terisi"), "${placedMiners.size}/${roomState?.unlockedSlots ?: 6}"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // ==== STATS ROW ====
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RetroStatBox(
                                emoji = "🔥",
                                label = LanguageManager.translate("game_mining_power", currentLanguage, "POWER"),
                                value = HashrateFormatter.format(totalPowerGhs),
                                color = RetroAmber,
                                modifier = Modifier.weight(1f)
                            )
                            RetroStatBox(
                                emoji = "💰",
                                label = LanguageManager.translate("game_mining_rtp", currentLanguage, "RTP"),
                                value = "${GlobalPointsManager.formatRtp(userPoints + liveUnclaimedPoints)}",
                                color = RetroGold,
                                modifier = Modifier.weight(1.8f),
                                useRtpIcon = true
                            )
                            RetroStatBox(
                                emoji = "⚡",
                                label = LanguageManager.translate("game_mining_rate", currentLanguage, "RATE"),
                                value = "${String.format(Locale.US, "%.4f", generationRatePerMin)}/m",
                                color = EmeraldLight,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // ==== LIVE MINING TICKER ====
                        Surface(
                            color = Color(0xFF0F172A).copy(alpha = 0.7f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                // Baris 1: Rate + Unclaimed
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Bolt,
                                            contentDescription = null,
                                            tint = EmeraldLight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "+${String.format(Locale.US, "%.4f", generationRatePerMin)} RTP/mnt",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight
                                        )
                                    }
                                    Text(
                                        text = "${GlobalPointsManager.formatRtp(liveUnclaimedPoints)} RTP",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldVip
                                    )
                                }

                                // Baris 2: Breakdown per miner (kalau > 1 miner)
                                if (minerBreakdown.size > 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(
                                        color = Color(0xFF334155),
                                        thickness = 0.5.dp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))

                                    minerBreakdown.take(3).forEach { item ->
                                        val (name, rate, power) = item
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 1.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(3.dp)
                                                        .clip(CircleShape)
                                                        .background(EmeraldPrimary.copy(alpha = 0.6f))
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = name.take(14),
                                                    fontSize = 9.sp,
                                                    color = Color(0xFF94A3B8),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "⚡${power.toInt()}",
                                                    fontSize = 8.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Text(
                                                text = "+${String.format(Locale.US, "%.4f", rate)}/m",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                    }

                                    if (minerBreakdown.size > 3) {
                                        Text(
                                            text = "... +${minerBreakdown.size - 3} miner lagi",
                                            fontSize = 9.sp,
                                            color = Color(0xFF64748B),
                                            modifier = Modifier.padding(top = 4.dp, start = 9.dp)
                                        )
                                    }
                                }
                            }
                        }

                        }

                        // Unclaimed Points Box & Claim Button
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B).copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // ===== STATUS MINER AKTIF / TIDAK =====
                                val isMinerActive = minerActiveUntil > System.currentTimeMillis()

                                if (isMinerActive) {
                                    // Miner sedang aktif — KOTAK ABU-ABU dengan status + countdown
                                    Surface(
                                        color = Color(0xFF334155),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 14.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "⛏️ ${LanguageManager.translate("game_miner_active", currentLanguage, "MINER SEDANG AKTIF")}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldLight
                                                )
                                                Text(
                                                    text = formatMinerCountdown(minerActiveRemainingMs),
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White
                                                )
                                            }
                                            Icon(
                                                imageVector = Icons.Filled.Bolt,
                                                contentDescription = "Active",
                                                tint = GoldVip,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                } else {
                                    // Miner tamat — tunjuk button aktivasi
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "⛔ ${LanguageManager.translate("game_miner_inactive", currentLanguage, "MINER TIDAK AKTIF")}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFF87171)
                                            )
                                            Text(
                                                text = LanguageManager.translate("game_miner_hint", currentLanguage, "Aktifkan untuk mula mining"),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF94A3B8)
                                            )
                                        }
                                        Text("💤", fontSize = 28.sp)
                                    }

                                    // Button aktivasi — tonton iklan
                                    Button(
                                        onClick = onActivateMiner,
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                            .testTag("activate_miner_button")
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "🎬 ${LanguageManager.translate("game_miner_button", currentLanguage, "Aktifkan Miner (Tonton Iklan)")}",
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        // ==== Banner AdMob ====
        item {
            BannerAdView()
        }

        // Section Title: Virtual Server Rack (6 Slots)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rak Penambang Ruang Game",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Setiap item yang terpasang di rak menghasilkan poin secara pasif",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    onClick = onOpenShop,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(imageVector = Icons.Filled.ShoppingBag, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(LanguageManager.translate("game_buy_item", currentLanguage, "Beli Item"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 6 Slots in Rack Room — Grid 3x2 Landscape (M36 + M37: locked by unlock)
        val occupiedMap = placedMiners.associateBy { it.placedSlotIndex }
        items(2) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (colIndex in 0 until 3) {
                    val slotIndex = rowIndex * 3 + colIndex
                    val isLocked = slotIndex >= unlockedSlots
                    Box(modifier = Modifier.weight(1f)) {
                        if (isLocked) {
                            // Slot locked — perlu beli
                            LockedSlotCard(
                                slotIndex = slotIndex,
                                currentLanguage = currentLanguage,
                                onClick = { onUnlockSlot(slotIndex) }
                            )
                        } else {
                            RackSlotItemCard(
                                slotIndex = slotIndex,
                                miner = occupiedMap[slotIndex],
                                pulseAlpha = pulseAlpha,
                                onUnplace = {
                                    occupiedMap[slotIndex]?.let { miner ->
                                        onUnplaceMiner(miner.id, slotIndex)
                                    }
                                },
                                onAssign = { onOpenAssignSlotModal(slotIndex) }
                            )
                        }
                    }
                }
            }
        }

        // Action Banner: Mini-Game Booster
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenMiniGame() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎮", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Butuh Tambahan Hash Power & Poin?",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Mainkan Mini-Game Arcade Coin Rush (+${HashrateFormatter.format(80.0)} Power Boost & Poin langsung)!",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = onOpenMiniGame,
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(LanguageManager.translate("game_play", currentLanguage, "Main"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
        }

@Composable
private fun LockedSlotCard(
    slotIndex: Int,
    currentLanguage: AppLanguage,
    onClick: () -> Unit
) {
    val price = when (slotIndex) {
        2 -> 500
        3 -> 1500
        4 -> 3000
        5 -> 5000
        else -> 500
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF1A1F2E),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    color = Color(0xFF334155),
                    shape = RoundedCornerShape(14.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF334155).copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔒", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = LanguageManager.translate("slot_locked", currentLanguage, "TERKUNCI"),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = RetroGold,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💎", fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        "${GlobalPointsManager.formatRtp(price)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Tap untuk buka",
                fontSize = 8.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RackSlotItemCard(
    slotIndex: Int,
    miner: GameMinerItemEntity?,
    pulseAlpha: Float,
    onUnplace: () -> Unit,
    onAssign: () -> Unit
) {
    val isOccupied = miner != null

    // Tier color coding
    val tierColor = when (miner?.tier?.uppercase()) {
        "COMMON" -> Color(0xFF94A3B8)
        "RARE" -> Color(0xFF3B82F6)
        "EPIC" -> Color(0xFFA855F7)
        "LEGENDARY" -> Color(0xFFFBBF24)
        "MYTHIC" -> Color(0xFFEF4444)
        else -> Color(0xFF6B7280)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { if (isOccupied) onUnplace() else onAssign() }
    ) {
        // ==== BACKGROUND ====
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        if (isOccupied) listOf(
                            tierColor.copy(alpha = 0.15f),
                            Color(0xFF0F172A),
                            Color(0xFF020617)
                        ) else listOf(
                            Color(0xFF0F172A),
                            Color(0xFF020617)
                        )
                    )
                )
                .border(
                    width = if (isOccupied) 1.5.dp else 1.dp,
                    color = if (isOccupied) tierColor.copy(alpha = 0.7f) else EmeraldPrimary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(14.dp)
                )
        )

        if (isOccupied) {
            // ==== OCCUPIED: Portrait layout ====
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon + status dot
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F0805))
                        .border(1.5.dp, tierColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(miner!!.iconEmoji, fontSize = 26.sp)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(3.dp)
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(tierColor.copy(alpha = pulseAlpha))
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    miner.name,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Surface(
                    color = tierColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        miner.tier,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = tierColor,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("\u26A1", fontSize = 9.sp)
                        Text(
                            HashrateFormatter.formatShort(miner.powerGhs),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("\uD83D\uDCB0", fontSize = 9.sp)
                        Text(
                            String.format("%.2f", miner.pointsPerMinute * 1440),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RetroGold
                        )
                    }
                }
            }
        } else {
            // ==== EMPTY: Tap untuk tambah ====
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("\u2795", fontSize = 28.sp, color = EmeraldPrimary.copy(alpha = 0.6f))
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tambah",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary.copy(alpha = 0.7f)
                )
                Text(
                    "Miner",
                    fontSize = 9.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun LoadingState(currentLanguage: AppLanguage) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = EmeraldLight,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = LanguageManager.translate("common_loading", currentLanguage, "Memuat data..."),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = LanguageManager.translate("common_loading_desc", currentLanguage, "Menyinkronkan data dari server"),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontSize = 11.sp
            )
        }
    }
}

// -------------------------------------------------------------
// TAB 1: TOKO ITEM MINER (SHOP)
// -------------------------------------------------------------
@Composable
private fun ItemShopTab(
    currentLanguage: AppLanguage,
    minerItems: List<GameMinerItemEntity>,
    userPoints: Double,
    onBuyItem: (GameMinerItemEntity) -> Unit,
    onUnlockMiner: (String) -> Unit = {},
    onNavigateToMissions: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Banner info Toko Item
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⛏️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageManager.translate("game_unlock_miner", currentLanguage, "Unlock Miner dengan RTP"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = LanguageManager.translate("game_unlock_miner_desc", currentLanguage, "Kumpulkan RTP dari iklan & misi untuk membuka miner baru!"),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = RetroGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("RTP: ", fontSize = 11.sp, color = RetroGold)
                                Text(
                                    text = "${String.format(Locale.US, "%.4f", userPoints)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = RetroGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fast earn points CTA banner
        item {
            Surface(
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToMissions() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(LanguageManager.translate("game_need_more_points", currentLanguage, "Butuh Poin Lebih Banyak?"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                            Text(LanguageManager.translate("game_complete_missions", currentLanguage, "Selesaikan misi Like, Subscribe, Nonton, & Web"), fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                    }
                    Button(
                        onClick = onNavigateToMissions,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldVip),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(LanguageManager.translate("game_open_missions", currentLanguage, "Buka Misi"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        // Shop items list
        items(minerItems) { item ->
            ShopMinerCard(
                item = item,
                userPoints = userPoints,
                onBuy = { onBuyItem(item) },
                onUnlock = { onUnlockMiner(item.id) }
            )
        }
    }
}

@Composable
private fun ShopMinerCard(
    item: GameMinerItemEntity,
    userPoints: Double = 0.0,
    onBuy: () -> Unit,
    onUnlock: () -> Unit = {}
) {
    val rarityColor = tierColor(item.tier)
    val canAfford = userPoints >= item.tokenCost

    // M50: Auto-switch frame ikut tier miner
    val frameRes = when (item.tier.uppercase()) {
        "COMMON" -> R.drawable.card_frame_common
        "RARE" -> R.drawable.card_frame_rare
        "EPIC" -> R.drawable.card_frame_epic
        "LEGENDARY" -> R.drawable.card_frame_legendary
        "MYTHIC" -> R.drawable.card_frame_mythic
        else -> R.drawable.card_frame_common
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(RetroCard)
            .border(2.dp, rarityColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ==== Header: Icon + Name + Tier ====
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F0805))
                        .border(1.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.micro_chip),
                        contentDescription = item.name,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = rarityColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.tier,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = rarityColor,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = item.description,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // ==== Specs ====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = RetroAmber.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, RetroAmber.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("⚡", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${HashrateFormatter.format(item.powerGhs)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RetroAmber
                        )
                    }
                }

                Surface(
                    color = EmeraldLight.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, EmeraldLight.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("💰", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+${String.format(Locale.US, "%.4f", item.pointsPerMinute)} RTP/mnt",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                    }
                }
            }

            // ==== Price & Action ====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HARGA",
                        fontSize = 9.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💰", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.tokenCost}",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = RetroGold
                        )
                        Text(
                            text = " RTP",
                            fontSize = 11.sp,
                            color = RetroGold.copy(alpha = 0.7f)
                        )
                    }
                }

                if (item.isOwned) {
                    Surface(
                        color = EmeraldLight.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✅", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (item.isPlacedInRoom) "Terpasang" else "Di Inventaris",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }
                    }
                } else {
                    Surface(
                        color = if (canAfford) RetroGold else Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable(enabled = canAfford) { onBuy() }
                    ) {
                        Text(
                            text = if (canAfford) "BELI" else "KURANG ${String.format(Locale.US, "%.2f", item.tokenCost - userPoints)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (canAfford) Color.Black else Color.White.copy(alpha = 0.7f),
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniGameArcadeTab(
    currentLanguage: AppLanguage,
    onFinishGame: (score: Int, durationMs: Long, gameNonce: String) -> Unit,
    highScore: Int
) {
    var isPlaying by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var secondsRemaining by remember { mutableIntStateOf(15) }
    var targetPositionX by remember { mutableIntStateOf(50) }
    var targetPositionY by remember { mutableIntStateOf(50) }
    var targetEmoji by remember { mutableStateOf("🪙") }
    var comboCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            score = 0
            secondsRemaining = 15
            comboCount = 0
            while (secondsRemaining > 0 && isPlaying) {
                delay(1000L)
                secondsRemaining--
            }
            if (isPlaying) {
                isPlaying = false
                onFinishGame(
                    score,
                    (15 - secondsRemaining).toLong() * 1000L,
                    java.util.UUID.randomUUID().toString()
                )
            }
        }
    }

    // Move target periodically during play
    LaunchedEffect(isPlaying, score) {
        if (isPlaying) {
            targetPositionX = Random.nextInt(10, 80)
            targetPositionY = Random.nextInt(10, 80)
            val emojis = listOf("🪙", "💎", "⚡", "💰", "⭐")
            targetEmoji = emojis[Random.nextInt(emojis.size)]
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Arcade Header Card
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
                                colors = listOf(
                                    Color(0xFF312E81),
                                    Color(0xFF4C1D95),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎮", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(LanguageManager.translate("game_coinrush_title", currentLanguage, "Crypto Coin Tap Rush"), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                    Text(LanguageManager.translate("game_coinrush_subtitle", currentLanguage, "Mini-Game Penghasil Power Royaltree"), fontSize = 11.sp, color = Color(0xFFC7D2FE))
                                }
                            }
                            Surface(
                                color = GoldVip.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "High Score: $highScore",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldVip,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = "Mainkan game 15 detik! Ketuk koin & permata secepat mungkin untuk mendapatkan +20 s/d 60 Poin instan dan +${HashrateFormatter.format(80.0)} Power Boost selama 2 jam.",
                            fontSize = 11.sp,
                            color = Color(0xFFE0E7FF)
                        )
                    }
                }
            }
        }

        // Game Arena Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            ) {
                if (!isPlaying) {
                    // Ready / Idle State
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("🎯", fontSize = 54.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Siap Uji Kecepatan Jari?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Kumpulkan koin sebanyak-banyaknya dalam 15 detik.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { isPlaying = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(LanguageManager.translate("game_start_now", currentLanguage, "Mulai Game Sekarang (15s)"), fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                } else {
                    // Active Game Playing State
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Game HUD (Timer & Score)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Filled.Timer, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${secondsRemaining}s",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFFEF4444)
                                    )
                                }
                            }

                            Surface(
                                color = EmeraldLight.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Skor: $score",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EmeraldLight,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Target Coin to tap
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(
                                    start = (targetPositionX * 2.5).dp.coerceIn(20.dp, 220.dp),
                                    top = (targetPositionY * 1.8).dp.coerceIn(50.dp, 200.dp)
                                )
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(GoldVip.copy(alpha = 0.25f))
                                .clickable {
                                    score += 10
                                    comboCount++
                                    targetPositionX = Random.nextInt(10, 80)
                                    targetPositionY = Random.nextInt(10, 80)
                                    val emojis = listOf("🪙", "💎", "⚡", "💰", "⭐")
                                    targetEmoji = emojis[Random.nextInt(emojis.size)]
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(targetEmoji, fontSize = 36.sp)
                        }

                        Text(
                            text = "KETUK KOIN / DIAMOND!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }
    }
}



// -------------------------------------------------------------
// -------------------------------------------------------------
private fun tierColor(tier: String): Color {
    return when (tier.uppercase()) {
        "COMMON" -> Color(0xFF94A3B8)
        "RARE" -> ElectricBlue
        "EPIC" -> PurpleSecondary
        "LEGENDARY" -> GoldVip
        "MYTHIC" -> Color(0xFFEC4899)
        else -> EmeraldLight
    }
}


/**
 * Format countdown ms → "5j 32m" atau "32m 15s" atau "15s"
 */
private fun formatMinerCountdown(ms: Long): String {
    if (ms <= 0L) return "Tamat"
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return when {
        hours > 0 -> "${hours}j ${minutes}m"
        minutes > 0 -> "${minutes}m ${seconds}s"
        else -> "${seconds}s"
    }
}

@Composable
private fun BuyStatRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = Color(0xFF94A3B8))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

