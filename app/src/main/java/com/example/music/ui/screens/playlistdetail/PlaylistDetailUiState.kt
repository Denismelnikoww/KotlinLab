package com.example.music.ui.screens.playlistdetail

import com.example.music.domain.models.Playlist
import com.example.music.domain.models.Track

sealed interface PlaylistDetailUiState {
    data object Loading : PlaylistDetailUiState
    data class Success(val playlist: Playlist, val tracks: List<Track>) : PlaylistDetailUiState
    data object NotFound : PlaylistDetailUiState
}
