package com.violetradio.app.data.local.dao

import androidx.room.*
import com.violetradio.app.data.local.entity.TrackHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackHistoryDao {

    @Query("""
        SELECT * FROM track_history
        ORDER BY timestamp DESC
        LIMIT :limit
    """)
    fun getRecentTracks(limit: Int = 100): Flow<List<TrackHistoryEntity>>

    @Query("""
        SELECT * FROM track_history
        WHERE addedToSpotify = 1
        ORDER BY timestamp DESC
    """)
    fun getTracksAddedToSpotify(): Flow<List<TrackHistoryEntity>>

    @Query("""
        SELECT * FROM track_history
        WHERE addedToYoutube = 1
        ORDER BY timestamp DESC
    """)
    fun getTracksAddedToYoutube(): Flow<List<TrackHistoryEntity>>

    @Query("""
        SELECT * FROM track_history
        WHERE stationId = :stationId
        ORDER BY timestamp DESC
        LIMIT :limit
    """)
    fun getTracksForStation(stationId: String, limit: Int = 50): Flow<List<TrackHistoryEntity>>

    @Query("""
        SELECT COUNT(*) FROM track_history
        WHERE addedToSpotify = 1
    """)
    fun getSpotifyTrackCount(): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM track_history
        WHERE addedToYoutube = 1
    """)
    fun getYoutubeTrackCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackHistoryEntity): Long

    @Update
    suspend fun updateTrack(track: TrackHistoryEntity)

    @Query("""
        UPDATE track_history
        SET addedToSpotify = :added,
            spotifyTrackId = :trackId,
            spotifyUri = :uri
        WHERE id = :trackHistoryId
    """)
    suspend fun updateSpotifyStatus(
        trackHistoryId: Long,
        added: Boolean,
        trackId: String?,
        uri: String?
    )

    @Query("""
        UPDATE track_history
        SET addedToYoutube = :added,
            youtubeVideoId = :videoId
        WHERE id = :trackHistoryId
    """)
    suspend fun updateYoutubeStatus(
        trackHistoryId: Long,
        added: Boolean,
        videoId: String?
    )

    @Query("""
        DELETE FROM track_history
        WHERE timestamp < :timestamp
    """)
    suspend fun deleteTracksOlderThan(timestamp: Long)

    @Query("DELETE FROM track_history")
    suspend fun deleteAllTracks()
}
