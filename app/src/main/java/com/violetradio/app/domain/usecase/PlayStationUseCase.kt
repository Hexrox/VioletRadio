package com.violetradio.app.domain.usecase

import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.repository.PlayerRepository
import javax.inject.Inject

/**
 * Use case for playing a radio station
 */
class PlayStationUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(station: Station) {
        playerRepository.playStation(station)
    }
}
