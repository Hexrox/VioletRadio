package com.violetradio.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// === YouTube Data API v3 DTOs ===

data class YouTubeSearchResponseDto(
    @SerializedName("items")
    val items: List<YouTubeVideoItemDto>,

    @SerializedName("pageInfo")
    val pageInfo: YouTubePageInfoDto?
)

data class YouTubeVideoItemDto(
    @SerializedName("id")
    val id: YouTubeVideoIdDto,

    @SerializedName("snippet")
    val snippet: YouTubeVideoSnippetDto
)

data class YouTubeVideoIdDto(
    @SerializedName("kind")
    val kind: String, // "youtube#video"

    @SerializedName("videoId")
    val videoId: String
)

data class YouTubeVideoSnippetDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("thumbnails")
    val thumbnails: YouTubeThumbnailsDto?,

    @SerializedName("channelTitle")
    val channelTitle: String?
)

data class YouTubeThumbnailsDto(
    @SerializedName("default")
    val default: YouTubeThumbnailDto?,

    @SerializedName("medium")
    val medium: YouTubeThumbnailDto?,

    @SerializedName("high")
    val high: YouTubeThumbnailDto?
)

data class YouTubeThumbnailDto(
    @SerializedName("url")
    val url: String,

    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?
)

data class YouTubePageInfoDto(
    @SerializedName("totalResults")
    val totalResults: Int,

    @SerializedName("resultsPerPage")
    val resultsPerPage: Int
)
