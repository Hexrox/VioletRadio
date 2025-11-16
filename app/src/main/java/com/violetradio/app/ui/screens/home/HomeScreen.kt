package com.violetradio.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.violetradio.app.ui.components.MiniPlayer
import com.violetradio.app.ui.components.StationCard
import com.violetradio.app.ui.player.PlayerViewModel

/**
 * Main home screen of the app
 * Shows featured stations, favorites, and recently played
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPlayer: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackState by playerViewModel.playbackState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Violet Radio 💜") },
                actions = {
                    if (uiState.isSyncing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(4.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Odśwież"
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Szukaj"
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Mini Player at bottom
            MiniPlayer(
                playbackState = playbackState,
                onPlayPauseClick = { playerViewModel.togglePlayPause() },
                onStopClick = { playerViewModel.stop() },
                onPlayerClick = onNavigateToPlayer,
                modifier = Modifier.padding(8.dp)
            )
        },
        snackbarHost = {
            // Show error snackbar if there's an error
            uiState.errorMessage?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome header
            item {
                Text(
                    text = "Twoje fale, twój świat",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Featured Polish Stations
            if (uiState.featuredStations.isNotEmpty()) {
                item {
                    Text(
                        text = "Polecane stacje",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.featuredStations) { station ->
                    StationCard(
                        station = station,
                        onStationClick = { playerViewModel.playStation(station) },
                        onFavoriteClick = {
                            viewModel.toggleFavorite(station.id, !station.isFavorite)
                        },
                        onMenuClick = { /* TODO: Show menu */ }
                    )
                }
            }

            // Favorite Stations
            if (uiState.favoriteStations.isNotEmpty()) {
                item {
                    Text(
                        text = "Ulubione",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(uiState.favoriteStations) { station ->
                            Card(
                                onClick = { playerViewModel.playStation(station) },
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(160.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = station.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = station.country,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recently Played
            if (uiState.recentlyPlayed.isNotEmpty()) {
                item {
                    Text(
                        text = "Ostatnio słuchane",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.recentlyPlayed) { station ->
                    StationCard(
                        station = station,
                        onStationClick = { playerViewModel.playStation(station) },
                        onFavoriteClick = {
                            viewModel.toggleFavorite(station.id, !station.isFavorite)
                        },
                        onMenuClick = { /* TODO: Show menu */ }
                    )
                }
            }

            // Empty state
            if (uiState.featuredStations.isEmpty() &&
                uiState.favoriteStations.isEmpty() &&
                uiState.recentlyPlayed.isEmpty() &&
                !uiState.isSyncing
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "📻",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Brak stacji",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Naciśnij odśwież aby pobrać stacje z internetu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Odśwież")
                        }
                    }
                }
            }
        }
    }
}
