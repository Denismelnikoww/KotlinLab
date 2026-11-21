package com.example.music.ui.components

fun trackCountText(count: Int): String = when {
    count % 10 == 1 && count % 100 != 11 -> "$count трек"
    count % 10 in 2..4 && count % 100 !in 12..14 -> "$count трека"
    else -> "$count треков"
}
