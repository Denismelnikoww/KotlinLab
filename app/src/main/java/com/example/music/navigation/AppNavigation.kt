package com.example.music.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.music.ui.screens.FavoritesScreen
import com.example.music.ui.screens.MainScreen
import com.example.music.ui.screens.PlayerScreen
import com.example.music.ui.screens.PlaylistDetailScreen
import com.example.music.ui.screens.PlaylistsScreen
import com.example.music.ui.screens.SearchScreen
import com.example.music.ui.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("search") { SearchScreen(navController) }
        composable("playlists") { PlaylistsScreen(navController) }
        composable("playlist/{playlistId}") { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")?.toIntOrNull() ?: 0
            PlaylistDetailScreen(navController, playlistId)
        }
        composable("favorites") { FavoritesScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
        composable("player") { PlayerScreen(navController) }
    }
}