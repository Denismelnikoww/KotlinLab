package com.example.music.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.music.data.db.entity.PlaylistEntity
import com.example.music.data.db.entity.PlaylistTrackCrossRef
import com.example.music.data.db.relation.PlaylistWithTrackCount
import com.example.music.data.db.relation.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    @Query(
        """
        SELECT playlists.*,
               COUNT(playlist_tracks.trackId) AS trackCount,
               (
                   SELECT tracks.artworkUrl
                   FROM playlist_tracks
                   INNER JOIN tracks ON tracks.id = playlist_tracks.trackId
                   WHERE playlist_tracks.playlistId = playlists.id
                     AND tracks.artworkUrl IS NOT NULL
                   ORDER BY tracks.trackName
                   LIMIT 1
               ) AS firstTrackArtwork
        FROM playlists
        LEFT JOIN playlist_tracks ON playlists.id = playlist_tracks.playlistId
        GROUP BY playlists.id
        ORDER BY playlists.id DESC
        """
    )
    fun observePlaylistsWithCount(): Flow<List<PlaylistWithTrackCount>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun observePlaylistWithTracks(playlistId: Long): Flow<PlaylistWithTracks?>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun observePlaylist(playlistId: Long): Flow<PlaylistEntity?>

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)
}
