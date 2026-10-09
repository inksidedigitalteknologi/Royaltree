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
    private const val KEY_ADMIN_TOKEN = "admin_token"
    private const val KEY_ONBOARDED = "is_onboarded"

    // Force dark mode only — user tak boleh ubah
    var isDarkMode by mutableStateOf(true)
        private set

    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)

    // Admin token (dari /api/v1/admin/login)
    var adminToken: String? by mutableStateOf(null)
        private set

    fun setAdminToken(context: Context, token: String?) {
        adminToken = token
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (token != null) {
            prefs.edit().putString(KEY_ADMIN_TOKEN, token).apply()
        } else {
            prefs.edit().remove(KEY_ADMIN_TOKEN).apply()
        }
    }

    fun loadAdminToken(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        adminToken = prefs.getString(KEY_ADMIN_TOKEN, null)
    }

    var isOnboarded by mutableStateOf(false)
        private set

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        isDarkMode = true  // Force dark mode
        val langCode = prefs.getString(KEY_LANGUAGE, "EN") ?: "EN"
        currentLanguage = AppLanguage.values().find { it.code == langCode } ?: AppLanguage.ENGLISH
        isOnboarded = prefs.getBoolean(KEY_ONBOARDED, false)
    }

    fun setOnboarded(context: Context, value: Boolean) {
        isOnboarded = value
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDED, value).apply()
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        currentLanguage = language
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }
}
