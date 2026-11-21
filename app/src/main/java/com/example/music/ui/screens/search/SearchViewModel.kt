package com.example.music.ui.screens.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.MusicApp
import com.example.music.data.repository.ITunesRepository
import com.example.music.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = ITunesRepository()

    private val historyRepository: SearchHistoryRepository =
        (application as MusicApp).container.searchHistoryRepository

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<String>> = historyRepository.history
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), emptyList())

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

    fun onQuerySelected(query: String) {
        lastQuery = query.trim()
        onQueryChange(query)
        viewModelScope.launch { historyRepository.addQuery(query) }
    }

    fun retry() {
        if (lastQuery.length < MIN_QUERY_LENGTH) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch { performSearch(lastQuery) }
    }

    fun clearHistory() {
        viewModelScope.launch { historyRepository.clear() }
    }

    private suspend fun performSearch(term: String) {
        _uiState.value = SearchUiState.Loading
        _uiState.value = repository.searchTracks(term).fold(
            onSuccess = { tracks ->
                if (tracks.isEmpty()) {
                    SearchUiState.Empty
                } else {
                    historyRepository.addQuery(term)
                    SearchUiState.Success(tracks)
                }
            },
            onFailure = { SearchUiState.Error(ERROR_MESSAGE) }
        )
    }

    private companion object {
        const val MIN_QUERY_LENGTH = 2
        const val DEBOUNCE_MS = 500L
        const val STOP_TIMEOUT_MS = 5_000L
        const val ERROR_MESSAGE = "Не удалось загрузить треки. Проверьте подключение к интернету"
    }
}
