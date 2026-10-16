package com.inkside.digital.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
        delay(2000)
        phase = 1
        delay(2000)
        phase = 2
        delay(2500)
        phase = 3
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
                (fadeIn(tween(800)) + scaleIn(
                    initialScale = 0.85f,
                    animationSpec = tween(800, easing = FastOutSlowInEasing)
                )).togetherWith(
                    fadeOut(tween(600)) + scaleOut(
                        targetScale = 1.15f,
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                )
            },
            label = "splash_phase"
        ) { currentPhase ->
            when (currentPhase) {
                0 -> LogoScene(
                    currentLanguage = currentLanguage,
                    labelKey = "splash_presented_by",
                    logoRes = R.drawable.inkside_logo,
                    brandName = "Inkside Digital",
                    tagline = "Software House · 2026"
                )
                1 -> LogoScene(
                    currentLanguage = currentLanguage,
                    labelKey = "splash_in_collaboration",
                    logoRes = R.drawable.kreatifin_logo,
                    brandName = "Kreatifin",
                    tagline = "Creative Partner"
                )
                2 -> RoyaltreeScene(currentLanguage, animatedProgress)
                3 -> LoadingScene(currentLanguage, animatedProgress, progressPercent, loadingState)
            }
        }
    }
}

@Composable
private fun LogoScene(
    currentLanguage: AppLanguage,
    labelKey: String,
    logoRes: Int,
    brandName: String,
    tagline: String
) {
    val infinite = rememberInfiniteTransition(label = "logo_glow")
    val glowScale by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    var visible by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = 1
    }

    val labelAlpha by animateFloatAsState(
        targetValue = if (visible >= 1) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "label_alpha"
    )

    val brandAlpha by animateFloatAsState(
        targetValue = if (visible >= 1) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 200, easing = FastOutSlowInEasing),
        label = "brand_alpha"
    )

    val taglineAlpha by animateFloatAsState(
        targetValue = if (visible >= 1) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "tagline_alpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
        ) {
            Text(
                text = LanguageManager.translate(labelKey, currentLanguage, "..."),
                fontSize = 13.sp,
                color = TextMuted,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(labelAlpha)
            )

            Spacer(modifier = Modifier.height(48.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.9f)
                        .scale(glowScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GoldAccent.copy(alpha = 0.25f),
                                    EmeraldLight.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = brandName,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = brandName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(brandAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tagline,
                fontSize = 13.sp,
                color = TextMuted,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }
    }
}

@Composable
private fun RoyaltreeScene(currentLanguage: AppLanguage, progress: Float) {
    val infinite = rememberInfiniteTransition(label = "tree_glow")
    val glowScale by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .scale(glowScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    EmeraldLight.copy(alpha = 0.2f),
                                    GoldAccent.copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                RoyaltreeTree(
                    level = 5,
                    growthProgress = progress,
                    modifier = Modifier.fillMaxWidth(0.85f).aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Royaltree",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                color = GoldAccent,
                letterSpacing = 5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = LanguageManager.translate("splash_tagline", currentLanguage, "Affiliate Rewarding Platform"),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingScene(
    currentLanguage: AppLanguage,
    progress: Float,
    progressPercent: Int,
    loadingState: AffiliateViewModel.LoadingState
) {
    val currentStageKey = loadingState.itemList()
        .firstOrNull { !it.second }
        ?.first
        ?: "vm_stage_connect_notif"

    val infinite = rememberInfiniteTransition(label = "loading_glow")
    val glowScale by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(0.75f).aspectRatio(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .scale(glowScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    EmeraldLight.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                RoyaltreeTree(
                    level = 5,
                    growthProgress = progress,
                    modifier = Modifier.fillMaxWidth(0.9f).aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "$progressPercent%",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = EmeraldLight,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(8.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(EmeraldLight, GoldAccent)
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = LanguageManager.translate(currentStageKey, currentLanguage, "..."),
                fontSize = 13.sp,
                color = TextMuted,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
