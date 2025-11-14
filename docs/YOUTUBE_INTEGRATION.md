# 📺 Violet Radio - YouTube Integration

> Multi-platform music discovery: Spotify + YouTube fallback strategy

**Last Updated:** 2025-11-14
**Rationale:** Not all tracks are available on Spotify - YouTube provides universal coverage
**Strategy:** Smart fallback with user preference support

---

## 📋 Table of Contents

1. [Why YouTube?](#why-youtube)
2. [Integration Strategies](#integration-strategies)
3. [YouTube Data API v3](#youtube-data-api-v3)
4. [Implementation Options](#implementation-options)
5. [UI/UX Design](#uiux-design)
6. [API Interfaces](#api-interfaces)
7. [Smart Fallback Logic](#smart-fallback-logic)
8. [Quota Management](#quota-management)
9. [Testing](#testing)

---

## 🎯 Why YouTube?

### Problem Statement

**Spotify limitations:**
- Not all songs available (regional restrictions, indie artists, remixes)
- Live sessions, covers, DJ sets often missing
- Local/regional Polish artists may not be on Spotify
- User uploaded content not available

**YouTube advantages:**
- ✅ Universal coverage - almost everything is available
- ✅ User-generated content (covers, remixes, live recordings)
- ✅ Free access (no subscription required)
- ✅ Music videos + audio
- ✅ Works globally without regional restrictions

### Use Cases

**Scenario 1:** Track not on Spotify
```
Radio plays: "Local Polish Band - New Single"
→ Spotify search: Not found ❌
→ YouTube search: Found ✅
→ User opens in YouTube app
```

**Scenario 2:** Live session/cover
```
Radio plays: "Artist - Song (Live at Festival)"
→ Spotify: Studio version only
→ YouTube: Exact live version found ✅
```

**Scenario 3:** User preference
```
User prefers YouTube Music over Spotify
→ Skip Spotify entirely
→ Direct YouTube search
```

---

## 🔄 Integration Strategies

### Strategy A: Fallback (Recommended for MVP)

**Flow:** Spotify first → YouTube if not found

```
┌─────────────────────────────────────────┐
│  Track detected from ICY metadata       │
│  "Artist - Title"                       │
└────────────┬────────────────────────────┘
             │
             v
┌─────────────────────────────────────────┐
│  1. Try Spotify Search                  │
│     (if authenticated)                  │
└────────────┬────────────────────────────┘
             │
        ┌────┴────┐
        │ Found?  │
        └────┬────┘
          ✅ │     ❌
             │      │
             v      v
    ┌────────────┐ ┌────────────────────┐
    │ Show       │ │ 2. Try YouTube     │
    │ Spotify    │ │    Search          │
    │ Button     │ └─────────┬──────────┘
    └────────────┘           │
                        ┌────┴────┐
                        │ Found?  │
                        └────┬────┘
                          ✅ │  ❌
                             │   │
                             v   v
                    ┌────────────┐ ┌──────────────┐
                    │ Show       │ │ Show generic │
                    │ YouTube    │ │ search button│
                    │ Button     │ └──────────────┘
                    └────────────┘
```

**Pros:**
- Respects Spotify as primary platform
- YouTube as safety net
- Good user experience

**Cons:**
- Two API calls per track
- Higher quota usage

---

### Strategy B: User Choice (Advanced)

**Flow:** User selects preferred platform in settings

```
Settings → Music Platform Priority
  ○ Spotify only
  ● Spotify, then YouTube (fallback)
  ○ YouTube only
  ○ Both (show both buttons)
```

**Pros:**
- User control
- Can optimize quota usage
- Flexible

**Cons:**
- More complex UI
- Settings screen needed

---

### Strategy C: Smart Parallel (Future)

**Flow:** Search both simultaneously, show best match

```
Track detected
   ├─→ Spotify search (async)
   └─→ YouTube search (async)
       ↓
   Wait for both (max 3s)
       ↓
   Show best result(s)
```

**Pros:**
- Fastest response
- Best match selection
- Great UX

**Cons:**
- Higher quota usage
- More complex logic
- Network overhead

---

## 📺 YouTube Data API v3

### API Overview

**Base URL:** `https://www.googleapis.com/youtube/v3/`

**Authentication:** API Key (read-only) or OAuth 2.0 (user data)

**Quota:** 10,000 units/day (default)

### Quota Costs

| Operation | Cost | Example |
|-----------|------|---------|
| Search | 100 units | Search for video |
| Video details | 1 unit | Get video metadata |
| Playlist insert | 50 units | Add to playlist |

**Daily limits:**
- ~100 searches per day (100 × 100 = 10,000 units)
- Or 10,000 video detail fetches
- Or mix of operations

### Required Scopes (OAuth)

```kotlin
// Read-only (no OAuth required with API key)
val READ_ONLY = "https://www.googleapis.com/auth/youtube.readonly"

// Manage playlists (requires OAuth)
val MANAGE_PLAYLISTS = "https://www.googleapis.com/auth/youtube"
```

### Setup

**Google Cloud Console:**
1. Create project at https://console.cloud.google.com/
2. Enable YouTube Data API v3
3. Create credentials:
   - **API Key** (for search only)
   - **OAuth 2.0 Client ID** (for playlist management)
4. Configure OAuth consent screen
5. Add redirect URI: `com.violetradio://youtube-callback`

---

## 🛠️ Implementation Options

### Option 1: Search + Open in App (MVP) ⭐ RECOMMENDED

**Features:**
- YouTube Data API search
- Open result in YouTube app
- No playlist management
- No OAuth required (API key only)

**Quota usage:** ~100 units per search

**Pros:**
- Simple implementation
- No OAuth complexity
- Low quota usage (can cache results)
- Works immediately

**Cons:**
- Can't add to YouTube playlists
- Requires YouTube app installed

**Implementation:**

```kotlin
// 1. Search via API
val searchResult = youtubeApi.search(
    query = "artist:$artist $title",
    type = "video",
    maxResults = 1
)

// 2. Open in YouTube app
val videoId = searchResult.items.first().id.videoId
val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://$videoId"))
    .apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

try {
    context.startActivity(intent)
} catch (e: ActivityNotFoundException) {
    // Fallback to browser
    val browserIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://www.youtube.com/watch?v=$videoId")
    )
    context.startActivity(browserIntent)
}
```

---

### Option 2: Full Integration with Playlists (Advanced)

**Features:**
- YouTube Data API search
- OAuth 2.0 authentication
- Add to YouTube/YouTube Music playlists
- Playlist management

**Quota usage:** 100 (search) + 50 (add) = 150 units per add

**Pros:**
- Complete feature parity with Spotify
- User library integration
- Better UX

**Cons:**
- Requires OAuth (complex)
- Higher quota usage
- User needs Google account

---

### Option 3: Hybrid (Recommended for v1.1)

**MVP (v1.0):**
- Search + Open in app (Option 1)
- Simple, fast to implement

**Enhanced (v1.1):**
- Add OAuth support
- Optional playlist feature
- User can upgrade

---

## 🎨 UI/UX Design

### Variant 1: Dual Buttons (Side by Side)

```
┌─────────────────────────────────────────┐
│  Now Playing                            │
│  ┌────┐  Artist - Title                 │
│  │IMG │                                  │
│  └────┘                                  │
│                                          │
│         ⏸️   ❤️   [💜]   [▶️]   ⏲️      │
│                  Spotify YouTube        │
└─────────────────────────────────────────┘

State logic:
- 💜 Spotify: Enabled if track found on Spotify
- ▶️ YouTube: Always enabled (universal fallback)
- Both can be active simultaneously
```

**Compose Implementation:**

```kotlin
@Composable
fun MusicPlatformButtons(
    track: TrackInfo?,
    spotifyState: SpotifyButtonState,
    youtubeState: YouTubeButtonState,
    onSpotifyClick: () -> Unit,
    onYouTubeClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Spotify Button
        IconButton(
            onClick = onSpotifyClick,
            enabled = track != null && spotifyState != SpotifyButtonState.DISABLED
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_spotify),
                contentDescription = "Add to Spotify",
                tint = when (spotifyState) {
                    SpotifyButtonState.READY -> Color(0xFF1DB954)
                    SpotifyButtonState.SUCCESS -> Color(0xFF1DB954)
                    SpotifyButtonState.ERROR -> Color(0xFFFFB800)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                }
            )
        }

        // YouTube Button
        IconButton(
            onClick = onYouTubeClick,
            enabled = track != null
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_youtube),
                contentDescription = "Open on YouTube",
                tint = when (youtubeState) {
                    YouTubeButtonState.READY -> Color(0xFFFF0000) // YouTube red
                    YouTubeButtonState.SEARCHING -> MaterialTheme.colorScheme.primary
                    YouTubeButtonState.SUCCESS -> Color(0xFF4CAF50)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                }
            )
        }
    }
}
```

---

### Variant 2: Smart Button with Menu

```
┌─────────────────────────────────────────┐
│  ⏸️   ❤️   [🎵 ▼]   ⏲️                 │
│            Save Track                   │
│              ↓ (tap)                    │
│       ┌─────────────────┐              │
│       │ 💜 Spotify      │              │
│       │ ▶️ YouTube      │              │
│       │ 📋 Copy Info    │              │
│       └─────────────────┘              │
└─────────────────────────────────────────┘

Better for:
- Less UI clutter
- More options in future (Apple Music, etc.)
- Cleaner design
```

**Compose Implementation:**

```kotlin
@Composable
fun SaveTrackButton(
    track: TrackInfo?,
    onSpotifyClick: () -> Unit,
    onYouTubeClick: () -> Unit,
    onCopyClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            onClick = { expanded = true },
            enabled = track != null
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, "Save track")
                Icon(Icons.Default.ArrowDropDown, null, Modifier.size(16.dp))
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_spotify),
                            contentDescription = null,
                            tint = Color(0xFF1DB954),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Add to Spotify")
                    }
                },
                onClick = {
                    onSpotifyClick()
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_youtube),
                            contentDescription = null,
                            tint = Color(0xFFFF0000),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Open on YouTube")
                    }
                },
                onClick = {
                    onYouTubeClick()
                    expanded = false
                }
            )

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ContentCopy, null)
                        Spacer(Modifier.width(12.dp))
                        Text("Copy track info")
                    }
                },
                onClick = {
                    onCopyClick()
                    expanded = false
                }
            )
        }
    }
}
```

---

### Variant 3: Auto-Fallback with Notification

```
Primary button: 💜 Spotify
  ↓ (if not found)
Automatic YouTube search
  ↓
Snackbar: "Not on Spotify, found on YouTube. [Open]"
```

**Less intrusive, smarter UX**

---

## 📡 API Interfaces

### YouTube API DTOs

**File:** `data/remote/youtube/dto/YouTubeDtos.kt`

```kotlin
package com.violetradio.data.remote.youtube.dto

import com.google.gson.annotations.SerializedName

// === Search Response ===

data class YouTubeSearchResponseDto(
    @SerializedName("items")
    val items: List<SearchResultDto>,

    @SerializedName("nextPageToken")
    val nextPageToken: String?,

    @SerializedName("pageInfo")
    val pageInfo: PageInfoDto
)

data class SearchResultDto(
    @SerializedName("id")
    val id: VideoIdDto,

    @SerializedName("snippet")
    val snippet: SnippetDto
)

data class VideoIdDto(
    @SerializedName("kind")
    val kind: String, // "youtube#video"

    @SerializedName("videoId")
    val videoId: String
)

data class SnippetDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("channelTitle")
    val channelTitle: String,

    @SerializedName("publishedAt")
    val publishedAt: String,

    @SerializedName("thumbnails")
    val thumbnails: ThumbnailsDto
)

data class ThumbnailsDto(
    @SerializedName("default")
    val default: ThumbnailDto,

    @SerializedName("medium")
    val medium: ThumbnailDto?,

    @SerializedName("high")
    val high: ThumbnailDto?
)

data class ThumbnailDto(
    @SerializedName("url")
    val url: String,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int
)

data class PageInfoDto(
    @SerializedName("totalResults")
    val totalResults: Int,

    @SerializedName("resultsPerPage")
    val resultsPerPage: Int
)

// === Video Details ===

data class VideoDetailsResponseDto(
    @SerializedName("items")
    val items: List<VideoDetailsDto>
)

data class VideoDetailsDto(
    @SerializedName("id")
    val id: String,

    @SerializedName("snippet")
    val snippet: SnippetDto,

    @SerializedName("contentDetails")
    val contentDetails: ContentDetailsDto?,

    @SerializedName("statistics")
    val statistics: StatisticsDto?
)

data class ContentDetailsDto(
    @SerializedName("duration")
    val duration: String, // ISO 8601 format: PT4M13S

    @SerializedName("definition")
    val definition: String // "hd" or "sd"
)

data class StatisticsDto(
    @SerializedName("viewCount")
    val viewCount: String,

    @SerializedName("likeCount")
    val likeCount: String?
)
```

### YouTube API Interface

**File:** `data/remote/youtube/YouTubeApi.kt`

```kotlin
package com.violetradio.data.remote.youtube

import com.violetradio.data.remote.youtube.dto.*
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApi {

    /**
     * Search for videos
     * Cost: 100 quota units
     *
     * @param apiKey Your Google API key
     * @param query Search query (e.g., "Artist Title")
     * @param type Resource type (video, channel, playlist)
     * @param maxResults Number of results (1-50, default 5)
     * @param order Sort order (relevance, date, rating, viewCount)
     * @param videoCategoryId Filter by category (10 = Music)
     */
    @GET("search")
    suspend fun search(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("part") part: String = "snippet",
        @Query("maxResults") maxResults: Int = 1,
        @Query("order") order: String = "relevance",
        @Query("videoCategoryId") videoCategoryId: String? = "10", // Music
        @Query("videoDefinition") videoDefinition: String? = null, // "high" or "any"
        @Query("safeSearch") safeSearch: String = "none"
    ): YouTubeSearchResponseDto

    /**
     * Get video details
     * Cost: 1 quota unit
     *
     * @param apiKey Your Google API key
     * @param id Video ID
     * @param part Comma-separated parts (snippet, contentDetails, statistics)
     */
    @GET("videos")
    suspend fun getVideoDetails(
        @Query("key") apiKey: String,
        @Query("id") id: String,
        @Query("part") part: String = "snippet,contentDetails,statistics"
    ): VideoDetailsResponseDto
}
```

### Network Module (Updated)

**File:** `di/NetworkModule.kt` (add to existing)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ... existing Spotify & Radio Browser providers ...

    // === YouTube ===

    @Provides
    @Singleton
    @Named("YouTubeOkHttp")
    fun provideYouTubeOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("YouTubeRetrofit")
    fun provideYouTubeRetrofit(
        @Named("YouTubeOkHttp") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideYouTubeApi(
        @Named("YouTubeRetrofit") retrofit: Retrofit
    ): YouTubeApi {
        return retrofit.create(YouTubeApi::class.java)
    }
}
```

---

## 🧠 Smart Fallback Logic

### Use Case Implementation

**File:** `domain/usecase/track/FindTrackOnPlatformsUseCase.kt`

```kotlin
package com.violetradio.domain.usecase.track

import com.violetradio.domain.model.PlatformResult
import com.violetradio.domain.model.TrackInfo
import com.violetradio.domain.repository.SpotifyRepository
import com.violetradio.domain.repository.YouTubeRepository
import com.violetradio.domain.repository.UserPreferencesRepository
import com.violetradio.domain.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class FindTrackOnPlatformsUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository,
    private val youtubeRepository: YouTubeRepository,
    private val preferencesRepository: UserPreferencesRepository
) {

    suspend operator fun invoke(track: TrackInfo): PlatformSearchResult {
        val strategy = preferencesRepository.getMusicPlatformStrategy()

        return when (strategy) {
            MusicPlatformStrategy.SPOTIFY_ONLY -> searchSpotifyOnly(track)
            MusicPlatformStrategy.YOUTUBE_ONLY -> searchYouTubeOnly(track)
            MusicPlatformStrategy.SPOTIFY_THEN_YOUTUBE -> searchWithFallback(track)
            MusicPlatformStrategy.BOTH_PARALLEL -> searchBothParallel(track)
        }
    }

    private suspend fun searchSpotifyOnly(track: TrackInfo): PlatformSearchResult {
        val spotifyResult = spotifyRepository.searchTrack(
            artist = track.artist ?: "",
            title = track.title
        )

        return PlatformSearchResult(
            spotify = spotifyResult,
            youtube = null,
            strategy = MusicPlatformStrategy.SPOTIFY_ONLY
        )
    }

    private suspend fun searchYouTubeOnly(track: TrackInfo): PlatformSearchResult {
        val youtubeResult = youtubeRepository.searchVideo(
            query = buildYouTubeQuery(track)
        )

        return PlatformSearchResult(
            spotify = null,
            youtube = youtubeResult,
            strategy = MusicPlatformStrategy.YOUTUBE_ONLY
        )
    }

    private suspend fun searchWithFallback(track: TrackInfo): PlatformSearchResult {
        // 1. Try Spotify first
        val spotifyResult = spotifyRepository.searchTrack(
            artist = track.artist ?: "",
            title = track.title
        )

        // 2. If Spotify failed, try YouTube
        val youtubeResult = if (spotifyResult is Resource.Error) {
            youtubeRepository.searchVideo(
                query = buildYouTubeQuery(track)
            )
        } else {
            null
        }

        return PlatformSearchResult(
            spotify = spotifyResult,
            youtube = youtubeResult,
            strategy = MusicPlatformStrategy.SPOTIFY_THEN_YOUTUBE
        )
    }

    private suspend fun searchBothParallel(track: TrackInfo): PlatformSearchResult = coroutineScope {
        // Search both platforms simultaneously
        val spotifyDeferred = async {
            spotifyRepository.searchTrack(
                artist = track.artist ?: "",
                title = track.title
            )
        }

        val youtubeDeferred = async {
            youtubeRepository.searchVideo(
                query = buildYouTubeQuery(track)
            )
        }

        PlatformSearchResult(
            spotify = spotifyDeferred.await(),
            youtube = youtubeDeferred.await(),
            strategy = MusicPlatformStrategy.BOTH_PARALLEL
        )
    }

    private fun buildYouTubeQuery(track: TrackInfo): String {
        return buildString {
            track.artist?.let { append("$it ") }
            append(track.title)
            append(" official") // Prefer official uploads
        }
    }
}

// Domain models
data class PlatformSearchResult(
    val spotify: Resource<SpotifyTrack?>?,
    val youtube: Resource<YouTubeVideo?>?,
    val strategy: MusicPlatformStrategy
)

enum class MusicPlatformStrategy {
    SPOTIFY_ONLY,
    YOUTUBE_ONLY,
    SPOTIFY_THEN_YOUTUBE, // Fallback
    BOTH_PARALLEL
}
```

### Repository Implementation

**File:** `data/repository/YouTubeRepositoryImpl.kt`

```kotlin
package com.violetradio.data.repository

import com.violetradio.BuildConfig
import com.violetradio.data.remote.youtube.YouTubeApi
import com.violetradio.data.mapper.YouTubeMapper
import com.violetradio.domain.model.YouTubeVideo
import com.violetradio.domain.repository.YouTubeRepository
import com.violetradio.domain.util.Resource
import javax.inject.Inject

class YouTubeRepositoryImpl @Inject constructor(
    private val youtubeApi: YouTubeApi,
    private val mapper: YouTubeMapper
) : YouTubeRepository {

    override suspend fun searchVideo(query: String): Resource<YouTubeVideo?> {
        return try {
            val response = youtubeApi.search(
                apiKey = BuildConfig.YOUTUBE_API_KEY,
                query = query,
                type = "video",
                maxResults = 1,
                videoCategoryId = "10" // Music category
            )

            val video = response.items.firstOrNull()
            if (video != null) {
                Resource.Success(mapper.toDomain(video))
            } else {
                Resource.Error("Video not found on YouTube")
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "YouTube search failed")
        }
    }

    override suspend fun openInYouTubeApp(videoId: String): Resource<Unit> {
        // This will be handled in UI layer with Android Intent
        // Repository just validates the videoId
        return if (videoId.isNotBlank()) {
            Resource.Success(Unit)
        } else {
            Resource.Error("Invalid video ID")
        }
    }
}
```

### Android Intent Helper

**File:** `ui/util/YouTubeIntentHelper.kt`

```kotlin
package com.violetradio.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object YouTubeIntentHelper {

    /**
     * Open YouTube video in app or browser
     * @param videoId YouTube video ID
     * @param context Android context
     */
    fun openVideo(videoId: String, context: Context) {
        // Try YouTube app first
        val appIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("vnd.youtube://$videoId")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(appIntent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to browser
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=$videoId")
            )
            context.startActivity(browserIntent)
        }
    }

    /**
     * Search on YouTube (opens search results)
     */
    fun searchOnYouTube(query: String, context: Context) {
        val searchIntent = Intent(Intent.ACTION_SEARCH).apply {
            setPackage("com.google.android.youtube")
            putExtra("query", query)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(searchIntent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to browser search
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")
            )
            context.startActivity(browserIntent)
        }
    }
}
```

---

## 📊 Quota Management

### Problem: 10,000 units/day limit

**Scenario:** 100 users × 5 searches/day = 500 searches = 50,000 units needed ❌

### Solutions

#### 1. Response Caching (Recommended)

```kotlin
class CachedYouTubeRepository @Inject constructor(
    private val youtubeApi: YouTubeApi,
    private val cache: YouTubeSearchCache // Room or in-memory
) : YouTubeRepository {

    override suspend fun searchVideo(query: String): Resource<YouTubeVideo?> {
        // 1. Check cache first
        val cached = cache.get(query)
        if (cached != null && !cached.isExpired()) {
            return Resource.Success(cached.video)
        }

        // 2. API call
        val result = youtubeApi.search(...)

        // 3. Cache result (24h TTL)
        cache.put(query, result, expiresAt = System.currentTimeMillis() + 24.hours)

        return Resource.Success(mapper.toDomain(result))
    }
}
```

**Impact:** ~90% quota reduction

#### 2. Debounce User Actions

```kotlin
// Only search after user stops typing for 500ms
LaunchedEffect(track) {
    delay(500)
    viewModel.searchOnPlatforms(track)
}
```

#### 3. Request Quota Increase

**When:** App has >1000 daily users

**How:** Google Cloud Console → YouTube Data API → Quota

**Approval:** Usually granted for legitimate apps

#### 4. User Quotas (Freemium Model)

```kotlin
// Free tier: 10 YouTube searches/day per user
// Premium: Unlimited

if (userQuota.youtubeSearchesToday >= 10 && !user.isPremium) {
    return Resource.Error("Daily YouTube search limit reached. Upgrade to Premium!")
}
```

---

## 🧪 Testing

### Mock YouTube API

**File:** `data/remote/youtube/MockYouTubeApi.kt`

```kotlin
class MockYouTubeApi : YouTubeApi {

    override suspend fun search(
        apiKey: String,
        query: String,
        type: String,
        part: String,
        maxResults: Int,
        order: String,
        videoCategoryId: String?,
        videoDefinition: String?,
        safeSearch: String
    ): YouTubeSearchResponseDto {
        return YouTubeSearchResponseDto(
            items = listOf(
                SearchResultDto(
                    id = VideoIdDto(
                        kind = "youtube#video",
                        videoId = "test_video_id_123"
                    ),
                    snippet = SnippetDto(
                        title = "Test Artist - Test Song (Official Video)",
                        description = "Official video for Test Song",
                        channelTitle = "Test Artist VEVO",
                        publishedAt = "2024-01-01T00:00:00Z",
                        thumbnails = ThumbnailsDto(
                            default = ThumbnailDto(
                                url = "https://i.ytimg.com/vi/test/default.jpg",
                                width = 120,
                                height = 90
                            ),
                            medium = null,
                            high = null
                        )
                    )
                )
            ),
            nextPageToken = null,
            pageInfo = PageInfoDto(
                totalResults = 1,
                resultsPerPage = 1
            )
        )
    }

    override suspend fun getVideoDetails(
        apiKey: String,
        id: String,
        part: String
    ): VideoDetailsResponseDto {
        // Mock implementation
        TODO()
    }
}
```

### Unit Test Example

```kotlin
class FindTrackOnPlatformsUseCaseTest {

    private val spotifyRepository: SpotifyRepository = mockk()
    private val youtubeRepository: YouTubeRepository = mockk()
    private val preferencesRepository: UserPreferencesRepository = mockk()

    private val useCase = FindTrackOnPlatformsUseCase(
        spotifyRepository,
        youtubeRepository,
        preferencesRepository
    )

    @Test
    fun `fallback strategy tries YouTube when Spotify fails`() = runTest {
        // Given
        val track = TrackInfo(artist = "Artist", title = "Title")
        coEvery { preferencesRepository.getMusicPlatformStrategy() } returns
            MusicPlatformStrategy.SPOTIFY_THEN_YOUTUBE
        coEvery { spotifyRepository.searchTrack(any(), any()) } returns
            Resource.Error("Not found")
        coEvery { youtubeRepository.searchVideo(any()) } returns
            Resource.Success(mockYouTubeVideo)

        // When
        val result = useCase(track)

        // Then
        assertTrue(result.spotify is Resource.Error)
        assertTrue(result.youtube is Resource.Success)
        coVerify(exactly = 1) { youtubeRepository.searchVideo(any()) }
    }
}
```

---

## 📋 Implementation Roadmap

### Phase 1: MVP (v1.0)

- [ ] YouTube Data API setup (Google Cloud)
- [ ] YouTube API interface (search only)
- [ ] YouTubeRepository implementation
- [ ] Intent helper for opening YouTube app
- [ ] Dual button UI (Spotify + YouTube)
- [ ] Basic caching (in-memory)
- [ ] Error handling
- [ ] Unit tests

**Timeline:** 1 week
**Quota usage:** ~100-200 searches/day

### Phase 2: Enhanced (v1.1)

- [ ] Smart fallback strategy
- [ ] User preferences (platform priority)
- [ ] Persistent cache (Room)
- [ ] Quota monitoring dashboard
- [ ] Snackbar notifications
- [ ] Integration tests

**Timeline:** 1 week

### Phase 3: Advanced (v1.2+)

- [ ] YouTube OAuth integration
- [ ] Add to YouTube Music playlist
- [ ] Parallel search (both platforms)
- [ ] User quota limits (freemium)
- [ ] Request quota increase from Google
- [ ] Analytics (which platform is used more)

**Timeline:** 2 weeks

---

## 🔐 Security & Privacy

### API Key Storage

**⚠️ DO NOT hardcode API key in source code**

```kotlin
// ❌ BAD
val API_KEY = "AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

// ✅ GOOD - Use BuildConfig
val API_KEY = BuildConfig.YOUTUBE_API_KEY
```

**In build.gradle.kts:**

```kotlin
android {
    buildTypes {
        release {
            buildConfigField("String", "YOUTUBE_API_KEY", "\"${project.findProperty("YOUTUBE_API_KEY")}\"")
        }
        debug {
            buildConfigField("String", "YOUTUBE_API_KEY", "\"${project.findProperty("YOUTUBE_API_KEY")}\"")
        }
    }
}
```

**In local.properties (gitignored):**

```properties
YOUTUBE_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

---

## 📊 Comparison: Spotify vs YouTube

| Feature | Spotify | YouTube | Winner |
|---------|---------|---------|--------|
| **Coverage** | ~100M tracks | Billions of videos | YouTube |
| **Audio Quality** | Up to 320kbps | 128-256kbps (AAC) | Spotify |
| **Availability** | Subscription model | Free with ads | YouTube |
| **Metadata** | Excellent | Good | Spotify |
| **Music Videos** | Limited | Extensive | YouTube |
| **User Content** | No | Yes (covers, remixes) | YouTube |
| **API Quota** | No limits (rate limit only) | 10,000 units/day | Spotify |
| **OAuth Complexity** | PKCE (moderate) | OAuth 2.0 (moderate) | Tie |
| **Regional Restrictions** | Yes | Fewer | YouTube |

**Conclusion:** Both platforms complement each other perfectly!

---

## 📝 Implementation Checklist

- [ ] Google Cloud project setup
- [ ] YouTube Data API v3 enabled
- [ ] API key generated
- [ ] DTOs created
- [ ] API interface implemented
- [ ] Repository layer implemented
- [ ] Intent helper created
- [ ] UI updated (dual buttons or menu)
- [ ] Caching strategy implemented
- [ ] Error handling added
- [ ] Unit tests written
- [ ] Integration tests written
- [ ] Quota monitoring added
- [ ] Documentation updated

---

**Document Version:** 1.0
**Last Updated:** 2025-11-14
**Status:** Ready for implementation

💜 **Violet Radio + 📺 YouTube = Universal Music Discovery!**
