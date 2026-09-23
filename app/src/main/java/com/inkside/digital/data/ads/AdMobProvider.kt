package com.inkside.digital.data.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * AdMobProvider: wrapper untuk load & show Rewarded Video dari AdMob.
 *
 * Fitur:
 * - Load rewarded ad
 * - Show ad dengan SSV (kalau ada)
 * - Callback: onRewardEarned, onAdDismissed, onAdFailed
 */
object AdMobProvider {

    private const val TAG = "AdMobProvider"

    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false

    /**
     * Init AdMob SDK — panggil sekali di MainActivity.onCreate.
     */
    fun init(context: Context) {
        if (isInitialized) return
        MobileAds.initialize(context) { status ->
            Log.d(TAG, "AdMob initialized: $status")
            isInitialized = true
        }
    }

    /**
     * Load rewarded ad.
     * @param userId Firebase UID (untuk SSV custom_data)
     */
    suspend fun loadRewardedAd(context: Context, userId: String? = null): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            // Set test device IDs di request configuration
            if (AdConfig.TEST_DEVICE_IDS.isNotEmpty()) {
                val config = com.google.android.gms.ads.RequestConfiguration.Builder()
                    .setTestDeviceIds(AdConfig.TEST_DEVICE_IDS)
                    .build()
                MobileAds.setRequestConfiguration(config)
            }

            val adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                context,
                AdConfig.REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(TAG, "Rewarded ad loaded")

                        // Set SSV options (kalau userId tersedia)
                        if (userId != null) {
                            val ssvOptions = ServerSideVerificationOptions.Builder()
                                .setCustomData(userId)
                                .build()
                            ad.setServerSideVerificationOptions(ssvOptions)
                        }

                        rewardedAd = ad
                        if (continuation.isActive) continuation.resume(Result.success(Unit))
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e(TAG, "Ad failed to load: ${error.message}")
                        rewardedAd = null
                        if (continuation.isActive) {
                            continuation.resume(Result.failure(Exception(error.message)))
                        }
                    }
                }
            )
        }
    }

    /**
     * Show rewarded ad.
     * @param activity Activity untuk menampilkan ad
     * @param onRewardEarned Callback saat user selesai nonton (dapat reward)
     * @param onAdDismissed Callback saat ad ditutup
     * @param onAdFailed Callback kalau ad gagal tampil
     */
    fun showRewardedAd(
        activity: Activity,
        onRewardEarned: (rewardAmount: Int, rewardType: String) -> Unit,
        onAdDismissed: () -> Unit = {},
        onAdFailed: (String) -> Unit = {}
    ) {
        val ad = rewardedAd
        if (ad == null) {
            Log.e(TAG, "Rewarded ad belum di-load")
            onAdFailed("Iklan belum siap. Coba lagi.")
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed")
                rewardedAd = null
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "Ad failed to show: ${error.message}")
                rewardedAd = null
                onAdFailed(error.message)
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed")
            }
        }

        ad.show(activity) { rewardItem ->
            Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
            onRewardEarned(rewardItem.amount, rewardItem.type)
        }
    }

    /**
     * Cek apakah rewarded ad siap ditampilkan.
     */
    fun isAdReady(): Boolean = rewardedAd != null

    /**
     * Reset ad (setelah ditampilkan).
     */
    fun reset() {
        rewardedAd = null
    }
}
