package com.inkside.digital.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "loadingProgress"
    )

    val progressPercent = (animatedProgress * 100).toInt()

    // Stage text — dari itemList yang belum selesai (current stage)
    val currentStageKey = loadingState.itemList()
        .firstOrNull { !it.second }
        ?.first
        ?: "vm_stage_connect_notif"  // semua selesai

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
            // ==== Pohon ====
            RoyaltreeTree(
                level = 5,
                growthProgress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ==== Title ====
            Text(
                text = "Royaltree",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = GoldAccent,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ==== Tagline ====
            Text(
                text = LanguageManager.translate("splash_tagline", currentLanguage, "Affiliate Rewarding Platform"),
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ==== Percentage ====
            Text(
                text = "$progressPercent%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = EmeraldLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ==== Progress Bar Horizontal ====
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(6.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(EmeraldLight, GoldAccent)
                            ),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==== Stage Text ====
            Text(
                text = LanguageManager.translate(currentStageKey, currentLanguage, "..."),
                fontSize = 12.sp,
                color = TextMuted,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
