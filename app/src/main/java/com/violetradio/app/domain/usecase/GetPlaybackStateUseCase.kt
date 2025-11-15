package com.violetradio.app.domain.usecase

import com.violetradio.app.domain.model.PlaybackState
import com.violetradio.app.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing playback state
 */
class GetPlaybackStateUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    operator fun invoke(): Flow<PlaybackState> {
        return playerRepository.getPlaybackState()
    }
}
