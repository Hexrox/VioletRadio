package com.violetradio.app.domain.repository

import com.violetradio.app.domain.model.PlaybackState
import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.model.Track
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for player operations
 */
interface PlayerRepository {

    /**
     * Get current playback state
     */
    fun getPlaybackState(): Flow<PlaybackState>

    /**
     * Get current station being played
     */
    fun getCurrentStation(): Flow<Station?>

    /**
     * Get current track metadata (from ICY)
     */
    fun getCurrentTrack(): Flow<Track?>

    /**
     * Check if player is playing
     */
    fun isPlaying(): Flow<Boolean>

    /**
     * Check if player is loading/buffering
     */
    fun isLoading(): Flow<Boolean>

    /**
     * Play a station
     */
    suspend fun playStation(station: Station)

    /**
     * Pause playback
     */
    suspend fun pause()

    /**
     * Resume playback
     */
    suspend fun resume()

    /**
     * Stop playback
     */
    suspend fun stop()

    /**
     * Record track to history
     */
    suspend fun recordTrackHistory(track: Track)
}
