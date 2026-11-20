package com.example.music.domain.repository

import com.example.music.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {

    fun observeFavorites(): Flow<List<Track>>

    fun observeTrack(trackId: Long): Flow<Track?>

    suspend fun getTrack(trackId: Long): Track?

    suspend fun setFavorite(track: Track, isFavorite: Boolean)
}
