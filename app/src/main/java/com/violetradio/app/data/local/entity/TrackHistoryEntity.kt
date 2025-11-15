package com.violetradio.app.data.local.entity

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

    // YouTube integration
    val addedToYoutube: Boolean = false,
    val youtubeVideoId: String? = null,

    val timestamp: Long = System.currentTimeMillis()
)
