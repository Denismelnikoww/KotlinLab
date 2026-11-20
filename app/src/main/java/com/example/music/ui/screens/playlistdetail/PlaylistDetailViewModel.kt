package com.example.music.ui.screens.playlistdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.models.Playlist
import com.example.music.data.repository.ITunesRepository
import com.example.music.data.repository.MusicRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = ITunesRepository()

    private val playlistId: Long =
        savedStateHandle.get<String>(PLAYLIST_ID_KEY)?.toLongOrNull() ?: 0L

    val playlist: Playlist? = MusicRepository.allPlaylists.find { it.id == playlistId }

    private val _uiState = MutableStateFlow<PlaylistDetailUiState>(PlaylistDetailUiState.Loading)
    val uiState: StateFlow<PlaylistDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        val collectionId = playlist?.collectionId
        if (collectionId == null) {
            _uiState.value = PlaylistDetailUiState.Error(ERROR_MESSAGE)
            return
        }
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = PlaylistDetailUiState.Loading
            _uiState.value = repository.getPlaylistTracks(collectionId).fold(
                onSuccess = { tracks ->
                    if (tracks.isEmpty()) {
                        PlaylistDetailUiState.Empty
                    } else {
                        PlaylistDetailUiState.Success(tracks)
                    }
                },
                onFailure = { PlaylistDetailUiState.Error(ERROR_MESSAGE) }
            )
        }
    }

    private companion object {
        const val PLAYLIST_ID_KEY = "playlistId"
        const val ERROR_MESSAGE = "Не удалось загрузить треки. Проверьте подключение к интернету"
    }
}
