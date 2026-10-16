package com.inkside.digital.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.R
import com.inkside.digital.localization.AppLanguage
import com.inkside.digital.localization.LanguageManager
import com.inkside.digital.ui.components.AnimatedBackground
import com.inkside.digital.ui.components.RoyaltreeTree
import com.inkside.digital.viewmodel.AffiliateViewModel
import kotlinx.coroutines.delay

private val EmeraldLight = Color(0xFF34D399)
private val GoldAccent = Color(0xFFFFD700)
private val TextMuted = Color(0xFF94A3B8)

@Composable
fun SplashSequenceScreen(
    loadingState: AffiliateViewModel.LoadingState,
    currentLanguage: AppLanguage
) {
    var phase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(1500)  // Phase 0: Inkside
        phase = 1
        delay(1500)  // Phase 1: Kreatifin
        phase = 2
        delay(2000)  // Phase 2: Royaltree
        phase = 3    // Phase 3: Loading
    }

    val animatedProgress by animateFloatAsState(
        targetValue = loadingState.progress,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
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
        AnimatedContent(
            targetState = phase,
            transitionSpec = {
                fadeIn(tween(500)) togetherWith fadeOut(tween(500))
            },
            label = "splash_phase"
        ) { currentPhase ->
            when (currentPhase) {
                0 -> LogoPhase(
                    currentLanguage = currentLanguage,
                    labelKey = "splash_presented_by",
                    logoRes = R.drawable.inkside_logo,
                    brandName = "Inkside Digital",
                    tagline = "Software House · 2026"
                )
                1 -> LogoPhase(
                    currentLanguage = currentLanguage,
                    labelKey = "splash_in_collaboration",
                    logoRes = R.drawable.kreatifin_logo,
                    brandName = "Kreatifin",
                    tagline = "Creative Partner"
                )
                2 -> RoyaltreeSplash(currentLanguage, animatedProgress)
                3 -> LoadingPhase(currentLanguage, animatedProgress, progressPercent, loadingState)
            }
        }
    }
}

@Composable
private fun LogoPhase(
    currentLanguage: AppLanguage,
    labelKey: String,
    logoRes: Int,
    brandName: String,
    tagline: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        Text(
            text = LanguageManager.translate(labelKey, currentLanguage, "..."),
            fontSize = 12.sp,
            color = TextMuted,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = brandName,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = brandName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = tagline,
            fontSize = 11.sp,
            color = TextMuted,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun RoyaltreeSplash(currentLanguage: AppLanguage, progress: Float) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        RoyaltreeTree(
            level = 5,
            growthProgress = progress,
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Royaltree",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = GoldAccent,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = LanguageManager.translate("splash_tagline", currentLanguage, "Affiliate Rewarding Platform"),
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingPhase(
    currentLanguage: AppLanguage,
    progress: Float,
    progressPercent: Int,
    loadingState: AffiliateViewModel.LoadingState
) {
    val currentStageKey = loadingState.itemList()
        .firstOrNull { !it.second }
        ?.first
        ?: "vm_stage_connect_notif"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(32.dp)
    ) {
        RoyaltreeTree(
            level = 5,
            growthProgress = progress,
            modifier = Modifier
                .fillMaxWidth(0.55f)
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "$progressPercent%",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = EmeraldLight
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Color.White.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
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

        Text(
            text = LanguageManager.translate(currentStageKey, currentLanguage, "..."),
            fontSize = 12.sp,
            color = TextMuted,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center
        )
    }
}
