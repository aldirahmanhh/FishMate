package com.bangkit.fishmate.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("theme_prefs")

class ThemeManager(private val context: Context) {

    companion object {
        private val THEME_KEY = booleanPreferencesKey("theme_key")
    }

    suspend fun saveTheme(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkMode
        }
    }

    val themeMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: false
        }
}