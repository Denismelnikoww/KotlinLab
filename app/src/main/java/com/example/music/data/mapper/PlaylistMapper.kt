package com.example.music.data.mapper

import com.example.music.data.db.relation.PlaylistWithTrackCount
import com.example.music.data.db.relation.PlaylistWithTracks
import com.example.music.domain.models.Playlist
import com.example.music.domain.models.PlaylistDetails

fun PlaylistWithTrackCount.toDomain(): Playlist = Playlist(
    id = playlist.id,
    name = playlist.name,
    description = playlist.description,
    trackCount = trackCount,
    artworkUrl = playlist.artworkUrl ?: firstTrackArtwork
)

fun PlaylistWithTracks.toDomain(): PlaylistDetails = PlaylistDetails(
    playlist = Playlist(
        id = playlist.id,
        name = playlist.name,
        description = playlist.description,
        trackCount = tracks.size,
        artworkUrl = playlist.artworkUrl
    ),
    tracks = tracks.map { it.toDomain() }
)
