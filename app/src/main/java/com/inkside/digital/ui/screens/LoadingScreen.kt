package com.inkside.digital.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.components.AnimatedBackground
import com.inkside.digital.ui.components.RoyaltreeTree
import com.inkside.digital.viewmodel.AffiliateViewModel

private val EmeraldLight = Color(0xFF34D399)
private val GoldAccent = Color(0xFFFFD700)
private val TextMuted = Color(0xFF94A3B8)

@Composable
fun LoadingScreen(
    loadingState: AffiliateViewModel.LoadingState,
    currentLanguage: AppLanguage
) {
    val animatedProgress by animateFloatAsState(
        targetValue = loadingState.progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "loadingProgress"
    )

    val progressPercent = (animatedProgress * 100).toInt()

    AnimatedBackground(
        accentColor = EmeraldLight,
        enableAurora = true,
        enableStars = true,
        enableGlow = true,
        enableParticles = true,
        starCount = 40,
        particleCount = 20,
        enableWind = true
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            // ==== Pohon + Progress Ring ====
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .aspectRatio(1f)
            ) {
                // Progress ring
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    color = EmeraldLight,
                    trackColor = Color.White.copy(alpha = 0.08f),
                    strokeWidth = 4.dp
                )

                // Pohon
                RoyaltreeTree(
                    level = 5,
                    growthProgress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==== Title ====
            Text(
                text = "Royaltree",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = GoldAccent,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ==== Tagline (i18n) ====
            Text(
                text = LanguageManager.translate("splash_tagline", currentLanguage, "Affiliate Rewarding Platform"),
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ==== Progress Percentage ====
            Text(
                text = "$progressPercent%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = EmeraldLight
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ==== Subtitle (i18n) ====
            Text(
                text = LanguageManager.translate("loading_growing_tree", currentLanguage, "Menumbuhkan pohonmu..."),
                fontSize = 11.sp,
                color = TextMuted,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ==== Stage Checklist ====
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.04f))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                loadingState.itemList().forEach { (key, isReady) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = if (isReady) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isReady) EmeraldLight else TextMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LanguageManager.translate(key, currentLanguage, key),
                            fontSize = 11.sp,
                            color = if (isReady) Color.White else TextMuted,
                            fontWeight = if (isReady) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}
