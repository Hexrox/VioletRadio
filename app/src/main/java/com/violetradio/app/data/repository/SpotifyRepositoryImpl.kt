package com.violetradio.app.data.repository

import com.violetradio.app.data.remote.api.SpotifyApi
import com.violetradio.app.data.remote.dto.TrackDto
import com.violetradio.app.data.remote.spotify.SpotifyAuthManager
import com.violetradio.app.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SpotifyRepository
 * Uses existing SpotifyApi and TrackDto from data layer
 */
@Singleton
class SpotifyRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApi,
    private val authManager: SpotifyAuthManager
) : SpotifyRepository {

    override fun searchTrack(query: String, limit: Int): Flow<Resource<List<TrackDto>>> = flow {
        emit(Resource.Loading())

        try {
            // Check if Spotify is configured
            if (!authManager.isConfigured()) {
                Timber.w("Spotify integration not configured")
                emit(Resource.Error("Spotify nie jest skonfigurowany"))
                return@flow
            }

            // Get auth token
            val tokenResult = authManager.getBearerHeader()
            if (tokenResult.isFailure) {
                Timber.e("Failed to get Spotify auth token")
                emit(Resource.Error("Błąd autoryzacji Spotify"))
                return@flow
            }

            val authHeader = tokenResult.getOrThrow()

            // Search tracks
            val response = spotifyApi.search(
                authorization = authHeader,
                query = query,
                type = "track",
                limit = limit,
                market = "PL"
            )

            val tracks = response.tracks?.items ?: emptyList()
            Timber.d("Found ${tracks.size} tracks for query: $query")

            emit(Resource.Success(tracks))

        } catch (e: Exception) {
            Timber.e(e, "Error searching Spotify tracks")
            emit(Resource.Error("Błąd wyszukiwania: ${e.localizedMessage}"))
        }
    }

    override fun getTrackById(trackId: String): Flow<Resource<TrackDto>> = flow {
        emit(Resource.Loading())

        try {
            if (!authManager.isConfigured()) {
                emit(Resource.Error("Spotify nie jest skonfigurowany"))
                return@flow
            }

            val tokenResult = authManager.getBearerHeader()
            if (tokenResult.isFailure) {
                emit(Resource.Error("Błąd autoryzacji Spotify"))
                return@flow
            }

            val authHeader = tokenResult.getOrThrow()

            // Use search to get track by ID since we don't have dedicated endpoint yet
            val response = spotifyApi.search(
                authorization = authHeader,
                query = "track:$trackId",
                type = "track",
                limit = 1,
                market = "PL"
            )

            val track = response.tracks?.items?.firstOrNull()
            if (track != null) {
                Timber.d("Retrieved track: ${track.name} by ${track.artists.firstOrNull()?.name}")
                emit(Resource.Success(track))
            } else {
                emit(Resource.Error("Nie znaleziono utworu"))
            }

        } catch (e: Exception) {
            Timber.e(e, "Error getting Spotify track by ID")
            emit(Resource.Error("Błąd pobierania utworu: ${e.localizedMessage}"))
        }
    }

    override fun findTrackMetadata(artist: String, title: String): Flow<Resource<TrackDto?>> = flow {
        emit(Resource.Loading())

        try {
            if (!authManager.isConfigured()) {
                Timber.d("Spotify not configured, skipping metadata fetch")
                emit(Resource.Success(null))
                return@flow
            }

            // Build search query
            val query = buildSearchQuery(artist, title)
            Timber.d("Searching Spotify for: $query")

            val tokenResult = authManager.getBearerHeader()
            if (tokenResult.isFailure) {
                Timber.w("Failed to get Spotify token, skipping metadata")
                emit(Resource.Success(null))
                return@flow
            }

            val authHeader = tokenResult.getOrThrow()
            val response = spotifyApi.search(
                authorization = authHeader,
                query = query,
                type = "track",
                limit = 5,
                market = "PL"
            )

            // Get best match (first result is usually most relevant)
            val bestMatch = response.tracks?.items?.firstOrNull()

            if (bestMatch != null) {
                Timber.d("Found Spotify match: ${bestMatch.name} by ${bestMatch.artists.firstOrNull()?.name}")
            } else {
                Timber.d("No Spotify match found for: $query")
            }

            emit(Resource.Success(bestMatch))

        } catch (e: Exception) {
            Timber.e(e, "Error finding track metadata on Spotify")
            // Don't fail if Spotify search fails - just return null
            emit(Resource.Success(null))
        }
    }

    override fun isSpotifyEnabled(): Boolean {
        return authManager.isConfigured()
    }

    /**
     * Build optimized search query for Spotify
     */
    private fun buildSearchQuery(artist: String, title: String): String {
        // Clean up common patterns in ICY metadata
        val cleanArtist = artist
            .replace(Regex("\\s*-\\s*.*"), "") // Remove everything after dash
            .trim()

        val cleanTitle = title
            .replace(Regex("\\s*\\(.*?\\)"), "") // Remove parentheses
            .replace(Regex("\\s*\\[.*?\\]"), "") // Remove brackets
            .trim()

        return "artist:$cleanArtist track:$cleanTitle"
    }
}
