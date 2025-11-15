package com.violetradio.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.violetradio.app.domain.usecase.GetFavoriteStationsUseCase
import com.violetradio.app.domain.usecase.GetFeaturedStationsUseCase
import com.violetradio.app.domain.usecase.GetRecentlyPlayedStationsUseCase
import com.violetradio.app.domain.usecase.SyncFeaturedStationsUseCase
import com.violetradio.app.domain.usecase.ToggleFavoriteStationUseCase
import com.violetradio.app.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for Home Screen
 * Manages featured stations, favorites, and recently played
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeaturedStationsUseCase: GetFeaturedStationsUseCase,
    private val getFavoriteStationsUseCase: GetFavoriteStationsUseCase,
    private val getRecentlyPlayedStationsUseCase: GetRecentlyPlayedStationsUseCase,
    private val syncFeaturedStationsUseCase: SyncFeaturedStationsUseCase,
    private val toggleFavoriteStationUseCase: ToggleFavoriteStationUseCase
) : ViewModel() {

    private val _isSyncing = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    // Combine all flows into a single UI state
    val uiState = combine(
        getFeaturedStationsUseCase(limit = 10),
        getFavoriteStationsUseCase(),
        getRecentlyPlayedStationsUseCase(limit = 10),
        _isSyncing,
        _errorMessage
    ) { featured, favorites, recent, syncing, error ->
        HomeUiState(
            featuredStations = featured,
            favoriteStations = favorites,
            recentlyPlayed = recent,
            isSyncing = syncing,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    init {
        Timber.d("HomeViewModel initialized")
        // Sync featured stations on first launch
        syncFeaturedStations()
    }

    /**
     * Sync featured Polish stations from API
     */
    fun syncFeaturedStations() {
        viewModelScope.launch {
            _isSyncing.value = true
            _errorMessage.value = null

            Timber.d("Starting sync of featured stations")

            when (val result = syncFeaturedStationsUseCase()) {
                is Resource.Success -> {
                    Timber.d("Successfully synced featured stations")
                    _errorMessage.value = null
                }
                is Resource.Error -> {
                    Timber.e("Error syncing featured stations: ${result.message}")
                    _errorMessage.value = result.message
                }
                is Resource.Loading -> {
                    // Should not happen
                }
            }

            _isSyncing.value = false
        }
    }

    /**
     * Toggle favorite status of a station
     */
    fun toggleFavorite(stationId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            Timber.d("Toggling favorite for station $stationId: $isFavorite")
            try {
                toggleFavoriteStationUseCase(stationId, isFavorite)
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
                _errorMessage.value = "Nie udało się zmienić statusu ulubionej stacji"
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Refresh all data
     */
    fun refresh() {
        syncFeaturedStations()
    }
}
