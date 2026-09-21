package com.example.ui.screens

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
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.data.model.GameMinerItemEntity
import com.example.data.model.GameRoomStateEntity
import com.example.data.model.UserEntity
import com.example.localization.AppLanguage
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldVip
import com.example.ui.theme.PurpleSecondary
import com.example.viewmodel.AffiliateViewModel
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
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
        onClaimMining = { viewModel.claimGameMiningPoints() },
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
    currentLanguage: AppLanguage
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
                        onClaimMining = onClaimMining,
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
                        onBuyItem = { item -> selectedItemToBuy = item },
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
                                Text("Harga:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${item.pricePoints} Poin", fontWeight = FontWeight.Bold, color = GoldVip, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mining Power:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+${item.powerGhs} GH/s", fontWeight = FontWeight.Bold, color = ElectricBlue, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Rate Poin:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("+${item.pointsPerMinute} Poin/Menit", fontWeight = FontWeight.Bold, color = EmeraldLight, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Saldo Poin Anda:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    Text("Beli Sekarang", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { selectedItemToBuy = null }) {
                    Text("Batal")
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
                                        Text("Pasang", fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
                        Text("Buka Toko Item")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { slotToAssignItem = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}


// -------------------------------------------------------------
// TAB 0: RUANG RAK MINING (ROLLERCOIN RACK ROOM)
// -------------------------------------------------------------
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
    onClaimMining: () -> Unit,
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
        // Hero Room Power & Harvest Dashboard
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
                                    Color(0xFF0F172A),
                                    Color(0xFF1E1B4B),
                                    Color(0xFF064E3B)
                                )
                            )
                        )
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
                                        Text("Total Hash Rate", fontSize = 10.sp, color = Color(0xFF94A3B8))
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
                                        Text("Kecepatan Poin", fontSize = 10.sp, color = Color(0xFF94A3B8))
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
                                            text = "${String.format(Locale.US, "%.3f", liveUnclaimedPoints)} POIN",
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
                                            text = "Klaim Hasil Mining (${liveUnclaimedPoints.toInt().coerceAtLeast(1)} Poin)",
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
                    Text("Beli Item", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 6 Slots in Rack Room
        val occupiedMap = placedMiners.associateBy { it.placedSlotIndex }
        items(6) { slotIndex ->
            val minerInSlot = occupiedMap[slotIndex]
            RackSlotItemCard(
                slotIndex = slotIndex,
                miner = minerInSlot,
                pulseAlpha = pulseAlpha,
                onUnplace = { onUnplaceMiner(minerInSlot!!.id, slotIndex) },
                onAssign = { onOpenAssignSlotModal(slotIndex) }
            )
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
                        Text("Main", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (miner != null) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (miner != null) EmeraldLight.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        if (miner != null) {
            // Occupied Rack Unit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LED Blinker & Icon
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(miner.iconEmoji, fontSize = 24.sp)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(EmeraldLight.copy(alpha = pulseAlpha))
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = miner.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = tierColor(miner.tier).copy(alpha = 0.18f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = miner.tier,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = tierColor(miner.tier),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Slot ${slotIndex + 1} • ",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "+${miner.powerGhs} GH/s",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricBlue
                        )
                        Text(
                            text = " • +${miner.pointsPerMinute} Poin/mnt",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight
                        )
                    }
                }

                OutlinedButton(
                    onClick = onUnplace,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text("Lepas", fontSize = 10.sp, color = MaterialTheme.colorScheme.error)
                }
            }
        } else {
            // Empty Slot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAssign() }
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Dns, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Rak Slot ${slotIndex + 1} (Kosong)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Ketuk untuk pasang item penambang",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Button(
                    onClick = onAssign,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary.copy(alpha = 0.15f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Pasang", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                }
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
    onBuyItem: (GameMinerItemEntity) -> Unit,
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
                    Text("🛒", fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Beli Item Penambang Pakai Poin",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Item yang Anda beli akan langsung ditempatkan di rak untuk memproduksi poin pasif setiap menit!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                            Text("Butuh Poin Lebih Banyak?", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                            Text("Selesaikan misi Like, Subscribe, Nonton, & Web", fontSize = 10.sp, color = Color(0xFF94A3B8))
                        }
                    }
                    Button(
                        onClick = onNavigateToMissions,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldVip),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Buka Misi", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
private fun ShopMinerCard(
    item: GameMinerItemEntity,
    userPoints: Int,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (item.isOwned) EmeraldLight.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A)),
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
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = tierColor(item.tier).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = item.tier,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = tierColor(item.tier),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = item.description,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Specs badges: Hash Rate & Point Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = ElectricBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.Speed, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Power: +${item.powerGhs} GH/s", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                    }
                }

                Surface(
                    color = EmeraldLight.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.Bolt, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Generate: +${item.pointsPerMinute}/mnt", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldLight)
                    }
                }
            }

            // Price & Buy Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Harga Item:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.pricePoints} Poin",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = GoldVip
                        )
                    }
                }

                if (item.isOwned) {
                    Surface(
                        color = EmeraldLight.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (item.isPlacedInRoom) "Terpasang di Rak" else "Di Inventaris",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }
                    }
                } else {
                    val canAfford = userPoints >= item.pricePoints
                    Button(
                        onClick = onBuy,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(38.dp)
                    ) {
                        Text(
                            text = if (canAfford) "Beli Pakai Poin" else "Poin Kurang (${userPoints}/${item.pricePoints})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (canAfford) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: MINI-GAME ARCADE (COIN RUSH)
// -------------------------------------------------------------
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
                                    Text("Crypto Coin Tap Rush", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                    Text("Mini-Game Penghasil Power RollerCoin", fontSize = 11.sp, color = Color(0xFFC7D2FE))
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
                            Text("Mulai Game Sekarang (15s)", fontWeight = FontWeight.Bold, color = Color.Black)
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
