package com.inkside.digital.ui.screens

import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.data.network.model.ReferralItem
import com.inkside.digital.data.network.model.ReferralStats
import com.inkside.digital.data.network.model.ReferralTier
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.components.BannerAdView
import com.inkside.digital.ui.components.WhatsAppIcon
import com.inkside.digital.ui.components.TelegramIcon
import com.inkside.digital.ui.components.QRCodeImage
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.util.GlobalPointsManager
import com.inkside.digital.viewmodel.ReferralViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReferralScreen(
    user: UserEntity?,
    currentLanguage: AppLanguage,
    viewModel: ReferralViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showQR by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadAll() }

    val referralCode = state.stats?.referralCode ?: user?.referralCode ?: "RT0001"
    val referralLink = state.stats?.referralLink ?: "https://royaltree.app/invite/$referralCode"

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(8.dp)) }

            item {
                Column {
                    Text(
                        LanguageManager.translate("referral_title", currentLanguage, "Invite Friends"),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp)
                    )
                    Text(
                        LanguageManager.translate("referral_subtitle", currentLanguage, "Get 5% passive commission"),
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item { HeroCard(stats = state.stats, loading = state.loadingStats, currentLanguage = currentLanguage) }

            item {
                CodeCard(
                    code = referralCode,
                    link = referralLink,
                    currentLanguage = currentLanguage,
                    onShowQR = { showQR = true }
                )
            }

            item { BannerAdView() }

            item { StatsGrid(stats = state.stats, currentLanguage = currentLanguage) }

            state.stats?.tier?.let { tier ->
                item { TierBadges(current = tier.current, next = tier.next, progress = tier.progress, currentLanguage = currentLanguage) }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        LanguageManager.translate("referral_invited_friends", currentLanguage, "Teman yang Diundang"),
                        fontWeight = FontWeight.Bold, fontSize = 14.sp
                    )
                    if (state.refreshing) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    }
                }
            }

            if (state.loadingList && state.items.isEmpty()) {
                item { LoadingListPlaceholder() }
            } else if (state.items.isEmpty()) {
                item { EmptyListCard(currentLanguage = currentLanguage, onRefresh = { viewModel.refresh() }) }
            } else {
                items(state.items) { item -> ReferralListItem(item) }
                if (state.hasMore) {
                    item {
                        LaunchedEffect(Unit) { viewModel.loadMore() }
                        Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        }
                    }
                }
            }

            item { HowItWorksCard(currentLanguage = currentLanguage) }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }

    if (showQR) {
        QRDialog(code = referralCode, link = referralLink, onDismiss = { showQR = false }, currentLanguage = currentLanguage)
    }
}

// ============================================================
// HERO CARD
// ============================================================
@Composable
private fun HeroCard(stats: ReferralStats?, loading: Boolean, currentLanguage: AppLanguage) {
    val targetTotal = stats?.totalInvited ?: 0
    val targetEarned = stats?.totalEarned ?: 0.0
    val animTotal by animateFloatAsState(
        targetValue = targetTotal.toFloat(),
        animationSpec = tween(durationMillis = 800), label = "total"
    )
    val animEarned by animateFloatAsState(
        targetValue = targetEarned.toFloat(),
        animationSpec = tween(durationMillis = 1000), label = "earned"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(ElectricBlue, Color(0xFF7C3AED), Color(0xFF2563EB))))
            .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(22.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\uD83C\uDFC6", fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    LanguageManager.translate("referral_your_stats", currentLanguage, "Statistik Referral"),
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${animTotal.toInt()}", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Text(
                        LanguageManager.translate("referral_friends", currentLanguage, "Teman Diundang"),
                        fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        GlobalPointsManager.formatBalanceIdr(animEarned.toDouble(), currentLanguage.code),
                        fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700)
                    )
                    Text(
                        LanguageManager.translate("referral_total_earned", currentLanguage, "Total Komisi"),
                        fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
            if (loading) {
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                    color = Color.White, trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }
    }
}

// ============================================================
// CODE CARD (copy, share, WA, TG, QR)
// ============================================================
@Composable
private fun CodeCard(
    code: String,
    link: String,
    currentLanguage: AppLanguage,
    onShowQR: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()

    fun shareMessage(): String =
        "Hai! Gabung Royaltree pakai kode referral saya: $code\n\nDownload: $link"

    fun copyToClipboard(text: String) {
        scope.launch {
            clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("Referral", text)))
        }
        Toast.makeText(context, "Tersalin!", Toast.LENGTH_SHORT).show()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\uD83C\uDF81", fontSize = 18.sp)
                Spacer(Modifier.width(6.dp))
                Text(
                    LanguageManager.translate("referral_your_code", currentLanguage, "Kode Referral Kamu"),
                    fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    code, fontSize = 28.sp, fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace, color = Color.White, letterSpacing = 2.sp
                )
                Row {
                    IconButton(onClick = { onShowQR() }) {
                        Icon(Icons.Default.QrCode, "QR", tint = Color.White)
                    }
                    IconButton(onClick = { copyToClipboard(code) }) {
                        Icon(Icons.Default.ContentCopy, "Copy", tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                link, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f),
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(14.dp))

            Button(
                onClick = {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareMessage())
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, "Bagikan via"))
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.15f), contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(LanguageManager.translate("referral_share", currentLanguage, "Bagikan"), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val text = shareMessage()
                        val waIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/?text=" + java.net.URLEncoder.encode(text, "UTF-8"))
                        }
                        try { context.startActivity(waIntent) }
                        catch (e: Exception) { Toast.makeText(context, "WhatsApp tak dipasang", Toast.LENGTH_SHORT).show() }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366), contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    WhatsAppIcon(size = 16)
                    Spacer(Modifier.width(6.dp))
                    Text("WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        val text = shareMessage()
                        val tgIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://t.me/share/url?url=" + java.net.URLEncoder.encode(link, "UTF-8") + "&text=" + java.net.URLEncoder.encode(text, "UTF-8"))
                        }
                        try { context.startActivity(tgIntent) }
                        catch (e: Exception) { Toast.makeText(context, "Telegram tak dipasang", Toast.LENGTH_SHORT).show() }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088CC), contentColor = Color.White),
                    modifier = Modifier.weight(1f)
                ) {
                    TelegramIcon(size = 16)
                    Spacer(Modifier.width(6.dp))
                    Text("Telegram", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// ============================================================
// STATS GRID (Total, Aktif, Pending, Earned)
// ============================================================
@Composable
private fun StatsGrid(stats: ReferralStats?, currentLanguage: AppLanguage) {
    val total = stats?.totalInvited ?: 0
    val active = stats?.activeCount ?: 0
    val pending = stats?.pendingCount ?: 0
    val earned = stats?.totalEarned ?: 0.0

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox(Modifier.weight(1f), "Total", "$total", EmeraldLight, "\uD83D\uDC65")
            StatBox(Modifier.weight(1f), "Aktif", "$active", Color(0xFF34D399), "\u2705")
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox(Modifier.weight(1f), "Pending", "$pending", Color(0xFFFBBF24), "\u23F3")
            StatBox(
                Modifier.weight(1f),
                LanguageManager.translate("referral_total_earned", currentLanguage, "Total Komisi"),
                GlobalPointsManager.formatBalanceIdr(earned, currentLanguage.code),
                GoldVip, "\uD83D\uDCB0"
            )
        }
    }
}

@Composable
private fun StatBox(modifier: Modifier, label: String, value: String, accent: Color, emoji: String) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(emoji, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = accent, maxLines = 1)
        }
    }
}

// ============================================================
// TIER BADGES (horizontal scroll + progress)
// ============================================================
@Composable
private fun TierBadges(
    current: ReferralTier,
    next: ReferralTier?,
    progress: Int,
    currentLanguage: AppLanguage
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val allTiers = listOf("starter", "bronze", "silver", "gold", "platinum")
            val labels = listOf("\uD83C\uDF31 Starter", "\uD83E\uDD49 Bronze", "\uD83E\uDD48 Silver", "\uD83E\uDD47 Gold", "\uD83D\uDC8E Platinum")
            allTiers.forEachIndexed { idx, id ->
                val isActive = current.id == id
                val isUnlocked = allTiers.indexOf(current.id) >= idx
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when {
                        isActive -> GoldVip.copy(alpha = 0.2f)
                        isUnlocked -> EmeraldLight.copy(alpha = 0.12f)
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, GoldVip) else null
                ) {
                    Text(
                        labels[idx],
                        fontSize = 11.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        if (next != null) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(String.format(LanguageManager.translate("referral_toward_tier", currentLanguage, "Menuju %s"), "${next.icon} ${next.name}"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("${current.max + 1}/${next.min} \u2022 ${next.min - (current.max + 1) + 1} lagi", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = GoldVip,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        } else {
            Text("\uD83C\uDF89 " + LanguageManager.translate("referral_tier_max", currentLanguage, "Kamu di tier tertinggi!"), fontSize = 11.sp, color = GoldVip, fontWeight = FontWeight.Bold)
        }
    }
}

// ============================================================
// REFERRAL LIST ITEM
// ============================================================
@Composable
private fun ReferralListItem(item: ReferralItem) {
    val dateStr = item.joinedAt?.let {
        SimpleDateFormat("dd MMM yyyy", Locale("id")).format(Date(it))
    } ?: "-"

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(42.dp).clip(CircleShape).background(ElectricBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.username, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(dateStr, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (item.city != null) {
                        Text(" \u2022 ${item.city}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (item.status == "active") Color(0xFF34D399).copy(alpha = 0.15f) else Color(0xFFFBBF24).copy(alpha = 0.15f)
                ) {
                    Text(
                        if (item.status == "active") "Aktif" else "Pending",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.status == "active") Color(0xFF34D399) else Color(0xFFFBBF24),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                if (item.earnedFromThem > 0) {
                    Spacer(Modifier.height(2.dp))
                    Text("+${item.earnedFromThem.toInt()} RTP", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldVip)
                }
            }
        }
    }
}

// ============================================================
// EMPTY / LOADING
// ============================================================
@Composable
private fun EmptyListCard(currentLanguage: AppLanguage, onRefresh: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("\uD83D\uDCEC", fontSize = 40.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                LanguageManager.translate("referral_empty_message", currentLanguage, "Belum ada teman yang diundang."),
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(LanguageManager.translate("referral_share_prompt", currentLanguage, "Ayo bagikan kode kamu!"), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(LanguageManager.translate("common_refresh", currentLanguage, "Refresh"), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun LoadingListPlaceholder() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant))
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Box(modifier = Modifier.fillMaxWidth(0.5f).height(12.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
                        Spacer(Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth(0.3f).height(10.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant))
                    }
                }
            }
        }
    }
}

// ============================================================
// HOW IT WORKS
// ============================================================
@Composable
private fun HowItWorksCard(currentLanguage: AppLanguage) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, null, tint = GoldVip, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    LanguageManager.translate("referral_how_it_works", currentLanguage, "Cara Kerja"),
                    fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                LanguageManager.translate(
                    "referral_steps", currentLanguage,
                    "1. Bagikan kode referral ke teman\n2. Teman daftar pakai kode kamu\n3. Kamu dapat komisi 5%\n4. Komisi masuk otomatis ke saldo"
                ),
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp
            )
        }
    }
}

// ============================================================
// QR DIALOG
// ============================================================
@Composable
private fun QRDialog(
    code: String,
    link: String,
    onDismiss: () -> Unit,
    currentLanguage: AppLanguage
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(LanguageManager.translate("referral_qr_title", currentLanguage, "Scan QR Code"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(String.format(LanguageManager.translate("referral_qr_subtitle", currentLanguage, "Bagikan kode %s"), code), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(16.dp))
                QRCodeImage(content = link, size = 220.dp)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text(LanguageManager.translate("common_close", currentLanguage, "Tutup"))
                }
            }
        }
    }
}
