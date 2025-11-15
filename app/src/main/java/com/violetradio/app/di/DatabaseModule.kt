package com.violetradio.app.di

import android.content.Context
import androidx.room.Room
import com.violetradio.app.data.local.DatabaseCallback
import com.violetradio.app.data.local.VioletDatabase
import com.violetradio.app.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVioletDatabase(
        @ApplicationContext context: Context
    ): VioletDatabase {
        return Room.databaseBuilder(
            context,
            VioletDatabase::class.java,
            VioletDatabase.DATABASE_NAME
        )
        // TODO: Add migrations when schema changes
        // .addMigrations(*Migrations.ALL)
        .addCallback(DatabaseCallback())
        // For development only - remove in production
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideStationDao(database: VioletDatabase): StationDao {
        return database.stationDao()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(database: VioletDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideTrackHistoryDao(database: VioletDatabase): TrackHistoryDao {
        return database.trackHistoryDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(database: VioletDatabase): PlaylistDao {
        return database.playlistDao()
    }
}
