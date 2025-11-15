package com.violetradio.app.ui.screens.home

import com.violetradio.app.domain.model.Station

/**
 * UI State for Home Screen
 */
data class HomeUiState(
    val featuredStations: List<Station> = emptyList(),
    val favoriteStations: List<Station> = emptyList(),
    val recentlyPlayed: List<Station> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null
)
