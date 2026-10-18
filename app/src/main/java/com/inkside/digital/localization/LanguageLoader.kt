package com.inkside.digital.localization

import android.content.Context
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap
import com.inkside.digital.util.AppLogger

/**
 * LanguageLoader — Load translations dari assets/i18n (JSON files)
 *
 * Keunggulan vs hardcoded mapOf:
 * - Tidak kena limit JVM (MethodTooLarge)
 * - Bisa update tanpa recompile logic
 * - Bundle size lebih kecil
 * - Support nested structure (future)
 */
object LanguageLoader {

    private const val TAG = "LanguageLoader"
    private const val ASSETS_PATH = "i18n"
    private const val FALLBACK = "id"

    // Cache: { language: { key: value } }
    private val cache = ConcurrentHashMap<String, Map<String, String>>()

    fun getTranslations(context: Context, languageCode: String): Map<String, String> {
        val lang = languageCode.lowercase()
        cache[lang]?.let { return it }

        val translations = try {
            val jsonString = context.assets
                .open("$ASSETS_PATH/$lang.json")
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(jsonString)
            val translationsObj = json.getJSONObject("translations")
            val map = mutableMapOf<String, String>()

            translationsObj.keys().forEach { key ->
                map[key] = translationsObj.getString(key)
            }

            AppLogger.d(TAG, "✅ Loaded $lang: ${map.size} keys")
            map

        } catch (e: Exception) {
            AppLogger.e(TAG, "❌ Failed to load $lang: ${e.message}", e)
            emptyMap()
        }

        cache[lang] = translations
        return translations
    }

    fun translate(context: Context, key: String, languageCode: String, fallback: String): String {
        // 1. Target language
        getTranslations(context, languageCode)[key]?.let { return it }

        // 2. Fallback language
        if (languageCode.lowercase() != FALLBACK) {
            getTranslations(context, FALLBACK)[key]?.let { return it }
        }

        // 3. Fallback param
        if (fallback != key) return fallback

        // 4. Last resort
        return key
    }

    fun clearCache() {
        cache.clear()
    }
}
