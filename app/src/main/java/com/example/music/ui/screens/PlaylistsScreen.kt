package com.example.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.music.data.repository.MusicRepository
import com.example.music.ui.components.BackHeader

@Composable
fun PlaylistsScreen(navController: NavController) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            BackHeader("Плейлисты", navController)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(MusicRepository.allPlaylists) { playlist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("playlist/${playlist.id}") }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(playlist.name, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text(playlist.count, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF3F51B5)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить", tint = Color.White)
        }
    }
}