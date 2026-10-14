package com.inkside.digital.util

import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

/**
 * AnalyticsHelper — central helper untuk Firebase Analytics events.
 */
object AnalyticsHelper {

    private const val TAG = "Analytics"

    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    // ============ SCREEN ============
    fun logScreen(screenName: String, screenClass: String = "") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            if (screenClass.isNotBlank()) {
                putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
            }
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
        Log.d(TAG, "Screen: $screenName")
    }

    // ============ USER ============
    fun setUserId(userId: String) {
        analytics.setUserId(userId)
        Log.d(TAG, "setUserId: $userId")
    }

    fun setUserProperty(name: String, value: String) {
        analytics.setUserProperty(name, value)
    }

    fun logLogin(method: String = "firebase") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    fun logSignUp(method: String = "firebase") {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
    }

    // ============ MINING ============
    fun logMiningClaim(amount: Double, tier: String = "") {
        val bundle = Bundle().apply {
            putDouble("amount", amount)
            if (tier.isNotBlank()) putString("tier", tier)
        }
        analytics.logEvent("mining_claim", bundle)
        Log.d(TAG, "Mining claim: $amount")
    }

    fun logMinerBuy(minerId: String, minerName: String, tier: String, cost: Int) {
        val bundle = Bundle().apply {
            putString("miner_id", minerId)
            putString("miner_name", minerName)
            putString("tier", tier)
            putInt("cost", cost)
        }
        analytics.logEvent("miner_buy", bundle)
    }

    fun logMinerActivate(minerId: String) {
        val bundle = Bundle().apply {
            putString("miner_id", minerId)
        }
        analytics.logEvent("miner_activate", bundle)
    }

    fun logMiniGameFinish(score: Int, durationMs: Long, bonusPoints: Int) {
        val bundle = Bundle().apply {
            putInt("score", score)
            putLong("duration_ms", durationMs)
            putInt("bonus_points", bonusPoints)
        }
        analytics.logEvent("minigame_finish", bundle)
    }

    // ============ ADS ============
    fun logAdReward(vendor: String, points: Int) {
        val bundle = Bundle().apply {
            putString("vendor", vendor)
            putInt("points", points)
        }
        analytics.logEvent("ad_reward", bundle)
    }

    fun logAdFail(vendor: String, reason: String) {
        val bundle = Bundle().apply {
            putString("vendor", vendor)
            putString("reason", reason)
        }
        analytics.logEvent("ad_fail", bundle)
    }

    // ============ DAILY ============
    fun logDailyCheckIn(streak: Int, points: Int) {
        val bundle = Bundle().apply {
            putInt("streak", streak)
            putInt("points", points)
        }
        analytics.logEvent("daily_check_in", bundle)
    }

    // ============ WITHDRAWAL ============
    fun logWithdrawRequest(amount: Double, method: String) {
        val bundle = Bundle().apply {
            putDouble("amount", amount)
            putString("method", method)
        }
        analytics.logEvent("withdraw_request", bundle)
    }

    // ============ THERMAL ============
    fun logThermalAlert(temp: Float, level: Int, isCharging: Boolean) {
        val bundle = Bundle().apply {
            putFloat("temperature", temp)
            putInt("battery_level", level)
            putString("is_charging", if (isCharging) "yes" else "no")
        }
        analytics.logEvent("thermal_alert", bundle)
    }

    fun logMiningPause(reason: String, temp: Float) {
        val bundle = Bundle().apply {
            putString("reason", reason)
            putFloat("temperature", temp)
        }
        analytics.logEvent("mining_pause", bundle)
    }

    // ============ GENERIC ============
    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
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
        analytics.logEvent(eventName, bundle)
    }
}
