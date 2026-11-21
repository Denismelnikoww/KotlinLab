package com.example.music.data.repository

import com.example.music.domain.models.Track
import com.example.music.data.network.ApiClient
import com.example.music.data.network.ITunesApi
import com.example.music.data.network.mappers.toTrack
import com.example.music.data.network.mappers.toTracks
import kotlinx.coroutines.CancellationException

class ITunesRepository(
    private val api: ITunesApi = ApiClient.iTunesApi
) {

    suspend fun searchTracks(query: String, limit: Int = 30): Result<List<Track>> =
        apiCall { api.search(term = query, entity = "song", limit = limit).toTracks() }

    suspend fun getTrack(trackId: Long): Result<Track?> =
        apiCall { api.lookup(id = trackId).results.firstNotNullOfOrNull { it.toTrack() } }

    private suspend fun <T> apiCall(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
