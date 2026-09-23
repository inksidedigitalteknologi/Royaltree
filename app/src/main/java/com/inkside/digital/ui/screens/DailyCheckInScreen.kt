package com.inkside.digital.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.inkside.digital.ui.components.CalendarDayItem
import com.inkside.digital.ui.components.DayStatus
import com.inkside.digital.ui.components.RecoveryDialog
import com.inkside.digital.ui.theme.ElectricBlue
import com.inkside.digital.ui.theme.EmeraldLight
import com.inkside.digital.ui.theme.GoldVip
import java.util.Calendar

@Composable
fun DailyCheckInScreen(
    onBack: () -> Unit,
    onClaimCheckIn: () -> Unit,
    onWatchAdForRecovery: (date: String) -> Unit,
    onPayPointsForRecovery: (date: String) -> Unit,
    streak: Int,
    checkedInToday: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?
) {
    val context = LocalContext.current
    var showRecoveryDialog by remember { mutableStateOf(false) }
    var selectedMissedDate by remember { mutableStateOf("") }

    // Info kalender
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH) + 1
    val today = calendar.get(Calendar.DAY_OF_MONTH)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = getFirstDayOfWeek(currentYear, currentMonth)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Column {
                    Text(
                        text = "Login Harian",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    )
                    Text(
                        text = "Klaim hadiah setiap hari",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Streak Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GoldVip.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = GoldVip,
                            modifier = Modifier.height(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Streak Kamu", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                "$streak hari",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldVip
                            )
                        }
                    }
                    Text(
                        text = "$currentMonth/$currentYear",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Info Weekend
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ElectricBlue.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "💡 Weekend (Sabtu/Minggu) = hadiah lebih besar!",
                    fontSize = 12.sp,
                    color = ElectricBlue,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Kalender
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header hari
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { dayName ->
                            Box(
                                modifier = Modifier.weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Grid kalender
                    var dayCounter = 1
                    for (week in 0 until 6) {
                        if (dayCounter > daysInMonth) break
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (dow in 0 until 7) {
                                if ((week == 0 && dow < firstDayOfWeek) || dayCounter > daysInMonth) {
                                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                                } else {
                                    val dayNum = dayCounter
                                    val isWeekend = (dow == 0 || dow == 6)
                                    val isToday = dayNum == today
                                    val isPast = dayNum < today
                                    val isFuture = dayNum > today

                                    val status = when {
                                        isToday && checkedInToday -> DayStatus.CHECKED_IN
                                        isToday -> DayStatus.TODAY
                                        isPast -> DayStatus.MISSED  // bisa dioverride nanti
                                        isFuture -> DayStatus.FUTURE
                                        else -> DayStatus.FUTURE
                                    }

                                    val points = if (isWeekend) 100 else 50

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                    ) {
                                        CalendarDayItem(
                                            day = dayNum,
                                            points = points,
                                            status = status,
                                            isWeekend = isWeekend,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                    dayCounter++
                                }
                            }
                        }
                    }
                }
            }
        }

        // Status
        item {
            if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFFEF4444).copy(alpha = 0.1f), RoundedCornerShape(10.dp)).padding(12.dp)
                ) {
                    Text("❌ $errorMessage", color = Color(0xFFEF4444), fontSize = 12.sp)
                }
            }
            if (successMessage != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(EmeraldLight.copy(alpha = 0.1f), RoundedCornerShape(10.dp)).padding(12.dp)
                ) {
                    Text("✅ $successMessage", color = EmeraldLight, fontSize = 12.sp)
                }
            }
        }

        // Tombol Klaim
        item {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (!checkedInToday) {
                Button(
                    onClick = onClaimCheckIn,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldLight),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.CardGiftcard, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("KLAIM HADIAH HARI INI", fontWeight = FontWeight.Bold)
                }
            } else {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldLight.copy(alpha = 0.15f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Kamu sudah check-in hari ini. Kembali besok!",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldLight
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Recovery Dialog
    if (showRecoveryDialog) {
        RecoveryDialog(
            missedDate = selectedMissedDate,
            pointCost = 500,
            userPoints = 0,
            adsWatchedToday = 0,
            maxAdsPerDay = 1,
            onWatchAd = {
                showRecoveryDialog = false
                onWatchAdForRecovery(selectedMissedDate)
            },
            onPayPoints = {
                showRecoveryDialog = false
                onPayPointsForRecovery(selectedMissedDate)
            },
            onDismiss = { showRecoveryDialog = false }
        )
    }
}

/**
 * Hitung hari pertama minggu (0 = Minggu, 1 = Senin, ...)
 */
private fun getFirstDayOfWeek(year: Int, month: Int): Int {
    val cal = Calendar.getInstance()
    cal.set(year, month - 1, 1)
    return cal.get(Calendar.DAY_OF_WEEK) - 1
}
