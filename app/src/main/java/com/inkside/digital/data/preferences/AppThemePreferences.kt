package com.inkside.digital.data.preferences

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.inkside.digital.localization.AppLanguage

/**
 * AppThemePreferences: simpan preferensi dark mode & bahasa di SharedPreferences.
 */
object AppThemePreferences {

    private const val PREFS_NAME = "royaltree_app_prefs"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_LANGUAGE = "app_language"
    private const val KEY_ONBOARDED = "is_onboarded"

    // State global untuk UI (auto-update)
    var isDarkMode by mutableStateOf(false)
        private set

    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)
        private set

    var isOnboarded by mutableStateOf(false)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false)
        val langCode = prefs.getString(KEY_LANGUAGE, "EN") ?: "EN"
        currentLanguage = AppLanguage.values().find { it.code == langCode } ?: AppLanguage.ENGLISH
        isOnboarded = prefs.getBoolean(KEY_ONBOARDED, false)
    }

    fun setOnboarded(context: Context, value: Boolean) {
        isOnboarded = value
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDED, value).apply()
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        isDarkMode = enabled
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        currentLanguage = language
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }
}
