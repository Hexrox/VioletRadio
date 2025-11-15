package com.violetradio.app.data.local.dao

import androidx.room.*
import com.violetradio.app.data.local.entity.StationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    // === QUERIES ===

    @Query("SELECT * FROM stations ORDER BY name ASC")
    fun getAllStations(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations WHERE id = :stationId")
    suspend fun getStationById(stationId: String): StationEntity?

    @Query("SELECT * FROM stations WHERE id = :stationId")
    fun observeStationById(stationId: String): Flow<StationEntity?>

    @Query("SELECT * FROM stations WHERE isFavorite = 1 ORDER BY lastPlayedAt DESC")
    fun getFavoriteStations(): Flow<List<StationEntity>>

    @Query("""
        SELECT * FROM stations
        WHERE isFeatured = 1 AND country = 'Poland'
        ORDER BY votes DESC, name ASC
        LIMIT :limit
    """)
    fun getFeaturedPolishStations(limit: Int = 10): Flow<List<StationEntity>>

    @Query("""
        SELECT * FROM stations
        WHERE country = :country
        ORDER BY votes DESC, name ASC
    """)
    fun getStationsByCountry(country: String): Flow<List<StationEntity>>

    @Query("""
        SELECT * FROM stations
        WHERE name LIKE '%' || :query || '%'
        OR tags LIKE '%' || :query || '%'
        OR country LIKE '%' || :query || '%'
        ORDER BY
            CASE WHEN name LIKE :query || '%' THEN 1
                 WHEN name LIKE '%' || :query || '%' THEN 2
                 ELSE 3
            END,
            votes DESC
    """)
    fun searchStations(query: String): Flow<List<StationEntity>>

    @Query("""
        SELECT * FROM stations
        WHERE lastPlayedAt IS NOT NULL
        ORDER BY lastPlayedAt DESC
        LIMIT :limit
    """)
    fun getRecentlyPlayed(limit: Int = 20): Flow<List<StationEntity>>

    @Query("""
        SELECT COUNT(*) FROM stations
        WHERE isFavorite = 1
    """)
    fun getFavoriteCount(): Flow<Int>

    // === INSERTS ===

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: StationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<StationEntity>)

    @Transaction
    suspend fun upsertStations(stations: List<StationEntity>) {
        stations.forEach { station ->
            val existing = getStationById(station.id)
            if (existing != null) {
                // Preserve user data
                insertStation(
                    station.copy(
                        isFavorite = existing.isFavorite,
                        lastPlayedAt = existing.lastPlayedAt ?: station.lastPlayedAt
                    )
                )
            } else {
                insertStation(station)
            }
        }
    }

    // === UPDATES ===

    @Update
    suspend fun updateStation(station: StationEntity)

    @Query("UPDATE stations SET isFavorite = :isFavorite WHERE id = :stationId")
    suspend fun updateFavoriteStatus(stationId: String, isFavorite: Boolean)

    @Query("UPDATE stations SET lastPlayedAt = :timestamp WHERE id = :stationId")
    suspend fun updateLastPlayed(stationId: String, timestamp: Long)

    @Query("UPDATE stations SET clickCount = clickCount + 1 WHERE id = :stationId")
    suspend fun incrementClickCount(stationId: String)

    // === DELETES ===

    @Delete
    suspend fun deleteStation(station: StationEntity)

    @Query("DELETE FROM stations WHERE id = :stationId")
    suspend fun deleteStationById(stationId: String)

    @Query("""
        DELETE FROM stations
        WHERE isFavorite = 0
        AND isFeatured = 0
        AND lastPlayedAt IS NULL
    """)
    suspend fun cleanupUnusedStations()

    @Query("DELETE FROM stations")
    suspend fun deleteAllStations()
}
