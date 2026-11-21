package com.example.music.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.music.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

class SearchHistoryPreferences(
    private val context: Context
) : SearchHistoryRepository {

    override val history: Flow<List<String>> = context.userPreferencesDataStore.data
        .map { preferences -> parseHistory(preferences[SEARCH_HISTORY_KEY]) }

    override suspend fun addQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        context.userPreferencesDataStore.edit { preferences ->
            val current = parseHistory(preferences[SEARCH_HISTORY_KEY])
            val updated = (listOf(trimmed) + current.filterNot { it.equals(trimmed, ignoreCase = true) })
                .take(MAX_HISTORY_SIZE)
            preferences[SEARCH_HISTORY_KEY] = JSONArray(updated).toString()
        }
    }

    override suspend fun clear() {
        context.userPreferencesDataStore.edit { preferences ->
            preferences.remove(SEARCH_HISTORY_KEY)
        }
    }

    private fun parseHistory(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index -> array.getString(index) }
        }.getOrDefault(emptyList())
    }

    private companion object {
        val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
        const val MAX_HISTORY_SIZE = 10
    }
}
