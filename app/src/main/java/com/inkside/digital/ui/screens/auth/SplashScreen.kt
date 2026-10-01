package com.inkside.digital.ui.screens.auth

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.components.AnimatedBackground
import com.inkside.digital.ui.components.RoyaltreeTree
import kotlinx.coroutines.delay

private val EmeraldLight = Color(0xFF34D399)
private val GoldAccent = Color(0xFFFFD700)

@Composable
fun SplashScreen() {
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val steps = 40
        for (i in 1..steps) {
            delay(50L)
            progress = i.toFloat() / steps
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "splash"
    )

    AnimatedBackground(
        accentColor = EmeraldLight,
        enableAurora = true,
        enableStars = true,
        enableGlow = true,
        enableParticles = true,
        starCount = 40,
        particleCount = 20
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            RoyaltreeTree(
                level = 5,
                growthProgress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth(0.68f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Royaltree",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = GoldAccent,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Affiliate Rewarding Platform",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(4.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(2.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(EmeraldLight, GoldAccent)
                            ),
                            shape = RoundedCornerShape(2.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${(animatedProgress * 100).toInt()}%  •  Memuatkan...",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
