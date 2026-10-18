package com.inkside.digital.data.ads

import android.app.Activity
import android.content.Context
import com.inkside.digital.data.network.ApiClient
import com.inkside.digital.util.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

/**
 * AdManager: high-level manager untuk reward video ads.
 */
object AdManager {

    private const val TAG = "AdManager"

    /**
     * Mode iklan:
     * - REWARD: reward iklan biasa (panggil /ads/reward)
     * - MINER_ACTIVATE: aktivasi miner (SKIP /ads/reward, langsung sukses)
     */
    enum class AdMode { REWARD, MINER_ACTIVATE }

    suspend fun showRewardedAdForReward(
        activity: Activity,
        userId: String,
        onSuccess: (points: Int) -> Unit,
        onFailure: (reason: AdFailureReason, message: String) -> Unit,
        mode: AdMode = AdMode.REWARD   // ✅ Default: REWARD
    ) {
        // 1. Load ad
        val loadResult = AdMobProvider.loadRewardedAd(activity, userId)
        if (loadResult.isFailure) {
            val ex = loadResult.exceptionOrNull()
            val reason = if (ex is AdLoadException) ex.reason else AdFailureReason.UNKNOWN
            val msg = ex?.message ?: "Gagal load iklan"
            onFailure(reason, msg)
            return
        }

        // 2. Show ad
        AdMobProvider.showRewardedAd(
            activity = activity,
            onRewardEarned = { rewardAmount, rewardType ->
                AppLogger.d(TAG, "Reward earned: $rewardAmount $rewardType (mode=$mode)")

                // ✅ FIX: Kalau MINER_ACTIVATE, SKIP /ads/reward
                if (mode == AdMode.MINER_ACTIVATE) {
                    AppLogger.d(TAG, "⛏️ Miner activate mode — skip /ads/reward, langsung onSuccess")
                    CoroutineScope(Dispatchers.Main).launch {
                        onSuccess(0)   // points = 0 (tidak ada reward)
                    }
                    return@showRewardedAd
                }

                // 3. Kirim ke backend (background) — untuk REWARD mode
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        withContext(Dispatchers.Main) {
                            // Placeholder — akan di-replace oleh callback sebenarnya
                        }
                        val transactionId = UUID.randomUUID().toString()
                        val body = JSONObject().apply {
                            put("vendor", "admob")
                            put("transactionId", transactionId)
                            put("rewardAmount", rewardAmount)
                            put("rewardType", rewardType)
                        }

                        val result = ApiClient.rewardAd(body)
                        withContext(Dispatchers.Main) {
                            result.onSuccess { json ->
                                if (json.optBoolean("success", false)) {
                                    val points = json.optInt("points", AdConfig.DEFAULT_REWARD_POINTS)
                                    AppLogger.d(TAG, "Backend reward OK: +$points poin")
                                    onSuccess(points)
                                } else {
                                    onFailure(AdFailureReason.UNKNOWN, json.optString("message", "Gagal klaim reward"))
                                }
                            }.onFailure { error ->
                                AppLogger.e(TAG, "Backend reward error: ${error.message}")
                                onFailure(AdFailureReason.UNKNOWN, "Gagal klaim reward: ${error.message}")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onFailure(AdFailureReason.UNKNOWN, "Error: ${e.message}")
                        }
                    }
                }
            },
            onAdDismissed = {
                AppLogger.d(TAG, "Ad dismissed")
            },
            onAdFailed = { error ->
                onFailure(AdFailureReason.SHOW_FAILED, error)
            }
        )
    }

    /**
     * Show interstitial setelah user action (check-in, game, mission).
     *
     * Strategy:
     * - Cek cooldown + daily limit + action count
     * - Load kalau belum siap
     * - Show kalau eligible, auto-skip kalau tidak
     */
    fun showInterstitialForAction(
        activity: Activity,
        actionName: String = "action",
        onComplete: () -> Unit = {}
    ) {
        val context = activity.applicationContext

        // Record action (buat hitung min action)
        AdStrategy.recordAction(context)

        // Cek boleh tampil?
        if (!AdStrategy.canShowInterstitial(context)) {
            AppLogger.d(TAG, "Interstitial skip — strategy not eligible ($actionName)")
            onComplete()
            return
        }

        AppLogger.d(TAG, "Interstitial eligible for $actionName")

        CoroutineScope(Dispatchers.Main).launch {
            val result = AdMobProvider.loadInterstitial(context)

            if (result.isFailure) {
                AppLogger.w(TAG, "Interstitial load failed: ${result.exceptionOrNull()?.message}")
                onComplete()
                return@launch
            }

            AdMobProvider.showInterstitial(
                activity = activity,
                onDismissed = {
                    AdStrategy.recordInterstitial(context)
                    AppLogger.d(TAG, "Interstitial shown & recorded ($actionName)")
                    onComplete()
                },
                onFailed = { err ->
                    AppLogger.e(TAG, "Interstitial show failed: $err")
                    onComplete()
                }
            )
        }
    }

    fun init(context: Context) {
        AdMobProvider.init(context)
    }
}
