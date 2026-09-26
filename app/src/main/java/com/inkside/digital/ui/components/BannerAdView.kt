package com.inkside.digital.ui.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.inkside.digital.data.ads.AdMobProvider

/**
 * BannerAdView: Composable untuk menampilkan Adaptive Banner AdMob.
 *
 * Cara pakai:
 *   BannerAdView()
 *
 * Otomatis menyesuaikan lebar dengan layar (adaptive).
 */
@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val adWidthDp = maxWidth.value.toInt().coerceAtLeast(320)

        AndroidView(
            factory = { ctx ->
                AdMobProvider.createBannerView(ctx, adWidthDp).apply {
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
    }
}
