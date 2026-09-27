package com.inkside.digital.widget

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WidgetUpdater: update data widget dari mana saja.
 * Panggil setelah user login / update profile / balance berubah.
 */
object WidgetUpdater {

    suspend fun updateWidget(
        context: Context,
        userName: String,
        balance: String,
        points: String,
        streak: String
    ) = withContext(Dispatchers.IO) {
        try {
            val glanceId = androidx.glance.appwidget.GlanceAppWidgetManager.getGlanceIds(
                RoyaltreeWidget::class.java
            ).firstOrNull() ?: return@withContext

            androidx.glance.appwidget.state.updateAppWidgetState(context, glanceId) { prefs ->
                prefs[KEY_USER_NAME] = userName
                prefs[KEY_BALANCE] = balance
                prefs[KEY_POINTS] = points
                prefs[KEY_STREAK] = streak
            }

            RoyaltreeWidget().updateAll(context)
        } catch (e: Exception) {
            android.util.Log.e("WidgetUpdater", "Gagal update widget: ${e.message}")
        }
    }

    /**
     * Update widget dari UserEntity — dipanggil setelah user sync.
     */
    suspend fun updateFromUser(
        context: Context,
        userName: String,
        balance: Double,
        points: Int,
        streak: Int
    ) {
        val balanceFormatted = "Rp ${String.format("%,.0f", balance)}"
        updateWidget(
            context = context,
            userName = userName.ifBlank { "User" },
            balance = balanceFormatted,
            points = points.toString(),
            streak = streak.toString()
        )
    }
}
