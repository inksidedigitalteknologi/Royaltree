package com.inkside.digital.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.data.model.UserEntity
import com.inkside.digital.ui.theme.ElectricBlue
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Surface
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager

@Composable
fun ReferralScreen(user: UserEntity?, currentLanguage: AppLanguage) {
    val context = LocalContext.current
    val referralCode = user?.referralCode ?: "RT0001"
    val referredCount = user?.referredCount ?: 0
    val affiliateBalance = user?.affiliateBalance ?: 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Column {
                Text(LanguageManager.translate("referral_title", currentLanguage, "Invite Friends"), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp))
                Text(LanguageManager.translate("referral_subtitle", currentLanguage, "Get 5% passive commission"), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElectricBlue,
                                ElectricBlue.copy(alpha = 0.85f),
                                Color(0xFF7C3AED)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            LanguageManager.translate("referral_your_code", currentLanguage, "Your Referral Code"),
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            referralCode,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Referral", referralCode))
                            Toast.makeText(context, LanguageManager.translate("referral_copied", currentLanguage, "Code copied!"), Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.ContentCopy, "Copy", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Hai! Gabung Royaltree pakai kode referral saya: " + referralCode + "\n\nDownload: https://play.google.com/store/apps/details?id=com.inkside.digital")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan via"))
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(LanguageManager.translate("referral_share", currentLanguage, "Share"), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = "Hai! Gabung Royaltree pakai kode referral saya: " + referralCode + "\n\nDownload: https://play.google.com/store/apps/details?id=com.inkside.digital"
                                val waIntent = Intent(Intent.ACTION_VIEW).apply {
                                    data = android.net.Uri.parse("https://wa.me/?text=" + java.net.URLEncoder.encode(text, "UTF-8"))
                                }
                                try { context.startActivity(waIntent) } catch (e: Exception) {
                                    Toast.makeText(context, "WhatsApp tak dipasang", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("WA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                val text = "Hai! Gabung Royaltree pakai kode referral saya: " + referralCode + "\n\nDownload: https://play.google.com/store/apps/details?id=com.inkside.digital"
                                val tgIntent = Intent(Intent.ACTION_VIEW).apply {
                                    data = android.net.Uri.parse("https://t.me/share/url?url=https://play.google.com/store/apps/details?id=com.inkside.digital&text=" + java.net.URLEncoder.encode(text, "UTF-8"))
                                }
                                try { context.startActivity(tgIntent) } catch (e: Exception) {
                                    Toast.makeText(context, "Telegram tak dipasang", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0088CC),
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Telegram", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Modifier.weight(1f), LanguageManager.translate("referral_friends", currentLanguage, "Teman"), referredCount.toString(), LanguageManager.translate("referral_invited", currentLanguage, "Diundang"))
                StatCard(Modifier.weight(1f), LanguageManager.translate("referral_commission", currentLanguage, "Komisi"), "Rp ${affiliateBalance.toInt()}", LanguageManager.translate("referral_total", currentLanguage, "Total"))
            }
        }

        item {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = GoldVip, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(LanguageManager.translate("referral_how_it_works", currentLanguage, "Cara Kerja"), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Bagikan kode referral ke teman\n2. Teman daftar pakai kode kamu\n3. Kamu dapat komisi 5%\n4. Komisi masuk otomatis ke saldo", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 20.sp)
                }
            }
        }

        item {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(LanguageManager.translate("referral_invited_friends", currentLanguage, "Teman yang Diundang"), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(
                            color = if (referredCount > 0) EmeraldLight.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$referredCount orang",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (referredCount > 0) EmeraldLight else Color.Gray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (referredCount > 0)
                            "🎉 Hebat! Anda sudah mengundang $referredCount teman. Komisi 5% akan masuk otomatis."
                        else
                            LanguageManager.translate("referral_empty_message", currentLanguage, "Belum ada teman yang diundang. Ayo bagikan kode kamu!"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun StatCard(modifier: Modifier, label1: String, value: String, label2: String) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label1, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = ElectricBlue)
            Text(label2, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
