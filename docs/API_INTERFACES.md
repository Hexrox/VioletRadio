# 🌐 Violet Radio - API Interfaces

> Complete Retrofit service interfaces for Radio Browser and Spotify Web API

**Last Updated:** 2025-11-14
**HTTP Client:** Retrofit 2.9.0 + OkHttp 4.12.0
**Serialization:** Gson
**Authentication:** OAuth 2.0 (Spotify), None (Radio Browser)

---

## 📋 Table of Contents

1. [Radio Browser API](#radio-browser-api)
2. [Spotify Web API](#spotify-web-api)
3. [Network Module](#network-module)
4. [Interceptors](#interceptors)
5. [Error Handling](#error-handling)
6. [Testing](#testing)

---

## 📻 Radio Browser API

### Base URL

```
https://de1.api.radio-browser.info/json/
```

**Note:** Use DNS-based load balancing - query `all.api.radio-browser.info` for server list

### DTOs (Data Transfer Objects)

**File:** `data/remote/radiobrowser/dto/StationDto.kt`

```kotlin
package com.violetradio.data.remote.radiobrowser.dto

import com.google.gson.annotations.SerializedName

data class StationDto(
    @SerializedName("stationuuid")
    val stationUuid: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("url_resolved")
    val urlResolved: String? = null,

    @SerializedName("homepage")
    val homepage: String? = null,

    @SerializedName("favicon")
    val favicon: String? = null,

    @SerializedName("tags")
    val tags: String? = null,

    @SerializedName("country")
    val country: String,

    @SerializedName("countrycode")
    val countryCode: String? = null,

    @SerializedName("state")
    val state: String? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("languagecodes")
    val languageCodes: String? = null,

    @SerializedName("votes")
    val votes: Int = 0,

    @SerializedName("codec")
    val codec: String? = null,

    @SerializedName("bitrate")
    val bitrate: Int? = null,

    @SerializedName("clickcount")
    val clickCount: Int = 0,

    @SerializedName("clicktrend")
    val clickTrend: Int = 0,

    @SerializedName("ssl_error")
    val sslError: Int = 0,

    @SerializedName("geo_lat")
    val geoLat: Double? = null,

    @SerializedName("geo_long")
    val geoLong: Double? = null,

    @SerializedName("has_extended_info")
    val hasExtendedInfo: Boolean = false
)
```

**File:** `data/remote/radiobrowser/dto/CountryDto.kt`

```kotlin
package com.violetradio.data.remote.radiobrowser.dto

import com.google.gson.annotations.SerializedName

data class CountryDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("iso_3166_1")
    val iso3166: String,

    @SerializedName("stationcount")
    val stationCount: Int
)
```

### API Interface

**File:** `data/remote/radiobrowser/RadioBrowserApi.kt`

```kotlin
package com.violetradio.data.remote.radiobrowser

import com.violetradio.data.remote.radiobrowser.dto.CountryDto
import com.violetradio.data.remote.radiobrowser.dto.StationDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RadioBrowserApi {

    /**
     * Get all stations
     * @param limit Maximum number of results (default: 100)
     * @param offset Offset for pagination
     * @param order Sort order (name, votes, clickcount, etc.)
     * @param reverse Reverse sort order
     */
    @GET("stations")
    suspend fun getStations(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true
    ): List<StationDto>

    /**
     * Get station by UUID
     */
    @GET("stations/byuuid/{uuid}")
    suspend fun getStationByUuid(
        @Path("uuid") uuid: String
    ): List<StationDto>

    /**
     * Search stations by various criteria
     * @param name Station name (partial match)
     * @param country Country name
     * @param countryCode ISO 3166-1 alpha-2 country code (e.g., "PL")
     * @param state State/region
     * @param language Language name
     * @param tag Tag/genre (e.g., "jazz")
     * @param tagList Comma-separated tags (AND logic)
     * @param codec Audio codec (MP3, AAC, etc.)
     * @param bitrateMin Minimum bitrate in kbps
     * @param bitrateMax Maximum bitrate in kbps
     * @param order Sort order
     * @param reverse Reverse sort order
     * @param limit Maximum results
     * @param offset Pagination offset
     */
    @GET("stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("country") country: String? = null,
        @Query("countrycode") countryCode: String? = null,
        @Query("state") state: String? = null,
        @Query("language") language: String? = null,
        @Query("tag") tag: String? = null,
        @Query("tagList") tagList: String? = null,
        @Query("codec") codec: String? = null,
        @Query("bitrateMin") bitrateMin: Int? = null,
        @Query("bitrateMax") bitrateMax: Int? = null,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<StationDto>

    /**
     * Get stations by country (exact match)
     */
    @GET("stations/bycountrycodeexact/{countryCode}")
    suspend fun getStationsByCountryCode(
        @Path("countryCode") countryCode: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<StationDto>

    /**
     * Get stations by tag (exact match)
     */
    @GET("stations/bytagexact/{tag}")
    suspend fun getStationsByTag(
        @Path("tag") tag: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<StationDto>

    /**
     * Get stations by language (exact match)
     */
    @GET("stations/bylanguageexact/{language}")
    suspend fun getStationsByLanguage(
        @Path("language") language: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<StationDto>

    /**
     * Get list of all countries with station counts
     */
    @GET("countries")
    suspend fun getCountries(
        @Query("order") order: String = "name"
    ): List<CountryDto>

    /**
     * Get list of all tags with station counts
     */
    @GET("tags")
    suspend fun getTags(
        @Query("order") order: String = "stationcount",
        @Query("reverse") reverse: Boolean = true,
        @Query("limit") limit: Int = 100
    ): List<TagDto>

    /**
     * Register station click (for statistics)
     * Should be called when user starts playing a station
     */
    @GET("url/{uuid}")
    suspend fun clickStation(
        @Path("uuid") uuid: String
    ): ClickResponseDto

    /**
     * Vote for a station (increases ranking)
     */
    @GET("vote/{uuid}")
    suspend fun voteStation(
        @Path("uuid") uuid: String
    ): VoteResponseDto
}

// Supporting DTOs
data class TagDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("stationcount")
    val stationCount: Int
)

data class ClickResponseDto(
    @SerializedName("ok")
    val ok: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("stationuuid")
    val stationUuid: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String
)

data class VoteResponseDto(
    @SerializedName("ok")
    val ok: Boolean,

    @SerializedName("message")
    val message: String
)
```

---

## 🎵 Spotify Web API

### Base URL

```
https://api.spotify.com/v1/
```

**Authentication:** OAuth 2.0 with PKCE (Authorization Code Flow)

### DTOs

**File:** `data/remote/spotify/dto/SpotifyDtos.kt`

```kotlin
package com.violetradio.data.remote.spotify.dto

import com.google.gson.annotations.SerializedName

// === User ===

data class SpotifyUserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("display_name")
    val displayName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("product")
    val product: String?, // "premium", "free"

    @SerializedName("images")
    val images: List<ImageDto>?
)

// === Search ===

data class SearchResponseDto(
    @SerializedName("tracks")
    val tracks: TracksDto?
)

data class TracksDto(
    @SerializedName("items")
    val items: List<TrackDto>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int
)

data class TrackDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("uri")
    val uri: String,

    @SerializedName("artists")
    val artists: List<ArtistDto>,

    @SerializedName("album")
    val album: AlbumDto?,

    @SerializedName("duration_ms")
    val durationMs: Int,

    @SerializedName("popularity")
    val popularity: Int,

    @SerializedName("preview_url")
    val previewUrl: String?
)

data class ArtistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("uri")
    val uri: String
)

data class AlbumDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("images")
    val images: List<ImageDto>?
)

data class ImageDto(
    @SerializedName("url")
    val url: String,

    @SerializedName("height")
    val height: Int?,

    @SerializedName("width")
    val width: Int?
)

// === Playlists ===

data class PlaylistsResponseDto(
    @SerializedName("items")
    val items: List<PlaylistDto>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int
)

data class PlaylistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("uri")
    val uri: String,

    @SerializedName("owner")
    val owner: OwnerDto,

    @SerializedName("tracks")
    val tracks: PlaylistTracksDto?,

    @SerializedName("images")
    val images: List<ImageDto>?,

    @SerializedName("public")
    val isPublic: Boolean?
)

data class OwnerDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("display_name")
    val displayName: String?
)

data class PlaylistTracksDto(
    @SerializedName("total")
    val total: Int,

    @SerializedName("items")
    val items: List<PlaylistTrackDto>?
)

data class PlaylistTrackDto(
    @SerializedName("track")
    val track: TrackDto,

    @SerializedName("added_at")
    val addedAt: String
)

// === Requests ===

data class CreatePlaylistRequestDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("public")
    val isPublic: Boolean = false
)

data class AddTracksRequestDto(
    @SerializedName("uris")
    val uris: List<String>
)
```

### API Interface

**File:** `data/remote/spotify/SpotifyApi.kt`

```kotlin
package com.violetradio.data.remote.spotify

import com.violetradio.data.remote.spotify.dto.*
import retrofit2.http.*

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
```

---

## 🔧 Network Module

**File:** `di/NetworkModule.kt`

```kotlin
package com.violetradio.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.violetradio.BuildConfig
import com.violetradio.data.remote.radiobrowser.RadioBrowserApi
import com.violetradio.data.remote.spotify.SpotifyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // === Common ===

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    // === Radio Browser ===

    @Provides
    @Singleton
    @Named("RadioBrowserOkHttp")
    fun provideRadioBrowserOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("User-Agent", "VioletRadio/1.0")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("RadioBrowserRetrofit")
    fun provideRadioBrowserRetrofit(
        @Named("RadioBrowserOkHttp") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://de1.api.radio-browser.info/json/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideRadioBrowserApi(
        @Named("RadioBrowserRetrofit") retrofit: Retrofit
    ): RadioBrowserApi {
        return retrofit.create(RadioBrowserApi::class.java)
    }

    // === Spotify ===

    @Provides
    @Singleton
    @Named("SpotifyOkHttp")
    fun provideSpotifyOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        spotifyAuthInterceptor: SpotifyAuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(spotifyAuthInterceptor) // Auto-refresh tokens
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("SpotifyRetrofit")
    fun provideSpotifyRetrofit(
        @Named("SpotifyOkHttp") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideSpotifyApi(
        @Named("SpotifyRetrofit") retrofit: Retrofit
    ): SpotifyApi {
        return retrofit.create(SpotifyApi::class.java)
    }
}
```

---

## 🔐 Interceptors

### Spotify Auth Interceptor (Token Refresh)

**File:** `data/remote/spotify/SpotifyAuthInterceptor.kt`

```kotlin
package com.violetradio.data.remote.spotify

import com.violetradio.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class SpotifyAuthInterceptor @Inject constructor(
    private val authRepository: AuthRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for non-Spotify requests
        if (!originalRequest.url.host.contains("spotify.com")) {
            return chain.proceed(originalRequest)
        }

        // Get current access token
        val accessToken = runBlocking {
            authRepository.getAccessToken()
        }

        // Add Authorization header if token exists
        val requestWithAuth = if (accessToken != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(requestWithAuth)

        // Handle 401 Unauthorized (token expired)
        if (response.code == 401 && accessToken != null) {
            response.close()

            // Refresh token
            val newAccessToken = runBlocking {
                authRepository.refreshAccessToken()
            }

            if (newAccessToken != null) {
                // Retry with new token
                val retryRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()

                return chain.proceed(retryRequest)
            }
        }

        return response
    }
}
```

### Rate Limit Handler

**File:** `data/remote/spotify/RateLimitInterceptor.kt`

```kotlin
package com.violetradio.data.remote.spotify

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RateLimitInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)

        // Handle 429 Too Many Requests
        var retryCount = 0
        while (response.code == 429 && retryCount < 3) {
            response.close()

            // Get retry-after header (seconds)
            val retryAfter = response.header("Retry-After")?.toLongOrNull() ?: 1L

            // Wait before retrying
            runBlocking {
                delay(retryAfter * 1000)
            }

            // Retry request
            response = chain.proceed(request)
            retryCount++
        }

        return response
    }
}
```

---

## ⚠️ Error Handling

### Network Result Wrapper

**File:** `domain/util/Resource.kt`

```kotlin
package com.violetradio.domain.util

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
```

### API Exception Handling

**File:** `data/util/ApiErrorHandler.kt`

```kotlin
package com.violetradio.data.util

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ApiErrorHandler {

    fun handleException(exception: Throwable): String {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    400 -> "Bad request. Please check your input."
                    401 -> "Unauthorized. Please log in again."
                    403 -> "Forbidden. You don't have permission."
                    404 -> "Not found."
                    429 -> "Too many requests. Please wait a moment."
                    500, 502, 503 -> "Server error. Please try again later."
                    else -> "HTTP error: ${exception.code()}"
                }
            }
            is SocketTimeoutException -> {
                "Connection timeout. Please check your internet."
            }
            is UnknownHostException -> {
                "No internet connection. Please check your network."
            }
            is IOException -> {
                "Network error. Please try again."
            }
            else -> {
                exception.localizedMessage ?: "Unknown error occurred."
            }
        }
    }
}
```

### Repository Usage Example

**File:** `data/repository/SpotifyRepositoryImpl.kt`

```kotlin
package com.violetradio.data.repository

import com.violetradio.data.remote.spotify.SpotifyApi
import com.violetradio.data.util.ApiErrorHandler
import com.violetradio.domain.model.Track
import com.violetradio.domain.repository.SpotifyRepository
import com.violetradio.domain.util.Resource
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi,
    private val authRepository: AuthRepository
) : SpotifyRepository {

    override suspend fun searchTrack(
        artist: String,
        title: String
    ): Resource<Track?> {
        return try {
            val accessToken = authRepository.getAccessToken()
                ?: return Resource.Error("Not authenticated")

            // Build search query with field filters
            val query = "artist:$artist track:$title"

            val response = spotifyApi.search(
                authorization = "Bearer $accessToken",
                query = query,
                type = "track",
                limit = 1
            )

            val track = response.tracks?.items?.firstOrNull()
            if (track != null) {
                Resource.Success(mapper.toDomain(track))
            } else {
                Resource.Error("Track not found")
            }
        } catch (e: Exception) {
            Resource.Error(ApiErrorHandler.handleException(e))
        }
    }

    override suspend fun addTrackToPlaylist(
        playlistId: String,
        trackUri: String
    ): Resource<Unit> {
        return try {
            val accessToken = authRepository.getAccessToken()
                ?: return Resource.Error("Not authenticated")

            spotifyApi.addTracksToPlaylist(
                authorization = "Bearer $accessToken",
                playlistId = playlistId,
                uris = trackUri
            )

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(ApiErrorHandler.handleException(e))
        }
    }
}
```

---

## 🧪 Testing

### Mock API Responses

**File:** `data/remote/radiobrowser/MockRadioBrowserApi.kt`

```kotlin
package com.violetradio.data.remote.radiobrowser

import com.violetradio.data.remote.radiobrowser.dto.StationDto

class MockRadioBrowserApi : RadioBrowserApi {

    private val mockStations = listOf(
        StationDto(
            stationUuid = "test-1",
            name = "Test Radio",
            url = "http://test.url",
            country = "Poland",
            countryCode = "PL",
            tags = "test,mock",
            codec = "MP3",
            bitrate = 128,
            votes = 100
        )
    )

    override suspend fun getStations(
        limit: Int,
        offset: Int,
        order: String,
        reverse: Boolean
    ): List<StationDto> {
        return mockStations
    }

    override suspend fun searchStations(
        name: String?,
        country: String?,
        countryCode: String?,
        state: String?,
        language: String?,
        tag: String?,
        tagList: String?,
        codec: String?,
        bitrateMin: Int?,
        bitrateMax: Int?,
        order: String,
        reverse: Boolean,
        limit: Int,
        offset: Int
    ): List<StationDto> {
        return mockStations.filter { station ->
            (name == null || station.name.contains(name, ignoreCase = true)) &&
            (country == null || station.country.equals(country, ignoreCase = true))
        }
    }

    // Implement other methods as needed for testing
}
```

### Integration Test Example

**File:** `data/remote/radiobrowser/RadioBrowserApiTest.kt`

```kotlin
package com.violetradio.data.remote.radiobrowser

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RadioBrowserApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: RadioBrowserApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RadioBrowserApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `searchStations with country returns filtered results`() = runTest {
        // Given
        val mockResponse = """
            [
                {
                    "stationuuid": "test-1",
                    "name": "Test Radio",
                    "url": "http://test.url",
                    "country": "Poland",
                    "votes": 100
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockResponse)
        )

        // When
        val result = api.searchStations(country = "Poland")

        // Then
        assertEquals(1, result.size)
        assertEquals("Poland", result.first().country)
    }
}
```

---

## 📋 Implementation Checklist

- [ ] Create all DTO classes
- [ ] Implement Radio Browser API interface
- [ ] Implement Spotify API interface
- [ ] Create network module (Hilt)
- [ ] Implement auth interceptor
- [ ] Implement rate limit handler
- [ ] Create error handling utilities
- [ ] Implement repository layer
- [ ] Add API integration tests
- [ ] Test with mock server
- [ ] Handle edge cases (timeout, no network)
- [ ] Implement retry logic
- [ ] Add logging for debugging

---

**Document Version:** 1.0
**Last Updated:** 2025-11-14

🌐 **Violet Radio** - Rock-Solid API Layer!
