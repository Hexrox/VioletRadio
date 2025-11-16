package com.violetradio.app.data.remote.spotify.models

import com.google.gson.annotations.SerializedName

/**
 * Spotify OAuth Token Response
 * Client Credentials Flow: https://developer.spotify.com/documentation/web-api/tutorials/client-credentials-flow
 */
data class SpotifyAuthToken(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("expires_in")
    val expiresIn: Long, // seconds

    @SerializedName("scope")
    val scope: String? = null
) {
    /**
     * Calculate token expiration timestamp
     */
    fun getExpirationTimestamp(): Long {
        return System.currentTimeMillis() + (expiresIn * 1000)
    }

    /**
     * Check if token is expired
     */
    fun isExpired(): Boolean {
        return System.currentTimeMillis() >= getExpirationTimestamp()
    }
}
