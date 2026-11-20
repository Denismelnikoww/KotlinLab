package com.example.music.di

import android.content.Context
import androidx.room.Room
import com.example.music.data.db.AppDatabase
import com.example.music.data.db.dao.PlaylistsDao
import com.example.music.data.db.dao.TracksDao
import com.example.music.data.repository.PlaylistsRepositoryImpl
import com.example.music.data.repository.TracksRepositoryImpl
import com.example.music.domain.repository.PlaylistsRepository
import com.example.music.domain.repository.TracksRepository

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

    val tracksRepository: TracksRepository by lazy {
        TracksRepositoryImpl(tracksDao)
    }

    val playlistsRepository: PlaylistsRepository by lazy {
        PlaylistsRepositoryImpl(playlistsDao, tracksDao)
    }

    private companion object {
        const val DATABASE_NAME = "music.db"
    }
}
