package com.inkside.digital.util

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.FirebaseApp
import com.inkside.digital.util.AppLogger

/**
 * AnalyticsHelper — central helper untuk Firebase Analytics events.
 *
 * Fix: pakai FirebaseAnalytics.getInstance() langsung (no KTX).
 */
object AnalyticsHelper {

    private const val TAG = "Analytics"

    @Volatile
    private var analytics: FirebaseAnalytics? = null

    /**
     * Init — panggil dari Application atau MainActivity.
     */
    fun init(context: Context) {
        try {
            if (analytics == null) {
                FirebaseApp.initializeApp(context)
                analytics = FirebaseAnalytics.getInstance(context)
                AppLogger.d(TAG, "✅ AnalyticsHelper initialized")
            }
        } catch (e: Exception) {
            AppLogger.e(TAG, "❌ Init error: ${e.message}", e)
        }
    }

    private fun getAnalytics(): FirebaseAnalytics? {
        if (analytics == null) {
            AppLogger.w(TAG, "⚠️ Analytics belum init — call AnalyticsHelper.init(context)")
        }
        return analytics
    }

    // ============ SCREEN ============
    fun logScreen(screenName: String, screenClass: String = "") {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
                if (screenClass.isNotBlank()) {
                    putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
                }
            }
            getAnalytics()?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
            AppLogger.d(TAG, "Screen: $screenName")
        } catch (e: Exception) {
            AppLogger.w(TAG, "logScreen error: ${e.message}")
        }
    }

    // ============ USER ============
    fun setUserId(userId: String) {
        try {
            getAnalytics()?.setUserId(userId)
            AppLogger.d(TAG, "setUserId: $userId")
        } catch (e: Exception) {
            AppLogger.w(TAG, "setUserId error: ${e.message}")
        }
    }

    fun setUserProperty(name: String, value: String) {
        try {
            getAnalytics()?.setUserProperty(name, value)
        } catch (e: Exception) {}
    }

    fun logLogin(method: String = "firebase") {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, method)
            }
            getAnalytics()?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        } catch (e: Exception) {}
    }

    fun logSignUp(method: String = "firebase") {
        try {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, method)
            }
            getAnalytics()?.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
        } catch (e: Exception) {}
    }

    // ============ MINING ============
    fun logMiningClaim(amount: Double, tier: String = "") {
        try {
            val bundle = Bundle().apply {
                putDouble("amount", amount)
                if (tier.isNotBlank()) putString("tier", tier)
            }
            getAnalytics()?.logEvent("mining_claim", bundle)
            AppLogger.d(TAG, "Mining claim: $amount")
        } catch (e: Exception) {}
    }

    fun logMinerBuy(minerId: String, minerName: String, tier: String, cost: Int) {
        try {
            val bundle = Bundle().apply {
                putString("miner_id", minerId)
                putString("miner_name", minerName)
                putString("tier", tier)
                putInt("cost", cost)
            }
            getAnalytics()?.logEvent("miner_buy", bundle)
        } catch (e: Exception) {}
    }

    fun logMinerActivate(minerId: String) {
        try {
            val bundle = Bundle().apply { putString("miner_id", minerId) }
            getAnalytics()?.logEvent("miner_activate", bundle)
        } catch (e: Exception) {}
    }

    fun logMiniGameFinish(score: Int, durationMs: Long, bonusPoints: Int) {
        try {
            val bundle = Bundle().apply {
                putInt("score", score)
                putLong("duration_ms", durationMs)
                putInt("bonus_points", bonusPoints)
            }
            getAnalytics()?.logEvent("minigame_finish", bundle)
        } catch (e: Exception) {}
    }

    // ============ ADS ============
    fun logAdReward(vendor: String, points: Int) {
        try {
            val bundle = Bundle().apply {
                putString("vendor", vendor)
                putInt("points", points)
            }
            getAnalytics()?.logEvent("ad_reward", bundle)
        } catch (e: Exception) {}
    }

    fun logAdFail(vendor: String, reason: String) {
        try {
            val bundle = Bundle().apply {
                putString("vendor", vendor)
                putString("reason", reason)
            }
            getAnalytics()?.logEvent("ad_fail", bundle)
        } catch (e: Exception) {}
    }

    // ============ DAILY ============
    fun logDailyCheckIn(streak: Int, points: Int) {
        try {
            val bundle = Bundle().apply {
                putInt("streak", streak)
                putInt("points", points)
            }
            getAnalytics()?.logEvent("daily_check_in", bundle)
        } catch (e: Exception) {}
    }

    // ============ WITHDRAWAL ============
    fun logWithdrawRequest(amount: Double, method: String) {
        try {
            val bundle = Bundle().apply {
                putDouble("amount", amount)
                putString("method", method)
            }
            getAnalytics()?.logEvent("withdraw_request", bundle)
        } catch (e: Exception) {}
    }

    // ============ THERMAL ============
    fun logThermalAlert(temp: Float, level: Int, isCharging: Boolean) {
        try {
            val bundle = Bundle().apply {
                putFloat("temperature", temp)
                putInt("battery_level", level)
                putString("is_charging", if (isCharging) "yes" else "no")
            }
            getAnalytics()?.logEvent("thermal_alert", bundle)
        } catch (e: Exception) {}
    }

    fun logMiningPause(reason: String, temp: Float) {
        try {
            val bundle = Bundle().apply {
                putString("reason", reason)
                putFloat("temperature", temp)
            }
            getAnalytics()?.logEvent("mining_pause", bundle)
        } catch (e: Exception) {}
    }

    // ============ GENERIC ============
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        try {
            val bundle = Bundle().apply {
                params.forEach { (k, v) ->
                    when (v) {
                        is String -> putString(k, v)
                        is Int -> putInt(k, v)
                        is Long -> putLong(k, v)
                        is Double -> putDouble(k, v)
                        is Float -> putFloat(k, v)
                        is Boolean -> putString(k, if (v) "true" else "false")
                        else -> putString(k, v.toString())
                    }
                }
            }
            getAnalytics()?.logEvent(eventName, bundle)
        } catch (e: Exception) {}
    }
}
