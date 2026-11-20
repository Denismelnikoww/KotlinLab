package com.example.music.ui.screens.player

import com.example.music.data.models.Track

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Success(val track: Track) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}
