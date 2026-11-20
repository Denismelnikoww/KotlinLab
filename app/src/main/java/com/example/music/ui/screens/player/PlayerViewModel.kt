package com.example.music.ui.screens.player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.ITunesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repository = ITunesRepository()

    private val trackId: Long =
        savedStateHandle.get<String>(TRACK_ID_KEY)?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            _uiState.value = repository.getTrack(trackId).fold(
                onSuccess = { track ->
                    if (track == null) {
                        PlayerUiState.Error(ERROR_MESSAGE)
                    } else {
                        PlayerUiState.Success(track)
                    }
                },
                onFailure = { PlayerUiState.Error(ERROR_MESSAGE) }
            )
        }
    }

    private companion object {
        const val TRACK_ID_KEY = "trackId"
        const val ERROR_MESSAGE = "Не удалось загрузить трек. Проверьте подключение к интернету"
    }
}
