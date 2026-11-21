package com.example.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.music.navigation.AppNavigation
import com.example.music.ui.theme.MusicTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val themeRepository = (application as MusicApp).container.themeRepository
        setContent {
            val darkTheme by themeRepository.darkTheme.collectAsStateWithLifecycle(
                initialValue = false
            )
            MusicTheme(darkTheme = darkTheme) {
                AppNavigation()
            }
        }
    }
}
