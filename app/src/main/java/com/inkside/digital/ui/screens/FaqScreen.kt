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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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

data class        HelpItem(
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
            "Dari daily check-in, mission, atau referral."
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
            val context = LocalContext.current
            val appVersion = remember {
                try {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
                } catch (e: Exception) {
                    "1.0"
                }
            }
            Text(
                "Royaltree v$appVersion",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                LanguageManager.translate("faq_last_update", currentLanguage, "Terakhir update: 15 Oktober 2026"),
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
