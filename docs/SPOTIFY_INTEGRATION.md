# 🎵 Spotify Integration

## Overview

**"Violet Sync"** - dodawaj utwory z radia bezpośrednio do Spotify playlisty.

### Workflow

```
Radio Stream → ICY Metadata → Parse Artist/Title
    → Show Spotify Button → User Clicks
    → Search on Spotify → Add to Playlist
```

## Implementation

### 1. Metadata Extraction

```kotlin
class MetadataParser {
    fun parse(metadata: String): TrackInfo? {
        // "Artist - Title"
        val parts = metadata.split(" - ", limit = 2)
        return if (parts.size == 2) {
            TrackInfo(
                artist = parts[0].trim(),
                title = parts[1].trim()
            )
        } else null
    }
}

// ExoPlayer listener
exoPlayer.addListener(object : Player.Listener {
    override fun onMetadata(metadata: Metadata) {
        metadata.entries.forEach { entry ->
            when (entry) {
                is IcyInfo -> {
                    entry.title?.let {
                        _currentTrack.value = parser.parse(it)
                    }
                }
            }
        }
    }
})
```

### 2. Spotify OAuth

**Setup:**
1. https://developer.spotify.com/dashboard
2. Create App → Name: Violet Radio
3. Redirect URI: `violet-radio://spotify-callback`
4. Save Client ID

**Auth Flow:**
```kotlin
class SpotifyAuthManager @Inject constructor(
    private val context: Context
) {
    fun startAuth(activity: Activity) {
        val request = AuthorizationRequest.Builder(
            CLIENT_ID,
            AuthorizationResponse.Type.TOKEN,
            "violet-radio://spotify-callback"
        )
        .setScopes(arrayOf(
            "playlist-modify-public",
            "playlist-modify-private"
        ))
        .build()

        AuthorizationClient.openLoginActivity(activity, REQUEST_CODE, request)
    }
}
```

### 3. Spotify API

```kotlin
interface SpotifyApi {

    @GET("v1/search")
    suspend fun searchTrack(
        @Header("Authorization") auth: String,
        @Query("q") query: String,
        @Query("type") type: String = "track",
        @Query("limit") limit: Int = 1
    ): SearchResponse

    @POST("v1/users/{user_id}/playlists")
    suspend fun createPlaylist(
        @Header("Authorization") auth: String,
        @Path("user_id") userId: String,
        @Body body: CreatePlaylistRequest
    ): PlaylistResponse

    @POST("v1/playlists/{playlist_id}/tracks")
    suspend fun addTracksToPlaylist(
        @Header("Authorization") auth: String,
        @Path("playlist_id") playlistId: String,
        @Body body: AddTracksRequest
    )
}
```

### 4. Use Case

```kotlin
class AddTrackToSpotifyUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    suspend operator fun invoke(track: TrackInfo): Result {
        // 1. Search
        val spotifyTrack = repository.searchTrack(
            artist = track.artist,
            title = track.title
        ) ?: return Result.TrackNotFound

        // 2. Get/Create playlist "Violet Radio Discoveries"
        val playlistId = repository.getOrCreateVioletPlaylist()
            ?: return Result.PlaylistError

        // 3. Add track
        repository.addTrackToPlaylist(playlistId, spotifyTrack.uri)

        return Result.Success
    }
}
```

### 5. UI Component

```kotlin
@Composable
fun SpotifyButton(
    track: TrackInfo?,
    onAddToSpotify: (TrackInfo) -> Unit
) {
    var state by remember { mutableStateOf(SpotifyButtonState.READY) }

    FloatingActionButton(
        onClick = {
            track?.let {
                state = SpotifyButtonState.LOADING
                onAddToSpotify(it)
            }
        },
        containerColor = when (state) {
            READY -> MaterialTheme.colorScheme.primary
            LOADING -> Color.Gray
            SUCCESS -> Color(0xFF1DB954) // Spotify Green
            ERROR -> Color(0xFFFFAB00)
            else -> MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        when (state) {
            READY -> Icon(painterResource(R.drawable.ic_spotify))
            LOADING -> CircularProgressIndicator(size = 24.dp)
            SUCCESS -> Icon(Icons.Default.Check)
            ERROR -> Icon(Icons.Default.Warning)
        }
    }
}
```

## Configuration

### build.gradle.kts

```kotlin
dependencies {
    // Spotify
    implementation("com.spotify.android:auth:2.1.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}
```

### AndroidManifest.xml

```xml
<activity
    android:name=".spotify.auth.SpotifyCallbackActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="violet-radio"
            android:host="spotify-callback" />
    </intent-filter>
</activity>
```

## Features Roadmap

### v1.1 - MVP
- [x] ICY metadata extraction
- [x] Spotify OAuth
- [x] Basic search & add
- [x] Auto-create playlist
- [x] UI button with states

### v1.2 - Enhanced
- [ ] Track history
- [ ] Offline queue
- [ ] Settings (choose playlist)
- [ ] Preview before adding

### v1.3 - Advanced
- [ ] Auto-sync mode
- [ ] Multiple playlists
- [ ] Statistics
- [ ] YouTube Music support

## Error Handling

```kotlin
sealed class Result {
    object Success : Result()
    object TrackNotFound : Result()
    object NotAuthenticated : Result()
    object PlaylistError : Result()
    data class Error(val message: String) : Result()
}

// UI handling
when (result) {
    Success -> showSnackbar("✓ Dodano do Spotify!")
    TrackNotFound -> showSnackbar("⚠️ Nie znaleziono")
    NotAuthenticated -> showSpotifyLoginDialog()
    is Error -> showSnackbar("❌ ${result.message}")
}
```

---

*Spotify Integration z 💜*
