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
import androidx.compose.material.icons.filled.HelpOutline
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

data class FaqItem(val q: String, val a: String)

@Composable
fun FaqScreen(currentLanguage: AppLanguage) {
    val faqs = listOf(
        FaqItem("Bagaimana cara mendapatkan poin?", "Poin didapat dari langkah kaki, menyelesaikan misi, bermain game, dan mengundang teman."),
        FaqItem("Kapan saya bisa melakukan penarikan?", "Minimal saldo Rp 50.000 untuk e-wallet, Rp 100.000 untuk bank. Verifikasi email wajib."),
        FaqItem("Apa itu Tier VIP?", "VIP memberi komisi lebih tinggi (30% vs 12%), 0% biaya penarikan, dan prioritas pencairan."),
        FaqItem("Bagaimana cara mengundang teman?", "Buka menu 'Undang Teman', salin kode referral, dan bagikan ke teman."),
        FaqItem("Kenapa penarikan saya pending?", "Penarikan butuh verifikasi admin maksimal 1x24 jam."),
        FaqItem("Bagaimana cara mengubah data profil?", "Buka menu Profil, ubah data, lalu simpan.")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Column {
                Text(LanguageManager.translate("faq_title", currentLanguage, "Help"), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                Text("Pertanyaan umum & dukungan", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        items(faqs) { faq ->
            FaqCard(faq)
        }

        item {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Masih butuh bantuan?", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Hubungi tim support kami", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { }) { Text(LanguageManager.translate("faq_contact_support", currentLanguage, "Contact Support")) }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun FaqCard(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HelpOutline, null, tint = MaterialTheme.colorScheme.primary)
                Text(item.q, modifier = Modifier.weight(1f).padding(start = 12.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.a, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
            }
        }
    }
}
