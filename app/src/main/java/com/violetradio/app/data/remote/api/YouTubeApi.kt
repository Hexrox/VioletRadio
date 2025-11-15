package com.violetradio.app.data.remote.api

import com.violetradio.app.data.remote.dto.YouTubeSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * YouTube Data API v3 interface
 * Base URL: https://www.googleapis.com/youtube/v3/
 * Authentication: API Key
 */
interface YouTubeApi {

    /**
     * Search for YouTube videos
     * @param key YouTube Data API key
     * @param query Search query (artist + title)
     * @param type Resource type (default: "video")
     * @param maxResults Maximum number of results (1-50)
     * @param videoCategoryId Category filter ("10" = Music)
     */
    @GET("search")
    suspend fun search(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: Int = 1,
        @Query("videoCategoryId") videoCategoryId: String? = "10" // Music category
    ): YouTubeSearchResponseDto
}
