package com.violetradio.app.data.local.dao

import androidx.room.*
import com.violetradio.app.data.local.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlists ORDER BY updatedAt DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun observePlaylistById(playlistId: Long): Flow<PlaylistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("""
        UPDATE playlists
        SET stationIds = :stationIds,
            updatedAt = :timestamp
        WHERE id = :playlistId
    """)
    suspend fun updateStationIds(playlistId: Long, stationIds: String, timestamp: Long)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Long)
}
