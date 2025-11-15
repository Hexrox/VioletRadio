package com.violetradio.app.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.violetradio.app.data.local.entity.StationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Callback to pre-populate database with featured Polish radio stations
 */
class DatabaseCallback : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.d("Database created - pre-populating with featured Polish stations")
        // Pre-populate will happen via sync from API in HomeViewModel
    }

    /**
     * Featured Polish radio stations for initial population
     * These will be synced from API, but we provide a minimal set here as fallback
     */
    private fun getInitialStations(): List<StationEntity> {
        return listOf(
            StationEntity(
                id = "polskie-radio-1",
                name = "Polskie Radio Program 1 - Jedynka",
                url = "https://stream.open.fm/1",
                homepage = "https://www.polskieradio.pl/7",
                country = "Poland",
                countryCode = "PL",
                language = "polish",
                favicon = "https://www.polskieradio.pl/images/logo.png",
                codec = "MP3",
                bitrate = 128,
                tags = "news,talk,polish",
                votes = 1000,
                clickCount = 0,
                isFavorite = false,
                isFeatured = true,
                lastPlayedAt = null
            ),
            StationEntity(
                id = "radio-357",
                name = "Radio 357",
                url = "https://stream.rcs.revma.com/ypqt40u0x1zuv",
                homepage = "https://radio357.pl",
                country = "Poland",
                countryCode = "PL",
                language = "polish",
                favicon = "https://radio357.pl/favicon.ico",
                codec = "AAC",
                bitrate = 128,
                tags = "talk,news,culture",
                votes = 800,
                clickCount = 0,
                isFavorite = false,
                isFeatured = true,
                lastPlayedAt = null
            ),
            StationEntity(
                id = "rmf-fm",
                name = "RMF FM",
                url = "https://rs9-krk2.rmfstream.pl/RMFFM48",
                homepage = "https://www.rmf.fm",
                country = "Poland",
                countryCode = "PL",
                language = "polish",
                favicon = "https://www.rmf.fm/favicon.ico",
                codec = "AAC+",
                bitrate = 64,
                tags = "pop,hits,polish",
                votes = 900,
                clickCount = 0,
                isFavorite = false,
                isFeatured = true,
                lastPlayedAt = null
            ),
            StationEntity(
                id = "radio-zet",
                name = "Radio ZET",
                url = "https://zet01-03.cdn.eurozet.pl/zet-net.mp3",
                homepage = "https://radiozet.pl",
                country = "Poland",
                countryCode = "PL",
                language = "polish",
                favicon = "https://radiozet.pl/favicon.ico",
                codec = "MP3",
                bitrate = 128,
                tags = "pop,news,talk",
                votes = 850,
                clickCount = 0,
                isFavorite = false,
                isFeatured = true,
                lastPlayedAt = null
            ),
            StationEntity(
                id = "trojka",
                name = "Polskie Radio Program 3 - Trójka",
                url = "https://stream.open.fm/3",
                homepage = "https://www.polskieradio.pl/9",
                country = "Poland",
                countryCode = "PL",
                language = "polish",
                favicon = "https://www.polskieradio.pl/images/logo.png",
                codec = "MP3",
                bitrate = 128,
                tags = "rock,alternative,polish",
                votes = 750,
                clickCount = 0,
                isFavorite = false,
                isFeatured = true,
                lastPlayedAt = null
            )
        )
    }
}
