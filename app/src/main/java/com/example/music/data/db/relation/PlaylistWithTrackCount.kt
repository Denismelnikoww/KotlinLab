package com.example.music.data.db.relation

import androidx.room.Embedded
import com.example.music.data.db.entity.PlaylistEntity

data class PlaylistWithTrackCount(
    @Embedded val playlist: PlaylistEntity,
    val trackCount: Int
)
