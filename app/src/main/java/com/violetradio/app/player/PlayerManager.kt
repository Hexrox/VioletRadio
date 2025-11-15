package com.violetradio.app.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.metadata.MetadataOutput
import androidx.media3.common.Metadata
import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages ExoPlayer instance and playback state
 * Handles ICY metadata extraction and state updates
 */
@Singleton
class PlayerManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val _currentStation = MutableStateFlow<Station?>(null)
    val currentStation: StateFlow<Station?> = _currentStation.asStateFlow()

    private val _currentTrack = MutableStateFlow<Track?>(null)
    val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ExoPlayer instance
    val player: ExoPlayer by lazy {
        createPlayer()
    }

    private fun createPlayer(): ExoPlayer {
        // Create HTTP data source factory with user agent
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("VioletRadio/1.0")
            .setConnectTimeoutMs(10000)
            .setReadTimeoutMs(10000)

        // Create media source factory
        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(dataSourceFactory)

        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
            .apply {
                // Add player listener
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        Timber.d("Playback state changed: $playbackState")
                        when (playbackState) {
                            Player.STATE_IDLE -> {
                                _isPlaying.value = false
                                _isLoading.value = false
                            }
                            Player.STATE_BUFFERING -> {
                                _isLoading.value = true
                            }
                            Player.STATE_READY -> {
                                _isLoading.value = false
                                _isPlaying.value = playWhenReady
                            }
                            Player.STATE_ENDED -> {
                                _isPlaying.value = false
                                _isLoading.value = false
                            }
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        Timber.d("Is playing changed: $isPlaying")
                        _isPlaying.value = isPlaying
                    }

                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        Timber.e(error, "Player error: ${error.errorCodeName}")
                        _isPlaying.value = false
                        _isLoading.value = false
                    }

                    override fun onMetadata(metadata: Metadata) {
                        // Extract ICY metadata (stream title)
                        for (i in 0 until metadata.length()) {
                            val entry = metadata.get(i)
                            if (entry is androidx.media3.extractor.metadata.icecast.IceCastMetadata) {
                                val streamTitle = entry.title
                                Timber.d("ICY Metadata: $streamTitle")
                                parseAndUpdateTrack(streamTitle)
                            }
                        }
                    }
                })

                // Set playWhenReady to false by default
                playWhenReady = false
            }
    }

    /**
     * Play a radio station
     */
    fun playStation(station: Station) {
        Timber.d("Playing station: ${station.name} - ${station.url}")

        _currentStation.value = station
        _isLoading.value = true

        // Create MediaItem with metadata
        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(station.name)
            .setArtist(station.country)
            .setStation(station.name)
            .setArtworkUri(station.favicon?.let { android.net.Uri.parse(it) })
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(station.url)
            .setMediaMetadata(mediaMetadata)
            .build()

        // Set media item and prepare
        player.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    /**
     * Pause playback
     */
    fun pause() {
        Timber.d("Pausing playback")
        player.playWhenReady = false
    }

    /**
     * Resume playback
     */
    fun resume() {
        Timber.d("Resuming playback")
        player.playWhenReady = true
    }

    /**
     * Stop playback
     */
    fun stop() {
        Timber.d("Stopping playback")
        player.stop()
        _currentStation.value = null
        _currentTrack.value = null
        _isPlaying.value = false
        _isLoading.value = false
    }

    /**
     * Parse ICY stream title and update current track
     * Format: "Artist - Title" or just "Title"
     */
    private fun parseAndUpdateTrack(streamTitle: String?) {
        if (streamTitle.isNullOrBlank()) return

        val station = _currentStation.value ?: return

        // Parse "Artist - Title" format
        val parts = streamTitle.split(" - ", limit = 2)
        val artist = if (parts.size == 2) parts[0].trim() else null
        val title = if (parts.size == 2) parts[1].trim() else streamTitle.trim()

        val track = Track(
            artist = artist,
            title = title,
            stationId = station.id,
            stationName = station.name
        )

        Timber.d("Parsed track: $artist - $title")
        _currentTrack.value = track
    }

    /**
     * Release player resources
     */
    fun release() {
        Timber.d("Releasing player")
        player.release()
    }
}
