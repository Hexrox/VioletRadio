# 💾 Violet Radio - Database Schema

> Complete Room database schema with entities, DAOs, and relationships

**Last Updated:** 2025-11-14
**Database:** Room (SQLite)
**ORM:** Room Persistence Library 2.7.0+
**Compiler:** KSP (not KAPT)

---

## 📋 Table of Contents

1. [Database Overview](#database-overview)
2. [Entity Definitions](#entity-definitions)
3. [DAO Interfaces](#dao-interfaces)
4. [Type Converters](#type-converters)
5. [Database Class](#database-class)
6. [Migrations](#migrations)
7. [Repository Implementations](#repository-implementations)

---

## 🗄️ Database Overview

### Schema Diagram

```
┌─────────────────┐     ┌──────────────────┐
│   StationEntity │     │  HistoryEntity   │
├─────────────────┤     ├──────────────────┤
│ id (PK)         │◄────│ id (PK)          │
│ name            │     │ stationId (FK)   │
│ url             │     │ playedAt         │
│ country         │     │ duration         │
│ language        │     └──────────────────┘
│ tags            │
│ favicon         │     ┌──────────────────┐
│ codec           │     │  TrackHistory    │
│ bitrate         │     ├──────────────────┤
│ isFavorite      │     │ id (PK)          │
│ lastPlayedAt    │◄────│ stationId (FK)   │
│ addedAt         │     │ artist           │
└─────────────────┘     │ title            │
                        │ addedToSpotify   │
┌─────────────────┐     │ timestamp        │
│  PlaylistEntity │     └──────────────────┘
├─────────────────┤
│ id (PK)         │     ┌──────────────────┐
│ name            │     │ UserPreferences  │
│ description     │     ├──────────────────┤
│ stationIds      │     │ (DataStore)      │
│ createdAt       │     │ - selectedCountry│
│ updatedAt       │     │ - darkMode       │
└─────────────────┘     │ - audioQuality   │
                        │ - spotifyTokens  │
                        └──────────────────┘
```

### Tables Summary

| Table | Purpose | Rows (estimate) |
|-------|---------|-----------------|
| `stations` | Radio station metadata | 100-500 |
| `history` | Playback history | 1000+ |
| `track_history` | Metadata of played tracks | 500+ |
| `playlists` | User-created station playlists | 5-10 |
| `user_preferences` | App settings (DataStore) | N/A |

---

## 📦 Entity Definitions

### 1. StationEntity

**File:** `data/local/entity/StationEntity.kt`

```kotlin
package com.violetradio.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stations",
    indices = [
        Index(value = ["country"]),
        Index(value = ["isFavorite"]),
        Index(value = ["lastPlayedAt"]),
        Index(value = ["name"])
    ]
)
data class StationEntity(
    @PrimaryKey
    val id: String,                    // UUID from Radio Browser or custom

    // Basic Info
    val name: String,                  // Station name
    val url: String,                   // Stream URL
    val homepage: String? = null,      // Official website

    // Geographic
    val country: String,               // Country name (e.g., "Poland")
    val countryCode: String? = null,   // ISO 3166-1 alpha-2 (e.g., "PL")
    val state: String? = null,         // State/Region
    val language: String? = null,      // Primary language

    // Media
    val favicon: String? = null,       // Logo/icon URL
    val codec: String? = null,         // MP3, AAC, OGG, etc.
    val bitrate: Int? = null,          // kbps

    // Categorization
    val tags: String = "",             // Comma-separated (jazz,smooth,etc)

    // Metadata
    val votes: Int = 0,                // Radio Browser votes
    val clickCount: Int = 0,           // Popularity metric

    // User Data
    val isFavorite: Boolean = false,   // Favorited by user
    val isFeatured: Boolean = false,   // Featured Polish station

    // Timestamps
    val lastPlayedAt: Long? = null,    // Last played timestamp
    val addedAt: Long = System.currentTimeMillis()
)
```

### 2. HistoryEntity

**File:** `data/local/entity/HistoryEntity.kt`

```kotlin
package com.violetradio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "history",
    foreignKeys = [
        ForeignKey(
            entity = StationEntity::class,
            parentColumns = ["id"],
            childColumns = ["stationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["stationId"]),
        Index(value = ["playedAt"])
    ]
)
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val stationId: String,             // FK to stations
    val stationName: String,           // Denormalized for performance
    val stationFavicon: String? = null,// Denormalized

    val playedAt: Long,                // Timestamp when started
    val duration: Long = 0,            // Seconds listened

    val wasCompleted: Boolean = false  // User listened > 30s
)
```

### 3. TrackHistoryEntity

**File:** `data/local/entity/TrackHistoryEntity.kt`

```kotlin
package com.violetradio.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "track_history",
    foreignKeys = [
        ForeignKey(
            entity = StationEntity::class,
            parentColumns = ["id"],
            childColumns = ["stationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["stationId"]),
        Index(value = ["timestamp"]),
        Index(value = ["addedToSpotify"])
    ]
)
data class TrackHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val stationId: String,             // FK to stations
    val stationName: String,           // Denormalized

    // Track metadata (from ICY)
    val artist: String? = null,
    val title: String,
    val album: String? = null,

    // Spotify integration
    val addedToSpotify: Boolean = false,
    val spotifyTrackId: String? = null,
    val spotifyUri: String? = null,

    val timestamp: Long = System.currentTimeMillis()
)
```

### 4. PlaylistEntity

**File:** `data/local/entity/PlaylistEntity.kt`

```kotlin
package com.violetradio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val description: String? = null,

    // Comma-separated station IDs
    val stationIds: String = "",       // "id1,id2,id3"

    val iconEmoji: String? = "📻",     // Optional emoji

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

---

## 🔌 DAO Interfaces

### 1. StationDao

**File:** `data/local/dao/StationDao.kt`

```kotlin
package com.violetradio.data.local.dao

import androidx.room.*
import com.violetradio.data.local.entity.StationEntity
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
```

### 2. HistoryDao

**File:** `data/local/dao/HistoryDao.kt`

```kotlin
package com.violetradio.data.local.dao

import androidx.room.*
import com.violetradio.data.local.entity.HistoryEntity
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
```

### 3. TrackHistoryDao

**File:** `data/local/dao/TrackHistoryDao.kt`

```kotlin
package com.violetradio.data.local.dao

import androidx.room.*
import com.violetradio.data.local.entity.TrackHistoryEntity
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
        DELETE FROM track_history
        WHERE timestamp < :timestamp
    """)
    suspend fun deleteTracksOlderThan(timestamp: Long)

    @Query("DELETE FROM track_history")
    suspend fun deleteAllTracks()
}
```

### 4. PlaylistDao

**File:** `data/local/dao/PlaylistDao.kt`

```kotlin
package com.violetradio.data.local.dao

import androidx.room.*
import com.violetradio.data.local.entity.PlaylistEntity
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
```

---

## 🔄 Type Converters

**File:** `data/local/Converters.kt`

```kotlin
package com.violetradio.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Long? {
        return value
    }

    @TypeConverter
    fun toTimestamp(value: Long?): Long? {
        return value
    }
}
```

---

## 🗄️ Database Class

**File:** `data/local/VioletDatabase.kt`

```kotlin
package com.violetradio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.violetradio.data.local.dao.*
import com.violetradio.data.local.entity.*

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
```

---

## 🔄 Migrations

**File:** `data/local/migrations/Migrations.kt`

```kotlin
package com.violetradio.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    // Example migration from version 1 to 2
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add new column to stations table
            database.execSQL(
                "ALTER TABLE stations ADD COLUMN playCount INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    // Example migration from version 2 to 3
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create new table for user ratings
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS station_ratings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    stationId TEXT NOT NULL,
                    rating INTEGER NOT NULL,
                    timestamp INTEGER NOT NULL,
                    FOREIGN KEY(stationId) REFERENCES stations(id) ON DELETE CASCADE
                )
            """)

            // Create index
            database.execSQL(
                "CREATE INDEX IF NOT EXISTS index_station_ratings_stationId ON station_ratings(stationId)"
            )
        }
    }

    // All migrations array for RoomDatabase.Builder
    val ALL = arrayOf(
        MIGRATION_1_2,
        MIGRATION_2_3
    )
}
```

---

## 🏗️ Database Module (Hilt)

**File:** `di/DatabaseModule.kt`

```kotlin
package com.violetradio.di

import android.content.Context
import androidx.room.Room
import com.violetradio.data.local.VioletDatabase
import com.violetradio.data.local.dao.*
import com.violetradio.data.local.migrations.Migrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVioletDatabase(
        @ApplicationContext context: Context
    ): VioletDatabase {
        return Room.databaseBuilder(
            context,
            VioletDatabase::class.java,
            VioletDatabase.DATABASE_NAME
        )
        .addMigrations(*Migrations.ALL)
        .addCallback(DatabaseCallback()) // Pre-populate callback
        // .fallbackToDestructiveMigration() // ONLY for development!
        .build()
    }

    @Provides
    @Singleton
    fun provideStationDao(database: VioletDatabase): StationDao {
        return database.stationDao()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(database: VioletDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideTrackHistoryDao(database: VioletDatabase): TrackHistoryDao {
        return database.trackHistoryDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(database: VioletDatabase): PlaylistDao {
        return database.playlistDao()
    }
}
```

---

## 🌱 Database Pre-population

**File:** `data/local/DatabaseCallback.kt`

```kotlin
package com.violetradio.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.violetradio.data.local.entity.StationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class DatabaseCallback @Inject constructor(
    private val database: Provider<VioletDatabase>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)

        // Pre-populate database with featured Polish stations
        CoroutineScope(Dispatchers.IO).launch {
            val dao = database.get().stationDao()
            val featuredStations = getFeaturedPolishStations()
            dao.insertStations(featuredStations)
        }
    }

    private fun getFeaturedPolishStations(): List<StationEntity> {
        return listOf(
            StationEntity(
                id = "polish_radio_357",
                name = "Radio 357",
                url = "http://stream.rcs.revma.com/4qm72cqmq0hvv",
                country = "Poland",
                countryCode = "PL",
                language = "polish",
                tags = "jazz,smooth",
                favicon = "https://radio357.pl/logo.png",
                codec = "MP3",
                bitrate = 192,
                isFeatured = true,
                votes = 500
            ),
            StationEntity(
                id = "polish_pr1",
                name = "Polskie Radio 1 (Jedynka)",
                url = "http://mp3.polskieradio.pl:8900/",
                country = "Poland",
                countryCode = "PL",
                language = "polish",
                tags = "news,talk,public",
                codec = "MP3",
                bitrate = 96,
                isFeatured = true,
                votes = 450
            ),
            StationEntity(
                id = "polish_pr3",
                name = "Polskie Radio 3 (Trójka)",
                url = "http://mp3.polskieradio.pl:8904/",
                country = "Poland",
                countryCode = "PL",
                language = "polish",
                tags = "rock,alternative,public",
                codec = "MP3",
                bitrate = 96,
                isFeatured = true,
                votes = 480
            ),
            // Add more featured stations from POLISH_STATIONS.md
        )
    }
}
```

---

## 📖 Repository Implementation Example

**File:** `data/repository/StationRepositoryImpl.kt`

```kotlin
package com.violetradio.data.repository

import com.violetradio.data.local.dao.StationDao
import com.violetradio.data.mapper.StationMapper
import com.violetradio.data.remote.RadioBrowserApi
import com.violetradio.domain.model.Station
import com.violetradio.domain.repository.StationRepository
import com.violetradio.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StationRepositoryImpl @Inject constructor(
    private val stationDao: StationDao,
    private val radioBrowserApi: RadioBrowserApi,
    private val mapper: StationMapper
) : StationRepository {

    override fun getFavoriteStations(): Flow<List<Station>> {
        return stationDao.getFavoriteStations()
            .map { entities -> entities.map(mapper::toDomain) }
    }

    override fun getStationsByCountry(country: String): Flow<Resource<List<Station>>> = flow {
        emit(Resource.Loading())

        // 1. Emit cached data first
        val cached = stationDao.getStationsByCountry(country).first()
        if (cached.isNotEmpty()) {
            emit(Resource.Success(cached.map(mapper::toDomain)))
        }

        // 2. Fetch from API
        try {
            val response = radioBrowserApi.searchStations(country = country)
            val entities = response.map(mapper::toEntity)
            stationDao.upsertStations(entities)
            emit(Resource.Success(entities.map(mapper::toDomain)))
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(Resource.Error(e.localizedMessage ?: "Unknown error"))
            }
        }
    }

    override suspend fun toggleFavorite(stationId: String) {
        val station = stationDao.getStationById(stationId)
        station?.let {
            stationDao.updateFavoriteStatus(stationId, !it.isFavorite)
        }
    }

    override suspend fun updateLastPlayed(stationId: String) {
        stationDao.updateLastPlayed(stationId, System.currentTimeMillis())
        stationDao.incrementClickCount(stationId)
    }
}
```

---

## 📝 Testing

**File:** `data/local/dao/StationDaoTest.kt`

```kotlin
package com.violetradio.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.violetradio.data.local.VioletDatabase
import com.violetradio.data.local.entity.StationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StationDaoTest {

    private lateinit var database: VioletDatabase
    private lateinit var stationDao: StationDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VioletDatabase::class.java
        ).build()

        stationDao = database.stationDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertStation_and_getById() = runTest {
        // Given
        val station = StationEntity(
            id = "test-1",
            name = "Test Radio",
            url = "http://test.url",
            country = "Poland",
            tags = "test"
        )

        // When
        stationDao.insertStation(station)
        val retrieved = stationDao.getStationById("test-1")

        // Then
        assertEquals(station.name, retrieved?.name)
    }

    @Test
    fun getFavoriteStations_returnsOnlyFavorites() = runTest {
        // Given
        val favorite = StationEntity(
            id = "fav-1",
            name = "Favorite",
            url = "http://fav.url",
            country = "Poland",
            tags = "test",
            isFavorite = true
        )
        val notFavorite = StationEntity(
            id = "not-fav-1",
            name = "Not Favorite",
            url = "http://notfav.url",
            country = "Poland",
            tags = "test",
            isFavorite = false
        )

        stationDao.insertStations(listOf(favorite, notFavorite))

        // When
        val favorites = stationDao.getFavoriteStations().first()

        // Then
        assertEquals(1, favorites.size)
        assertTrue(favorites.all { it.isFavorite })
    }
}
```

---

## 📋 Implementation Checklist

- [ ] Create all entity classes
- [ ] Create all DAO interfaces
- [ ] Implement type converters
- [ ] Create VioletDatabase class
- [ ] Add database module (Hilt)
- [ ] Implement pre-population callback
- [ ] Create repository implementations
- [ ] Add proper indices for performance
- [ ] Implement migrations (when schema changes)
- [ ] Write DAO unit tests
- [ ] Test database on Android device
- [ ] Verify foreign key constraints
- [ ] Test query performance with large datasets

---

**Document Version:** 1.0
**Last Updated:** 2025-11-14

💾 **Violet Radio** - Solid Database Foundation!
