package com.violetradio.app.domain.repository

import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for radio stations
 * Defines contract for data operations on stations
 */
interface StationRepository {

    /**
     * Get all stations from local database
     */
    fun getAllStations(): Flow<List<Station>>

    /**
     * Get a single station by ID
     */
    suspend fun getStationById(stationId: String): Station?

    /**
     * Observe a single station by ID
     */
    fun observeStationById(stationId: String): Flow<Station?>

    /**
     * Get favorite stations
     */
    fun getFavoriteStations(): Flow<List<Station>>

    /**
     * Get featured Polish stations
     */
    fun getFeaturedPolishStations(limit: Int = 10): Flow<List<Station>>

    /**
     * Get recently played stations
     */
    fun getRecentlyPlayed(limit: Int = 20): Flow<List<Station>>

    /**
     * Search stations locally
     */
    fun searchStations(query: String): Flow<List<Station>>

    /**
     * Search stations from API and cache results
     */
    suspend fun searchStationsFromApi(
        country: String? = null,
        tag: String? = null,
        name: String? = null,
        limit: Int = 100
    ): Resource<List<Station>>

    /**
     * Get stations by country from API
     */
    suspend fun getStationsByCountry(countryCode: String): Resource<List<Station>>

    /**
     * Toggle favorite status
     */
    suspend fun toggleFavorite(stationId: String, isFavorite: Boolean)

    /**
     * Update last played timestamp
     */
    suspend fun updateLastPlayed(stationId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Click station (for Radio Browser statistics)
     */
    suspend fun clickStation(stationId: String)

    /**
     * Sync featured Polish stations from API
     */
    suspend fun syncFeaturedPolishStations(): Resource<Unit>

    /**
     * Get favorite count
     */
    fun getFavoriteCount(): Flow<Int>

    /**
     * Delete station
     */
    suspend fun deleteStation(stationId: String)

    /**
     * Cleanup unused stations (not favorite, not featured, not played)
     */
    suspend fun cleanupUnusedStations()
}
