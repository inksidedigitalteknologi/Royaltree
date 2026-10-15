package com.inkside.digital.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager

// ============================================================
// DATA MODELS
// ============================================================

data class HelpItem(
    val emoji: String,
    val title: String,
    val content: String
)

data class FaqItem(val q: String, val a: String)

// ============================================================
// MAIN SCREEN
// ============================================================

@Composable
fun FaqScreen(currentLanguage: AppLanguage) {
    val panduanItems = listOf(
        HelpItem(
            emoji = "⛏️",
            title = "Cara Mining (Mining Tycoon)",
            content = """
1. Beli Miner
   • Buka Game Room → tab "Toko Item"
   • Pilih miner (Common, Rare, Epic, Legendary, Mythic)
   • Beli pakai Miner Token

2. Tempatkan Miner di Rak
   • Tab "Ruang Rak Mining"
   • Tap slot kosong → pilih miner → Pasang
   • Miner otomatis hasilkan RTP (Royaltree Points)

3. Aktifkan Miner
   • Tap "Aktifkan Miner (Tonton Iklan)"
   • Tonton iklan rewarded
   • Miner aktif 6 jam
   • Setelah aktif, RTP bertambah otomatis

4. Klaim RTP
   • RTP terkumpul di "Unclaimed"
   • Tap "Klaim" untuk masuk saldo

5. Buka Slot Rak
   • Awalnya 2 slot terbuka
   • Beli slot tambahan pakai RTP
            """.trimIndent()
        ),
        HelpItem(
            emoji = "🌡️",
            title = "Thermal Monitoring",
            content = """
Aplikasi memantau suhu baterai HP real-time:

🟢 < 35°C   → Normal, Hashrate 100%
🟡 35-45°C  → Warm, Hashrate 90%
🟠 45-55°C  → Hot, Hashrate 70%
🔴 55-60°C  → Very Hot, Hashrate 50%
⛔ ≥ 60°C   → PAUSE (mining berhenti)
✅ < 55°C   → RESUME (mining lanjut)

Baterai kritis (< 5%) → mining otomatis pause.
Charge HP untuk lanjut mining.
            """.trimIndent()
        ),
        HelpItem(
            emoji = "🎮",
            title = "Mini-Game Arcade",
            content = """
• Buka tab "Mini-Game Arcade" di Game Room
• Main Coin Rush — tap coin yang muncul
• Dapat bonus points + power boost untuk mining
• Skor tinggi → hadiah lebih besar
            """.trimIndent()
        ),
        HelpItem(
            emoji = "💰",
            title = "Withdrawal (Tarik RTP)",
            content = """
Metode:
1. E-Wallet — Dana, OVO, GoPay, ShopeePay
2. Bank — BCA, BNI, BRI, Mandiri
3. Crypto — USDT, BTC, ETH

Cara:
1. Buka Profil → Withdrawal
2. Pilih metode
3. Isi jumlah + data akun
4. Submit → tunggu approval admin (1-3 hari kerja)
            """.trimIndent()
        ),
        HelpItem(
            emoji = "📢",
            title = "Affiliate & Referral",
            content = """
Dapat Kode Referral:
• Buka Profil → lihat "Referral Code"

Ajak Teman:
1. Share kode referral ke teman
2. Teman daftar pakai kode kamu
3. Kamu dapat komisi dari aktivitas mereka

Cek Statistik:
• Buka Affiliate → lihat:
  - Jumlah referral
  - Total komisi
  - Link referral
            """.trimIndent()
        ),
        HelpItem(
            emoji = "🎁",
            title = "Daily Check-in",
            content = """
• Buka Home → tap "Check-in"
• Dapat reward RTP setiap hari
• Streak bertambah → bonus makin besar
• Bolos 1 hari → streak reset
• Pulihkan streak pakai "Streak Recovery"
            """.trimIndent()
        ),
        HelpItem(
            emoji = "🎯",
            title = "Missions & Campaigns",
            content = """
• Buka Misi di bottom nav
• Selesaikan misi (contoh: tonton 5 iklan, klaim 10x mining)
• Dapat reward RTP atau Miner Token
            """.trimIndent()
        ),
        HelpItem(
            emoji = "💬",
            title = "Global Chat",
            content = """
• Buka Chat dari Home
• Kirim pesan ke semua user
• Ikuti aturan — spam/abuse bisa di-ban
            """.trimIndent()
        ),
        HelpItem(
            emoji = "🎡",
            title = "Sweepstakes",
            content = """
• Beli tiket pakai RTP
• Tiket masuk ke undian
• Pemenang dipilih acak setiap periode
• Hadiah: RTP besar / Miner Token
            """.trimIndent()
        ),
        HelpItem(
            emoji = "⚙️",
            title = "Settings",
            content = """
• Bahasa: Indonesia / English
• Notifikasi: On/Off per kategori
• Thermal Alert: On/Off peringatan suhu
• Dark Mode: On/Off
            """.trimIndent()
        ),
        HelpItem(
            emoji = "🔒",
            title = "Keamanan",
            content = """
• Jangan bagikan password ke siapapun
• Jangan share akun ke orang lain
• Verifikasi email untuk keamanan tambahan
• 2FA tersedia (opsional)
• Admin tidak pernah minta password
            """.trimIndent()
        ),
        HelpItem(
            emoji = "🎯",
            title = "Tips Sukses",
            content = """
1. Check-in setiap hari — streak bonus besar
2. Klaim mining rutin — jangan biarkan menumpuk
3. Aktifkan miner — tonton iklan 6 jam sekali
4. Main mini-game — bonus power untuk mining
5. Ajak teman — komisi referral pasif
6. Jaga suhu HP — mining optimal di suhu normal
7. Selesaikan misi — reward RTP besar
            """.trimIndent()
        ),
        HelpItem(
            emoji = "📜",
            title = "Aturan Penggunaan",
            content = """
1. 1 akun per orang — multi-akun dilarang
2. No bot / auto-clicker — bisa di-ban
3. No spam di chat
4. No abuse fitur (fake step, fake ads)
5. Pelanggaran → warning → ban
            """.trimIndent()
        )
    )

    val faqs = listOf(
        FaqItem(
            "Bagaimana cara mendapatkan poin?",
            "Poin didapat dari langkah kaki, menyelesaikan misi, bermain game, dan mengundang teman."
        ),
        FaqItem(
            "Kapan saya bisa melakukan penarikan?",
            "Minimal saldo Rp 50.000 untuk e-wallet, Rp 100.000 untuk bank. Verifikasi email wajib."
        ),
        FaqItem(
            "Apa itu Tier VIP?",
            "VIP memberi komisi lebih tinggi (30% vs 12%), 0% biaya penarikan, dan prioritas pencairan."
        ),
        FaqItem(
            "Bagaimana cara mengundang teman?",
            "Buka menu 'Undang Teman', salin kode referral, dan bagikan ke teman."
        ),
        FaqItem(
            "Kenapa penarikan saya pending?",
            "Penarikan butuh verifikasi admin maksimal 1x24 jam."
        ),
        FaqItem(
            "Bagaimana cara mengubah data profil?",
            "Buka menu Profil, ubah data, lalu simpan."
        ),
        FaqItem(
            "Kenapa mining saya berhenti?",
            "Cek suhu HP (≥ 60°C) atau baterai (< 5%). Dinginkan HP / charge."
        ),
        FaqItem(
            "Berapa lama miner aktif?",
            "6 jam per aktivasi (tonton iklan)."
        ),
        FaqItem(
            "Bagaimana cara dapat Miner Token?",
            "Dari daily check-in, mission, sweepstakes, atau referral."
        ),
        FaqItem(
            "Apakah data saya aman?",
            "Ya — pakai Firebase Auth + encryption. Data thermal hanya untuk optimasi mining."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // ============ HEADER ============
        item {
            Column {
                Text(
                    LanguageManager.translate("faq_title", currentLanguage, "Bantuan & FAQ"),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    LanguageManager.translate("faq_subtitle", currentLanguage, "Panduan lengkap penggunaan Royaltree"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ============ SECTION: PANDUAN ============
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "📖 PANDUAN",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(panduanItems) { item ->
            HelpAccordionCard(item)
        }

        // ============ SECTION: FAQ ============
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "❓ FAQ",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(faqs) { faq ->
            FaqCard(faq)
        }

        // ============ SECTION: SUPPORT ============
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        LanguageManager.translate("faq_need_help", currentLanguage, "Masih butuh bantuan?"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Email: support@royaltree.id",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Atau chat via Global Chat",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { }) {
                        Text(
                            LanguageManager.translate("faq_contact_support", currentLanguage, "Hubungi Support")
                        )
                    }
                }
            }
        }

        // ============ FOOTER ============
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Royaltree v1.11.0",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                "Terakhir update: 15 Oktober 2026",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ============================================================
// COMPONENTS
// ============================================================

@Composable
private fun HelpAccordionCard(item: HelpItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.emoji, fontSize = 20.sp)
                Text(
                    item.title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    item.content,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun FaqCard(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    item.q,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.a,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
