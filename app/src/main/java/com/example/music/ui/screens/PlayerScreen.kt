package com.example.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.music.domain.models.Playlist
import com.example.music.domain.models.Track
import com.example.music.ui.components.BackHeader
import com.example.music.ui.components.CreatePlaylistDialog
import com.example.music.ui.components.TrackArtwork
import com.example.music.ui.components.trackCountText
import com.example.music.ui.screens.player.PlaybackUiState
import com.example.music.ui.screens.player.PlayerUiState
import com.example.music.ui.screens.player.PlayerViewModel
import java.util.Locale

@Composable
fun PlayerScreen(
    navController: NavController,
    viewModel: PlayerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val playback by viewModel.playbackState.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    var showPlaylistSheet by remember { mutableStateOf(false) }
    var showCreateDialog by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                viewModel.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BackHeader("", navController)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (val currentState = state) {
                PlayerUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                is PlayerUiState.Error -> ErrorContent(
                    message = currentState.message,
                    onRetry = viewModel::load
                )
                is PlayerUiState.Success -> PlayerContent(
                    track = currentState.track,
                    playback = playback,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onAddToPlaylist = { showPlaylistSheet = true },
                    onTogglePlayPause = viewModel::togglePlayPause,
                    onSeek = viewModel::seekTo
                )
            }
        }
    }

    if (showPlaylistSheet) {
        AddToPlaylistSheet(
            playlists = playlists,
            onDismiss = { showPlaylistSheet = false },
            onPlaylistClick = { playlistId ->
                viewModel.addTrackToPlaylist(playlistId)
                showPlaylistSheet = false
            },
            onCreatePlaylist = {
                showPlaylistSheet = false
                showCreateDialog = true
            }
        )
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description ->
                viewModel.createPlaylistWithTrack(name, description)
                showCreateDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddToPlaylistSheet(
    playlists: List<Playlist>,
    onDismiss: () -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onCreatePlaylist: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Text(
                "Добавить в плейлист",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = onCreatePlaylist) {
                Text("Создать плейлист")
            }
            if (playlists.isEmpty()) {
                Text("Пока нет плейлистов", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                    items(playlists, key = { it.id }) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlaylistClick(playlist.id) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TrackArtwork(artworkUrl = playlist.artworkUrl, size = 40.dp)
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text(
                                    playlist.name,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    trackCountText(playlist.trackCount),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerContent(
    track: Track,
    playback: PlaybackUiState,
    onToggleFavorite: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        TrackArtwork(
            artworkUrl = track.artworkUrl,
            size = 280.dp,
            cornerRadius = 8.dp,
            iconSize = 100.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            track.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            track.artist,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        PlaybackControls(
            playback = playback,
            onTogglePlayPause = onTogglePlayPause,
            onSeek = onSeek
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onAddToPlaylist) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Добавить в плейлист",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (track.isFavorite) {
                        Icons.Default.Favorite
                    } else {
                        Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (track.isFavorite) {
                        "Убрать из избранного"
                    } else {
                        "Добавить в избранное"
                    },
                    tint = if (track.isFavorite) {
                        Color(0xFFE91E63)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PlaybackControls(
    playback: PlaybackUiState,
    onTogglePlayPause: () -> Unit,
    onSeek: (Long) -> Unit
) {
    if (!playback.hasPreview) {
        Text(
            "Предпрослушивание недоступно",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        return
    }
    if (playback.hasError) {
        Text(
            "Не удалось воспроизвести фрагмент",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        return
    }

    var dragPosition by remember { mutableStateOf<Float?>(null) }
    val duration = playback.durationMs.coerceAtLeast(1L)
    val progress = (dragPosition ?: (playback.positionMs.toFloat() / duration)).coerceIn(0f, 1f)
    val displayedPosition = (progress * duration).toLong()

    Column(modifier = Modifier.fillMaxWidth()) {
        Slider(
            value = progress,
            onValueChange = { dragPosition = it },
            onValueChangeFinished = {
                dragPosition?.let { onSeek((it * duration).toLong()) }
                dragPosition = null
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                formatPlaybackTime(displayedPosition),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Text(
                formatPlaybackTime(playback.durationMs),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier.size(56.dp)
            ) {
                if (playback.isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        imageVector = if (playback.isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = if (playback.isPlaying) "Пауза" else "Воспроизвести",
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

private fun formatPlaybackTime(positionMs: Long): String {
    val totalSeconds = (positionMs / 1000).coerceAtLeast(0)
    return String.format(Locale.US, "%d:%02d", totalSeconds / 60, totalSeconds % 60)
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        TextButton(onClick = onRetry) {
            Text("Повторить")
        }
    }
}
