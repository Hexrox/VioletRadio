package com.violetradio.app.data.local.entity

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
