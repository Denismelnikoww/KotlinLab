package com.example.music.di

import android.content.Context
import androidx.room.Room
import com.example.music.data.db.AppDatabase
import com.example.music.data.db.dao.PlaylistsDao
import com.example.music.data.db.dao.TracksDao

class AppContainer(context: Context) {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    val tracksDao: TracksDao by lazy { database.tracksDao() }

    val playlistsDao: PlaylistsDao by lazy { database.playlistsDao() }

    private companion object {
        const val DATABASE_NAME = "music.db"
    }
}
