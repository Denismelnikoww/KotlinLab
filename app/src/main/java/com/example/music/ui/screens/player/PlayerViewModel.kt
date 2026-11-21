package com.example.music.ui.screens.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.music.MusicApp
import com.example.music.data.repository.ITunesRepository
import com.example.music.domain.models.Playlist
import com.example.music.domain.models.Track
import com.example.music.domain.repository.PlaylistsRepository
import com.example.music.domain.repository.TracksRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val tracksRepository: TracksRepository =
        (application as MusicApp).container.tracksRepository

    private val playlistsRepository: PlaylistsRepository =
        (application as MusicApp).container.playlistsRepository

    private val iTunesRepository = ITunesRepository()

    private val trackId: Long =
        savedStateHandle.get<String>(TRACK_ID_KEY)?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val playlists: StateFlow<List<Playlist>> = playlistsRepository.observePlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    private var track: Track? = null
    private var loadJob: Job? = null
    private var favoriteJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = PlayerUiState.Loading
            track = tracksRepository.getTrack(trackId)
                ?: iTunesRepository.getTrack(trackId).getOrNull()
            val loadedTrack = track
            if (loadedTrack == null) {
                _uiState.value = PlayerUiState.Error(ERROR_MESSAGE)
                return@launch
            }
            _uiState.value = PlayerUiState.Success(loadedTrack)
            observeFavorite()
        }
    }

    fun toggleFavorite() {
        val currentTrack = track ?: return
        val isFavorite = (uiState.value as? PlayerUiState.Success)?.track?.isFavorite
            ?: currentTrack.isFavorite
        viewModelScope.launch {
            tracksRepository.setFavorite(currentTrack, !isFavorite)
        }
    }

    fun addTrackToPlaylist(playlistId: Long) {
        val currentTrack = track ?: return
        viewModelScope.launch {
            playlistsRepository.addTrackToPlaylist(playlistId, currentTrack)
        }
    }

    fun createPlaylistWithTrack(name: String, description: String) {
        val currentTrack = track ?: return
        if (name.isBlank()) return
        viewModelScope.launch {
            val playlistId = playlistsRepository.createPlaylist(name, description)
            playlistsRepository.addTrackToPlaylist(playlistId, currentTrack)
        }
    }

    private fun observeFavorite() {
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            tracksRepository.observeTrack(trackId).collect { stored ->
                val base = track ?: return@collect
                _uiState.value = PlayerUiState.Success(
                    base.copy(isFavorite = stored?.isFavorite == true)
                )
            }
        }
    }

    private companion object {
        const val TRACK_ID_KEY = "trackId"
        const val STOP_TIMEOUT_MS = 5_000L
        const val ERROR_MESSAGE = "Не удалось загрузить трек. Проверьте подключение к интернету"
    }
}
