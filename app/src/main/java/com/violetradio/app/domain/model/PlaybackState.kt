package com.violetradio.app.domain.model

/**
 * Current state of the radio player
 */
sealed class PlaybackState {
    data object Idle : PlaybackState()
    data object Loading : PlaybackState()
    data object Buffering : PlaybackState()
    data class Playing(val station: Station, val currentTrack: Track? = null) : PlaybackState()
    data class Paused(val station: Station) : PlaybackState()
    data class Error(val message: String) : PlaybackState()
}
