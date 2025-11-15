package com.violetradio.app.data.local.entity

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
