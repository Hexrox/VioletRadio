package com.violetradio.app.data.remote.api

import com.violetradio.app.data.remote.dto.*
import retrofit2.http.*

/**
 * Spotify Web API interface
 * Base URL: https://api.spotify.com/v1/
 * Authentication: OAuth 2.0 with PKCE (Authorization Code Flow)
 */
interface SpotifyApi {

    // === User Profile ===

    /**
     * Get current user's profile
     */
    @GET("me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String // "Bearer {token}"
    ): SpotifyUserDto

    // === Search ===

    /**
     * Search for tracks, artists, albums, etc.
     * @param query Search query (supports field filters: artist:name track:title)
     * @param type Comma-separated list: track, artist, album, playlist
     * @param limit Number of results (1-50, default 20)
     * @param offset Pagination offset
     */
    @GET("search")
    suspend fun search(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("market") market: String? = null
    ): SearchResponseDto

    // === Playlists ===

    /**
     * Get current user's playlists
     */
    @GET("me/playlists")
    suspend fun getUserPlaylists(
        @Header("Authorization") authorization: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): PlaylistsResponseDto

    /**
     * Get playlist details
     */
    @GET("playlists/{playlist_id}")
    suspend fun getPlaylist(
        @Header("Authorization") authorization: String,
        @Path("playlist_id") playlistId: String,
        @Query("fields") fields: String? = null
    ): PlaylistDto

    /**
     * Get playlist tracks
     */
    @GET("playlists/{playlist_id}/tracks")
    suspend fun getPlaylistTracks(
        @Header("Authorization") authorization: String,
        @Path("playlist_id") playlistId: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): PlaylistTracksDto

    /**
     * Create a new playlist for user
     */
    @POST("users/{user_id}/playlists")
    suspend fun createPlaylist(
        @Header("Authorization") authorization: String,
        @Path("user_id") userId: String,
        @Body request: CreatePlaylistRequestDto
    ): PlaylistDto

    /**
     * Add tracks to playlist
     * @param uris Comma-separated Spotify URIs (e.g., "spotify:track:xxx,spotify:track:yyy")
     */
    @POST("playlists/{playlist_id}/tracks")
    suspend fun addTracksToPlaylist(
        @Header("Authorization") authorization: String,
        @Path("playlist_id") playlistId: String,
        @Query("uris") uris: String
    )

    /**
     * Add tracks to playlist (body version for large lists)
     */
    @POST("playlists/{playlist_id}/tracks")
    suspend fun addTracksToPlaylistBody(
        @Header("Authorization") authorization: String,
        @Path("playlist_id") playlistId: String,
        @Body request: AddTracksRequestDto
    )

    /**
     * Check if tracks are in user's library
     */
    @GET("me/tracks/contains")
    suspend fun checkSavedTracks(
        @Header("Authorization") authorization: String,
        @Query("ids") ids: String // Comma-separated track IDs
    ): List<Boolean>
}
