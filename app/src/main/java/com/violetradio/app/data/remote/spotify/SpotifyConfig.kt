package com.violetradio.app.data.remote.spotify

/**
 * Spotify API Configuration
 *
 * IMPORTANT: These credentials should be stored securely in production.
 * Consider using:
 * - BuildConfig fields (build.gradle)
 * - local.properties
 * - Environment variables
 * - Remote configuration service
 *
 * To get credentials:
 * 1. Go to: https://developer.spotify.com/dashboard
 * 2. Create a new app
 * 3. Copy Client ID and Client Secret
 */
object SpotifyConfig {

    /**
     * Spotify API Base URLs
     */
    const val API_BASE_URL = "https://api.spotify.com/"
    const val AUTH_BASE_URL = "https://accounts.spotify.com/"

    /**
     * Spotify Client Credentials
     * TODO: Move to BuildConfig or secure storage
     */
    const val CLIENT_ID = "YOUR_SPOTIFY_CLIENT_ID"
    const val CLIENT_SECRET = "YOUR_SPOTIFY_CLIENT_SECRET"

    /**
     * Redirect URI for OAuth (required for user authentication flow)
     * Not needed for Client Credentials Flow
     */
    const val REDIRECT_URI = "violetradio://spotify-callback"

    /**
     * Scopes for user authentication (if using Authorization Code Flow)
     * Not needed for Client Credentials Flow
     */
    val SCOPES = arrayOf(
        "user-read-currently-playing",
        "user-read-playback-state"
    )

    /**
     * Get Basic Auth header for token request
     * Format: "Basic <base64(client_id:client_secret)>"
     */
    fun getBasicAuthHeader(): String {
        val credentials = "$CLIENT_ID:$CLIENT_SECRET"
        val encodedCredentials = android.util.Base64.encodeToString(
            credentials.toByteArray(),
            android.util.Base64.NO_WRAP
        )
        return "Basic $encodedCredentials"
    }

    /**
     * Get Bearer token header for API requests
     */
    fun getBearerTokenHeader(accessToken: String): String {
        return "Bearer $accessToken"
    }
}
