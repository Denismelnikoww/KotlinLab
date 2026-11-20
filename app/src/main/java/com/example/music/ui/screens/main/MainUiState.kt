package com.example.music.ui.screens.main

import com.example.music.data.models.Track

data class MainSection(
    val title: String,
    val tracks: List<Track>
)

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val sections: List<MainSection>) : MainUiState
    data class Error(val message: String) : MainUiState
}
