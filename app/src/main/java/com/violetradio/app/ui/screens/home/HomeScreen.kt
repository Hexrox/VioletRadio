package com.violetradio.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Main home screen of the app
 * Shows featured stations, favorites, and recently played
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Violet Radio") },
                actions = {
                    IconButton(onClick = { /* TODO: Navigate to search */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Szukaj"
                        )
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "💜 Witaj w Violet Radio",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Text(
                    text = "Projekt gotowy do implementacji!\n\nStruktura projektu została utworzona z:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✓ Room Database (4 encje, 4 DAO)", style = MaterialTheme.typography.bodyMedium)
                        Text("✓ Retrofit API (Radio Browser, Spotify, YouTube)", style = MaterialTheme.typography.bodyMedium)
                        Text("✓ Material 3 Theme (jasny/ciemny)", style = MaterialTheme.typography.bodyMedium)
                        Text("✓ Hilt Dependency Injection", style = MaterialTheme.typography.bodyMedium)
                        Text("✓ Clean Architecture (UI/Domain/Data)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
