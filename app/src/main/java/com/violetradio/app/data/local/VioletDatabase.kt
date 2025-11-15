package com.violetradio.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.violetradio.app.data.local.dao.*
import com.violetradio.app.data.local.entity.*

@Database(
    entities = [
        StationEntity::class,
        HistoryEntity::class,
        TrackHistoryEntity::class,
        PlaylistEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class VioletDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao
    abstract fun historyDao(): HistoryDao
    abstract fun trackHistoryDao(): TrackHistoryDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        const val DATABASE_NAME = "violet_radio_db"
    }
}
