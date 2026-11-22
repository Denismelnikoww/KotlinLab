package com.example.music.domain.repository

import com.example.music.domain.models.Playlist
import com.example.music.domain.models.PlaylistDetails
import com.example.music.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    fun observePlaylists(): Flow<List<Playlist>>

    fun observePlaylist(playlistId: Long): Flow<PlaylistDetails?>

    suspend fun createPlaylist(name: String, description: String): Long

    suspend fun addTrackToPlaylist(playlistId: Long, track: Track)

    suspend fun deletePlaylist(playlistId: Long)
}
