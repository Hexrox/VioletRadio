package com.violetradio.app.domain.usecase.spotify

import com.violetradio.app.data.remote.dto.TrackDto
import com.violetradio.app.data.repository.SpotifyRepository
import com.violetradio.app.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for finding track metadata on Spotify
 * Used to enrich ICY metadata with Spotify data (album art, preview, etc.)
 */
class FindTrackMetadataUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {

    /**
     * Find track metadata using artist and title from ICY stream
     */
    operator fun invoke(artist: String, title: String): Flow<Resource<TrackDto?>> {
        return spotifyRepository.findTrackMetadata(artist, title)
    }
}
