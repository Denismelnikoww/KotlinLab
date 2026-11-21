package com.example.music.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.example.music.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemePreferences(
    private val context: Context
) : ThemeRepository {

    override val darkTheme: Flow<Boolean> = context.userPreferencesDataStore.data
        .map { preferences -> preferences[DARK_THEME_KEY] ?: false }

    override suspend fun setDarkTheme(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[DARK_THEME_KEY] = enabled
        }
    }

    private companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }
}
