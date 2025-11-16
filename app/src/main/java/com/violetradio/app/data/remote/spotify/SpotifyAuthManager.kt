package com.violetradio.app.data.remote.spotify

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.violetradio.app.data.remote.spotify.models.SpotifyAuthToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Spotify OAuth tokens
 * Uses Client Credentials Flow for server-to-server authentication
 */
@Singleton
class SpotifyAuthManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authService: SpotifyAuthService
) {

    private val Context.spotifyTokenStore: DataStore<Preferences> by preferencesDataStore(
        name = "spotify_token_store"
    )

    private val tokenMutex = Mutex()

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_EXPIRATION_TIME = longPreferencesKey("expiration_time")
    }

    /**
     * Get current access token flow
     */
    val accessTokenFlow: Flow<String?> = context.spotifyTokenStore.data.map { preferences ->
        preferences[KEY_ACCESS_TOKEN]
    }

    /**
     * Get valid access token (refreshes if expired)
     */
    suspend fun getValidAccessToken(): Result<String> = tokenMutex.withLock {
        return try {
            // Check if we have a valid cached token
            val cachedToken = getCachedToken()
            if (cachedToken != null && !isTokenExpired()) {
                Timber.d("Using cached Spotify token")
                return@withLock Result.success(cachedToken)
            }

            // Request new token
            Timber.d("Requesting new Spotify token")
            val response = authService.getAuthToken(
                authorization = SpotifyConfig.getBasicAuthHeader(),
                grantType = "client_credentials"
            )

            // Save token
            saveToken(response)

            Timber.d("Spotify token obtained successfully")
            Result.success(response.accessToken)

        } catch (e: Exception) {
            Timber.e(e, "Failed to get Spotify access token")
            Result.failure(e)
        }
    }

    /**
     * Get Bearer authorization header
     */
    suspend fun getBearerHeader(): Result<String> {
        return getValidAccessToken().map { token ->
            SpotifyConfig.getBearerTokenHeader(token)
        }
    }

    /**
     * Clear stored token
     */
    suspend fun clearToken() {
        context.spotifyTokenStore.edit { preferences ->
            preferences.remove(KEY_ACCESS_TOKEN)
            preferences.remove(KEY_EXPIRATION_TIME)
        }
    }

    /**
     * Get cached token
     */
    private suspend fun getCachedToken(): String? {
        return context.spotifyTokenStore.data.map { preferences ->
            preferences[KEY_ACCESS_TOKEN]
        }.first()
    }

    /**
     * Check if token is expired
     */
    private suspend fun isTokenExpired(): Boolean {
        val expirationTime = context.spotifyTokenStore.data.map { preferences ->
            preferences[KEY_EXPIRATION_TIME] ?: 0L
        }.first()

        return System.currentTimeMillis() >= expirationTime
    }

    /**
     * Save token to DataStore
     */
    private suspend fun saveToken(token: SpotifyAuthToken) {
        context.spotifyTokenStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = token.accessToken
            // Add 1 minute buffer before actual expiration
            val expirationTime = System.currentTimeMillis() + ((token.expiresIn - 60) * 1000)
            preferences[KEY_EXPIRATION_TIME] = expirationTime
        }
    }

    /**
     * Check if Spotify integration is configured
     */
    fun isConfigured(): Boolean {
        return SpotifyConfig.CLIENT_ID != "YOUR_SPOTIFY_CLIENT_ID" &&
                SpotifyConfig.CLIENT_SECRET != "YOUR_SPOTIFY_CLIENT_SECRET"
    }
}
