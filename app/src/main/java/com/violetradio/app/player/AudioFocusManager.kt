package com.violetradio.app.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages audio focus for the radio player
 * CRITICAL: Handles phone calls, notifications, and other audio interruptions
 * - Automatically pauses when phone rings
 * - Resumes after call ends
 * - Handles temporary focus loss (notifications)
 */
@Singleton
class AudioFocusManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playerManager: PlayerManager
) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var hasAudioFocus = false
    private var playbackDelayed = false
    private var resumeOnFocusGain = false

    /**
     * Audio focus change listener
     */
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Timber.d("Audio focus GAINED")
                hasAudioFocus = true

                if (playbackDelayed || resumeOnFocusGain) {
                    // Resume playback after interruption
                    playerManager.resume()
                    playbackDelayed = false
                    resumeOnFocusGain = false
                }

                // Restore normal volume
                playerManager.player.volume = 1.0f
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                Timber.d("Audio focus LOST (permanent) - pausing playback")
                hasAudioFocus = false
                playbackDelayed = false
                resumeOnFocusGain = false

                // Permanent loss - pause playback
                playerManager.pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Timber.d("Audio focus LOST TRANSIENT (temporary) - pausing")
                // Temporary loss (e.g., phone call)
                playbackDelayed = false
                resumeOnFocusGain = true

                // Pause playback
                playerManager.pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Timber.d("Audio focus LOST TRANSIENT CAN DUCK - lowering volume")
                // Temporary loss but can duck (lower volume)
                // e.g., navigation voice, notification
                playerManager.player.volume = 0.3f
            }
        }
    }

    /**
     * Request audio focus
     * Call this before starting playback
     */
    fun requestAudioFocus(): Boolean {
        if (hasAudioFocus) {
            Timber.d("Already has audio focus")
            return true
        }

        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8+
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()

            audioManager.requestAudioFocus(audioFocusRequest!!)
        } else {
            // Android 7 and below
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }

        when (result) {
            AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                Timber.d("Audio focus GRANTED")
                hasAudioFocus = true
                return true
            }
            AudioManager.AUDIOFOCUS_REQUEST_FAILED -> {
                Timber.w("Audio focus REQUEST FAILED")
                hasAudioFocus = false
                return false
            }
            AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
                Timber.d("Audio focus DELAYED (will start when available)")
                playbackDelayed = true
                return false
            }
            else -> {
                Timber.w("Unknown audio focus result: $result")
                return false
            }
        }
    }

    /**
     * Abandon audio focus
     * Call this when stopping playback
     */
    fun abandonAudioFocus() {
        if (!hasAudioFocus) {
            Timber.d("No audio focus to abandon")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                audioManager.abandonAudioFocusRequest(it)
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }

        hasAudioFocus = false
        playbackDelayed = false
        resumeOnFocusGain = false

        Timber.d("Audio focus abandoned")
    }
}
