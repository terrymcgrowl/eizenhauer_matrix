package com.eisenhower.matrix.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер настроек приложения через DataStore.
 * Хранит: показывался ли туториал, выбранная тема.
 */
@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        // Ключ: был ли показан туториал при первом запуске
        val TUTORIAL_SHOWN = booleanPreferencesKey("tutorial_shown")
        // Ключ: использовать тёмную тему (null = системная)
        val USE_DARK_THEME = booleanPreferencesKey("use_dark_theme")
        val USE_SYSTEM_THEME = booleanPreferencesKey("use_system_theme")
    }

    /**
     * Поток: показывался ли туториал.
     */
    val isTutorialShown: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[TUTORIAL_SHOWN] ?: false
    }

    /**
     * Поток: настройка темы (true=тёмная, false=светлая, null=системная).
     */
    val themePreference: Flow<ThemePreference> = dataStore.data.map { prefs ->
        when {
            prefs[USE_SYSTEM_THEME] == true -> ThemePreference.SYSTEM
            prefs[USE_DARK_THEME] == true -> ThemePreference.DARK
            else -> ThemePreference.LIGHT
        }
    }

    /**
     * Отметить, что туториал был показан.
     */
    suspend fun setTutorialShown() {
        dataStore.edit { prefs ->
            prefs[TUTORIAL_SHOWN] = true
        }
    }

    /**
     * Сохранить выбор темы пользователем.
     */
    suspend fun setThemePreference(theme: ThemePreference) {
        dataStore.edit { prefs ->
            prefs[USE_SYSTEM_THEME] = (theme == ThemePreference.SYSTEM)
            prefs[USE_DARK_THEME] = (theme == ThemePreference.DARK)
        }
    }
}

/**
 * Варианты темы оформления.
 */
enum class ThemePreference { LIGHT, DARK, SYSTEM }
