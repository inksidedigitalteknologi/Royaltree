package com.inkside.digital.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import androidx.compose.runtime.remember
import androidx.compose.foundation.lazy.items

// ==== DESIGN SYSTEM ====
private val SurfaceDark = Color(0xFF0F172A)
private val SurfaceDeep = Color(0xFF020617)
private val EmeraldPrimary = Color(0xFF10B981)
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldDeep = Color(0xFF064E3B)
private val GoldVip = Color(0xFFFBBF24)
private val GoldAmber = Color(0xFFF59E0B)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFCBD5E1)
private val TextMuted = Color(0xFF64748B)

data class WhitepaperSection(
    val icon: ImageVector,
    val title: String,
    val content: String,
    val accent: Color = EmeraldPrimary
)

@Composable
fun WhitepaperScreen(
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val sections = remember(currentLanguage) {
        listOf(
            WhitepaperSection(
                icon = Icons.AutoMirrored.Filled.MenuBook,
                title = LanguageManager.translate("wp_what_title", currentLanguage, "Apa itu Royaltree?"),
                content = LanguageManager.translate("wp_what_content", currentLanguage,
                    "Royaltree adalah platform mining berbasis mobile yang menggabungkan gamifikasi, " +
                    "teknologi blockchain-inspired, dan ekonomi token yang adil. Pengguna dapat menambang " +
                    "RTP (Royaltree Points) melalui miner virtual, menyelesaikan misi, dan mengikuti mini-game.")
            ),
            WhitepaperSection(
                icon = Icons.Filled.Bolt,
                title = LanguageManager.translate("wp_mining_title", currentLanguage, "Cara Production"),
                content = LanguageManager.translate("wp_mining_content", currentLanguage,
                    "1. Beli miner dari marketplace menggunakan RTP.\n" +
                    "2. Tempatkan miner di rak (slot terbatas).\n" +
                    "3. Aktifkan miner 6 jam dengan menonton iklan.\n" +
                    "4. Processor menghasilkan RTP secara pasif setiap menit.\n" +
                    "5. Block reward dibagikan setiap 30 menit (epoch).")
            ),
            WhitepaperSection(
                icon = Icons.Filled.Paid,
                title = LanguageManager.translate("wp_token_title", currentLanguage, "Token RTP"),
                content = LanguageManager.translate("wp_token_content", currentLanguage,
                    "RTP adalah token utility internal Royaltree. Digunakan untuk:\n" +
                    "• Membeli & upgrade miner\n" +
                    "• Transfer ke sesama pengguna\n" +
                    "• RTP hanya untuk ekosistem game (tidak bisa diuangkan)\n\n" +
                    "Total supply terbatas, dengan mekanisme halving setiap 1,000 block.")
            ),
            WhitepaperSection(
                icon = Icons.Filled.AccountBalanceWallet,
                title = LanguageManager.translate("wp_economy_title", currentLanguage, "Ekonomi"),
                content = LanguageManager.translate("wp_economy_content", currentLanguage,
                    "Ekonomi Royaltree dirancang berkelanjutan:\n" +
                    "• 10% rate user masuk ke Pool Fund\n" +
                    "• Pool Fund membiayai block reward\n" +
                    "• Difficulty naik seiring total production rate network\n" +
                    "• Anti-whale: diminishing return & soft cap")
            ),
            WhitepaperSection(
                icon = Icons.Filled.Map,
                title = LanguageManager.translate("wp_roadmap_title", currentLanguage, "Roadmap"),
                content = LanguageManager.translate("wp_roadmap_content", currentLanguage,
                    "Q4 2026: Rilis publik, mining tycoon\n" +
                    "Q1 2027: Mini-game arcade, leaderboard musiman\n" +
                    "Q2 2027: Secondary market, tier miner baru\n" +
                    "Q3 2027: Fitur game baru & event komunitas"),
                accent = GoldVip
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SurfaceDeep, SurfaceDark, SurfaceDeep)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ==== HEADER ====
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = LanguageManager.translate("cd_back", currentLanguage, "Back"),
                            tint = TextPrimary
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageManager.translate("wp_title", currentLanguage, "Royaltree Whitepaper"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = LanguageManager.translate("wp_subtitle", currentLanguage, "Versi 1.0 • 2026"),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            // ==== SECTIONS ====
            items(sections.size) { index ->
                val section = sections[index]
                WhitepaperCard(section)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun WhitepaperCard(section: WhitepaperSection) {
    Surface(
        color = SurfaceDark.copy(alpha = 0.7f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = section.accent.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: icon + title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(section.accent.copy(alpha = 0.15f))
                        .border(1.dp, section.accent.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        tint = section.accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = section.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = section.accent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Content
            Text(
                text = section.content,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
