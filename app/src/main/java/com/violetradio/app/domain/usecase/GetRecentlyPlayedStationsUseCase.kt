package com.violetradio.app.domain.usecase

import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting recently played stations
 */
class GetRecentlyPlayedStationsUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    operator fun invoke(limit: Int = 20): Flow<List<Station>> {
        return stationRepository.getRecentlyPlayed(limit)
    }
}
