package com.violetradio.app.ui.screens.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.violetradio.app.domain.model.PlaybackState
import com.violetradio.app.ui.player.PlayerViewModel
import kotlin.math.sin

/**
 * Full player screen with large controls and waveform animation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    onNavigateBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Odtwarzacz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Wróć")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = playbackState) {
                is PlaybackState.Playing,
                is PlaybackState.Paused,
                is PlaybackState.Buffering -> {
                    val station = when (state) {
                        is PlaybackState.Playing -> state.station
                        is PlaybackState.Paused -> state.station
                        else -> null
                    }

                    val currentTrack = (state as? PlaybackState.Playing)?.currentTrack

                    station?.let {
                        PlayerContent(
                            stationName = it.name,
                            stationCountry = it.country,
                            stationFavicon = it.favicon,
                            trackTitle = currentTrack?.title,
                            trackArtist = currentTrack?.artist,
                            isPlaying = state is PlaybackState.Playing,
                            isBuffering = state is PlaybackState.Buffering,
                            onPlayPauseClick = { viewModel.togglePlayPause() },
                            onStopClick = { viewModel.stop() }
                        )
                    }
                }
                else -> {
                    EmptyPlayerState()
                }
            }
        }
    }
}

@Composable
private fun PlayerContent(
    stationName: String,
    stationCountry: String,
    stationFavicon: String?,
    trackTitle: String?,
    trackArtist: String?,
    isPlaying: Boolean,
    isBuffering: Boolean,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Station logo/artwork
        AsyncImage(
            model = stationFavicon,
            contentDescription = stationName,
            modifier = Modifier
                .size(280.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Station info
        Text(
            text = stationName,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = stationCountry,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Track info (from ICY metadata)
        if (trackTitle != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = trackTitle,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    trackArtist?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Waveform animation
        if (isPlaying && !isBuffering) {
            AnimatedWaveform(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )
        } else if (isBuffering) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Playback controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stop button
            FilledIconButton(
                onClick = onStopClick,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.Stop,
                    contentDescription = "Stop",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Play/Pause button (large)
            FilledTonalButton(
                onClick = onPlayPauseClick,
                modifier = Modifier.size(80.dp),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pauza" else "Odtwórz",
                    modifier = Modifier.size(40.dp)
                )
            }

            // Favorite button (placeholder)
            FilledIconButton(
                onClick = { /* TODO */ },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "Ulubione",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedWaveform(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val centerY = size.height / 2
        val bars = 40
        val barWidth = size.width / bars

        for (i in 0 until bars) {
            val x = i * barWidth
            val amplitude = sin((i * 10 + phase) * Math.PI / 180).toFloat()
            val barHeight = (amplitude * centerY * 0.8f + centerY * 0.2f).coerceAtLeast(4f)

            drawLine(
                color = Color(0xFF6750A4),
                start = Offset(x, centerY - barHeight / 2),
                end = Offset(x, centerY + barHeight / 2),
                strokeWidth = barWidth * 0.6f
            )
        }
    }
}

@Composable
private fun EmptyPlayerState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nie odtwarzasz żadnej stacji",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
