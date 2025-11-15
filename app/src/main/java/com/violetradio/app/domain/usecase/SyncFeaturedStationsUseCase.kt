package com.violetradio.app.domain.usecase

import com.violetradio.app.domain.repository.StationRepository
import com.violetradio.app.domain.util.Resource
import timber.log.Timber
import javax.inject.Inject

/**
 * Use case for syncing featured Polish stations from API
 */
class SyncFeaturedStationsUseCase @Inject constructor(
    private val stationRepository: StationRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        Timber.d("Syncing featured Polish stations")
        return stationRepository.syncFeaturedPolishStations()
    }
}
