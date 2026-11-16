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

/** Default phrases offered as quick suggestions while registering a vendor's specialty. */
val DEFAULT_SPECIALTY_SUGGESTIONS = listOf(
    "میلگرد",
    "نبشی",
    "ناودانی",
    "تیرآهن",
    "ورق سیاه",
    "ورق گالوانیزه",
    "ورق روغنی",
    "ورق استیل",
    "لوله مانیسمان",
    "لوله درزدار",
    "لوله پلی‌اتیلن",
    "پروفیل و قوطی",
    "سیم و کابل",
    "کابل برق",
    "کابل شبکه",
    "تابلو برق",
    "ترانسفورماتور",
    "کنتاکتور و رله",
    "لامپ و روشنایی",
    "الکتروموتور",
    "پمپ آب",
    "پمپ صنعتی",
    "شیرآلات صنعتی",
    "اتصالات و فلنج",
    "بلبرینگ و رولبرینگ",
    "کمپرسور",
    "جک هیدرولیک",
    "پیچ و مهره",
    "یراق‌آلات",
    "ابزارآلات",
    "رنگ صنعتی",
    "چسب و درزگیر",
    "سیمان",
    "گچ و خاک",
    "آجر و بلوک",
    "کاشی و سرامیک",
    "شیشه و آینه",
    "چوب و MDF",
    "عایق رطوبتی",
    "داربست و قالب‌بندی",
    "لوازم ایمنی و حفاظت فردی",
    "مواد شیمیایی",
    "روغن و گریس",
    "قطعات یدکی"
)

class AppSettingsDataStore(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
        val IS_CARD_VIEW_MODE = booleanPreferencesKey("is_card_view_mode")
        val IS_APP_LOCK_ENABLED = booleanPreferencesKey("is_app_lock_enabled")
        val IS_SCREENSHOT_BLOCK_ENABLED = booleanPreferencesKey("is_screenshot_block_enabled")
        val IS_DYNAMIC_COLOR_ENABLED = booleanPreferencesKey("is_dynamic_color_enabled")
        val RECENT_SEARCHES = stringPreferencesKey("recent_searches")
        val SPECIALTY_SUGGESTIONS = stringPreferencesKey("specialty_suggestions")
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

    val isAppLockEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_APP_LOCK_ENABLED] ?: false
    }

    val isScreenshotBlockEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_SCREENSHOT_BLOCK_ENABLED] ?: false
    }

    val isDynamicColorEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_DYNAMIC_COLOR_ENABLED] ?: false
    }

    /** Last committed search phrases, newest first (max 8). */
    val recentSearchesFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        (preferences[PreferencesKeys.RECENT_SEARCHES] ?: "").split("\n").filter { it.isNotBlank() }
    }

    suspend fun addRecentSearch(query: String) {
        val clean = query.trim().replace("\n", " ")
        if (clean.length < 2) return
        context.dataStore.edit { preferences ->
            val current = (preferences[PreferencesKeys.RECENT_SEARCHES] ?: "").split("\n").filter { it.isNotBlank() }
            val updated = (listOf(clean) + current.filter { !it.equals(clean, ignoreCase = true) }).take(8)
            preferences[PreferencesKeys.RECENT_SEARCHES] = updated.joinToString("\n")
        }
    }

    suspend fun clearRecentSearches() {
        context.dataStore.edit { preferences -> preferences.remove(PreferencesKeys.RECENT_SEARCHES) }
    }

    /** User-editable specialty suggestions (stored newline-separated; defaults until first edit). */
    val specialtySuggestionsFlow: Flow<List<String>> = context.dataStore.data.map { preferences ->
        val raw = preferences[PreferencesKeys.SPECIALTY_SUGGESTIONS]
        if (raw == null) DEFAULT_SPECIALTY_SUGGESTIONS else raw.split("\n").filter { it.isNotBlank() }
    }

    suspend fun setSpecialtySuggestions(list: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SPECIALTY_SUGGESTIONS] = list.joinToString("\n")
        }
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

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_APP_LOCK_ENABLED] = enabled
        }
    }

    suspend fun setScreenshotBlockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_SCREENSHOT_BLOCK_ENABLED] = enabled
        }
    }

    suspend fun setDynamicColorEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_DYNAMIC_COLOR_ENABLED] = enabled
        }
    }
}
