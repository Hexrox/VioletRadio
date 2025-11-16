package com.violetradio.app.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.violetradio.app.BuildConfig

/**
 * Settings screen
 * TODO: Add theme selection, audio quality, Spotify/YouTube preferences
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ustawienia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Wróć")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // App info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Violet Radio") },
                        supportingContent = { Text("Wersja ${BuildConfig.VERSION_NAME}") },
                        leadingContent = {
                            Icon(Icons.Default.Info, "Informacje")
                        }
                    )
                }
            }

            // Theme setting (placeholder)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Motyw") },
                        supportingContent = { Text("Systemowy") },
                        trailingContent = {
                            Text("TODO", style = MaterialTheme.typography.bodySmall)
                        }
                    )
                }
            }

            // Audio quality (placeholder)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Jakość dźwięku") },
                        supportingContent = { Text("Wysoka") },
                        trailingContent = {
                            Text("TODO", style = MaterialTheme.typography.bodySmall)
                        }
                    )
                }
            }

            // Music platform preference (placeholder)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Platforma muzyczna") },
                        supportingContent = { Text("Spotify → YouTube") },
                        trailingContent = {
                            Text("TODO", style = MaterialTheme.typography.bodySmall)
                        }
                    )
                }
            }

            // Spotify login (placeholder)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text("Spotify") },
                        supportingContent = { Text("Nie połączono") },
                        trailingContent = {
                            TextButton(onClick = { /* TODO: Spotify OAuth */ }) {
                                Text("Połącz")
                            }
                        }
                    )
                }
            }

            // Battery optimization warning
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    ListItem(
                        headlineContent = { Text("Oszczędzanie energii") },
                        supportingContent = {
                            Text("Wyłącz optymalizację baterii dla Violet Radio w ustawieniach Androida, aby odtwarzanie nie było przerywane")
                        }
                    )
                }
            }
        }
    }
}
