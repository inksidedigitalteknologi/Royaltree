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

    suspend fun showRewardedAdForReward(
        activity: Activity,
        userId: String,
        onSuccess: (points: Int) -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        // 1. Load ad
        val loadResult = AdMobProvider.loadRewardedAd(activity, userId)
        if (loadResult.isFailure) {
            onFailure("Gagal load iklan: ${loadResult.exceptionOrNull()?.message}")
            return
        }

        // 2. Show ad
        AdMobProvider.showRewardedAd(
            activity = activity,
            onRewardEarned = { rewardAmount, rewardType ->
                Log.d(TAG, "Reward earned: $rewardAmount $rewardType")

                // 3. Kirim ke backend (background)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
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
                                    onFailure(json.optString("message", "Gagal klaim reward"))
                                }
                            }.onFailure { error ->
                                Log.e(TAG, "Backend reward error: ${error.message}")
                                onFailure("Gagal klaim reward: ${error.message}")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            onFailure("Error: ${e.message}")
                        }
                    }
                }
            },
            onAdDismissed = {
                Log.d(TAG, "Ad dismissed")
            },
            onAdFailed = { error ->
                onFailure(error)
            }
        )
    }

    fun init(context: Context) {
        AdMobProvider.init(context)
    }
}
