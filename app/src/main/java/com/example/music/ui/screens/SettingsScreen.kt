package com.example.music.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.music.ui.components.BackHeader
import com.example.music.ui.components.SettingRowIcon
import com.example.music.ui.components.SettingRowSwitch
import com.example.music.ui.screens.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val darkTheme by viewModel.darkTheme.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        BackHeader("Настройки", navController)
        Spacer(modifier = Modifier.height(16.dp))

        SettingRowSwitch("Тёмная тема", darkTheme, viewModel::setDarkTheme)
        SettingRowIcon("Поделиться приложением", Icons.Default.Share)
        SettingRowIcon("Написать в поддержку", Icons.Default.Email)
        SettingRowIcon("Пользовательское соглашение", Icons.Default.Info)
    }
}
