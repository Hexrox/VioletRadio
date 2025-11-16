package com.violetradio.app.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.MediaStyleNotificationHelper
import com.violetradio.app.MainActivity
import com.violetradio.app.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Background service for radio playback using Media3
 * CRITICAL: Implements foreground service to prevent system kill
 * CRITICAL: Manages WakeLock for playback with screen off
 * CRITICAL: Handles audio focus for phone calls
 */
@AndroidEntryPoint
class RadioPlaybackService : MediaSessionService() {

    @Inject
    lateinit var playerManager: PlayerManager

    @Inject
    lateinit var audioFocusManager: AudioFocusManager

    private var mediaSession: MediaSession? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "violet_radio_playback"
        private const val WAKELOCK_TAG = "VioletRadio::PlayerWakeLock"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("RadioPlaybackService created")

        // Create notification channel (Android 8+)
        createNotificationChannel()

        // Create session activity PendingIntent
        val sessionActivityIntent = Intent(this, MainActivity::class.java)
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create MediaSession
        mediaSession = MediaSession.Builder(this, playerManager.player)
            .setSessionActivity(sessionActivityPendingIntent)
            .setCallback(MediaSessionCallback())
            .build()

        // Add player listener for foreground management
        playerManager.player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Timber.d("Player isPlaying changed: $isPlaying")
                if (isPlaying) {
                    startForegroundService()
                    acquireWakeLock()
                } else {
                    releaseWakeLock()
                    // Don't stop foreground immediately - keep notification visible
                }
            }
        })

        Timber.d("MediaSession created with foreground support")
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Stop playback when app is swiped away
        val session = mediaSession ?: return
        if (!session.player.playWhenReady) {
            // Stop service only if not playing
            stopSelf()
        }
    }

    override fun onDestroy() {
        Timber.d("RadioPlaybackService destroyed")

        releaseWakeLock()
        audioFocusManager.abandonAudioFocus()

        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }

        super.onDestroy()
    }

    /**
     * Start foreground service with notification
     * CRITICAL: Prevents Android from killing the service
     */
    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        Timber.d("Started foreground service")
    }

    /**
     * Create notification channel for Android 8+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Radio Playback",
                NotificationManager.IMPORTANCE_LOW // Low importance = no sound
            ).apply {
                description = "Pokazuje aktualnie odtwarzaną stację radiową"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            Timber.d("Notification channel created")
        }
    }

    /**
     * Create media notification
     */
    private fun createNotification(): Notification {
        val session = mediaSession ?: throw IllegalStateException("MediaSession not initialized")

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Violet Radio")
            .setContentText("Odtwarzanie radia...")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: Create proper icon
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .build()
    }

    /**
     * Acquire partial wake lock to keep CPU running with screen off
     * CRITICAL: Allows playback to continue when screen is off
     */
    private fun acquireWakeLock() {
        if (wakeLock?.isHeld == true) {
            Timber.d("WakeLock already held")
            return
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK, // PARTIAL = CPU only, not screen
            WAKELOCK_TAG
        ).apply {
            setReferenceCounted(false)
            acquire(10 * 60 * 60 * 1000L) // 10 hours max (safety)
        }

        Timber.d("WakeLock acquired - can play with screen off")
    }

    /**
     * Release wake lock when not playing
     */
    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
                Timber.d("WakeLock released")
            }
        }
        wakeLock = null
    }

    /**
     * MediaSession callback for handling media button events
     */
    private inner class MediaSessionCallback : MediaSession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            Timber.d("Controller connected: ${controller.packageName}")
            return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                .build()
        }

        override fun onPlay(session: MediaSession, controller: MediaSession.ControllerInfo) {
            Timber.d("onPlay from notification")
            audioFocusManager.requestAudioFocus()
            super.onPlay(session, controller)
        }

        override fun onPause(session: MediaSession, controller: MediaSession.ControllerInfo) {
            Timber.d("onPause from notification")
            super.onPause(session, controller)
        }

        override fun onStop(session: MediaSession, controller: MediaSession.ControllerInfo) {
            Timber.d("onStop from notification")
            audioFocusManager.abandonAudioFocus()
            super.onStop(session, controller)
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: androidx.media3.session.SessionCommand,
            args: Bundle
        ): com.google.common.util.concurrent.ListenableFuture<androidx.media3.session.SessionResult> {
            Timber.d("Custom command: ${customCommand.customAction}")
            return super.onCustomCommand(session, controller, customCommand, args)
        }
    }
}
