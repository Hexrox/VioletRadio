package com.violetradio.app.data.remote.api

import com.violetradio.app.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Radio Browser API interface
 * Base URL: https://de1.api.radio-browser.info/json/
 */
interface RadioBrowserApi {

    /**
     * Get all stations with pagination
     */
    @GET("stations")
    suspend fun getStations(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true
    ): List<StationDto>

    /**
     * Get station by UUID
     */
    @GET("stations/byuuid/{uuid}")
    suspend fun getStationByUuid(
        @Path("uuid") uuid: String
    ): List<StationDto>

    /**
     * Search stations by various criteria
     */
    @GET("stations/search")
    suspend fun searchStations(
        @Query("name") name: String? = null,
        @Query("country") country: String? = null,
        @Query("countrycode") countryCode: String? = null,
        @Query("state") state: String? = null,
        @Query("language") language: String? = null,
        @Query("tag") tag: String? = null,
        @Query("tagList") tagList: String? = null,
        @Query("codec") codec: String? = null,
        @Query("bitrateMin") bitrateMin: Int? = null,
        @Query("bitrateMax") bitrateMax: Int? = null,
        @Query("order") order: String = "votes",
        @Query("reverse") reverse: Boolean = true,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<StationDto>

    /**
     * Get stations by country code (exact match)
     */
    @GET("stations/bycountrycodeexact/{countryCode}")
    suspend fun getStationsByCountryCode(
        @Path("countryCode") countryCode: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<StationDto>

    /**
     * Get stations by tag (exact match)
     */
    @GET("stations/bytagexact/{tag}")
    suspend fun getStationsByTag(
        @Path("tag") tag: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<StationDto>

    /**
     * Get stations by language (exact match)
     */
    @GET("stations/bylanguageexact/{language}")
    suspend fun getStationsByLanguage(
        @Path("language") language: String,
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): List<StationDto>

    /**
     * Get list of all countries with station counts
     */
    @GET("countries")
    suspend fun getCountries(
        @Query("order") order: String = "name"
    ): List<CountryDto>

    /**
     * Get list of all tags with station counts
     */
    @GET("tags")
    suspend fun getTags(
        @Query("order") order: String = "stationcount",
        @Query("reverse") reverse: Boolean = true,
        @Query("limit") limit: Int = 100
    ): List<TagDto>

    /**
     * Register station click (for statistics)
     * Should be called when user starts playing a station
     */
    @GET("url/{uuid}")
    suspend fun clickStation(
        @Path("uuid") uuid: String
    ): ClickResponseDto

    /**
     * Vote for a station (increases ranking)
     */
    @GET("vote/{uuid}")
    suspend fun voteStation(
        @Path("uuid") uuid: String
    ): VoteResponseDto
}
