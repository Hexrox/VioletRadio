package com.violetradio.app.data.mapper

import com.violetradio.app.data.local.entity.TrackHistoryEntity
import com.violetradio.app.domain.model.Track

/**
 * Map TrackHistoryEntity (Room) to Track (Domain)
 */
fun TrackHistoryEntity.toDomain(): Track {
    return Track(
        artist = artist,
        title = title,
        album = album,
        stationId = stationId,
        stationName = stationName,
        timestamp = timestamp
    )
}

/**
 * Map Track (Domain) to TrackHistoryEntity (Room)
 */
fun Track.toEntity(): TrackHistoryEntity {
    return TrackHistoryEntity(
        stationId = stationId,
        stationName = stationName,
        artist = artist,
        title = title,
        album = album,
        timestamp = timestamp
    )
}

/**
 * Map list of TrackHistoryEntity to list of Track
 */
fun List<TrackHistoryEntity>.toDomain(): List<Track> {
    return map { it.toDomain() }
}
