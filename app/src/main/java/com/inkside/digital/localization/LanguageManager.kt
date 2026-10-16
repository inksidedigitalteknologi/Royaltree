package com.inkside.digital.localization

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    INDONESIAN("ID", "Bahasa Indonesia", "🇮🇩"),
    ENGLISH("EN", "English", "🇺🇸"),
    SPANISH("ES", "Español", "🇪🇸"),
    CHINESE("ZH", "中文 (简体)", "🇨🇳"),
    ARABIC("AR", "العربية", "🇸🇦"),
    JAPANESE("JA", "日本語", "🇯🇵")
}

object LanguageManager {

    // Application context (di-set dari MainActivity.onCreate)
    private var appContext: android.content.Context? = null

    /**
     * Init — dipanggil sekali dari MainActivity.onCreate()
     */
    fun init(context: android.content.Context) {
        appContext = context.applicationContext
    }

    /**
     * Get translation langsung dari LanguageLoader (tanpa context param).
     */
    fun getString(key: String, language: AppLanguage): String {
        val ctx = appContext ?: return key
        return LanguageLoader.translate(ctx, key, language.code, key)
    }

    /**
     * Translate dengan fallback.
     * @param key Kunci terjemahan
     * @param language Bahasa target
     * @param fallback Fallback kalau key tidak ditemukan
     */
    fun translate(key: String, language: AppLanguage, fallback: String = key): String {
        val ctx = appContext ?: return fallback
        return LanguageLoader.translate(ctx, key, language.code, fallback)
    }
}
