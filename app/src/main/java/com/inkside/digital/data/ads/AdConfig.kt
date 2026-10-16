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
    // PRODUCTION: Production Ad Unit ID
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"  // TEST ID (Google)

    // Banner Ad Unit ID (Adaptive Banner)
    // DEVELOPMENT: pakai Test Ad Unit dari Google
    // PRODUCTION: ganti ke Ad Unit ID Banner kamu
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"

    // Interstitial Ad Unit ID (Full screen, skip 5s)
    // TEST ID: ca-app-pub-3940256099942544/1033173712
    // PRODUCTION: ganti ke Ad Unit ID Interstitial kamu
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // App Open Ad Unit ID (Splash screen)
    // TEST ID: ca-app-pub-3940256099942544/9257395921
    // PRODUCTION: ganti ke Ad Unit ID App Open kamu
    const val APP_OPEN_AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"

    // Interstitial cooldown (biar tidak terlalu sering)
    const val INTERSTITIAL_COOLDOWN_MS = 5 * 60 * 1000L  // 5 menit
    const val INTERSTITIAL_MIN_ACTIONS = 3  // Setelah 3 action baru boleh tampil

    // Reward default (harus sama dengan backend)
    const val DEFAULT_REWARD_POINTS = 50

    // Batas harian
    const val MAX_ADS_PER_DAY = 10

    // Timeout load iklan (detik)
    const val AD_LOAD_TIMEOUT_SECONDS = 15

    // Test device ID (untuk development — biar tidak kena ban)
    val TEST_DEVICE_IDS: List<String> = listOf(
        "62B56FF94B0401A463C1448C4D8E2027",  // Device utama (Vivo V2237)
        // Tambah device ID lain di sini (dari logcat)
        // Format: "33BE2250B43518CCDA7DE426D04EE231"
    )
}
