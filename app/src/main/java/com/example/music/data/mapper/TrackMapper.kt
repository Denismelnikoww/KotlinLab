package com.example.music.data.mapper

import com.example.music.data.db.entity.TrackEntity
import com.example.music.domain.models.Track

fun TrackEntity.toDomain(): Track = Track(
    id = id,
    title = trackName,
    artist = artistName,
    duration = formatDuration(trackTimeMillis),
    durationMs = trackTimeMillis,
    artworkUrl = artworkUrl,
    previewUrl = previewUrl,
    albumName = collectionName,
    isFavorite = isFavorite
)

fun Track.toEntity(isFavorite: Boolean = this.isFavorite): TrackEntity = TrackEntity(
    id = id,
    trackName = title,
    artistName = artist,
    trackTimeMillis = durationMs,
    artworkUrl = artworkUrl,
    previewUrl = previewUrl,
    collectionName = albumName,
    isFavorite = isFavorite
)
