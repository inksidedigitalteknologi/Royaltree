package com.inkside.digital.data.ads

import android.content.Context
import android.content.SharedPreferences

/**
 * AdStrategy: atur strategi & frekuensi iklan.
 * 
 * Tujuan:
 * - Maximize revenue TANPA ganggu user experience
 * - Cooldown per format biar tidak agresif
 * - Track impressions untuk monitoring
 */
object AdStrategy {

    private const val PREFS_NAME = "ad_strategy"
    private const val KEY_LAST_INTERSTITIAL = "last_interstitial_ms"
    private const val KEY_LAST_APP_OPEN = "last_app_open_ms"
    private const val KEY_INTERSTITIAL_TODAY = "interstitial_today"
    private const val KEY_INTERSTITIAL_DATE = "interstitial_date"
    private const val KEY_ACTION_COUNT = "action_count"

    // ═══════════════════════════════════════════════════════════
    // COOLDOWN (ms)
    // ═══════════════════════════════════════════════════════════
    private const val INTERSTITIAL_COOLDOWN = 5 * 60 * 1000L       // 5 menit
    private const val APP_OPEN_COOLDOWN = 2 * 60 * 60 * 1000L       // 2 jam

    // ═══════════════════════════════════════════════════════════
    // DAILY LIMIT
    // ═══════════════════════════════════════════════════════════
    private const val MAX_INTERSTITIAL_PER_DAY = 3
    private const val MAX_APP_OPEN_PER_DAY = 4
    private const val MIN_ACTIONS_BEFORE_INTERSTITIAL = 3

    // ═══════════════════════════════════════════════════════════
    // CHECK: Boleh tampil Interstitial?
    // ═══════════════════════════════════════════════════════════
    fun canShowInterstitial(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()

        // 1. Cek cooldown
        val lastShown = prefs.getLong(KEY_LAST_INTERSTITIAL, 0L)
        if (now - lastShown < INTERSTITIAL_COOLDOWN) {
            return false
        }

        // 2. Cek daily limit
        val today = getToday()
        val lastDate = prefs.getString(KEY_INTERSTITIAL_DATE, "") ?: ""
        val countToday = if (lastDate == today) {
            prefs.getInt(KEY_INTERSTITIAL_TODAY, 0)
        } else {
            0
        }
        if (countToday >= MAX_INTERSTITIAL_PER_DAY) {
            return false
        }

        // 3. Cek action count (min 3 action)
        val actionCount = prefs.getInt(KEY_ACTION_COUNT, 0)
        if (actionCount < MIN_ACTIONS_BEFORE_INTERSTITIAL) {
            return false
        }

        return true
    }

    // ═══════════════════════════════════════════════════════════
    // CHECK: Boleh tampil App Open?
    // ═══════════════════════════════════════════════════════════
    fun canShowAppOpen(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()

        val lastShown = prefs.getLong(KEY_LAST_APP_OPEN, 0L)
        return (now - lastShown) >= APP_OPEN_COOLDOWN
    }

    // ═══════════════════════════════════════════════════════════
    // RECORD: Interstitial shown
    // ═══════════════════════════════════════════════════════════
    fun recordInterstitial(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val now = System.currentTimeMillis()
        val today = getToday()

        editor.putLong(KEY_LAST_INTERSTITIAL, now)

        val lastDate = prefs.getString(KEY_INTERSTITIAL_DATE, "") ?: ""
        val countToday = if (lastDate == today) {
            prefs.getInt(KEY_INTERSTITIAL_TODAY, 0)
        } else {
            0
        }

        editor.putInt(KEY_INTERSTITIAL_TODAY, countToday + 1)
        editor.putString(KEY_INTERSTITIAL_DATE, today)
        editor.putInt(KEY_ACTION_COUNT, 0)  // Reset action count
        editor.apply()
    }

    // ═══════════════════════════════════════════════════════════
    // RECORD: App Open shown
    // ═══════════════════════════════════════════════════════════
    fun recordAppOpen(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putLong(KEY_LAST_APP_OPEN, System.currentTimeMillis()).apply()
    }

    // ═══════════════════════════════════════════════════════════
    // RECORD: User action (untuk hitung kapan boleh interstitial)
    // ═══════════════════════════════════════════════════════════
    fun recordAction(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_ACTION_COUNT, 0)
        prefs.edit().putInt(KEY_ACTION_COUNT, count + 1).apply()
    }

    // ═══════════════════════════════════════════════════════════
    // STATS (untuk admin/debug)
    // ═══════════════════════════════════════════════════════════
    fun getStats(context: Context): Map<String, Any> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = getToday()
        val lastDate = prefs.getString(KEY_INTERSTITIAL_DATE, "") ?: ""
        val countToday = if (lastDate == today) prefs.getInt(KEY_INTERSTITIAL_TODAY, 0) else 0

        return mapOf(
            "interstitial_today" to countToday,
            "max_interstitial" to MAX_INTERSTITIAL_PER_DAY,
            "action_count" to prefs.getInt(KEY_ACTION_COUNT, 0),
            "min_actions" to MIN_ACTIONS_BEFORE_INTERSTITIAL
        )
    }

    // ═══════════════════════════════════════════════════════════
    // HELPER
    // ═══════════════════════════════════════════════════════════
    private fun getToday(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }

    // Reset (untuk testing)
    fun reset(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
