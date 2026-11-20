package com.example.music.data.network

import com.example.music.data.network.dto.ITunesSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {

    @GET("search")
    suspend fun search(
        @Query("term") term: String,
        @Query("entity") entity: String = "song",
        @Query("limit") limit: Int = 30
    ): ITunesSearchResponse

    @GET("lookup")
    suspend fun lookup(
        @Query("id") id: Long,
        @Query("entity") entity: String = "song"
    ): ITunesSearchResponse
}
