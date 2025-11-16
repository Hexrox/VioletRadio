package com.violetradio.app.data.remote.spotify

import com.violetradio.app.data.remote.spotify.models.SpotifyAuthToken
import retrofit2.http.*

/**
 * Spotify Auth Service
 * Base URL: https://accounts.spotify.com/
 *
 * Used for obtaining OAuth tokens via Client Credentials Flow
 * Documentation: https://developer.spotify.com/documentation/web-api/tutorials/client-credentials-flow
 */
interface SpotifyAuthService {

    @FormUrlEncoded
    @POST("api/token")
    suspend fun getAuthToken(
        @Header("Authorization") authorization: String, // "Basic <base64(client_id:client_secret)>"
        @Field("grant_type") grantType: String = "client_credentials"
    ): SpotifyAuthToken
}
