package com.violetradio.app.domain.model

/**
 * Domain model for a radio station
 */
data class Station(
    val id: String,
    val name: String,
    val url: String,
    val homepage: String? = null,
    val country: String,
    val countryCode: String? = null,
    val state: String? = null,
    val language: String? = null,
    val favicon: String? = null,
    val codec: String? = null,
    val bitrate: Int? = null,
    val tags: List<String> = emptyList(),
    val votes: Int = 0,
    val clickCount: Int = 0,
    val isFavorite: Boolean = false,
    val isFeatured: Boolean = false,
    val lastPlayedAt: Long? = null,
    val addedAt: Long = System.currentTimeMillis()
)
