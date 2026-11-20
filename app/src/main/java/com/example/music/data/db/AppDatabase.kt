package com.example.music.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.music.data.db.dao.PlaylistsDao
import com.example.music.data.db.dao.TracksDao
import com.example.music.data.db.entity.PlaylistEntity
import com.example.music.data.db.entity.PlaylistTrackCrossRef
import com.example.music.data.db.entity.TrackEntity

@Database(
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tracksDao(): TracksDao

    abstract fun playlistsDao(): PlaylistsDao
}
