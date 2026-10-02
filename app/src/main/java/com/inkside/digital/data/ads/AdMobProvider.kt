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
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class AdLoadException(
    val reason: AdFailureReason,
    message: String
) : Exception(message)

enum class AdFailureReason {
    NO_FILL,
    NETWORK,
    INTERNAL,
    INVALID,
    NOT_READY,
    SHOW_FAILED,
    UNKNOWN
}

object AdMobProvider {

    private const val TAG = "AdMobProvider"

    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false

    // State: null = loading, true = ready, false = no ad
    private val _adState = MutableStateFlow<Boolean?>(null)
    val adState: StateFlow<Boolean?> = _adState.asStateFlow()

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
        _adState.value = null  // loading
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
                        _adState.value = true  // ready
                        if (continuation.isActive) continuation.resume(Result.success(Unit))
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e(TAG, "Ad failed to load: code=${error.code}, msg=${error.message}")
                        rewardedAd = null
                        _adState.value = false  // no ad

                        val (reason, userMsg) = when (error.code) {
                            AdRequest.ERROR_CODE_NO_FILL ->
                                AdFailureReason.NO_FILL to "Iklan tidak dapat dimuat. Matikan AdGuard/AdBlock DNS untuk mendapat reward."
                            AdRequest.ERROR_CODE_NETWORK_ERROR ->
                                AdFailureReason.NETWORK to "Koneksi internet bermasalah. Periksa jaringan Anda."
                            AdRequest.ERROR_CODE_INTERNAL_ERROR ->
                                AdFailureReason.INTERNAL to "Layanan iklan sedang gangguan. Coba lagi nanti."
                            AdRequest.ERROR_CODE_INVALID_REQUEST ->
                                AdFailureReason.INVALID to "Konfigurasi iklan bermasalah. Hubungi support."
                            else ->
                                AdFailureReason.UNKNOWN to "Iklan gagal dimuat. Coba lagi nanti."
                        }

                        if (continuation.isActive) {
                            continuation.resume(Result.failure(AdLoadException(reason, userMsg)))
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
            onAdFailed("Iklan belum siap. Tunggu beberapa detik lalu coba lagi.")
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Ad dismissed")
                rewardedAd = null
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                Log.e(TAG, "Ad failed to show: code=${error.code}, msg=${error.message}")
                rewardedAd = null
                onAdFailed("Iklan gagal tampil. Coba lagi nanti.")
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
        _adState.value = null
    }

    /**
     * Buat AdView banner adaptive — untuk ditaruh di Compose via AndroidView.
     * @param context Context
     * @param adWidthDp Lebar banner dalam dp (dari BoxWithConstraints)
     */
    @Suppress("DEPRECATION")
    fun createBannerView(context: Context, adWidthDp: Int, onFailed: () -> Unit = {}): AdView {
        val adView = AdView(context)
        adView.setAdSize(
            AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidthDp)  // TODO: update to new API
        )
        adView.adUnitId = AdConfig.BANNER_AD_UNIT_ID

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "Banner ad loaded")
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e(TAG, "Banner failed: code=${error.code}, msg=${error.message}")
                try { onFailed() } catch (_: Exception) { }
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        return adView
    }
}
