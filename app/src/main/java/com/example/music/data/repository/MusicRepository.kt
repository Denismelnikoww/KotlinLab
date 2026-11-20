package com.example.music.data.repository

import com.example.music.domain.models.Playlist

object MusicRepository {

    val allPlaylists = listOf(
        Playlist(0L, "Abbey Road (2019 Mix)", "The Beatles", 17),
        Playlist(1L, "Random Access Memories", "Daft Punk", 14),
        Playlist(2L, "MTV Unplugged In New York", "Nirvana", 14)
    )

    val playlistCollectionIds = mapOf(
        0L to 1474815798L,
        1L to 617154241L,
        2L to 1440892370L
    )
}
