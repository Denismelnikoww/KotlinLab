package com.example.music.data.repository

import com.example.music.data.models.Playlist
import com.example.music.data.models.Track

object MusicRepository {

    val allPlaylists = listOf(
        Playlist(0, "Best songs 2021", "86 треков"),
        Playlist(1, "Summer Party", "157 треков"),
        Playlist(2, "Morning", "12 треков")
    )

    val playlistTracks = mapOf(
        0 to listOf(
            Track("Yesterday", "The Beatles", "2:05"),
            Track("Let It Be", "The Beatles", "4:03"),
            Track("Hey Jude", "The Beatles", "7:11")
        ),
        1 to listOf(
            Track("Here Comes the Sun", "The Beatles", "3:05"),
            Track("Come Together", "The Beatles", "4:19"),
            Track("Something", "The Beatles", "3:02")
        ),
        2 to listOf(
            Track("Blackbird", "The Beatles", "2:18"),
            Track("Norwegian Wood", "The Beatles", "2:05"),
            Track("Michelle", "The Beatles", "2:42")
        )
    )
}