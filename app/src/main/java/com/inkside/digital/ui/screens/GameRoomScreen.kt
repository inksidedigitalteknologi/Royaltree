package com.inkside.digital.ui.screens

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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.inkside.digital.data.model.GameMinerItemEntity
import com.inkside.digital.data.model.GameRoomStateEntity
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.components.BannerAdView
import com.inkside.digital.ui.components.MiningClaimDialog
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.EmeraldPrimary
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.ui.theme.PurpleSecondary
import com.inkside.digital.viewmodel.AffiliateViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random

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

    GameRoomScreenContent(
        user = user,
        minerItems = minerItems,
        placedMiners = placedMiners,
        roomState = roomState,
        onClaimMining = { viewModel.showAdRewardModal.value = true; viewModel.setPendingMiningClaim(true) },
        onToggleMinerSlot = { id, slot -> viewModel.toggleMinerSlot(id, slot) },
        onBuyGameMinerItem = { id -> viewModel.buyGameMinerItem(id) },
        onFinishGame = { score -> viewModel.finishMiniGame(score) },
        onNavigateToMissions = onNavigateToMissions,
        onBack = onBack,
        currentLanguage = currentLanguage
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameRoomScreenContent(
    user: UserEntity?,
    minerItems: List<GameMinerItemEntity>,
    placedMiners: List<GameMinerItemEntity>,
    roomState: GameRoomStateEntity?,
    onClaimMining: () -> Unit,
    onToggleMinerSlot: (String, Int) -> Unit,
    onBuyGameMinerItem: (String) -> Unit,
    onFinishGame: (Int) -> Unit,
    onNavigateToMissions: () -> Unit,
    onBack: () -> Unit,
    currentLanguage: AppLanguage,
    minerTokens: Int = 0,
    onClaimMinerToken: (String) -> Unit = {},
    onUnlockMiner: (String) -> Unit = {}
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

    val generationRatePerMin = remember(placedMiners) {
        placedMiners.sumOf { it.pointsPerMinute }
    }

    LaunchedEffect(roomState, placedMiners) {
        while (true) {
            val now = System.currentTimeMillis()
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
                                text = "Ruang Mining RollerCoin",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = EmeraldLight.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "PASSIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Beli item penambang pakai poin & panen hasil pasif",
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
                actions = {
                    Surface(
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { onNavigateToMissions() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⭐", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${user?.points ?: 0} Poin",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldVip
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                    MiningRoomTab(
                        placedMiners = placedMiners,
                        allMiners = minerItems,
                        totalPowerGhs = totalPowerGhs,
                        basePowerGhs = basePowerGhs,
                        bonusPowerGhs = bonusPowerGhs,
                        generationRatePerMin = generationRatePerMin,
                        liveUnclaimedPoints = liveUnclaimedPoints,
                        roomState = roomState,
                        minerTokens = minerTokens,
                        onClaimMining = onClaimMining,
                        onClaimMinerToken = onClaimMinerToken,
                        onUnlockMiner = onUnlockMiner,
                        onUnplaceMiner = { minerId, slot -> onToggleMinerSlot(minerId, slot) },
                        onOpenShop = { activeTab = 1 },
                        onOpenAssignSlotModal = { slot -> slotToAssignItem = slot },
                        onOpenMiniGame = { activeTab = 2 }
                    )
                }
                1 -> {
                    // TAB 1: TOKO ITEM (SHOP)
                    ItemShopTab(
                        minerItems = minerItems,
                        userPoints = user?.points ?: 0,
                        minerTokens = minerTokens,
                        onBuyItem = { item -> selectedItemToBuy = item },
                        onUnlockMiner = onUnlockMiner,
                        onNavigateToMissions = onNavigateToMissions
                    )
                }
                2 -> {
                    // TAB 2: MINI-GAME ARCADE
                    MiniGameArcadeTab(
                        onFinishGame = { score -> onFinishGame(score) },
                        highScore = roomState?.miniGameHighScore ?: 0
                    )
                }
            }
        }
    }

    // Modal: Confirmation Beli Item
    selectedItemToBuy?.let { item ->
        AlertDialog(
            onDismissRequest = { selectedItemToBuy = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.iconEmoji, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Beli ${item.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = item.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(LanguageManager.translate("game_price", currentLanguage, "Harga:"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${item.pricePoints} Poin", fontWeight = FontWeight.Bold, color = GoldVip, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(LanguageManager.translate("game_mining_power", currentLanguage, "Mining Power:"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+${item.powerGhs} GH/s", fontWeight = FontWeight.Bold, color = ElectricBlue, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(LanguageManager.translate("game_rate_points", currentLanguage, "Rate Poin:"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+${item.pointsPerMinute} Poin/Menit", fontWeight = FontWeight.Bold, color = EmeraldLight, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(LanguageManager.translate("game_your_points", currentLanguage, "Saldo Poin Anda:"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${user?.points ?: 0} Poin", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    val canAfford = (user?.points ?: 0) >= item.pricePoints
                    if (!canAfford) {
                        Surface(
                            color = Color(0xFFEF4444).copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Poin tidak mencukupi! Kurang ${item.pricePoints - (user?.points ?: 0)} Poin. Selesaikan misi Like, Subscribe, Nonton, & Web untuk menambah poin.",
                                fontSize = 11.sp,
                                color = Color(0xFFEF4444),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                val canAfford = (user?.points ?: 0) >= item.pricePoints
                Button(
                    onClick = {
                        onBuyGameMinerItem(item.id)
                        selectedItemToBuy = null
                    },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text(LanguageManager.translate("game_buy_now", currentLanguage, "Beli Sekarang"), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedItemToBuy = null }) {
                    Text(LanguageManager.translate("common_cancel", currentLanguage, "Batal"))
                }
            }
        )
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
                                    Text(miner.iconEmoji, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(miner.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("+${miner.powerGhs} GH/s • +${miner.pointsPerMinute} Poin/mnt", fontSize = 10.sp, color = EmeraldLight)
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

}


// -------------------------------------------------------------
// TAB 0: RUANG RAK MINING (ROLLERCOIN RACK ROOM)
// -------------------------------------------------------------
@Composable
private fun HeroBanner(
    totalPowerGhs: Double,
    liveUnclaimedPoints: Double,
    generationRatePerMin: Double
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(RetroBrown, RetroBgDark, Color(0xFF0F0805))
                )
            )
            .border(2.dp, RetroBorder, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        // Emoji besar background
        Text(
            text = "⛏️",
            fontSize = 110.sp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp),
            color = Color.White.copy(alpha = 0.08f)
        )

        Column {
            Text(
                text = "MINING TYCOON",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = RetroGold,
                letterSpacing = 2.sp
            )
            Text(
                text = "Royaltree Points Miner",
                fontSize = 11.sp,
                color = RetroAmber,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RetroStatBox(
                    emoji = "🔥",
                    label = "POWER",
                    value = "${String.format("%.0f", totalPowerGhs)} GH/s",
                    color = RetroAmber,
                    modifier = Modifier.weight(1f)
                )
                RetroStatBox(
                    emoji = "💰",
                    label = "RTP",
                    value = String.format(Locale.US, "%.8f", liveUnclaimedPoints),
                    color = RetroGold,
                    modifier = Modifier.weight(1f)
                )
                RetroStatBox(
                    emoji = "⚡",
                    label = "RATE",
                    value = "${String.format("%.2f", generationRatePerMin)}/m",
                    color = EmeraldLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RetroStatBox(
    emoji: String,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(emoji, fontSize = 16.sp)
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
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Composable
private fun MiningRoomTab(
    placedMiners: List<GameMinerItemEntity>,
    allMiners: List<GameMinerItemEntity>,
    totalPowerGhs: Double,
    basePowerGhs: Double,
    bonusPowerGhs: Double,
    generationRatePerMin: Double,
    liveUnclaimedPoints: Double,
    roomState: GameRoomStateEntity?,
    minerTokens: Int = 0,
    onClaimMining: () -> Unit,
    onClaimMinerToken: (String) -> Unit = {},
    onUnlockMiner: (String) -> Unit = {},
    onUnplaceMiner: (minerId: String, slot: Int) -> Unit,
    onOpenShop: () -> Unit,
    onOpenAssignSlotModal: (slotIndex: Int) -> Unit,
    onOpenMiniGame: () -> Unit
) {
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
        // ==== Hero Banner Retro + Token Counter ====
        item {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                HeroBanner(
                    totalPowerGhs = totalPowerGhs,
                    liveUnclaimedPoints = liveUnclaimedPoints,
                    generationRatePerMin = generationRatePerMin
                )
                // Token badge di pojok kanan atas
                Surface(
                    color = RetroGold,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⛏️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$minerTokens",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

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
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    RetroBrown,
                                    RetroBgDark,
                                    Color(0xFF0F0805)
                                )
                            )
                        )
                        .border(2.dp, RetroBorder, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (placedMiners.isNotEmpty()) EmeraldLight.copy(alpha = pulseAlpha) else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (placedMiners.isNotEmpty()) "MINING AKTIF (24/7 ONLINE)" else "RAK KOSONG - PASANG ITEM",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (placedMiners.isNotEmpty()) EmeraldLight else Color(0xFF94A3B8)
                                )
                            }
                            Surface(
                                color = Color(0xFF1E293B).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${placedMiners.size}/6 Rak Terisi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Hash Power & Rate Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = Color(0xFF0F172A).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.Speed, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(LanguageManager.translate("game_total_hash", currentLanguage, "Total Hash Rate"), fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", totalPowerGhs)} GH/s",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (bonusPowerGhs > 0) {
                                        Text(
                                            text = "+${bonusPowerGhs.toInt()} GH/s Boost Arcade",
                                            fontSize = 9.sp,
                                            color = GoldVip,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Surface(
                                color = Color(0xFF0F172A).copy(alpha = 0.8f),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.Bolt, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(LanguageManager.translate("game_point_speed", currentLanguage, "Kecepatan Poin"), fontSize = 10.sp, color = Color(0xFF94A3B8))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "+${String.format(Locale.US, "%.2f", generationRatePerMin)} /mnt",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight
                                    )
                                    Text(
                                        text = "≈ +${String.format(Locale.US, "%.1f", generationRatePerMin * 60)} Poin/Jam",
                                        fontSize = 9.sp,
                                        color = Color(0xFF94A3B8)
                                    )
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "HASIL MINING SIAP DIKLAIM",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight
                                        )
                                        Text(
                                            text = "${String.format(Locale.US, "%.8f", liveUnclaimedPoints)} RTP",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                    Text("⛏️", fontSize = 28.sp)
                                }

                                Button(
                                    onClick = onClaimMining,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .testTag("claim_mining_points_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.Bolt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Klaim Hasil Mining (${String.format(Locale.US, "%.8f", liveUnclaimedPoints)} RTP)",
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

        // 6 Slots in Rack Room — Grid 2x3
        val occupiedMap = placedMiners.associateBy { it.placedSlotIndex }
        items(3) { rowIndex ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (colIndex in 0 until 2) {
                    val slotIndex = rowIndex * 2 + colIndex
                    Box(modifier = Modifier.weight(1f)) {
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
                                text = "Mainkan Mini-Game Arcade Coin Rush (+80 GH/s Power Boost & Poin langsung)!",
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
private fun RackSlotItemCard(
    slotIndex: Int,
    miner: GameMinerItemEntity?,
    pulseAlpha: Float,
    onUnplace: () -> Unit,
    onAssign: () -> Unit
) {
    val isOccupied = miner != null
    val borderColor = if (isOccupied) RetroGold else RetroBorder.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isOccupied) RetroCard else RetroBgDark.copy(alpha = 0.7f))
            .border(
                width = if (isOccupied) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isOccupied) { onAssign() }
    ) {
        if (isOccupied) {
            // ==== Occupied Slot ====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon + LED
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F0805))
                        .border(1.dp, RetroBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(miner!!.iconEmoji, fontSize = 24.sp)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(RetroGold.copy(alpha = pulseAlpha))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = miner.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = RetroGold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = RetroAmber.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = miner.tier,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = RetroAmber,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Slot ${slotIndex + 1}",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = " • +${miner.powerGhs} GH/s",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = RetroAmber
                        )
                    }
                    Text(
                        text = "+${miner.pointsPerMinute} poin/mnt",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldLight
                    )
                }

                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { onUnplace() }
                ) {
                    Text(
                        text = "Lepas",
                        fontSize = 10.sp,
                        color = Color(0xFFEF4444),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        } else {
            // ==== Empty Slot ====
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onAssign() }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F0805))
                        .border(1.dp, RetroBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⛏️", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Slot ${slotIndex + 1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Text(
                    text = "Tap untuk isi",
                    fontSize = 9.sp,
                    color = RetroAmber.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 1: TOKO ITEM MINER (SHOP)
// -------------------------------------------------------------
@Composable
private fun ItemShopTab(
    minerItems: List<GameMinerItemEntity>,
    userPoints: Int,
    minerTokens: Int = 0,
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
                            text = "Unlock Miner dengan Token",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Kumpulkan Miner Token dari iklan & misi untuk membuka miner baru!",
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
                                Text(LanguageManager.translate("profile_token", currentLanguage, "Token") + ": ", fontSize = 11.sp, color = RetroGold)
                                Text(
                                    text = "$minerTokens",
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
                minerTokens = minerTokens,
                onBuy = { onBuyItem(item) },
                onUnlock = { onUnlockMiner(item.id) }
            )
        }
    }
}

@Composable
private fun ShopMinerCard(
    item: GameMinerItemEntity,
    userPoints: Int,
    minerTokens: Int = 0,
    onBuy: () -> Unit,
    onUnlock: () -> Unit = {}
) {
    val rarityColor = tierColor(item.tier)
    val canAfford = userPoints >= item.pricePoints

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
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F0805))
                        .border(1.dp, rarityColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.iconEmoji, fontSize = 26.sp)
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
                            text = "+${item.powerGhs} GH/s",
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
                            text = "+${item.pointsPerMinute}/mnt",
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
                            text = "${item.pricePoints}",
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
                            text = if (canAfford) "BELI" else "KURANG ${item.pricePoints - userPoints}",
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
    onFinishGame: (score: Int) -> Unit,
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
                onFinishGame(score)
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
                                    Text(LanguageManager.translate("game_coinrush_subtitle", currentLanguage, "Mini-Game Penghasil Power RollerCoin"), fontSize = 11.sp, color = Color(0xFFC7D2FE))
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
                            text = "Mainkan game 15 detik! Ketuk koin & permata secepat mungkin untuk mendapatkan +20 s/d 60 Poin instan dan +80 GH/s Power Boost selama 2 jam.",
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
