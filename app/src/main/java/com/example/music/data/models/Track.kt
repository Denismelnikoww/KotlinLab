package com.example.music.data.models

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: String,
    val durationMs: Long = 0L,
    val artworkUrl: String? = null,
    val previewUrl: String? = null
)
