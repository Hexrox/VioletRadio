package com.violetradio.app.data.repository

import com.violetradio.app.data.local.dao.HistoryDao
import com.violetradio.app.data.local.dao.TrackHistoryDao
import com.violetradio.app.data.local.entity.HistoryEntity
import com.violetradio.app.data.mapper.toEntity
import com.violetradio.app.domain.model.PlaybackState
import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.model.Track
import com.violetradio.app.domain.repository.PlayerRepository
import com.violetradio.app.domain.repository.StationRepository
import com.violetradio.app.player.PlayerManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PlayerRepository
 * Coordinates between PlayerManager, StationRepository, and local database
 */
@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val playerManager: PlayerManager,
    private val stationRepository: StationRepository,
    private val historyDao: HistoryDao,
    private val trackHistoryDao: TrackHistoryDao
) : PlayerRepository {

    private var playbackStartTime: Long? = null

    override fun getPlaybackState(): Flow<PlaybackState> {
        return combine(
            playerManager.currentStation,
            playerManager.currentTrack,
            playerManager.isPlaying,
            playerManager.isLoading
        ) { station, track, isPlaying, isLoading ->
            when {
                isLoading && station != null -> PlaybackState.Buffering
                isPlaying && station != null -> PlaybackState.Playing(station, track)
                station != null && !isPlaying -> PlaybackState.Paused(station)
                isLoading -> PlaybackState.Loading
                else -> PlaybackState.Idle
            }
        }
    }

    override fun getCurrentStation(): Flow<Station?> {
        return playerManager.currentStation
    }

    override fun getCurrentTrack(): Flow<Track?> {
        return playerManager.currentTrack
    }

    override fun isPlaying(): Flow<Boolean> {
        return playerManager.isPlaying
    }

    override fun isLoading(): Flow<Boolean> {
        return playerManager.isLoading
    }

    override suspend fun playStation(station: Station) {
        Timber.d("PlayerRepository: Playing station ${station.name}")

        // Update last played timestamp
        stationRepository.updateLastPlayed(station.id)

        // Register click with Radio Browser
        stationRepository.clickStation(station.id)

        // Start playback
        playerManager.playStation(station)

        // Record playback start time
        playbackStartTime = System.currentTimeMillis()

        // Create history entry
        val historyEntry = HistoryEntity(
            stationId = station.id,
            stationName = station.name,
            stationFavicon = station.favicon,
            playedAt = System.currentTimeMillis(),
            duration = 0,
            wasCompleted = false
        )
        historyDao.insertHistory(historyEntry)
    }

    override suspend fun pause() {
        Timber.d("PlayerRepository: Pausing playback")
        playerManager.pause()
    }

    override suspend fun resume() {
        Timber.d("PlayerRepository: Resuming playback")
        playerManager.resume()
    }

    override suspend fun stop() {
        Timber.d("PlayerRepository: Stopping playback")
        playerManager.stop()
        playbackStartTime = null
    }

    override suspend fun recordTrackHistory(track: Track) {
        Timber.d("Recording track history: ${track.artist} - ${track.title}")
        try {
            val trackEntity = track.toEntity()
            trackHistoryDao.insertTrack(trackEntity)
        } catch (e: Exception) {
            Timber.e(e, "Error recording track history")
        }
    }
}
