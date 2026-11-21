package com.example.music.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {

    val history: Flow<List<String>>

    suspend fun addQuery(query: String)

    suspend fun clear()
}
