package com.inkside.digital.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
            RoyaltreeTree(
                level = 5,
                growthProgress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Royaltree",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = GoldAccent,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Menyiapkan pengalaman terbaik...",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                loadingState.itemList().forEach { (label, ready) ->
                    ChecklistItem(label = label, ready = ready)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(5.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(EmeraldLight, GoldAccent)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(animatedProgress * 100).toInt()}%  •  ${loadingState.readyCount}/${loadingState.totalItems}",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ChecklistItem(
    label: String,
    ready: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (ready) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldLight,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.15f))
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (ready) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.4f),
            fontWeight = if (ready) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
