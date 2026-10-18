package com.inkside.digital

import android.app.Application
import android.os.Build
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.inkside.digital.data.network.ApiClient
import com.inkside.digital.util.AnalyticsHelper
import com.inkside.digital.util.AppLogger

/**
 * RoyaltreeApp — Application class.
 * Init Firebase, Crashlytics, ApiClient saat app start.
 */
class RoyaltreeApp : Application() {

    companion object {
        private const val TAG = "RoyaltreeApp"
    }

    override fun onCreate() {
        super.onCreate()

        try {
            // 1. Firebase
            FirebaseApp.initializeApp(this)
            AppLogger.d(TAG, "✅ Firebase initialized")

            // 2. Crashlytics
            val crashlytics = FirebaseCrashlytics.getInstance()
            // Disable di debug biar tidak spam console
            crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

            crashlytics.setCustomKey("app_version", BuildConfig.VERSION_NAME)
            crashlytics.setCustomKey("version_code", BuildConfig.VERSION_CODE.toString())
            crashlytics.setCustomKey("device_model", Build.MODEL)
            crashlytics.setCustomKey("device_brand", Build.BRAND)
            crashlytics.setCustomKey("android_version", Build.VERSION.RELEASE)
            crashlytics.setCustomKey("android_sdk", Build.VERSION.SDK_INT.toString())
            crashlytics.setCustomKey("build_type", if (BuildConfig.DEBUG) "debug" else "release")

            if (BuildConfig.DEBUG) {
                AppLogger.d(TAG, "🔧 Crashlytics DISABLED (debug build)")
            } else {
                AppLogger.d(TAG, "✅ Crashlytics ENABLED (release build)")
            }

            // 3. ApiClient
            ApiClient.init(this)
            AppLogger.d(TAG, "✅ ApiClient initialized")

            // Analytics — init helper + enabled (release only)
            AnalyticsHelper.init(this)
            val analytics = com.google.firebase.analytics.FirebaseAnalytics.getInstance(this)
            analytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)
            AppLogger.d(TAG, "✅ Analytics: ${if (BuildConfig.DEBUG) "OFF (debug)" else "ON (release)"}")

        } catch (e: Exception) {
            AppLogger.e(TAG, "❌ Init error: ${e.message}", e)
            // Jangan crash — biar app tetap jalan kalau init gagal
        }
    }
}
