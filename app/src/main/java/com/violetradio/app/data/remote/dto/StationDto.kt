package com.violetradio.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class StationDto(
    @SerializedName("stationuuid")
    val stationUuid: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String,

    @SerializedName("url_resolved")
    val urlResolved: String? = null,

    @SerializedName("homepage")
    val homepage: String? = null,

    @SerializedName("favicon")
    val favicon: String? = null,

    @SerializedName("tags")
    val tags: String? = null,

    @SerializedName("country")
    val country: String,

    @SerializedName("countrycode")
    val countryCode: String? = null,

    @SerializedName("state")
    val state: String? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("languagecodes")
    val languageCodes: String? = null,

    @SerializedName("votes")
    val votes: Int = 0,

    @SerializedName("codec")
    val codec: String? = null,

    @SerializedName("bitrate")
    val bitrate: Int? = null,

    @SerializedName("clickcount")
    val clickCount: Int = 0,

    @SerializedName("clicktrend")
    val clickTrend: Int = 0,

    @SerializedName("ssl_error")
    val sslError: Int = 0,

    @SerializedName("geo_lat")
    val geoLat: Double? = null,

    @SerializedName("geo_long")
    val geoLong: Double? = null,

    @SerializedName("has_extended_info")
    val hasExtendedInfo: Boolean = false
)

data class CountryDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("iso_3166_1")
    val iso3166: String,

    @SerializedName("stationcount")
    val stationCount: Int
)

data class TagDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("stationcount")
    val stationCount: Int
)

data class ClickResponseDto(
    @SerializedName("ok")
    val ok: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("stationuuid")
    val stationUuid: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("url")
    val url: String
)

data class VoteResponseDto(
    @SerializedName("ok")
    val ok: Boolean,

    @SerializedName("message")
    val message: String
)
