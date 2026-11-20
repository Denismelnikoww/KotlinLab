package com.example.music.data.network.mappers

import com.example.music.data.models.Track
import com.example.music.data.network.dto.ITunesItemDto
import com.example.music.data.network.dto.ITunesSearchResponse
import java.util.Locale

private const val ARTWORK_PLACEHOLDER = "100x100bb"
private const val ARTWORK_BIG = "600x600bb"

fun ITunesSearchResponse.toTracks(): List<Track> =
    results
        .filter { it.wrapperType == "track" && it.kind == "song" }
        .mapNotNull { it.toTrack() }

fun ITunesItemDto.toTrack(): Track? {
    val id = this.trackId ?: return null
    val title = this.trackName ?: return null
    return Track(
        id = id,
        title = title,
        artist = artistName.orEmpty(),
        duration = formatDuration(trackTimeMillis),
        durationMs = trackTimeMillis ?: 0L,
        artworkUrl = artworkUrl100?.replace(ARTWORK_PLACEHOLDER, ARTWORK_BIG),
        previewUrl = previewUrl
    )
}

fun formatDuration(durationMs: Long?): String {
    if (durationMs == null || durationMs <= 0L) return "--:--"
    val totalSeconds = durationMs / 1000
    return String.format(Locale.US, "%d:%02d", totalSeconds / 60, totalSeconds % 60)
}
