package com.inkside.digital.ui.screens

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.components.AnimatedBackground
import com.inkside.digital.ui.components.RoyaltreeTree
import com.inkside.digital.viewmodel.AffiliateViewModel

private val EmeraldLight = Color(0xFF34D399)
private val GoldAccent = Color(0xFFFFD700)

@Composable
fun LoadingScreen(
    loadingState: AffiliateViewModel.LoadingState
) {
    val animatedProgress by animateFloatAsState(
        targetValue = loadingState.progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "loadingProgress"
    )

    AnimatedBackground(
        accentColor = EmeraldLight,
        enableAurora = true,
        enableStars = true,
        enableGlow = true,
        enableParticles = true
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            // Pohon besar di tengah — animate grow
            RoyaltreeTree(
                level = 5,
                growthProgress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Royaltree",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = GoldAccent,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle subtle — natural feel
            Text(
                text = "Menumbuhkan pohonmu...",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Progress bar — tanpa text
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.08f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(4.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(EmeraldLight, GoldAccent)
                            )
                        )
                )
            }
        }
    }
}


