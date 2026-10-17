package com.inkside.digital.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────
// Palette mewah
// ─────────────────────────────────────────────────────────────
private val EmeraldLight = Color(0xFF34D399)
private val EmeraldDeep = Color(0xFF059669)
private val GoldAccent = Color(0xFFFFD700)
private val GoldSoft = Color(0xFFFBBF24)
private val TextMuted = Color(0xFF94A3B8)
private val BgDeep = Color(0xFF0B1220)

// Durasi per scene (ms)
private const val SCENE_INKSIDE_MS = 2500L
private const val SCENE_ROYALTREE_MS = 3000L
private const val SCENE_KREATIFIN_MS = 2500L

@Composable
fun SplashSequenceScreen(
    loadingState: AffiliateViewModel.LoadingState,
    currentLanguage: AppLanguage,
    onComplete: () -> Unit = {}
) {
    // 0 = Inkside, 1 = Royaltree, 2 = Kreatifin
    var phase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(SCENE_INKSIDE_MS)
        phase = 1
        delay(SCENE_ROYALTREE_MS)
        phase = 2
        delay(SCENE_KREATIFIN_MS)
        onComplete()
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
        particleCount = 24,
        enableWind = true
    ) {
        AnimatedContent(
            targetState = phase,
            transitionSpec = {
                (fadeIn(tween(900)) + scaleIn(
                    initialScale = 0.88f,
                    animationSpec = tween(900, easing = FastOutSlowInEasing)
                )).togetherWith(
                    fadeOut(tween(650)) + scaleOut(
                        targetScale = 1.12f,
                        animationSpec = tween(650, easing = FastOutSlowInEasing)
                    )
                )
            },
            label = "splash_phase"
        ) { currentPhase ->
            when (currentPhase) {
                0 -> InksideScene(currentLanguage)
                1 -> RoyaltreeScene(currentLanguage, animatedProgress)
                2 -> KreatifinScene(
                    currentLanguage = currentLanguage,
                    progress = animatedProgress,
                    progressPercent = progressPercent,
                    loadingState = loadingState
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SCENE 1 — Inkside Digital
// Aura pulse + logo scale-in + gold particles + text reveal
// ─────────────────────────────────────────────────────────────
@Composable
private fun InksideScene(currentLanguage: AppLanguage) {
    val infinite = rememberInfiniteTransition(label = "inkside")
    val auraScale by infinite.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )
    val auraAlpha by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    // Logo scale-in
    var revealed by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { delay(150); revealed = 1 }

    val logoScale by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0.72f,
        animationSpec = tween(1100, easing = FastOutSlowInEasing),
        label = "logo_scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )
    val brandAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 350, easing = FastOutSlowInEasing),
        label = "brand_alpha"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 550, easing = FastOutSlowInEasing),
        label = "tagline_alpha"
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
        ) {
            Text(
                text = LanguageManager.translate("splash_presented_by", currentLanguage, "Presented By"),
                fontSize = 13.sp,
                color = TextMuted,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(taglineAlpha * 0.9f)
            )
            Spacer(Modifier.height(40.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(0.82f).aspectRatio(1f)
            ) {
                // Aura ring emas
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.95f)
                        .scale(auraScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GoldAccent.copy(alpha = 0.28f * auraAlpha),
                                    EmeraldLight.copy(alpha = 0.12f * auraAlpha),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Ring tipis emas
                Canvas(modifier = Modifier.fillMaxSize(0.95f)) {
                    drawCircle(
                        color = GoldAccent.copy(alpha = 0.35f),
                        radius = size.minDimension / 2f * 0.92f,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2f)
                    )
                }
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.inkside_logo),
                    contentDescription = "Inkside Digital",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .aspectRatio(1f)
                        .scale(logoScale)
                        .alpha(logoAlpha)
                )
            }

            Spacer(Modifier.height(30.dp))
            Text(
                text = "Inkside Digital",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(brandAlpha)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Software House · 2026",
                fontSize = 13.sp,
                color = TextMuted,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SCENE 2 — Royaltree
// Pohon + radial gold burst + shimmer text + sway
// ─────────────────────────────────────────────────────────────
@Composable
private fun RoyaltreeScene(currentLanguage: AppLanguage, progress: Float) {
    val infinite = rememberInfiniteTransition(label = "royaltree")
    val glowScale by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    // Shimmer position 0..1 loop
    val shimmer by infinite.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    // Radial burst rotation
    val burstRot by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "burst_rot"
    )

    var revealed by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { delay(200); revealed = 1 }
    val treeScale by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0.7f,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "tree_scale"
    )
    val treeAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "tree_alpha"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 400, easing = FastOutSlowInEasing),
        label = "title_alpha"
    )
    val subAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 650, easing = FastOutSlowInEasing),
        label = "sub_alpha"
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(0.92f).aspectRatio(1f)
            ) {
                // Radial gold burst (rotating rays)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .scale(glowScale)
                        .alpha(treeAlpha * 0.9f)
                ) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val radius = size.minDimension / 2f
                    rotate(burstRot, pivot = Offset(cx, cy)) {
                        val rays = 18
                        for (i in 0 until rays) {
                            val angle = (2.0 * PI * i / rays).toFloat()
                            val r1 = radius * 0.55f
                            val r2 = radius * (if (i % 2 == 0) 0.98f else 0.82f)
                            drawLine(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        GoldAccent.copy(alpha = 0.0f),
                                        GoldAccent.copy(alpha = 0.28f),
                                        GoldAccent.copy(alpha = 0.0f)
                                    ),
                                    start = Offset(cx + cos(angle) * r1, cy + sin(angle) * r1),
                                    end = Offset(cx + cos(angle) * r2, cy + sin(angle) * r2)
                                ),
                                start = Offset(cx + cos(angle) * r1, cy + sin(angle) * r1),
                                end = Offset(cx + cos(angle) * r2, cy + sin(angle) * r2),
                                strokeWidth = 2.2f
                            )
                        }
                    }
                }
                // Glow belakang pohon
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .scale(glowScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    EmeraldLight.copy(alpha = 0.22f),
                                    GoldAccent.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Pohon
                RoyaltreeTree(
                    level = 5,
                    growthProgress = progress,
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .aspectRatio(1f)
                        .scale(treeScale)
                        .alpha(treeAlpha)
                )
            }

            Spacer(Modifier.height(28.dp))

            // Shimmer title
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Royaltree",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldAccent,
                    letterSpacing = 5.sp,
                    modifier = Modifier.alpha(titleAlpha)
                )
                // Shine sweep overlay
                Text(
                    text = "Royaltree",
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 5.sp,
                    modifier = Modifier
                        .alpha(titleAlpha * 0.9f)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.75f),
                                    Color.Transparent
                                ),
                                startX = shimmer * 600f - 200f,
                                endX = shimmer * 600f + 100f
                            )
                        )
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.Transparent
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = LanguageManager.translate(
                    "splash_tagline",
                    currentLanguage,
                    "Affiliate Rewarding Platform"
                ),
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.78f),
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subAlpha)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SCENE 3 — Kreatifin (progress bar digabung)
// Rotation glow + shine sweep logo + confetti + slim progress
// ─────────────────────────────────────────────────────────────
@Composable
private fun KreatifinScene(
    currentLanguage: AppLanguage,
    progress: Float,
    progressPercent: Int,
    loadingState: AffiliateViewModel.LoadingState
) {
    val currentStageKey = loadingState.itemList()
        .firstOrNull { !it.second }
        ?.first
        ?: "vm_stage_connect_notif"

    val infinite = rememberInfiniteTransition(label = "kreatifin")
    val glowScale by infinite.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    val rotGlow by infinite.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rot_glow"
    )
    val shine by infinite.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine"
    )

    var revealed by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { delay(150); revealed = 1 }
    val logoScale by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0.75f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "logo_scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(900, easing = FastOutSlowInEasing),
        label = "logo_alpha"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(1000, delayMillis = 300, easing = FastOutSlowInEasing),
        label = "title_alpha"
    )
    val barAlpha by animateFloatAsState(
        targetValue = if (revealed >= 1) 1f else 0f,
        animationSpec = tween(900, delayMillis = 500, easing = FastOutSlowInEasing),
        label = "bar_alpha"
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp)
        ) {
            Text(
                text = LanguageManager.translate("splash_in_collaboration", currentLanguage, "In Collaboration With"),
                fontSize = 12.sp,
                color = TextMuted,
                letterSpacing = 4.sp,
                modifier = Modifier.alpha(titleAlpha * 0.85f)
            )
            Spacer(Modifier.height(36.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(0.72f).aspectRatio(1f)
            ) {
                // Rotation glow ring
                Box(
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .scale(glowScale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GoldAccent.copy(alpha = 0.22f),
                                    EmeraldLight.copy(alpha = 0.10f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Rotating thin arc
                Canvas(
                    modifier = Modifier
                        .fillMaxSize(0.98f)
                        .scale(glowScale)
                ) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val r = size.minDimension / 2f * 0.92f
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                GoldAccent.copy(alpha = 0.6f),
                                EmeraldLight.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        startAngle = rotGlow * 10f,
                        sweepAngle = 300f,
                        useCenter = false,
                        topLeft = Offset(cx - r, cy - r),
                        size = androidx.compose.ui.geometry.Size(r * 2, r * 2),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.4f)
                    )
                }
                // Logo with shine sweep
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.78f)
                        .aspectRatio(1f)
                        .scale(logoScale)
                        .alpha(logoAlpha)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.kreatifin_logo),
                        contentDescription = "Kreatifin",
                        modifier = Modifier.fillMaxSize()
                    )
                    // Shine overlay
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val band = w * 0.22f
                        val cx = w * shine
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                startX = cx - band,
                                endX = cx + band
                            ),
                            topLeft = Offset(0f, 0f),
                            size = androidx.compose.ui.geometry.Size(w, h)
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Kreatifin",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(titleAlpha)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Creative Partner",
                fontSize = 12.sp,
                color = TextMuted,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(titleAlpha * 0.85f)
            )

            Spacer(Modifier.height(44.dp))

            // Slim progress bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(0.78f).alpha(barAlpha)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .height(6.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(EmeraldLight, GoldAccent)
                                ),
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "$progressPercent% · ${LanguageManager.translate(currentStageKey, currentLanguage, "...")}",
                    fontSize = 11.sp,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
