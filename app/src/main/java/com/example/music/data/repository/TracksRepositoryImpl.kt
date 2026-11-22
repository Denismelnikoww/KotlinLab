package com.example.music.data.repository

import com.example.music.data.db.dao.TracksDao
import com.example.music.data.mapper.toDomain
import com.example.music.data.mapper.toEntity
import com.example.music.domain.models.Track
import com.example.music.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksRepositoryImpl(
    private val tracksDao: TracksDao
) : TracksRepository {

    override fun observeFavorites(): Flow<List<Track>> =
        tracksDao.observeFavorites().map { tracks -> tracks.map { it.toDomain() } }

    override fun observeTrack(trackId: Long): Flow<Track?> =
        tracksDao.observeById(trackId).map { it?.toDomain() }

    override suspend fun getTrack(trackId: Long): Track? =
        tracksDao.getById(trackId)?.toDomain()

    override suspend fun setFavorite(track: Track, isFavorite: Boolean) {
        tracksDao.insertIfAbsent(track.toEntity(isFavorite))
        tracksDao.setFavorite(track.id, isFavorite)
    }
}
