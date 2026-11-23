package com.example.music.ui.screens.player

data class PlaybackUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val durationMs: Long = PREVIEW_DURATION_MS,
    val hasPreview: Boolean = false,
    val hasError: Boolean = false
) {
    companion object {
        const val PREVIEW_DURATION_MS = 30_000L
    }
}
