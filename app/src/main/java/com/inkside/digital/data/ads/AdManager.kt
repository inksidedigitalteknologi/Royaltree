package com.inkside.digital.data.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.inkside.digital.data.network.ApiClient
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
        Log.d("MinerFlow", "🎯 [6] AdManager.showRewardedAdForReward START — userId=$userId")
        // 1. Load ad
        val loadResult = AdMobProvider.loadRewardedAd(activity, userId)
        Log.d("MinerFlow", "🎯 [7] Load result: isSuccess=${loadResult.isSuccess}, exception=${loadResult.exceptionOrNull()?.message}")
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
                Log.d(TAG, "Reward earned: $rewardAmount $rewardType (mode=$mode)")

                // ✅ FIX: Kalau MINER_ACTIVATE, SKIP /ads/reward
                if (mode == AdMode.MINER_ACTIVATE) {
                    Log.d(TAG, "⛏️ Miner activate mode — skip /ads/reward, langsung onSuccess")
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
                                    Log.d(TAG, "Backend reward OK: +$points poin")
                                    onSuccess(points)
                                } else {
                                    onFailure(AdFailureReason.UNKNOWN, json.optString("message", "Gagal klaim reward"))
                                }
                            }.onFailure { error ->
                                Log.e(TAG, "Backend reward error: ${error.message}")
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
                Log.d(TAG, "Ad dismissed")
            },
            onAdFailed = { error ->
                onFailure(AdFailureReason.SHOW_FAILED, error)
            }
        )
    }

    fun init(context: Context) {
        AdMobProvider.init(context)
    }
}
