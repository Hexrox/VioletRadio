# 🏗️ Violet Radio - Architecture

## Overview

**Pattern:** MVVM (Model-View-ViewModel) + Clean Architecture
**Language:** Kotlin
**UI:** Jetpack Compose
**DI:** Hilt

## Layers

```
┌─────────────────────────────────────┐
│      UI Layer (Compose)             │ ← Views, ViewModels
├─────────────────────────────────────┤
│      Domain Layer (Use Cases)       │ ← Business Logic
├─────────────────────────────────────┤
│      Data Layer (Repository)        │ ← Data Management
├─────────────────────────────────────┤
│  Data Sources (API / DB)            │ ← Retrofit / Room
└─────────────────────────────────────┘
```

## Module Structure

```
com.violetradio.app/
├── ui/
│   ├── theme/              # Material 3 theme
│   ├── components/         # Reusable components
│   ├── home/              # Home screen
│   ├── player/            # Player screen
│   ├── browse/            # Browse screen
│   └── navigation/        # Nav graph
├── domain/
│   ├── model/             # Domain models
│   ├── repository/        # Interfaces
│   └── usecase/           # Business logic
├── data/
│   ├── repository/        # Implementations
│   ├── local/            # Room DB
│   ├── remote/           # Retrofit API
│   └── preferences/      # DataStore
├── spotify/              # Spotify module
│   ├── auth/
│   ├── api/
│   └── ui/
├── player/               # ExoPlayer
│   └── metadata/         # ICY parser
└── di/                   # Hilt modules
```

## Core Components

### UI Layer (Compose)

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { HomeTopBar() },
        floatingActionButton = { AddStationFab() }
    ) { padding ->
        HomeContent(state, padding)
    }
}
```

### ViewModel

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStationsUseCase: GetStationsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init { loadStations() }
}
```

### Domain Layer

```kotlin
// Model
data class Station(
    val id: String,
    val name: String,
    val url: String,
    val country: String,
    val tags: List<String>
)

// Repository Interface
interface StationRepository {
    fun getStations(): Flow<Resource<List<Station>>>
    suspend fun toggleFavorite(id: String)
}

// Use Case
class GetStationsUseCase @Inject constructor(
    private val repository: StationRepository
) {
    operator fun invoke() = repository.getStations()
}
```

### Data Layer

```kotlin
class StationRepositoryImpl @Inject constructor(
    private val api: RadioBrowserApi,
    private val dao: StationDao
) : StationRepository {

    override fun getStations() = flow {
        emit(Resource.Loading())

        // Cache first
        val cached = dao.getAllStations()
        if (cached.isNotEmpty()) {
            emit(Resource.Success(cached.map(mapper::toDomain)))
        }

        // Fetch from API
        try {
            val remote = api.getStations()
            dao.insertStations(remote.map(mapper::toEntity))
            emit(Resource.Success(remote.map(mapper::toDomain)))
        } catch (e: Exception) {
            if (cached.isEmpty()) {
                emit(Resource.Error(e.message))
            }
        }
    }.flowOn(Dispatchers.IO)
}
```

## Spotify Integration

```kotlin
// ExoPlayer metadata listener
exoPlayer.addListener(object : Player.Listener {
    override fun onMetadata(metadata: Metadata) {
        metadata.entries.forEach { entry ->
            when (entry) {
                is IcyInfo -> {
                    val track = parser.parse(entry.title)
                    _currentTrack.value = track
                }
            }
        }
    }
})

// Add to Spotify use case
class AddTrackToSpotifyUseCase @Inject constructor(
    private val repository: SpotifyRepository
) {
    suspend operator fun invoke(track: TrackInfo): Result {
        // 1. Search on Spotify
        val spotifyTrack = repository.searchTrack(
            artist = track.artist,
            title = track.title
        ) ?: return Result.TrackNotFound

        // 2. Get/Create playlist
        val playlistId = repository.getOrCreateVioletPlaylist()

        // 3. Add track
        repository.addTrackToPlaylist(playlistId, spotifyTrack.uri)

        return Result.Success
    }
}
```

## Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://de1.api.radio-browser.info/json/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRadioBrowserApi(retrofit: Retrofit): RadioBrowserApi {
        return retrofit.create(RadioBrowserApi::class.java)
    }
}
```

## Testing

```kotlin
class AddTrackToSpotifyUseCaseTest {

    private val repository: SpotifyRepository = mockk()
    private val useCase = AddTrackToSpotifyUseCase(repository)

    @Test
    fun `addTrack success returns Success`() = runTest {
        // Given
        val track = TrackInfo("Artist", "Title")
        coEvery { repository.searchTrack(any(), any()) } returns mockTrack
        coEvery { repository.getOrCreateVioletPlaylist() } returns "playlist123"

        // When
        val result = useCase(track)

        // Then
        assertTrue(result is Result.Success)
    }
}
```

---

*Architecture z 💜 dla Violet Radio*
