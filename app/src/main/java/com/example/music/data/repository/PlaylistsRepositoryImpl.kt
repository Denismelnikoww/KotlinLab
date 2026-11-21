package com.example.music.data.repository

import com.example.music.data.db.dao.PlaylistsDao
import com.example.music.data.db.dao.TracksDao
import com.example.music.data.db.entity.PlaylistEntity
import com.example.music.data.db.entity.PlaylistTrackCrossRef
import com.example.music.data.mapper.toDomain
import com.example.music.data.mapper.toEntity
import com.example.music.domain.models.Playlist
import com.example.music.domain.models.PlaylistDetails
import com.example.music.domain.models.Track
import com.example.music.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(
    private val playlistsDao: PlaylistsDao,
    private val tracksDao: TracksDao
) : PlaylistsRepository {

    override fun observePlaylists(): Flow<List<Playlist>> =
        playlistsDao.observePlaylistsWithCount()
            .map { playlists -> playlists.map { it.toDomain() } }

    override fun observePlaylist(playlistId: Long): Flow<PlaylistDetails?> =
        playlistsDao.observePlaylistWithTracks(playlistId)
            .map { it?.toDomain() }

    override suspend fun createPlaylist(name: String, description: String): Long =
        playlistsDao.insertPlaylist(
            PlaylistEntity(name = name.trim(), description = description.trim())
        )

    override suspend fun addTrackToPlaylist(playlistId: Long, track: Track) {
        tracksDao.insertIfAbsent(track.toEntity())
        playlistsDao.addTrackToPlaylist(
            PlaylistTrackCrossRef(playlistId = playlistId, trackId = track.id)
        )
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistsDao.deletePlaylist(playlistId)
    }
}
