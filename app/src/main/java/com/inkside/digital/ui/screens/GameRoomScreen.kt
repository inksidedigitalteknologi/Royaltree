package com.inkside.digital.ui.screens

import com.inkside.digital.R
import com.inkside.digital.data.preferences.AppThemePreferences
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Surface
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.GameMinerItemEntity
import com.inkside.digital.data.model.GameRoomStateEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.components.BannerAdView
import com.inkside.digital.ui.components.ThermalStatusCard
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.viewmodel.AffiliateViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import java.util.Locale
import kotlin.random.Random
import com.inkside.digital.util.HashrateFormatter
import com.inkside.digital.util.GlobalPointsManager
import com.inkside.digital.util.AppLogger
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.window.DialogProperties
import androidx.compose.animation.core.LinearEasing
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.layout.fillMaxHeight

// ==== Retro Pixel Palette ====
private val RetroGold = Color(0xFFFFD700)
private val RetroAmber = Color(0xFFF59E0B)
private val RetroCard = Color(0xFF2A1810)

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
    LaunchedEffect(minerItems.size) {
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

    LaunchedEffect(Unit) {
        viewModel.loadGameStateThenRefreshMiner()
    }

    // ============ THERMAL + LOCATION MONITORING ============
    // Note: monitoring sekarang di MainActivity RootNav (app-wide)
    // Tidak perlu trigger di sini — sudah di-handle global

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
        placedMiners.sumOf { it.powerHs }
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

    // Local timer — sync dari server unclaimed + elapsed dari sync
    LaunchedEffect(roomState, placedMiners, minerActiveUntil) {
        val serverUnclaimed = roomState?.unclaimedMiningPoints ?: 0.0
        val syncTime = System.currentTimeMillis()

        while (true) {
            val now = System.currentTimeMillis()

            if (minerActiveUntil > now) {
                val elapsedSec = (now - syncTime) / 1000.0
                val generated = (elapsedSec / 60.0) * generationRatePerMin
                liveUnclaimedPoints = serverUnclaimed + generated
            } else {
                liveUnclaimedPoints = serverUnclaimed
            }
            delay(1000.milliseconds)
        }
    }

    // Polling server — sync unclaimedMiningPoints setiap 10 detik
    LaunchedEffect(Unit) {
        while (true) {
            delay(10.seconds)
            if (minerActiveUntil > System.currentTimeMillis()) {
                viewModel.loadGameState()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = LanguageManager.translate("game_room_title", AppThemePreferences.currentLanguage, "Royaltree Mining Room"),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EmeraldLight.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = LanguageManager.translate("badge_passive", AppThemePreferences.currentLanguage, "PASSIVE"),
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
                            text = LanguageManager.translate("game_room_subtitle", AppThemePreferences.currentLanguage, "Buy miners with RTP & earn passive income"),
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = LanguageManager.translate("cd_back", AppThemePreferences.currentLanguage, "Kembali"))
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
                            totalPowerGhs = totalPowerGhs,
                            generationRatePerMin = generationRatePerMin,
                            liveUnclaimedPoints = liveUnclaimedPoints,
                            roomState = roomState,
                            userPoints = user?.points ?: 0.0,
                            minerActiveUntil = minerActiveUntil,
                            minerActiveRemainingMs = minerActiveRemainingMs,
                            onActivateMiner = onActivateMiner,
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

    // Modal: Confirmation Beli Item — v2 Lebar & Canggih
    selectedItemToBuy?.let { item ->
        val userPoints = user?.points ?: 0.0
        val canAfford = userPoints >= item.tokenCost
        val unlockedSlots = roomState?.unlockedSlots ?: 2
        val usedSlots = minerItems
            .filter { it.isPlacedInRoom }
            .map { it.placedSlotIndex }
            .toSet()
        val hasEmptySlot = (0 until unlockedSlots).any { it !in usedSlots }
        val rarityColor = tierColor(item.tier)

        // Current stats untuk comparison
        val placedMiners = minerItems.filter { it.isPlacedInRoom }
        val currentPower = placedMiners.sumOf { it.powerHs }
        val currentRate = placedMiners.sumOf { it.pointsPerMinute }
        val newPower = currentPower + item.powerHs
        val newRate = currentRate + item.pointsPerMinute

        // State untuk checkbox & countdown
        var agreeChecked by remember { mutableStateOf(false) }
        var isBuying by remember { mutableStateOf(false) }
        
        val infiniteTransition = rememberInfiniteTransition(label = "miner_pulse")

        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
        val rotateAngle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(8000, easing = androidx.compose.animation.core.LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotate"
        )

        Dialog(
            onDismissRequest = { if (!isBuying) selectedItemToBuy = null },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0F172A),
                border = BorderStroke(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(rarityColor, GoldVip, rarityColor)
                    )
                ),
                shadowElevation = 24.dp,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .widthIn(max = 520.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ==== Header: Close + Countdown ====
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = rarityColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Timer,
                                    contentDescription = null,
                                    tint = rarityColor,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = rarityColor
                                )
                            }
                        }
                        if (!isBuying) {
                            IconButton(onClick = { selectedItemToBuy = null }) {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.Close,
                                    contentDescription = LanguageManager.translate("cd_close_en", AppThemePreferences.currentLanguage, "Close"),
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        }
                    }

                    // ==== Icon Miner Besar + Glow + Animasi ====
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Glow layer
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .scale(pulseScale)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            rarityColor.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                        // Icon miner
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .rotate(rotateAngle * 0.1f)
                                .scale(pulseScale)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF0A0E1A).copy(alpha = 0.7f))
                                .border(2.dp, rarityColor, RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.micro_chip),
                                contentDescription = item.name,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                        // Tier badge besar
                        Surface(
                            color = rarityColor,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .offset(y = 8.dp)
                        ) {
                            Text(
                                text = item.tier,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    // ==== Nama + Deskripsi ====
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                    }

                    // ==== Stat Grid 2x2 ====
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, rarityColor.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MiniStatBox(
                                    icon = "💰",
                                    label = "Harga",
                                    value = "${item.tokenCost} RTP",
                                    valueColor = GoldVip,
                                    modifier = Modifier.weight(1f)
                                )
                                MiniStatBox(
                                    icon = "⚡",
                                    label = "Power",
                                    value = "+${HashrateFormatter.format(item.powerHs)}",
                                    valueColor = EmeraldLight,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                MiniStatBox(
                                    icon = "📈",
                                    label = "Rate",
                                    value = "+${String.format(Locale.US, "%.4f", item.pointsPerMinute)}/mnt",
                                    valueColor = EmeraldLight,
                                    modifier = Modifier.weight(1f)
                                )
                                MiniStatBox(
                                    icon = "💎",
                                    label = "Saldo",
                                    value = String.format(Locale.US, "%.2f", userPoints),
                                    valueColor = if (canAfford) Color.White else Color(0xFFEF4444),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // ==== Comparison Before → After ====
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0A0E1A),
                        border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "📊 PERBANDINGAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight,
                                letterSpacing = 1.sp
                            )
                            ComparisonRow(
                                label = "Power",
                                before = HashrateFormatter.format(currentPower),
                                after = HashrateFormatter.format(newPower),
                                delta = "+${HashrateFormatter.format(item.powerHs)}"
                            )
                            ComparisonRow(
                                label = "Rate",
                                before = String.format(Locale.US, "%.4f", currentRate),
                                after = String.format(Locale.US, "%.4f", newRate),
                                delta = "+${String.format(Locale.US, "%.4f", item.pointsPerMinute)}"
                            )
                        }
                    }

                    // ==== Warning kalau tidak cukup ====
                    if (!canAfford) {
                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RTP tidak mencukupi — kurang ${String.format(Locale.US, "%.2f", item.tokenCost - userPoints)} RTP",
                                    fontSize = 11.sp,
                                    color = Color(0xFFEF4444),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // ==== Confirmation Checkbox ====
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = canAfford && !isBuying) { agreeChecked = !agreeChecked }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = agreeChecked,
                            onCheckedChange = { agreeChecked = it },
                            enabled = canAfford && !isBuying,
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = EmeraldLight,
                                uncheckedColor = Color(0xFF475569)
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Saya setuju membeli ${item.name} dengan ${item.tokenCost} RTP",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    // ==== Tombol Full-width ====
                    Button(
                        onClick = {
                            if (isBuying) return@Button
                            try {
                                isBuying = true
                                onBuyGameMinerItem(item.id)
                            } catch (e: Exception) {
                                AppLogger.e("GameRoomBuy", "❌ Error onClick: " + e.message, e)
                                isBuying = false
                            }
                        },
                        enabled = canAfford && agreeChecked && !isBuying,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford && agreeChecked) EmeraldPrimary else Color(0xFF334155),
                            disabledContainerColor = Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isBuying) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(LanguageManager.translate("game_room_processing", AppThemePreferences.currentLanguage, "Memproses..."), fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                text = if (hasEmptySlot) "🔥 BELI & PASANG" else "🔥 BELI SAHAJA",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // ==== Tombol Batal ====
                    if (!isBuying) {
                        OutlinedButton(
                            onClick = { selectedItemToBuy = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            border = BorderStroke(1.dp, Color(0xFF475569)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(LanguageManager.translate("common_cancel", AppThemePreferences.currentLanguage, "Batal"), color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }

    // Modal: Pilih Item dari Inventaris untuk dipasang di Slot Kosong
    slotToAssignItem?.let { targetSlot ->
        val unplacedOwnedMiners = minerItems.filter { 
            it.isOwned && !it.isPlacedInRoom && it.powerHs > 0 
        }

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
                    // Group by tier
                    val tierOrder = listOf("COMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC")
                    val grouped = unplacedOwnedMiners.groupBy { it.tier }
                    val sortedTiers = grouped.keys.sortedBy { tierOrder.indexOf(it) }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        sortedTiers.forEach { tier ->
                            val tierColor = tierColor(tier)
                            
                            // Section header
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = tier,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = tierColor,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "(${grouped[tier]!!.size})",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            
                            // Items per tier
                            items(grouped[tier]!!) { miner ->
                                SlotMinerCard(
                                    miner = miner,
                                    tierColor = tierColor,
                                    onInstall = {
                                        onToggleMinerSlot(miner.id, targetSlot)
                                        slotToAssignItem = null
                                    }
                                )
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
                        Text(LanguageManager.translate("game_open_shop", AppThemePreferences.currentLanguage, "Buka Toko Item"))
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { slotToAssignItem = null }) {
                    Text(LanguageManager.translate("common_close", AppThemePreferences.currentLanguage, "Tutup"))
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
                    Text(LanguageManager.translate("game_room_ok_understood", AppThemePreferences.currentLanguage, "OK, Mengerti"))
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
                    contentDescription = LanguageManager.translate("cd_rtp", AppThemePreferences.currentLanguage, "RTP"),
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
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MiningRoomTab(
    currentLanguage: AppLanguage,
    placedMiners: List<GameMinerItemEntity>,
    totalPowerGhs: Double,
    generationRatePerMin: Double,
    liveUnclaimedPoints: Double,
    roomState: GameRoomStateEntity?,
    userPoints: Double = 0.0,
    minerActiveUntil: Long = 0L,
    minerActiveRemainingMs: Long = 0L,
    onActivateMiner: () -> Unit = {},
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
                Triple(miner.name, miner.pointsPerMinute, miner.powerHs)
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
                    // ==== CARD 1: HEADER ====
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Card 1: Header saja
                        Surface(
                            color = Color(0xFF0F172A).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = LanguageManager.translate("game_mining_tycoon", AppThemePreferences.currentLanguage, "MINING TYCOON"),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldLight,
                                        letterSpacing = 1.5.sp
                                    )
                                    Text(
                                        text = LanguageManager.translate("game_points_miner", AppThemePreferences.currentLanguage, "Royaltree Points Miner"),
                                        fontSize = 10.sp,
                                        color = RetroAmber,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                Surface(
                                    color = EmeraldPrimary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "${placedMiners.size}/${roomState?.unlockedSlots ?: 6}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // ==== CARD 2: STATS ====
                        Surface(
                            color = Color(0xFF0F172A).copy(alpha = 0.85f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Stats Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RetroStatBox(
                                        emoji = "🔥",
                                        label = LanguageManager.translate("game_mining_power", AppThemePreferences.currentLanguage, "POWER"),
                                        value = HashrateFormatter.format(totalPowerGhs),
                                        color = RetroAmber,
                                        modifier = Modifier.weight(1f)
                                    )
                                    RetroStatBox(
                                        emoji = "💰",
                                        label = LanguageManager.translate("game_mining_rtp", AppThemePreferences.currentLanguage, "RTP"),
                                        value = "${GlobalPointsManager.formatRtp(userPoints + liveUnclaimedPoints)}",
                                        color = RetroGold,
                                        modifier = Modifier.weight(1.5f),
                                        useRtpIcon = true
                                    )
                                    RetroStatBox(
                                        emoji = "⚡",
                                        label = LanguageManager.translate("game_mining_rate", AppThemePreferences.currentLanguage, "RATE"),
                                        value = "${String.format(Locale.US, "%.4f", generationRatePerMin)}/m",
                                        color = EmeraldLight,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                // Live ticker — compact single row
                                Surface(
                                    color = Color(0xFF064E3B).copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
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
                                }

                                // Miner breakdown — compact
                                if (minerBreakdown.isNotEmpty()) {
                                    val totalRate = minerBreakdown.sumOf { it.second }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "🔥 ${placedMiners.size} miner aktif",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF94A3B8)
                                        )
                                        Text(
                                            text = "Total: +${String.format(Locale.US, "%.4f", totalRate)}/m",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight
                                        )
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
                                    // ✅ MINER ACTIVE — CARD CANGGIH dengan animasi
                                    val pulseTransition = rememberInfiniteTransition(label = "miner_pulse")
                                    val pulseAlpha by pulseTransition.animateFloat(
                                        initialValue = 0.6f,
                                        targetValue = 1f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1500, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "pulse_alpha"
                                    )
                                    val pulseScale by pulseTransition.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 1.15f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1500, easing = FastOutSlowInEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "pulse_scale"
                                    )

                                    // Progress: sisa waktu / 6 jam
                                    val totalDurationMs = 6 * 60 * 60 * 1000L
                                    val progress = (minerActiveRemainingMs.toFloat() / totalDurationMs).coerceIn(0f, 1f)

                                    // Gradien background
                                    val gradientBrush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF064E3B),
                                            Color(0xFF065F46),
                                            Color(0xFF047857)
                                        )
                                    )

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(
                                                elevation = 12.dp,
                                                shape = RoundedCornerShape(16.dp),
                                                ambientColor = EmeraldLight.copy(alpha = 0.3f),
                                                spotColor = EmeraldLight.copy(alpha = 0.5f)
                                            ),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(gradientBrush)
                                                .padding(16.dp)
                                        ) {
                                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                // Header: icon + label + timer
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        // Icon berdenyut
                                                        Box(
                                                            modifier = Modifier
                                                                .size(40.dp)
                                                                .alpha(pulseAlpha)
                                                                .scale(pulseScale)
                                                                .background(
                                                                    brush = Brush.radialGradient(
                                                                        colors = listOf(
                                                                            GoldVip.copy(alpha = 0.5f),
                                                                            Color.Transparent
                                                                        )
                                                                    ),
                                                                    shape = CircleShape
                                                                ),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text("⛏️", fontSize = 24.sp)
                                                        }
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Column {
                                                            Text(
                                                                text = LanguageManager.translate("game_miner_active", AppThemePreferences.currentLanguage, "MINER SEDANG AKTIF"),
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = EmeraldLight
                                                            )
                                                            Text(
                                                                text = "Mining berjalan...",
                                                                fontSize = 10.sp,
                                                                color = Color.White.copy(alpha = 0.6f)
                                                            )
                                                        }
                                                    }
                                                    // Timer dengan format HH:MM:SS
                                                    Text(
                                                        text = formatMinerCountdownHHMMSS(minerActiveRemainingMs),
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color.White,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }

                                                // Progress bar
                                                Column {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "Sisa waktu",
                                                            fontSize = 9.sp,
                                                            color = Color.White.copy(alpha = 0.7f)
                                                        )
                                                        Text(
                                                            text = "${(progress * 100).toInt()}%",
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = GoldVip
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(6.dp)
                                                            .clip(RoundedCornerShape(3.dp))
                                                            .background(Color.White.copy(alpha = 0.15f))
                                                    ) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxWidth(progress)
                                                                .fillMaxHeight()
                                                                .background(
                                                                    brush = Brush.horizontalGradient(
                                                                        colors = listOf(GoldVip, EmeraldLight)
                                                                    )
                                                                )
                                                        )
                                                    }
                                                }
                                            }
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
                                                text = "⛔ ${LanguageManager.translate("game_miner_inactive", AppThemePreferences.currentLanguage, "MINER TIDAK AKTIF")}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFF87171)
                                            )
                                            Text(
                                                text = LanguageManager.translate("game_miner_hint", AppThemePreferences.currentLanguage, "Aktifkan untuk mula mining"),
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
                                                text = "🎬 ${LanguageManager.translate("game_miner_button", AppThemePreferences.currentLanguage, "Aktifkan Miner (Tonton Iklan)")}",
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
                    Text(LanguageManager.translate("game_buy_item", AppThemePreferences.currentLanguage, "Beli Item"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                        Text(LanguageManager.translate("game_play", AppThemePreferences.currentLanguage, "Main"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                text = LanguageManager.translate("slot_locked", AppThemePreferences.currentLanguage, "TERKUNCI"),
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
                    Text(miner.iconEmoji, fontSize = 26.sp)
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
                            HashrateFormatter.formatShort(miner.powerHs),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("\uD83D\uDCB0", fontSize = 9.sp)
                        Text(
                            String.format(Locale.US, "%.2f", miner.pointsPerMinute * 1440),
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
                text = LanguageManager.translate("common_loading", AppThemePreferences.currentLanguage, "Memuat data..."),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = LanguageManager.translate("common_loading_desc", AppThemePreferences.currentLanguage, "Menyinkronkan data dari server"),
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        // Banner info Toko Item (full-width)
        item(span = { GridItemSpan(2) }) {
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
                            text = LanguageManager.translate("game_unlock_miner", AppThemePreferences.currentLanguage, "Unlock Miner dengan RTP"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = LanguageManager.translate("game_unlock_miner_desc", AppThemePreferences.currentLanguage, "Kumpulkan RTP dari iklan & misi untuk membuka miner baru!"),
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

        // Fast earn points CTA banner (full-width)
        item(span = { GridItemSpan(2) }) {
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
                            Text(LanguageManager.translate("game_need_more_points", AppThemePreferences.currentLanguage, "Butuh Poin Lebih Banyak?"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                            Text(LanguageManager.translate("game_complete_missions", AppThemePreferences.currentLanguage, "Selesaikan misi Like, Subscribe, Nonton, & Web"), fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                    }
                    Button(
                        onClick = onNavigateToMissions,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldVip),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text(LanguageManager.translate("game_open_missions", AppThemePreferences.currentLanguage, "Buka Misi"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        // Shop items list
        items(minerItems) { item ->
            ShopMinerCard(
                item = item,
                userPoints = userPoints,
                onBuy = { onBuyItem(item) }
            )
        }
    }
}

@Composable
private fun SlotMinerCard(
    miner: GameMinerItemEntity,
    tierColor: Color,
    onInstall: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = tierColor.copy(alpha = 0.08f)),
        border = BorderStroke(1.dp, tierColor.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onInstall() }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon 40dp
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(tierColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.micro_chip),
                    contentDescription = miner.name,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = miner.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "+${HashrateFormatter.format(miner.powerHs)} · ${miner.tier}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = tierColor,
                    maxLines = 1
                )
                Text(
                    text = "+${String.format(Locale.US, "%.4f", miner.pointsPerMinute)} RTP/mnt",
                    fontSize = 9.sp,
                    color = EmeraldLight,
                    maxLines = 1
                )
            }
            Button(
                onClick = onInstall,
                colors = ButtonDefaults.buttonColors(containerColor = tierColor),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(
                    text = LanguageManager.translate("game_install", AppThemePreferences.currentLanguage, "Pasang"),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun ShopMinerCard(
    item: GameMinerItemEntity,
    userPoints: Double = 0.0,
    onBuy: () -> Unit
) {
    val rarityColor = tierColor(item.tier)
    val canAfford = userPoints >= item.tokenCost

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(RetroCard)
            .border(1.5.dp, rarityColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0805))
                    .border(1.5.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.micro_chip),
                    contentDescription = item.name,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 13.sp
            )
            Surface(
                color = rarityColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = item.tier,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = rarityColor,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("⚡", fontSize = 9.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = HashrateFormatter.format(item.powerHs),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = RetroAmber,
                        maxLines = 1
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("💰", fontSize = 9.sp)
                    Spacer(Modifier.width(3.dp))
                    Text(
                        text = "${String.format(Locale.US, "%.4f", item.pointsPerMinute)}/mnt",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Medium,
                        color = EmeraldLight,
                        maxLines = 1
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.08f))
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("💰", fontSize = 11.sp)
                Spacer(Modifier.width(3.dp))
                Text(
                    text = item.tokenCost.toString(),
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = RetroGold
                )
                Text(
                    text = " RTP",
                    fontSize = 9.sp,
                    color = RetroGold.copy(alpha = 0.7f)
                )
            }
            if (item.isOwned) {
                Surface(
                    color = EmeraldLight.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, EmeraldLight.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("✅", fontSize = 10.sp)
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = if (item.isPlacedInRoom) "Terpasang" else "Dimiliki",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight,
                            maxLines = 1
                        )
                    }
                }
            } else {
                Surface(
                    color = if (canAfford) RetroGold else Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = canAfford) { onBuy() }
                ) {
                    Text(
                        text = if (canAfford) "BELI" else "KURANG",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (canAfford) Color.Black else Color.White.copy(alpha = 0.7f),
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 7.dp)
                    )
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
                delay(1000.milliseconds)
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
                                    Text(LanguageManager.translate("game_coinrush_title", AppThemePreferences.currentLanguage, "Crypto Coin Tap Rush"), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                    Text(LanguageManager.translate("game_coinrush_subtitle", AppThemePreferences.currentLanguage, "Mini-Game Penghasil Power Royaltree"), fontSize = 11.sp, color = Color(0xFFC7D2FE))
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
                            Text(LanguageManager.translate("game_start_now", AppThemePreferences.currentLanguage, "Mulai Game Sekarang (15s)"), fontWeight = FontWeight.Bold, color = Color.Black)
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

/**
 * Format countdown dengan detik: HH:MM:SS
 */
private fun formatMinerCountdownHHMMSS(ms: Long): String {
    if (ms <= 0L) return "00:00:00"
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
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



@Composable
private fun MiniStatBox(
    icon: String,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0A0E1A),
        border = BorderStroke(1.dp, valueColor.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = valueColor
            )
        }
    }
}

@Composable
private fun ComparisonRow(
    label: String,
    before: String,
    after: String,
    delta: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = before,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
        Text(
            text = " → ",
            fontSize = 11.sp,
            color = Color(0xFF94A3B8)
        )
        Text(
            text = after,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = delta,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = EmeraldLight
        )
    }
}
