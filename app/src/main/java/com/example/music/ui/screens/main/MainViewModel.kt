package com.example.music.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.ITunesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ITunesRepository = ITunesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            val sections = SECTIONS
                .map { (title, query) ->
                    async { title to repository.searchTracks(query, limit = SECTION_LIMIT) }
                }
                .awaitAll()
                .mapNotNull { (title, result) ->
                    result.getOrNull()
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { MainSection(title, it) }
                }
            _uiState.value = if (sections.isEmpty()) {
                MainUiState.Error(ERROR_MESSAGE)
            } else {
                MainUiState.Success(sections)
            }
        }
    }

    private companion object {
        val SECTIONS = listOf(
            "Популярное" to "hits",
            "Рок" to "rock",
            "Джаз" to "jazz",
            "Электроника" to "electronic"
        )
        const val SECTION_LIMIT = 10
        const val ERROR_MESSAGE = "Не удалось загрузить подборки. Проверьте подключение к интернету"
    }
}
