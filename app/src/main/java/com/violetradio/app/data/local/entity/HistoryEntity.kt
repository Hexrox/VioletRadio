package com.violetradio.app.data.local.entity

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
