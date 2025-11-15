package com.violetradio.app.domain.usecase

import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting featured Polish radio stations
 */
class GetFeaturedStationsUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    operator fun invoke(limit: Int = 10): Flow<List<Station>> {
        return stationRepository.getFeaturedPolishStations(limit)
    }
}
