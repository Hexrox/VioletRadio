package com.violetradio.app.di

import com.violetradio.app.data.repository.PlayerRepositoryImpl
import com.violetradio.app.data.repository.SpotifyRepository
import com.violetradio.app.data.repository.SpotifyRepositoryImpl
import com.violetradio.app.data.repository.StationRepositoryImpl
import com.violetradio.app.domain.repository.PlayerRepository
import com.violetradio.app.domain.repository.StationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for repository bindings
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStationRepository(
        stationRepositoryImpl: StationRepositoryImpl
    ): StationRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindSpotifyRepository(
        spotifyRepositoryImpl: SpotifyRepositoryImpl
    ): SpotifyRepository
}
