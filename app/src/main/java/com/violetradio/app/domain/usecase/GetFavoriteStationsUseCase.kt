package com.violetradio.app.domain.usecase

import com.violetradio.app.domain.model.Station
import com.violetradio.app.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting favorite radio stations
 */
class GetFavoriteStationsUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    operator fun invoke(): Flow<List<Station>> {
        return stationRepository.getFavoriteStations()
    }
}
