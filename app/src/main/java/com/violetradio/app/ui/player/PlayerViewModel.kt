package com.violetradio.app.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.violetradio.app.domain.model.PlaybackState
import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.usecase.GetPlaybackStateUseCase
import com.violetradio.app.domain.usecase.PausePlaybackUseCase
import com.violetradio.app.domain.usecase.PlayStationUseCase
import com.violetradio.app.domain.usecase.ResumePlaybackUseCase
import com.violetradio.app.domain.usecase.StopPlaybackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Player functionality
 * Manages playback controls and state
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playStationUseCase: PlayStationUseCase,
    private val pausePlaybackUseCase: PausePlaybackUseCase,
    private val resumePlaybackUseCase: ResumePlaybackUseCase,
    private val stopPlaybackUseCase: StopPlaybackUseCase,
    getPlaybackStateUseCase: GetPlaybackStateUseCase
) : ViewModel() {

    // Observe playback state
    val playbackState = getPlaybackStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlaybackState.Idle
        )

    init {
        Timber.d("PlayerViewModel initialized")
    }

    /**
     * Play a station
     */
    fun playStation(station: Station) {
        viewModelScope.launch {
            Timber.d("PlayerViewModel: Playing station ${station.name}")
            try {
                playStationUseCase(station)
            } catch (e: Exception) {
                Timber.e(e, "Error playing station")
            }
        }
    }

    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        viewModelScope.launch {
            when (val state = playbackState.value) {
                is PlaybackState.Playing -> {
                    Timber.d("PlayerViewModel: Pausing")
                    pausePlaybackUseCase()
                }
                is PlaybackState.Paused -> {
                    Timber.d("PlayerViewModel: Resuming")
                    resumePlaybackUseCase()
                }
                else -> {
                    Timber.d("PlayerViewModel: Cannot toggle play/pause in state $state")
                }
            }
        }
    }

    /**
     * Stop playback
     */
    fun stop() {
        viewModelScope.launch {
            Timber.d("PlayerViewModel: Stopping")
            try {
                stopPlaybackUseCase()
            } catch (e: Exception) {
                Timber.e(e, "Error stopping playback")
            }
        }
    }
}
