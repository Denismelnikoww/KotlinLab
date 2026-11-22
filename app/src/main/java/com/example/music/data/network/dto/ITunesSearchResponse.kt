package com.example.music.data.network.dto

import androidx.annotation.Keep

@Keep
data class ITunesSearchResponse(
    val resultCount: Int = 0,
    val results: List<ITunesItemDto> = emptyList()
)
