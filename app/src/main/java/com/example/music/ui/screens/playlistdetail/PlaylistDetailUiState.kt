package com.example.music.ui.screens.playlistdetail

import com.example.music.domain.models.Track

sealed interface PlaylistDetailUiState {
    data object Loading : PlaylistDetailUiState
    data class Success(val tracks: List<Track>) : PlaylistDetailUiState
    data object Empty : PlaylistDetailUiState
    data class Error(val message: String) : PlaylistDetailUiState
}
