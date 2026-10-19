package com.inkside.digital.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.homeDataStore by preferencesDataStore(name = "home_prefs")

enum class HomeDisplayMode {
    RTP, COMMISSION, BOTH
}

object HomePreferences {
    private val KEY_MODE = stringPreferencesKey("home_display_mode")

    fun getDisplayMode(context: Context): Flow<HomeDisplayMode> {
        return context.homeDataStore.data.map { prefs ->
            val raw = prefs[KEY_MODE] ?: HomeDisplayMode.RTP.name
            try {
                HomeDisplayMode.valueOf(raw)
            } catch (e: Exception) {
                HomeDisplayMode.RTP
            }
        }
    }

    suspend fun setDisplayMode(context: Context, mode: HomeDisplayMode) {
        context.homeDataStore.edit { prefs ->
            prefs[KEY_MODE] = mode.name
        }
    }
}
