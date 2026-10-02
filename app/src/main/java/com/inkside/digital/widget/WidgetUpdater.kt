package com.inkside.digital.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import com.inkside.digital.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * WidgetUpdater: baca data user dari Room, lalu update widget.
 *
 * Panggil fungsi ini:
 * - Setelah user login
 * - Setelah nonton iklan (reward)
 * - Setelah check-in harian
 * - Setelah profile update
 */
object WidgetUpdater {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Update widget berdasarkan userId — baca dari Room.
     */
    fun refreshFromRoom(context: Context, userId: String) {
        scope.launch {
            try {
                val db = AppDatabase.getDatabase(context, scope)
                val user = db.appDao().getUserSync(userId) ?: return@launch

                val glanceManager = GlanceAppWidgetManager(context)
                val glanceIds = glanceManager.getGlanceIds(RoyaltreeWidget::class.java)
                if (glanceIds.isEmpty()) return@launch

                val balanceFormatted = "Rp ${formatNumber(user.balance)}"
                val pointsFormatted = formatCompact(user.points)
                val stepsFormatted = formatCompact(user.todaySteps)

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[KEY_USER_NAME] = user.name.ifBlank { "User" }
                        prefs[KEY_BALANCE] = balanceFormatted
                        prefs[KEY_POINTS] = pointsFormatted
                        prefs[KEY_STREAK] = user.checkInStreak.toString()
                        prefs[KEY_STEPS] = stepsFormatted
                        prefs[KEY_TIER] = user.tier
                        prefs[KEY_REFERRAL] = user.referredCount.toString()
                        // Token dari gameRoomState
                        try {
                            val room = db.appDao().getGameRoomStateSync()
                            prefs[KEY_TOKEN] = "0"  // Room takde field token, guna 0
                        } catch (e: Exception) { prefs[KEY_TOKEN] = "0" }
                    }
                }

                RoyaltreeWidget().updateAll(context)
                android.util.Log.d("WidgetUpdater", "✅ Widget updated for user: ${user.name}")
            } catch (e: Exception) {
                android.util.Log.e("WidgetUpdater", "Gagal update widget: ${e.message}")
            }
        }
    }

    /**
     * Update langsung dengan data (tanpa baca Room).
     * Berguna kalau data sudah ada di memory.
     */
    suspend fun updateDirect(
        context: Context,
        userName: String,
        balance: Double,
        points: Int,
        streak: Int,
        steps: Int,
        tier: String
    ) = withContext(Dispatchers.IO) {
        try {
            val glanceManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceManager.getGlanceIds(RoyaltreeWidget::class.java)
            if (glanceIds.isEmpty()) return@withContext

            val balanceFormatted = "Rp ${formatNumber(balance)}"
            val pointsFormatted = formatCompact(points)
            val stepsFormatted = formatCompact(steps)

            glanceIds.forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[KEY_USER_NAME] = userName.ifBlank { "User" }
                    prefs[KEY_BALANCE] = balanceFormatted
                    prefs[KEY_POINTS] = pointsFormatted
                    prefs[KEY_STREAK] = streak.toString()
                    prefs[KEY_STEPS] = stepsFormatted
                    prefs[KEY_TIER] = tier
                    prefs[KEY_TOKEN] = "0"
                    prefs[KEY_REFERRAL] = "0"
                }
            }

            RoyaltreeWidget().updateAll(context)
        } catch (e: Exception) {
            android.util.Log.e("WidgetUpdater", "Gagal update widget: ${e.message}")
        }
    }

    // ==== Helper: format angka ====
    private fun formatNumber(value: Double): String {
        return String.format("%,.0f", value).replace(',', '.')
    }

    private fun formatCompact(value: Int): String {
        return when {
            value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000.0)
            value >= 1_000 -> String.format("%.1fK", value / 1_000.0)
            else -> value.toString()
        }
    }
}
