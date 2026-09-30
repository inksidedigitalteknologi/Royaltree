package com.inkside.digital.ui.components

import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.inkside.digital.data.ads.AdMobProvider

private const val HONEYGAIN_AFFILIATE_URL = "https://join.honeygain.com/INKSI16957"

/**
 * BannerAdView: Composable untuk menampilkan Adaptive Banner AdMob.
 *
 * Kalau AdMob gagal load (contoh: AdGuard/adblocker block, no fill),
 * otomatis ganti ke fallback promo Honeygain.
 */
@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var adFailed by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val adWidthDp = maxWidth.value.toInt().coerceAtLeast(320)

        if (!adFailed) {
            AndroidView(
                factory = { ctx ->
                    AdMobProvider.createBannerView(
                        context = ctx,
                        adWidthDp = adWidthDp,
                        onFailed = { adFailed = true }
                    ).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0E1A))
                    .padding(vertical = 2.dp)
            )
        } else {
            HoneygainFallbackBanner(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(HONEYGAIN_AFFILIATE_URL))
                        context.startActivity(intent)
                    } catch (_: Exception) { }
                }
            )
        }
    }
}

@Composable
private fun HoneygainFallbackBanner(onClick: () -> Unit) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1E3A8A), Color(0xFF312E81))
                )
            )
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("\uD83D\uDCB0", fontSize = 26.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Dapat $3 Bonus \u2014 Honeygain",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Jual bandwidth tak terpakai \u2022 Cash out PayPal",
                    color = Color(0xFFB4C6FF),
                    fontSize = 9.sp
                )
            }
            Surface(
                color = Color(0xFFFFD700),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Cuba",
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}
