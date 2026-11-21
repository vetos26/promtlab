package com.promtlab.app.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.promtlab.app.ui.theme.AccentPalette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "promtlab_settings",
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() }
)

enum class ThemeMode(val label: String) {
    SYSTEM("Системная"),
    LIGHT("Светлая"),
    DARK("Тёмная")
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accent: AccentPalette = AccentPalette.VIOLET,
    val dynamicColor: Boolean = false,
    val showTokenCounter: Boolean = true,
    val defaultLanguage: String = "Русский",
    val completedLessons: Set<String> = emptySet()
)

class SettingsRepository(context: Context) {

    private val dataStore = context.applicationContext.dataStore

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            themeMode = prefs[KEY_THEME]?.let { value ->
                ThemeMode.entries.firstOrNull { it.name == value }
            } ?: ThemeMode.SYSTEM,
            accent = prefs[KEY_ACCENT]?.let { value ->
                AccentPalette.entries.firstOrNull { it.name == value }
            } ?: AccentPalette.VIOLET,
            dynamicColor = prefs[KEY_DYNAMIC] ?: false,
            showTokenCounter = prefs[KEY_TOKENS] ?: true,
            defaultLanguage = prefs[KEY_LANGUAGE] ?: "Русский",
            completedLessons = prefs[KEY_LESSONS] ?: emptySet()
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[KEY_THEME] = mode.name }
    }

    suspend fun setAccent(accent: AccentPalette) {
        dataStore.edit { it[KEY_ACCENT] = accent.name }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[KEY_DYNAMIC] = enabled }
    }

    suspend fun setShowTokenCounter(enabled: Boolean) {
        dataStore.edit { it[KEY_TOKENS] = enabled }
    }

    suspend fun setDefaultLanguage(language: String) {
        dataStore.edit { it[KEY_LANGUAGE] = language }
    }

    suspend fun toggleLessonCompleted(id: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_LESSONS] ?: emptySet()
            prefs[KEY_LESSONS] = if (id in current) current - id else current + id
        }
    }

    private companion object {
        val KEY_THEME = stringPreferencesKey("theme_mode")
        val KEY_ACCENT = stringPreferencesKey("accent")
        val KEY_DYNAMIC = booleanPreferencesKey("dynamic_color")
        val KEY_TOKENS = booleanPreferencesKey("show_tokens")
        val KEY_LANGUAGE = stringPreferencesKey("default_language")
        val KEY_LESSONS = stringSetPreferencesKey("completed_lessons")
    }
}
