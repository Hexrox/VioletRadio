package com.violetradio.app.data.repository

import com.violetradio.app.data.remote.dto.TrackDto
import com.violetradio.app.domain.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Spotify integration
 * Uses TrackDto from existing data layer
 */
interface SpotifyRepository {

    /**
     * Search for a track on Spotify
     * @param query Search query (artist + title)
     * @param limit Maximum number of results
     * @return Flow of Resource containing list of tracks
     */
    fun searchTrack(query: String, limit: Int = 10): Flow<Resource<List<TrackDto>>>

    /**
     * Get track by Spotify ID
     * @param trackId Spotify track ID
     * @return Flow of Resource containing track details
     */
    fun getTrackById(trackId: String): Flow<Resource<TrackDto>>

    /**
     * Search for track metadata using artist and title
     * Returns the best match
     */
    fun findTrackMetadata(artist: String, title: String): Flow<Resource<TrackDto?>>

    /**
     * Check if Spotify integration is enabled and configured
     */
    fun isSpotifyEnabled(): Boolean
}
