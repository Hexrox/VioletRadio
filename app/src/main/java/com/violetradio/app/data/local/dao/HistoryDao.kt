package com.violetradio.app.data.local.dao

import androidx.room.*
import com.violetradio.app.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("""
        SELECT * FROM history
        ORDER BY playedAt DESC
        LIMIT :limit
    """)
    fun getRecentHistory(limit: Int = 50): Flow<List<HistoryEntity>>

    @Query("""
        SELECT * FROM history
        WHERE stationId = :stationId
        ORDER BY playedAt DESC
        LIMIT :limit
    """)
    fun getHistoryForStation(stationId: String, limit: Int = 20): Flow<List<HistoryEntity>>

    @Query("""
        SELECT SUM(duration) FROM history
        WHERE stationId = :stationId
    """)
    suspend fun getTotalListeningTime(stationId: String): Long?

    @Query("""
        SELECT SUM(duration) FROM history
    """)
    fun getTotalListeningTimeAll(): Flow<Long?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Update
    suspend fun updateHistory(history: HistoryEntity)

    @Query("""
        DELETE FROM history
        WHERE playedAt < :timestamp
    """)
    suspend fun deleteHistoryOlderThan(timestamp: Long)

    @Query("DELETE FROM history")
    suspend fun deleteAllHistory()
}
