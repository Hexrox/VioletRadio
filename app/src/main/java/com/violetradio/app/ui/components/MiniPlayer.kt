package com.violetradio.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.violetradio.app.domain.model.PlaybackState
import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.model.Track

/**
 * Mini player component shown at bottom of screen when playing
 */
@Composable
fun MiniPlayer(
    playbackState: PlaybackState,
    onPlayPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    onPlayerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Only show when playing or paused
    val isVisible = playbackState is PlaybackState.Playing ||
            playbackState is PlaybackState.Paused ||
            playbackState is PlaybackState.Buffering

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Card(
            onClick = onPlayerClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Station logo
                val station = when (playbackState) {
                    is PlaybackState.Playing -> playbackState.station
                    is PlaybackState.Paused -> playbackState.station
                    else -> null
                }

                station?.let {
                    AsyncImage(
                        model = it.favicon,
                        contentDescription = it.name,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 12.dp)
                    )
                }

                // Track info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    when (playbackState) {
                        is PlaybackState.Playing -> {
                            // Show current track or station name
                            playbackState.currentTrack?.let { track ->
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                track.artist?.let { artist ->
                                    Text(
                                        text = artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            } ?: run {
                                Text(
                                    text = playbackState.station.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Na żywo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        is PlaybackState.Paused -> {
                            Text(
                                text = playbackState.station.name,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Wstrzymano",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        is PlaybackState.Buffering -> {
                            Text(
                                text = "Ładowanie...",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                        else -> {}
                    }
                }

                // Loading indicator
                if (playbackState is PlaybackState.Buffering) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(4.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Play/Pause button
                IconButton(onClick = onPlayPauseClick) {
                    Icon(
                        imageVector = when (playbackState) {
                            is PlaybackState.Playing -> Icons.Default.Pause
                            else -> Icons.Default.PlayArrow
                        },
                        contentDescription = if (playbackState is PlaybackState.Playing) "Pauza" else "Odtwórz",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Stop button
                IconButton(onClick = onStopClick) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
