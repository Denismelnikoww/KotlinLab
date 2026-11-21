package com.example.music.domain.models

data class PlaylistDetails(
    val playlist: Playlist,
    val tracks: List<Track>
)
