package com.inkside.digital.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.GoldVip

@Composable
fun RecoveryDialog(
    missedDate: String,
    pointCost: Int,
    userPoints: Int,
    adsWatchedToday: Int,
    maxAdsPerDay: Int,
    onWatchAd: () -> Unit,
    onPayPoints: () -> Unit,
    onDismiss: () -> Unit
) {
    val canWatchAd = adsWatchedToday < maxAdsPerDay
    val canPayPoints = userPoints >= pointCost

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Pulihkan Streak", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Hari $missedDate", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Kamu bolos di hari ini. Pulihkan streak dengan salah satu cara:", fontSize = 13.sp)

                Button(
                    onClick = onWatchAd,
                    enabled = canWatchAd,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tonton Iklan", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = if (canWatchAd) "Sisa hari ini: ${maxAdsPerDay - adsWatchedToday}/${maxAdsPerDay}"
                                   else "Batas iklan hari ini tercapai",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                OutlinedButton(
                    onClick = onPayPoints,
                    enabled = canPayPoints,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        null,
                        tint = if (canPayPoints) GoldVip else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Bayar $pointCost Poin", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = if (canPayPoints) "Poin kamu: $userPoints"
                                   else "Poin tidak cukup (butuh $pointCost)",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
