package com.inkside.digital.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * MaintenanceBanner — banner yang tampil saat backend dalam maintenance mode.
 * Full-width, warna kuning/orange, dengan icon + message.
 */
@Composable
fun MaintenanceBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF59E0B),
        shape = RoundedCornerShape(0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("\uD83D\uDD27", fontSize = 22.sp)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "MAINTENANCE MODE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    letterSpacing = 0.5.sp
                )
                Text(
                    message.ifBlank { "Sistem sedang dalam perbaikan. Mohon tunggu." },
                    fontSize = 11.sp,
                    color = Color(0xFF374151)
                )
            }
        }
    }
}
