package com.violetradio.app.domain.usecase

import com.violetradio.app.domain.repository.StationRepository
import javax.inject.Inject

/**
 * Use case for toggling favorite status of a station
 */
class ToggleFavoriteStationUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    suspend operator fun invoke(stationId: String, isFavorite: Boolean) {
        stationRepository.toggleFavorite(stationId, isFavorite)
    }
}
