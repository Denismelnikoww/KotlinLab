package com.example.music.data.repository

import com.example.music.data.models.Playlist
import com.example.music.data.models.Track

object MusicRepository {

    val allPlaylists = listOf(
        Playlist(0L, "Abbey Road (2019 Mix)", "17 треков", 1474815798),
        Playlist(1L, "Random Access Memories", "14 треков", 617154241),
        Playlist(2L, "MTV Unplugged In New York", "14 треков", 1440892370)
    )

    val playlistTracks = mapOf(
        1474815798L to listOf(
            Track(1, "Come Together", "The Beatles", "4:19", 259946),
            Track(2, "Something", "The Beatles", "3:02", 182333),
            Track(3, "Here Comes the Sun", "The Beatles", "3:05", 185733)
        ),
        617154241L to listOf(
            Track(4, "Get Lucky", "Daft Punk", "6:09", 369626),
            Track(5, "Instant Crush", "Daft Punk", "5:37", 337560),
            Track(6, "Lose Yourself to Dance", "Daft Punk", "5:53", 353293)
        ),
        1440892370L to listOf(
            Track(7, "About a Girl", "Nirvana", "3:37", 217226),
            Track(8, "Come as You Are", "Nirvana", "4:13", 253333),
            Track(9, "The Man Who Sold the World", "Nirvana", "4:20", 260400)
        )
    )
}
