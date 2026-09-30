package com.inkside.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.components.RoyaltreeTree

/**
 * TreePreviewScreen — test screen untuk lihat 5 level pohon.
 * Hanya untuk development. Boleh dihapus sebelum release.
 */
@Composable
fun TreePreviewScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF05060A))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Royaltree — Tree Preview",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "5 level pertumbuhan. Idle animation goyang lembut.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TreeCard(level = 1, label = "Sprout", modifier = Modifier.weight(1f))
                TreeCard(level = 2, label = "Sapling", modifier = Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TreeCard(level = 3, label = "Young Tree", modifier = Modifier.weight(1f))
                TreeCard(level = 4, label = "Mature", modifier = Modifier.weight(1f))
            }
            TreeCard(level = 5, label = "ROYALTREE", modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun TreeCard(
    level: Int,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
    ) {
        RoyaltreeTree(
            level = level,
            growthProgress = 1f,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                "LVL $level",
                color = Color(0xFF34D399),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============ Preview untuk Android Studio ============
@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    backgroundColor = 0xFF05060A,
    showSystemUi = true
)
@Composable
private fun TreePreviewScreenPreview() {
    TreePreviewScreen()
}
