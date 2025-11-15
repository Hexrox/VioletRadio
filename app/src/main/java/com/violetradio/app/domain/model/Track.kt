package com.violetradio.app.domain.model

/**
 * Domain model for a music track (from ICY metadata)
 */
data class Track(
    val artist: String?,
    val title: String,
    val album: String? = null,
    val stationId: String,
    val stationName: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Status of track search on music platforms
 */
data class PlatformSearchResult(
    val spotifyResult: SpotifySearchResult? = null,
    val youtubeResult: YouTubeSearchResult? = null
)

sealed class SpotifySearchResult {
    data class Success(val trackId: String, val uri: String, val name: String) : SpotifySearchResult()
    data object NotFound : SpotifySearchResult()
    data class Error(val message: String) : SpotifySearchResult()
}

sealed class YouTubeSearchResult {
    data class Success(val videoId: String, val title: String) : YouTubeSearchResult()
    data object NotFound : YouTubeSearchResult()
    data class Error(val message: String) : YouTubeSearchResult()
}
