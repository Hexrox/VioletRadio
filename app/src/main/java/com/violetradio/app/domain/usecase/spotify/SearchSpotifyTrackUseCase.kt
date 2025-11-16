package com.violetradio.app.domain.usecase.spotify

import com.violetradio.app.data.remote.dto.TrackDto
import com.violetradio.app.data.repository.SpotifyRepository
import com.violetradio.app.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching Spotify tracks
 */
class SearchSpotifyTrackUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {

    /**
     * Search for tracks on Spotify
     */
    operator fun invoke(query: String, limit: Int = 10): Flow<Resource<List<TrackDto>>> {
        require(query.isNotBlank()) { "Search query cannot be blank" }
        return spotifyRepository.searchTrack(query, limit)
    }
}
