package com.violetradio.app.domain.usecase

import com.violetradio.app.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Use case for pausing playback
 */
class PausePlaybackUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke() {
        playerRepository.pause()
    }
}
