package com.example.music

import android.app.Application
import com.example.music.di.AppContainer

class MusicApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
