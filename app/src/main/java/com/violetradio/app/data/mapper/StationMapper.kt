package com.violetradio.app.data.mapper

import com.violetradio.app.data.local.entity.StationEntity
import com.violetradio.app.data.remote.dto.StationDto
import com.violetradio.app.domain.model.Station

/**
 * Map StationEntity (Room) to Station (Domain)
 */
fun StationEntity.toDomain(): Station {
    return Station(
        id = id,
        name = name,
        url = url,
        homepage = homepage,
        country = country,
        countryCode = countryCode,
        state = state,
        language = language,
        favicon = favicon,
        codec = codec,
        bitrate = bitrate,
        tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        votes = votes,
        clickCount = clickCount,
        isFavorite = isFavorite,
        isFeatured = isFeatured,
        lastPlayedAt = lastPlayedAt,
        addedAt = addedAt
    )
}

/**
 * Map Station (Domain) to StationEntity (Room)
 */
fun Station.toEntity(): StationEntity {
    return StationEntity(
        id = id,
        name = name,
        url = url,
        homepage = homepage,
        country = country,
        countryCode = countryCode,
        state = state,
        language = language,
        favicon = favicon,
        codec = codec,
        bitrate = bitrate,
        tags = tags.joinToString(","),
        votes = votes,
        clickCount = clickCount,
        isFavorite = isFavorite,
        isFeatured = isFeatured,
        lastPlayedAt = lastPlayedAt,
        addedAt = addedAt
    )
}

/**
 * Map StationDto (API) to StationEntity (Room)
 */
fun StationDto.toEntity(isFeatured: Boolean = false): StationEntity {
    return StationEntity(
        id = stationUuid,
        name = name,
        url = urlResolved ?: url,
        homepage = homepage,
        country = country,
        countryCode = countryCode,
        state = state,
        language = language,
        favicon = favicon,
        codec = codec,
        bitrate = bitrate,
        tags = tags ?: "",
        votes = votes,
        clickCount = clickCount,
        isFavorite = false,
        isFeatured = isFeatured,
        lastPlayedAt = null,
        addedAt = System.currentTimeMillis()
    )
}

/**
 * Map StationDto (API) to Station (Domain)
 */
fun StationDto.toDomain(): Station {
    return Station(
        id = stationUuid,
        name = name,
        url = urlResolved ?: url,
        homepage = homepage,
        country = country,
        countryCode = countryCode,
        state = state,
        language = language,
        favicon = favicon,
        codec = codec,
        bitrate = bitrate,
        tags = tags?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList(),
        votes = votes,
        clickCount = clickCount,
        isFavorite = false,
        isFeatured = false,
        lastPlayedAt = null,
        addedAt = System.currentTimeMillis()
    )
}

/**
 * Map list of StationEntity to list of Station
 */
fun List<StationEntity>.toDomain(): List<Station> {
    return map { it.toDomain() }
}

/**
 * Map list of StationDto to list of StationEntity
 */
fun List<StationDto>.toEntity(isFeatured: Boolean = false): List<StationEntity> {
    return map { it.toEntity(isFeatured) }
}
