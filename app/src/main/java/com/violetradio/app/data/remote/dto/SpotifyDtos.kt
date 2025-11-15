package com.violetradio.app.data.remote.dto

import com.google.gson.annotations.SerializedName

// === User ===

data class SpotifyUserDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("display_name")
    val displayName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("product")
    val product: String?, // "premium", "free"

    @SerializedName("images")
    val images: List<ImageDto>?
)

// === Search ===

data class SearchResponseDto(
    @SerializedName("tracks")
    val tracks: TracksDto?
)

data class TracksDto(
    @SerializedName("items")
    val items: List<TrackDto>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int
)

data class TrackDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("uri")
    val uri: String,

    @SerializedName("artists")
    val artists: List<ArtistDto>,

    @SerializedName("album")
    val album: AlbumDto?,

    @SerializedName("duration_ms")
    val durationMs: Int,

    @SerializedName("popularity")
    val popularity: Int,

    @SerializedName("preview_url")
    val previewUrl: String?
)

data class ArtistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("uri")
    val uri: String
)

data class AlbumDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("images")
    val images: List<ImageDto>?
)

data class ImageDto(
    @SerializedName("url")
    val url: String,

    @SerializedName("height")
    val height: Int?,

    @SerializedName("width")
    val width: Int?
)

// === Playlists ===

data class PlaylistsResponseDto(
    @SerializedName("items")
    val items: List<PlaylistDto>,

    @SerializedName("total")
    val total: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("offset")
    val offset: Int
)

data class PlaylistDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("uri")
    val uri: String,

    @SerializedName("owner")
    val owner: OwnerDto,

    @SerializedName("tracks")
    val tracks: PlaylistTracksDto?,

    @SerializedName("images")
    val images: List<ImageDto>?,

    @SerializedName("public")
    val isPublic: Boolean?
)

data class OwnerDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("display_name")
    val displayName: String?
)

data class PlaylistTracksDto(
    @SerializedName("total")
    val total: Int,

    @SerializedName("items")
    val items: List<PlaylistTrackDto>?
)

data class PlaylistTrackDto(
    @SerializedName("track")
    val track: TrackDto,

    @SerializedName("added_at")
    val addedAt: String
)

// === Requests ===

data class CreatePlaylistRequestDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("public")
    val isPublic: Boolean = false
)

data class AddTracksRequestDto(
    @SerializedName("uris")
    val uris: List<String>
)
