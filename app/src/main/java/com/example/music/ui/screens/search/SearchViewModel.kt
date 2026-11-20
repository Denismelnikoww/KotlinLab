package com.example.music.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.repository.ITunesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: ITunesRepository = ITunesRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var lastQuery: String = ""

    fun onQueryChange(value: String) {
        lastQuery = value.trim()
        searchJob?.cancel()
        if (lastQuery.length < MIN_QUERY_LENGTH) {
            _uiState.value = SearchUiState.Idle
            return
        }
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            performSearch(lastQuery)
        }
    }

    fun retry() {
        if (lastQuery.length < MIN_QUERY_LENGTH) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch { performSearch(lastQuery) }
    }

    private suspend fun performSearch(term: String) {
        _uiState.value = SearchUiState.Loading
        _uiState.value = repository.searchTracks(term).fold(
            onSuccess = { tracks ->
                if (tracks.isEmpty()) SearchUiState.Empty else SearchUiState.Success(tracks)
            },
            onFailure = { SearchUiState.Error(ERROR_MESSAGE) }
        )
    }

    private companion object {
        const val MIN_QUERY_LENGTH = 2
        const val DEBOUNCE_MS = 500L
        const val ERROR_MESSAGE = "Не удалось загрузить треки. Проверьте подключение к интернету"
    }
}
