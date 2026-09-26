package com.inkside.digital.data.ads

/**
 * AdConfig: konfigurasi AdMob untuk aplikasi Royaltree.
 *
 * TEST ID (development):
 * - App ID: ca-app-pub-5261912953437884~3728027368
 * - Rewarded: ca-app-pub-5261912953437884/9112305270
 *
 * PRODUCTION (nanti):
 * - Ganti dengan Ad Unit ID asli dari AdMob Console
 */
object AdConfig {

    // AdMob App ID (dipasang di AndroidManifest.xml juga)
    const val ADMOB_APP_ID = "ca-app-pub-5261912953437884~3728027368"

    // Rewarded Video Ad Unit ID
    // DEVELOPMENT: pakai Test Ad Unit dari Google (PASTI muncul)
    // PRODUCTION: ganti ke "ca-app-pub-5261912953437884/9112305270"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    // Banner Ad Unit ID (Adaptive Banner)
    // DEVELOPMENT: pakai Test Ad Unit dari Google
    // PRODUCTION: ganti ke Ad Unit ID Banner kamu
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // Reward default (harus sama dengan backend)
    const val DEFAULT_REWARD_POINTS = 50

    // Batas harian
    const val MAX_ADS_PER_DAY = 10

    // Timeout load iklan (detik)
    const val AD_LOAD_TIMEOUT_SECONDS = 15

    // Test device ID (untuk development — biar tidak kena ban)
    val TEST_DEVICE_IDS: List<String> = listOf(
        // Tambah device ID kamu di sini (dari logcat)
        // Format: "33BE2250B43518CCDA7DE426D04EE231"
    )
}
