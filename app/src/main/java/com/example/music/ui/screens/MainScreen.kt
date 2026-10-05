// ui/screens/MainScreen.kt
package com.example.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.music.ui.components.MenuItem

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3F51B5))
                .padding(24.dp)
        ) {
            Text(
                "Playlist Maker",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        MenuItem("Поиск", Icons.Default.Search) { navController.navigate("search") }
        MenuItem("Плейлисты", Icons.Default.List) { navController.navigate("playlists") }
        MenuItem("Избранное", Icons.Default.FavoriteBorder) { navController.navigate("favorites") }
        MenuItem("Настройки", Icons.Default.Settings) { navController.navigate("settings") }
    }
}