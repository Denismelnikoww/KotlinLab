package com.example.music.ui.screens.playlistdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.music.MusicApp
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PlaylistDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository = (application as MusicApp).container.playlistsRepository

    private val playlistId: Long =
        savedStateHandle.get<String>(PLAYLIST_ID_KEY)?.toLongOrNull() ?: 0L

    val uiState: StateFlow<PlaylistDetailUiState> = repository.observePlaylist(playlistId)
        .map { details ->
            if (details == null) {
                PlaylistDetailUiState.NotFound
            } else {
                PlaylistDetailUiState.Success(details.playlist, details.tracks)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            PlaylistDetailUiState.Loading
        )

    private companion object {
        const val PLAYLIST_ID_KEY = "playlistId"
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
