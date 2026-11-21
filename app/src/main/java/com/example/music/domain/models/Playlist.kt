package com.example.music.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val trackCount: Int,
    val artworkUrl: String? = null
)
