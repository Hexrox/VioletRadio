package com.violetradio.app.data.repository

import com.violetradio.app.data.local.dao.StationDao
import com.violetradio.app.data.mapper.toDomain
import com.violetradio.app.data.mapper.toEntity
import com.violetradio.app.data.remote.api.RadioBrowserApi
import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.repository.StationRepository
import com.violetradio.app.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of StationRepository
 * Coordinates between local database (Room) and remote API (Radio Browser)
 */
@Singleton
class StationRepositoryImpl @Inject constructor(
    private val stationDao: StationDao,
    private val radioBrowserApi: RadioBrowserApi
) : StationRepository {

    override fun getAllStations(): Flow<List<Station>> {
        return stationDao.getAllStations().map { it.toDomain() }
    }

    override suspend fun getStationById(stationId: String): Station? {
        return stationDao.getStationById(stationId)?.toDomain()
    }

    override fun observeStationById(stationId: String): Flow<Station?> {
        return stationDao.observeStationById(stationId).map { it?.toDomain() }
    }

    override fun getFavoriteStations(): Flow<List<Station>> {
        return stationDao.getFavoriteStations().map { it.toDomain() }
    }

    override fun getFeaturedPolishStations(limit: Int): Flow<List<Station>> {
        return stationDao.getFeaturedPolishStations(limit).map { it.toDomain() }
    }

    override fun getRecentlyPlayed(limit: Int): Flow<List<Station>> {
        return stationDao.getRecentlyPlayed(limit).map { it.toDomain() }
    }

    override fun searchStations(query: String): Flow<List<Station>> {
        return stationDao.searchStations(query).map { it.toDomain() }
    }

    override suspend fun searchStationsFromApi(
        country: String?,
        tag: String?,
        name: String?,
        limit: Int
    ): Resource<List<Station>> {
        return try {
            Timber.d("Searching stations from API: country=$country, tag=$tag, name=$name")

            val response = radioBrowserApi.searchStations(
                country = country,
                tag = tag,
                name = name,
                limit = limit
            )

            // Cache results in database
            val entities = response.toEntity()
            stationDao.upsertStations(entities)

            Timber.d("Found ${response.size} stations from API")
            Resource.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Timber.e(e, "Error searching stations from API")
            Resource.Error("Nie udało się pobrać stacji: ${e.message}", e)
        }
    }

    override suspend fun getStationsByCountry(countryCode: String): Resource<List<Station>> {
        return try {
            Timber.d("Getting stations for country: $countryCode")

            val response = radioBrowserApi.getStationsByCountryCode(countryCode)

            // Cache results
            val entities = response.toEntity()
            stationDao.upsertStations(entities)

            Timber.d("Found ${response.size} stations for $countryCode")
            Resource.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Timber.e(e, "Error getting stations by country")
            Resource.Error("Nie udało się pobrać stacji: ${e.message}", e)
        }
    }

    override suspend fun toggleFavorite(stationId: String, isFavorite: Boolean) {
        Timber.d("Toggling favorite for station $stationId: $isFavorite")
        stationDao.updateFavoriteStatus(stationId, isFavorite)
    }

    override suspend fun updateLastPlayed(stationId: String, timestamp: Long) {
        Timber.d("Updating last played for station $stationId")
        stationDao.updateLastPlayed(stationId, timestamp)
        stationDao.incrementClickCount(stationId)
    }

    override suspend fun clickStation(stationId: String) {
        try {
            Timber.d("Registering click for station $stationId")
            radioBrowserApi.clickStation(stationId)
        } catch (e: Exception) {
            Timber.w(e, "Failed to register click for station (non-critical)")
            // Non-critical, don't throw
        }
    }

    override suspend fun syncFeaturedPolishStations(): Resource<Unit> {
        return try {
            Timber.d("Syncing featured Polish stations from API")

            val response = radioBrowserApi.searchStations(
                countryCode = "PL",
                order = "votes",
                reverse = true,
                limit = 50
            )

            // Mark as featured
            val entities = response.toEntity(isFeatured = true)
            stationDao.upsertStations(entities)

            Timber.d("Synced ${response.size} featured Polish stations")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error syncing featured Polish stations")
            Resource.Error("Nie udało się zsynchronizować stacji: ${e.message}", e)
        }
    }

    override fun getFavoriteCount(): Flow<Int> {
        return stationDao.getFavoriteCount()
    }

    override suspend fun deleteStation(stationId: String) {
        Timber.d("Deleting station $stationId")
        stationDao.deleteStationById(stationId)
    }

    override suspend fun cleanupUnusedStations() {
        Timber.d("Cleaning up unused stations")
        stationDao.cleanupUnusedStations()
    }
}
