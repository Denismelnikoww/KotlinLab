package com.example.music.ui.screens.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.music.MusicApp
import com.example.music.data.repository.ITunesRepository
import com.example.music.domain.models.Playlist
import com.example.music.domain.models.Track
import com.example.music.domain.repository.PlaylistsRepository
import com.example.music.domain.repository.TracksRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
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

    private val _playbackState = MutableStateFlow(PlaybackUiState())
    val playbackState: StateFlow<PlaybackUiState> = _playbackState.asStateFlow()

    private val _positionMs = MutableStateFlow(0L)
    val positionMs: StateFlow<Long> = _positionMs.asStateFlow()

    val playlists: StateFlow<List<Playlist>> = playlistsRepository.observePlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

    private var track: Track? = null
    private var loadJob: Job? = null
    private var favoriteJob: Job? = null
    private var progressJob: Job? = null
    private var player: ExoPlayer? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playbackState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) startProgressUpdates() else progressJob?.cancel()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _playbackState.update {
                it.copy(isBuffering = playbackState == Player.STATE_BUFFERING)
            }
            if (playbackState == Player.STATE_READY) {
                val duration = player?.duration?.takeIf { it > 0 }
                    ?: PlaybackUiState.PREVIEW_DURATION_MS
                _playbackState.update { it.copy(durationMs = duration) }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            _playbackState.update {
                it.copy(isPlaying = false, isBuffering = false, hasError = true)
            }
        }
    }

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        player?.stop()
        _playbackState.value = PlaybackUiState()
        _positionMs.value = 0L
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
            preparePlayback(loadedTrack)
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

    fun togglePlayPause() {
        val exoPlayer = player ?: return
        val state = _playbackState.value
        if (!state.hasPreview || state.hasError) return
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_ENDED) {
                exoPlayer.seekTo(0)
            }
            exoPlayer.play()
        }
    }

    fun seekTo(positionMs: Long) {
        val exoPlayer = player ?: return
        val state = _playbackState.value
        if (!state.hasPreview) return
        val target = positionMs.coerceIn(0L, state.durationMs)
        exoPlayer.seekTo(target)
        _positionMs.value = target
    }

    fun pause() {
        player?.pause()
    }

    private fun preparePlayback(currentTrack: Track) {
        val exoPlayer = player
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        val previewUrl = currentTrack.previewUrl
        _playbackState.value = PlaybackUiState(hasPreview = previewUrl != null)
        _positionMs.value = 0L
        if (previewUrl == null) return
        obtainPlayer().apply {
            setMediaItem(
                MediaItem.Builder()
                    .setUri(previewUrl)
                    .setClippingConfiguration(
                        MediaItem.ClippingConfiguration.Builder()
                            .setEndPositionMs(PlaybackUiState.PREVIEW_DURATION_MS)
                            .build()
                    )
                    .build()
            )
            prepare()
        }
    }

    private fun obtainPlayer(): ExoPlayer = player ?: run {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(USER_AGENT)
            .setConnectTimeoutMs(HTTP_TIMEOUT_MS)
            .setReadTimeoutMs(HTTP_TIMEOUT_MS)
            .setAllowCrossProtocolRedirects(true)
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                MIN_BUFFER_MS,
                MAX_BUFFER_MS,
                BUFFER_FOR_PLAYBACK_MS,
                BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
            )
            .build()
        ExoPlayer.Builder(getApplication())
            .setMediaSourceFactory(DefaultMediaSourceFactory(httpDataSourceFactory))
            .setLoadControl(loadControl)
            .build()
            .apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                        .setUsage(C.USAGE_MEDIA)
                        .build(),
                    true
                )
                setHandleAudioBecomingNoisy(true)
                addListener(playerListener)
            }
            .also { player = it }
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (isActive) {
                val exoPlayer = player ?: break
                _positionMs.value = exoPlayer.currentPosition
                delay(PROGRESS_UPDATE_MS)
            }
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

    override fun onCleared() {
        progressJob?.cancel()
        favoriteJob?.cancel()
        player?.release()
        player = null
        super.onCleared()
    }

    private companion object {
        const val TRACK_ID_KEY = "trackId"
        const val STOP_TIMEOUT_MS = 5_000L
        const val PROGRESS_UPDATE_MS = 500L
        const val MIN_BUFFER_MS = 30_000
        const val MAX_BUFFER_MS = 60_000
        const val BUFFER_FOR_PLAYBACK_MS = 1_000
        const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 2_000
        const val HTTP_TIMEOUT_MS = 15_000
        const val USER_AGENT = "MusicApp/1.0"
        const val ERROR_MESSAGE = "Не удалось загрузить трек. Проверьте подключение к интернету"
    }
}
