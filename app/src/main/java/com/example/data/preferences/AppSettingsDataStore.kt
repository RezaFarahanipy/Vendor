package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.util.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "vendor_app_settings")

class AppSettingsDataStore(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val IS_CARD_VIEW_MODE = booleanPreferencesKey("is_card_view_mode")
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { preferences ->
        val raw = preferences[PreferencesKeys.THEME_MODE] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(raw)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    val appLanguageFlow: Flow<AppLanguage> = context.dataStore.data.map { preferences ->
        val raw = preferences[PreferencesKeys.APP_LANGUAGE] ?: AppLanguage.FA.name
        try {
            AppLanguage.valueOf(raw)
        } catch (_: Exception) {
            AppLanguage.FA
        }
    }

    val isCardViewModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_CARD_VIEW_MODE] ?: true
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode.name
        }
    }

    suspend fun setAppLanguage(lang: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_LANGUAGE] = lang.name
        }
    }

    suspend fun setCardViewMode(isCardView: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_CARD_VIEW_MODE] = isCardView
        }
    }
}
