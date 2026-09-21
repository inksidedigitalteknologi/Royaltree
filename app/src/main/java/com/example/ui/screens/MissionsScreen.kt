package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskMissionEntity
import com.example.data.model.UserEntity
import com.example.localization.AppLanguage
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldVip
import com.example.ui.theme.PurpleSecondary
import com.example.util.GlobalPointsManager
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsScreen(
    user: UserEntity?,
    missions: List<TaskMissionEntity>,
    currentLanguage: AppLanguage,
    onBack: () -> Unit,
    onCheckIn: () -> Unit,
    onClaimMission: (missionId: String) -> Unit,
    onCompleteTaskAction: (missionId: String) -> Unit,
    onOpenRedeemPoints: () -> Unit,
    onOpenTransferQr: () -> Unit = {},
    onOpenWatchAd: () -> Unit,
    onNavigateToGameRoom: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var activeInteractiveMission by remember { mutableStateOf<TaskMissionEntity?>(null) }

    val filteredMissions = when (selectedFilter) {
        "DAILY" -> missions.filter { it.type == "DAILY_TASK" }
        "LIKE_SUB" -> missions.filter { it.category in listOf("LIKE", "SUBSCRIBE") }
        "VIEW_WATCH" -> missions.filter { it.category in listOf("VIEW", "WATCH", "AD") }
        "WEB" -> missions.filter { it.category == "WEB" }
        "GAME" -> missions.filter { it.category == "GAME" }
        "MILESTONE" -> missions.filter { it.type == "MILESTONE_MISSION" }
        else -> missions
    }

    val totalPoints = user?.points ?: 0
    val usdValue = GlobalPointsManager.getUsdValue(totalPoints)
    val streakDay = user?.checkInStreak ?: 1
    val isCheckedInToday = remember(user?.lastCheckInDate) {
        val lastDay = (user?.lastCheckInDate ?: 0L) / 86400000L
        val today = System.currentTimeMillis() / 86400000L
        (user?.lastCheckInDate ?: 0L) > 0 && lastDay == today
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Misi & Tugas Hadiah RTP",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Royaltree Point Reward Center",
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
                        modifier = Modifier.testTag("missions_back_button")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Hero Balance & Value Card
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
                                        Color(0xFF1E293B),
                                        Color(0xFF0D9488)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(GoldVip.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🌲", fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Saldo Royaltree Point",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "RTP Universal Rate (100 RTP = $1.00 USD)",
                                            fontSize = 9.sp,
                                            color = EmeraldLight
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        color = EmeraldLight.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.clickable { onOpenTransferQr() }
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(13.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Transfer QR",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldLight
                                            )
                                        }
                                    }

                                    Surface(
                                        color = EmeraldLight.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.clickable { onOpenRedeemPoints() }
                                    ) {
                                        Text(
                                            text = "Tukar >",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldLight,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = "$totalPoints RTP",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "≈ $${String.format(Locale.US, "%.2f", usdValue)} USD  |  Rp ${String.format("%,.0f", GlobalPointsManager.getIdrValue(totalPoints))}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EmeraldLight
                                    )
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("🔥 Streak: ", fontSize = 11.sp, color = Color.White)
                                        Text(
                                            text = "Hari ke-$streakDay",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldVip
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Daily Check-in Streak Section
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = GoldVip,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Login Harian Royaltree",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = if (isCheckedInToday) "Sudah Klaim ✓" else "Tersedia!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCheckedInToday) EmeraldLight else GoldVip
                            )
                        }

                        Text(
                            text = "Login 7 hari berturut-turut untuk membuka Jackpot +150 RTP di Hari ke-7!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // 7 Days Streak Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            (1..7).forEach { day ->
                                val reward = GlobalPointsManager.getLoginRewardForStreak(day)
                                val isPastOrCurrent = day <= streakDay && isCheckedInToday
                                val isCurrentTarget = day == streakDay && !isCheckedInToday

                                val bgColor by animateColorAsState(
                                    targetValue = when {
                                        isPastOrCurrent -> EmeraldLight.copy(alpha = 0.2f)
                                        isCurrentTarget -> GoldVip.copy(alpha = 0.25f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    label = "bg_color"
                                )

                                val borderColor = when {
                                    isPastOrCurrent -> EmeraldLight
                                    isCurrentTarget -> GoldVip
                                    else -> Color.Transparent
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 2.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bgColor)
                                        .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "H-$day",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (day == 7) "🎁" else "⭐",
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "+$reward",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPastOrCurrent) EmeraldLight else if (isCurrentTarget) GoldVip else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onCheckIn,
                            enabled = !isCheckedInToday,
                            colors = ButtonDefaults.buttonColors(containerColor = if (isCheckedInToday) Color.Gray else EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("claim_daily_checkin_button")
                        ) {
                            Icon(
                                imageVector = if (isCheckedInToday) Icons.Default.CheckCircle else Icons.Default.CardGiftcard,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isCheckedInToday) "Sudah Check-in Hari Ini (Streak H-$streakDay)" else "Check-in Sekarang (+${GlobalPointsManager.getLoginRewardForStreak(if (isCheckedInToday) streakDay else streakDay)} RTP)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Mission Category Filter Tabs
            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        "ALL" to "Semua (${missions.size})",
                        "LIKE_SUB" to "❤️ Like & Sub",
                        "VIEW_WATCH" to "👁️ View & Nonton",
                        "WEB" to "🌐 Melayari Web",
                        "GAME" to "🎮 Game RollerCoin",
                        "DAILY" to "⭐ Tugas Harian",
                        "MILESTONE" to "🏆 Pencapaian"
                    )
                    items(filterOptions) { (key, label) ->
                        val isSel = selectedFilter == key
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedFilter = key }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Task & Mission Cards List
            items(filteredMissions, key = { it.id }) { mission ->
                MissionItemCard(
                    mission = mission,
                    onClaim = { onClaimMission(mission.id) },
                    onAction = {
                        when (mission.category) {
                            "AD" -> onOpenWatchAd()
                            "GAME" -> onNavigateToGameRoom()
                            "LIKE", "SUBSCRIBE", "VIEW", "WATCH", "WEB" -> {
                                activeInteractiveMission = mission
                            }
                            else -> onCompleteTaskAction(mission.id)
                        }
                    }
                )
            }
        }
    }

    // Modal: Interactive Mission Simulator (Like, Sub, View, Watch, Web)
    activeInteractiveMission?.let { mission ->
        InteractiveMissionDialog(
            mission = mission,
            onDismiss = { activeInteractiveMission = null },
            onComplete = {
                onCompleteTaskAction(mission.id)
                activeInteractiveMission = null
            },
            onOpenGame = {
                activeInteractiveMission = null
                onNavigateToGameRoom()
            }
        )
    }
}

@Composable
fun MissionItemCard(
    mission: TaskMissionEntity,
    onClaim: () -> Unit,
    onAction: () -> Unit
) {
    val progressRatio = if (mission.maxProgress > 0) {
        (mission.currentProgress.toFloat() / mission.maxProgress.toFloat()).coerceIn(0f, 1f)
    } else 1f

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (mission.isClaimed) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val iconEmoji = when (mission.category) {
                        "LIKE" -> "❤️"
                        "SUBSCRIBE" -> "🔔"
                        "VIEW" -> "👁️"
                        "WATCH" -> "🎬"
                        "WEB" -> "🌐"
                        "GAME" -> "🎮"
                        "LOGIN" -> "📅"
                        "SHARE" -> "🔗"
                        "CLICKS" -> "🖱️"
                        "AD" -> "📺"
                        "CONVERSION" -> "🏆"
                        "SECURITY" -> "🛡️"
                        "WITHDRAWAL" -> "💳"
                        "REFERRAL" -> "👥"
                        "VIP" -> "👑"
                        else -> "⭐"
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (mission.type) {
                                    "DAILY_TASK" -> ElectricBlue.copy(alpha = 0.15f)
                                    else -> GoldVip.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(iconEmoji, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mission.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            mission.targetPlatform?.let { platform ->
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = platform,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = mission.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Reward Tag
                Surface(
                    color = GoldVip.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+${mission.rtpReward} RTP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldVip
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar and State
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progres: ${mission.currentProgress} / ${mission.maxProgress}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${(progressRatio * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (mission.isCompleted) EmeraldLight else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (mission.isCompleted) EmeraldLight else ElectricBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when {
                    mission.isClaimed -> {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Hadiah Diklaim ✓",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    mission.isCompleted -> {
                        Button(
                            onClick = onClaim,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("claim_reward_${mission.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Klaim +${mission.rtpReward} RTP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    else -> {
                        val (icon, label) = when (mission.category) {
                            "LIKE" -> Pair(Icons.Default.Favorite, "Sukai Konten ❤️")
                            "SUBSCRIBE" -> Pair(Icons.Default.NotificationsActive, "Langganan / Sub 🔔")
                            "VIEW" -> Pair(Icons.Default.Visibility, "Lihat Promo (10s) 👁️")
                            "WATCH" -> Pair(Icons.Default.PlayArrow, "Tonton Video (15s) 🎬")
                            "WEB" -> Pair(Icons.Default.Language, "Melayari Web (15s) 🌐")
                            "GAME" -> Pair(Icons.Default.SportsEsports, "Buka Ruang Game 🎮")
                            "AD" -> Pair(Icons.Default.PlayArrow, "Tonton Iklan (+${mission.rtpReward} RTP)")
                            else -> Pair(Icons.Default.FlashOn, "Kerjakan Tugas")
                        }

                        OutlinedButton(
                            onClick = onAction,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("action_task_${mission.id}")
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveMissionDialog(
    mission: TaskMissionEntity,
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    onOpenGame: () -> Unit
) {
    var timerCountdown by remember { mutableIntStateOf(mission.durationSeconds) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var hasLiked by remember { mutableStateOf(false) }
    var hasSubscribed by remember { mutableStateOf(false) }

    LaunchedEffect(mission.id, isTimerRunning) {
        if (mission.category in listOf("VIEW", "WATCH", "WEB") && isTimerRunning) {
            while (timerCountdown > 0) {
                delay(1000L)
                timerCountdown--
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = when (mission.category) {
                        "LIKE" -> "❤️ Misi Like Konten"
                        "SUBSCRIBE" -> "🔔 Misi Subscribe Channel"
                        "VIEW" -> "👁️ Misi Lihat Promo"
                        "WATCH" -> "🎬 Misi Tonton Video"
                        "WEB" -> "🌐 Misi Melayari Website"
                        "GAME" -> "🎮 Misi Game RollerCoin"
                        else -> "⭐ Misi Hadiah RTP"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = mission.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = mission.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                when (mission.category) {
                    "LIKE" -> {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📱 Postingan ${mission.targetPlatform ?: "Media Sosial"}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(if (hasLiked) Color(0xFFEF4444).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                        .clickable { hasLiked = !hasLiked },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = "Like",
                                        tint = if (hasLiked) Color(0xFFEF4444) else Color.Gray,
                                        modifier = Modifier.size(34.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (hasLiked) "Disukai! Ketuk Konfirmasi di bawah" else "Ketuk tombol hati untuk menyukai",
                                    fontSize = 11.sp,
                                    fontWeight = if (hasLiked) FontWeight.Bold else FontWeight.Normal,
                                    color = if (hasLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    "SUBSCRIBE" -> {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📢 Saluran Resmi ${mission.targetPlatform ?: "Platform"}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { hasSubscribed = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (hasSubscribed) EmeraldPrimary else Color(0xFFEF4444)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (hasSubscribed) Icons.Default.CheckCircle else Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (hasSubscribed) "SUBSCRIBED ✓" else "SUBSCRIBE SEKARANG",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    "VIEW", "WATCH" -> {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (mission.category == "WATCH") "🎬 Video Player Streaming" else "👁️ Promo Sponsor Display", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (timerCountdown > 0) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = GoldVip, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Sisa waktu tonton: ${timerCountdown}s", color = GoldVip, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Text("Selesai Ditonton! ✓", color = EmeraldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        LinearProgressIndicator(
                            progress = {
                                if (mission.durationSeconds > 0) {
                                    ((mission.durationSeconds - timerCountdown).toFloat() / mission.durationSeconds.toFloat()).coerceIn(0f, 1f)
                                } else 1f
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = EmeraldLight
                        )
                    }

                    "WEB" -> {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Surface(
                                    color = Color(0xFF0F172A),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = mission.actionUrl ?: "https://royaltree.id/sponsor/finance",
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            maxLines = 1
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (timerCountdown > 0) "Melayari halaman web sponsor... (${timerCountdown}s)" else "Halaman web selesai dijelajahi! ✓",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (timerCountdown > 0) GoldVip else EmeraldLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = {
                                        if (mission.durationSeconds > 0) {
                                            ((mission.durationSeconds - timerCountdown).toFloat() / mission.durationSeconds.toFloat()).coerceIn(0f, 1f)
                                        } else 1f
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = EmeraldLight
                                )
                            }
                        }
                    }

                    "GAME" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text("🎮", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Buka Ruang Game RollerCoin untuk menaruh item dan memproduksi poin pasif 24/7!", fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            when (mission.category) {
                "LIKE" -> {
                    Button(
                        onClick = onComplete,
                        enabled = hasLiked,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Konfirmasi Suka (+${mission.rtpReward} RTP)", fontWeight = FontWeight.Bold)
                    }
                }
                "SUBSCRIBE" -> {
                    Button(
                        onClick = onComplete,
                        enabled = hasSubscribed,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Verifikasi Subscribe (+${mission.rtpReward} RTP)", fontWeight = FontWeight.Bold)
                    }
                }
                "VIEW", "WATCH", "WEB" -> {
                    Button(
                        onClick = onComplete,
                        enabled = timerCountdown == 0,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(
                            text = if (timerCountdown == 0) "Klaim Selesai (+${mission.rtpReward} RTP)" else "Tunggu ${timerCountdown}s",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                "GAME" -> {
                    Button(
                        onClick = onOpenGame,
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleSecondary)
                    ) {
                        Text("Buka Ruang Game", fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    Button(onClick = onComplete, colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)) {
                        Text("Selesaikan (+${mission.rtpReward} RTP)")
                    }
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}
