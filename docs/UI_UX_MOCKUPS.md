# 🎨 Violet Radio - UI/UX Mockups & Specifications

> Detailed screen designs, layouts, and interactions for implementation

**Last Updated:** 2025-11-14
**Design System:** Material Design 3
**Target Platform:** Android (Mobile, Tablets)
**Min SDK:** 26 (Android 8.0)

---

## 📋 Table of Contents

1. [Design Tokens](#design-tokens)
2. [Component Library](#component-library)
3. [Screen Mockups](#screen-mockups)
4. [Navigation Flow](#navigation-flow)
5. [Animations & Transitions](#animations--transitions)
6. [Responsive Design](#responsive-design)

---

## 🎨 Design Tokens

### Color System

```kotlin
// colors.xml / Theme.kt

// Light Theme
val md_theme_light_primary = Color(0xFF6750A4)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFEADDFF)
val md_theme_light_onPrimaryContainer = Color(0xFF21005D)

val md_theme_light_secondary = Color(0xFF7F67BE)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFE8DEF8)
val md_theme_light_onSecondaryContainer = Color(0xFF1D192B)

val md_theme_light_tertiary = Color(0xFF9D7FC8)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_background = Color(0xFFFFFBFF)
val md_theme_light_surface = Color(0xFFFFFBFF)
val md_theme_light_outline = Color(0xFF79747E)

// Dark Theme
val md_theme_dark_primary = Color(0xFFD0BCFF)
val md_theme_dark_onPrimary = Color(0xFF381E72)
val md_theme_dark_primaryContainer = Color(0xFF4F378B)
val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF)

val md_theme_dark_secondary = Color(0xFFCCC2DC)
val md_theme_dark_onSecondary = Color(0xFF332D41)
val md_theme_dark_secondaryContainer = Color(0xFF4A4458)
val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)

val md_theme_dark_background = Color(0xFF1C1B1F)
val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_surfaceVariant = Color(0xFF49454F)

// Semantic Colors
val color_live = Color(0xFF4CAF50)        // Playing indicator
val color_offline = Color(0xFFF44336)     // Error/Offline
val color_buffering = Color(0xFFFF9800)   // Loading state
val color_spotify = Color(0xFF1DB954)     // Spotify brand
```

### Typography Scale

```kotlin
// Typography.kt
val Typography = Typography(
    // Display - Large titles
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),

    // Headline - Section headers
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // Title - Card titles, dialogs
    titleLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),

    // Body - Main content
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    // Label - Buttons, chips
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### Spacing System

```kotlin
// Dimens.kt
object Spacing {
    val none = 0.dp
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
    val huge = 48.dp
    val massive = 64.dp
}

// Touch targets
val MinTouchTarget = 48.dp
```

### Elevation

```kotlin
object Elevation {
    val level0 = 0.dp      // Surface
    val level1 = 1.dp      // Cards at rest
    val level2 = 3.dp      // Cards raised
    val level3 = 6.dp      // FAB, raised buttons
    val level4 = 8.dp      // Navigation drawer
    val level5 = 12.dp     // Modal dialogs
}
```

### Corner Radius

```kotlin
object CornerRadius {
    val none = 0.dp
    val small = 8.dp       // Chips
    val medium = 12.dp     // Cards
    val large = 16.dp      // FAB
    val extraLarge = 28.dp // Search bar, bottom sheet
    val full = 999.dp      // Circular
}
```

---

## 🧩 Component Library

### 1. Station Card Component

**Variants:** Compact, Featured, Grid

#### Compact (List Item)
```
┌─────────────────────────────────────────────┐
│  ┌──────┐  Radio 357                    💜  │ 56dp height
│  │ LOGO │  Jazz, Smooth                  │  │
│  └──────┘  🔴 LIVE · 192kbps             ⋮  │
│   48x48                                      │
└─────────────────────────────────────────────┘

Padding: 16dp horizontal, 8dp vertical
Avatar: 48x48dp, radius 8dp
Title: titleMedium, maxLines 1
Subtitle: bodySmall, color outline, maxLines 1
Live indicator: 8dp circle, color_live
Favorite: IconButton 48x48dp
Menu: IconButton 48x48dp
```

**Compose Implementation:**
```kotlin
@Composable
fun StationListItem(
    station: Station,
    onStationClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onStationClick,
        modifier = modifier.height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Station Avatar
            AsyncImage(
                model = station.favicon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                placeholder = painterResource(R.drawable.ic_radio_placeholder)
            )

            Spacer(Modifier.width(16.dp))

            // Station Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Live indicator
                    if (station.isPlaying) {
                        Canvas(modifier = Modifier.size(8.dp)) {
                            drawCircle(color = Color(0xFF4CAF50))
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = " · ${station.bitrate}kbps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    } else {
                        Text(
                            text = station.tags.joinToString(", "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Favorite button
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (station.isFavorite)
                        Icons.Filled.Favorite
                    else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (station.isFavorite)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outline
                )
            }

            // Menu button
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
        }
    }
}
```

#### Featured Card (Horizontal Scroll)
```
┌─────────────────────┐
│   ┌─────────────┐   │ 160dp height
│   │             │   │ 140dp width
│   │    LOGO     │   │
│   │   80x80     │   │
│   └─────────────┘   │
│                     │
│   Radio 357         │
│   🔴 LIVE           │
└─────────────────────┘

Padding: 12dp all sides
Card: radius 12dp, elevation 1dp
Avatar: 80x80dp, radius 12dp, centered
Title: titleMedium, centered, maxLines 1
Status: bodySmall, centered
```

**Compose Implementation:**
```kotlin
@Composable
fun FeaturedStationCard(
    station: Station,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(140.dp)
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AsyncImage(
                model = station.favicon,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = station.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (station.isPlaying) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(color = Color(0xFF4CAF50))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "LIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}
```

### 2. Now Playing Bar (Sticky)

```
┌──────────────────────────────────────────────────┐
│  ┌────┐  Radio 357                               │ 72dp
│  │LOGO│  Pink Floyd - Comfortably Numb           │ height
│  └────┘  ━━━━━━━●━━━━━━━━━  ⏸  ❤️  💜  ⏲️    │
│   56x56                      48  48  48  48      │
└──────────────────────────────────────────────────┘

Padding: 8dp horizontal, 8dp vertical
Card: elevation 2dp, surface color
Avatar: 56x56dp, radius 8dp
Progress: LinearProgressIndicator
Buttons: 48x48dp touch target
```

**Compose Implementation:**
```kotlin
@Composable
fun NowPlayingBar(
    state: NowPlayingState,
    onPlayPauseClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSpotifyClick: () -> Unit,
    onTimerClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(72.dp),
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Station avatar
                AsyncImage(
                    model = state.station.favicon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(Modifier.width(12.dp))

                // Station & track info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.station.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    state.currentTrack?.let { track ->
                        Text(
                            text = "${track.artist} - ${track.title}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Play/Pause
                IconButton(onClick = onPlayPauseClick) {
                    Icon(
                        imageVector = if (state.isPlaying)
                            Icons.Filled.Pause
                        else
                            Icons.Filled.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play"
                    )
                }

                // Favorite
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (state.station.isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (state.station.isFavorite)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }

                // Spotify
                IconButton(
                    onClick = onSpotifyClick,
                    enabled = state.currentTrack != null
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_spotify),
                        contentDescription = "Add to Spotify",
                        tint = if (state.currentTrack != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    )
                }

                // Sleep timer
                IconButton(onClick = onTimerClick) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Sleep timer"
                    )
                }
            }

            // Progress indicator (buffering)
            if (state.isBuffering) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}
```

### 3. Search Bar

```
┌──────────────────────────────────────────────┐
│  🔍  Search stations...              ✕  ⚙️  │ 56dp height
└──────────────────────────────────────────────┘

Padding: 4dp horizontal
Radius: 28dp (full rounded)
Elevation: 1dp
Icons: 48x48dp touch target
```

**Compose Implementation:**
```kotlin
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.padding(start = 12.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "Search stations...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
            )

            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
        }
    }
}
```

---

## 📱 Screen Mockups

### 1. Home Screen

**Layout:** Scaffold + LazyColumn

```
╔════════════════════════════════════════╗
║  Violet Radio                     ⚙️   ║ TopAppBar 64dp
╠════════════════════════════════════════╣
║ ┌────────────────────────────────────┐ ║
║ │ 🔍  Search stations...         ✕  │ ║ SearchBar 56dp
║ └────────────────────────────────────┘ ║
║                                        ║
║ ┌────────────────────────────────────┐ ║ Now Playing
║ │ LOGO  Radio 357         ⏸ ❤️ 💜 ⏲️│ ║ (if playing)
║ │       Pink Floyd - ...             │ ║ 72dp sticky
║ └────────────────────────────────────┘ ║
║                                        ║
║ 🇵🇱 Polskie Stacje            Zobacz wszystkie >  ║
║ ┌───────┐ ┌───────┐ ┌───────┐ ┌────  ║ Horizontal
║ │ LOGO  │ │ LOGO  │ │ LOGO  │ │      ║ scroll
║ │Radio  │ │ RMF   │ │Radio  │ │      ║ 160dp height
║ │357    │ │ FM    │ │ ZET   │ │      ║
║ └───────┘ └───────┘ └───────┘ └────  ║
║                                        ║
║ 💜 Ulubione (5)               Zobacz > ║
║ ┌────────────────────────────────────┐ ║
║ │ LOGO Radio Kraków              💜 ⋮│ ║ 56dp each
║ ├────────────────────────────────────┤ ║
║ │ LOGO Polskie Radio 3           💜 ⋮│ ║
║ └────────────────────────────────────┘ ║
║                                        ║
║ 🌍 Odkryj                     Zobacz > ║
║ ┌────────────────────────────────────┐ ║
║ │ LOGO BBC Radio 1              ♡  ⋮│ ║
║ ├────────────────────────────────────┤ ║
║ │ LOGO NPR                      ♡  ⋮│ ║
║ └────────────────────────────────────┘ ║
║                                        ║
║                                    [+] ║ FAB 56x56dp
╚════════════════════════════════════════╝

Padding: 16dp horizontal (sections)
Section spacing: 24dp vertical
Section header: titleLarge, 16dp bottom padding
```

**Compose Implementation:**
```kotlin
@Composable
fun HomeScreen(
    state: HomeState,
    onStationClick: (Station) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddStationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Violet Radio") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddStationClick) {
                Icon(Icons.Default.Add, "Add station")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 88.dp) // FAB clearance
        ) {
            // Search bar
            item {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = {},
                    onClearClick = {},
                    onSettingsClick = onSettingsClick,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Now Playing (if active)
            state.nowPlaying?.let { nowPlaying ->
                item {
                    NowPlayingBar(
                        state = nowPlaying,
                        onPlayPauseClick = {},
                        onFavoriteClick = {},
                        onSpotifyClick = {},
                        onTimerClick = {},
                        onClick = { /* Navigate to player */ },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            // Polish Stations Section
            item {
                SectionHeader(
                    title = "🇵🇱 Polskie Stacje",
                    onSeeAllClick = { /* Navigate to browse with filter */ },
                    modifier = Modifier.padding(horizontal = 16.dp, top = 24.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.polishStations) { station ->
                        FeaturedStationCard(
                            station = station,
                            onClick = { onStationClick(station) }
                        )
                    }
                }
            }

            // Favorites Section
            if (state.favorites.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "💜 Ulubione (${state.favorites.size})",
                        onSeeAllClick = { /* Navigate to favorites */ },
                        modifier = Modifier.padding(horizontal = 16.dp, top = 24.dp)
                    )
                }

                items(state.favorites.take(3)) { station ->
                    StationListItem(
                        station = station,
                        onStationClick = { onStationClick(station) },
                        onFavoriteClick = {},
                        onMenuClick = {},
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Discover Section
            item {
                SectionHeader(
                    title = "🌍 Odkryj",
                    onSeeAllClick = { /* Navigate to browse */ },
                    modifier = Modifier.padding(horizontal = 16.dp, top = 24.dp)
                )
            }

            items(state.discoverStations.take(5)) { station ->
                StationListItem(
                    station = station,
                    onStationClick = { onStationClick(station) },
                    onFavoriteClick = {},
                    onMenuClick = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
```

### 2. Player Screen (Full Screen)

```
╔════════════════════════════════════════╗
║  ←                                 ⋮   ║ TopAppBar
╠════════════════════════════════════════╣
║                                        ║
║           ┌──────────────┐             ║
║           │              │             ║ 280x280dp
║           │              │             ║ rounded 28dp
║           │     LOGO     │             ║ centered
║           │   (280x280)  │             ║
║           │              │             ║
║           └──────────────┘             ║
║                                        ║
║          Radio 357                     ║ titleLarge
║          Jazz, Smooth                  ║ bodyMedium
║                                        ║
║      ━━━━━━━━━━●━━━━━━━━              ║ Waveform
║                                        ║ 48dp height
║                                        ║
║      Pink Floyd                        ║ headlineSmall
║      Comfortably Numb                  ║ titleMedium
║                                        ║
║                                        ║
║         ⏮️        ⏸️        ⏭️          ║ Primary
║         64dp     72dp      64dp        ║ controls
║                                        ║
║                                        ║
║      ❤️       💜       ⏲️      🔗      ║ Secondary
║      48dp     48dp    48dp    48dp     ║ actions
║                                        ║
╚════════════════════════════════════════╝

Padding: 24dp horizontal
Vertical spacing: 16-24dp between elements
```

**Compose Implementation:**
```kotlin
@Composable
fun PlayerScreen(
    state: PlayerState,
    onBackClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSpotifyClick: () -> Unit,
    onTimerClick: () -> Unit,
    onShareClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            // Album Art
            AsyncImage(
                model = state.station.favicon,
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .clip(RoundedCornerShape(28.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(8.dp))

            // Station Info
            Text(
                text = state.station.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Text(
                text = state.station.tags.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // Waveform Animation
            WaveformVisualizer(
                isPlaying = state.isPlaying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Track Info
            state.currentTrack?.let { track ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = track.artist ?: "Unknown Artist",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Primary Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Previous
                IconButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Play/Pause
                FilledIconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (state.isPlaying)
                            Icons.Filled.Pause
                        else
                            Icons.Filled.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Next
                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Secondary Actions
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Favorite
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (state.station.isFavorite)
                            Icons.Filled.Favorite
                        else
                            Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (state.station.isFavorite)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                }

                // Spotify
                SpotifyButton(
                    track = state.currentTrack,
                    onClick = onSpotifyClick
                )

                // Sleep Timer
                IconButton(onClick = onTimerClick) {
                    Icon(Icons.Default.Timer, "Sleep timer")
                }

                // Share
                IconButton(onClick = onShareClick) {
                    Icon(Icons.Default.Share, "Share")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
```

### 3. Browse/Search Screen

```
╔════════════════════════════════════════╗
║  🔍  Search...                    ✕ ⚙️ ║ SearchBar
╠════════════════════════════════════════╣
║ 🇵🇱 Polska  🌍 All  🎵 Jazz  🎸 Rock  ║ Filter chips
║                                        ║ Horizontal scroll
╠════════════════════════════════════════╣
║ ┌────────────────────────────────────┐ ║
║ │ LOGO Radio 357                 ♡ ⋮│ ║ 56dp each
║ ├────────────────────────────────────┤ ║ Lazy load
║ │ LOGO RMF FM                    ♡ ⋮│ ║
║ ├────────────────────────────────────┤ ║
║ │ LOGO Radio ZET                 ♡ ⋮│ ║
║ ├────────────────────────────────────┤ ║
║ │ LOGO Polskie Radio 3           ♡ ⋮│ ║
║ ├────────────────────────────────────┤ ║
║ │ LOGO Radio Kraków              ♡ ⋮│ ║
║ ├────────────────────────────────────┤ ║
║ │ ...                                │ ║
║ └────────────────────────────────────┘ ║
╚════════════════════════════════════════╝

Filter chips: 8dp spacing, 16dp horizontal padding
List: dividers between items
Pull-to-refresh supported
```

**Compose Implementation:**
```kotlin
@Composable
fun BrowseScreen(
    state: BrowseState,
    onQueryChange: (String) -> Unit,
    onFilterChange: (Filter) -> Unit,
    onStationClick: (Station) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Search Bar
        SearchBar(
            query = state.query,
            onQueryChange = onQueryChange,
            onClearClick = { onQueryChange("") },
            onSettingsClick = {},
            modifier = Modifier.padding(16.dp)
        )

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = state.selectedCountry == "Poland",
                    onClick = { onFilterChange(Filter.Country("Poland")) },
                    label = { Text("🇵🇱 Polska") },
                    leadingIcon = if (state.selectedCountry == "Poland") {
                        { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) }
                    } else null
                )
            }

            item {
                FilterChip(
                    selected = state.selectedCountry == null,
                    onClick = { onFilterChange(Filter.Country(null)) },
                    label = { Text("🌍 All") }
                )
            }

            items(state.availableTags) { tag ->
                FilterChip(
                    selected = tag in state.selectedTags,
                    onClick = { onFilterChange(Filter.Tag(tag)) },
                    label = { Text(tag) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Stations List with Pull-to-Refresh
        val pullRefreshState = rememberPullRefreshState(
            refreshing = state.isRefreshing,
            onRefresh = onRefresh
        )

        Box(Modifier.pullRefresh(pullRefreshState)) {
            LazyColumn {
                items(
                    items = state.stations,
                    key = { it.id }
                ) { station ->
                    StationListItem(
                        station = station,
                        onStationClick = { onStationClick(station) },
                        onFavoriteClick = {},
                        onMenuClick = {},
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    if (station != state.stations.last()) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }

                // Loading indicator
                if (state.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
```

### 4. Settings Screen

```
╔════════════════════════════════════════╗
║  ←  Settings                           ║ TopAppBar
╠════════════════════════════════════════╣
║ Playback                               ║ Section header
║ ┌────────────────────────────────────┐ ║
║ │ Audio Quality              192 kbps >│ ║
║ ├────────────────────────────────────┤ ║
║ │ Buffer Size                   30s  >│ ║
║ ├────────────────────────────────────┤ ║
║ │ Auto-play on connect          [ON] │ ║
║ └────────────────────────────────────┘ ║
║                                        ║
║ Appearance                             ║
║ ┌────────────────────────────────────┐ ║
║ │ Dark Mode              System    >  │ ║
║ ├────────────────────────────────────┤ ║
║ │ Dynamic Colors (Material You) [ON] │ ║
║ └────────────────────────────────────┘ ║
║                                        ║
║ Spotify                                ║
║ ┌────────────────────────────────────┐ ║
║ │ 🟢 Connected: user@email.com       │ ║
║ │ [ Disconnect ]                     │ ║
║ ├────────────────────────────────────┤ ║
║ │ Target Playlist                   >│ ║
║ │ 💜 Violet Radio Discoveries        │ ║
║ ├────────────────────────────────────┤ ║
║ │ Auto-sync                     [OFF]│ ║
║ │ Wi-Fi Only                     [ON]│ ║
║ └────────────────────────────────────┘ ║
║                                        ║
║ About                                  ║
║ ┌────────────────────────────────────┐ ║
║ │ Version                      1.0.0  │ ║
║ ├────────────────────────────────────┤ ║
║ │ Licenses                          >│ ║
║ └────────────────────────────────────┘ ║
╚════════════════════════════════════════╝
```

---

## 🔄 Navigation Flow

```
┌─────────────┐
│   Splash    │
│   Screen    │
└──────┬──────┘
       │
       v
┌─────────────────────────────────────────┐
│          Home Screen                    │◄────┐
│  (Bottom Nav: Home / Browse / Profile)  │     │
└──┬────────────┬──────────────┬──────────┘     │
   │            │              │                │
   │            v              v                │
   │     ┌─────────────┐  ┌──────────┐         │
   │     │   Browse    │  │ Profile  │         │
   │     └─────────────┘  └──────────┘         │
   │                                            │
   v                                            │
┌──────────────┐                                │
│   Player     │────────────────────────────────┘
│   Screen     │         (Back)
└──────┬───────┘
       │
       ├──> [ Sleep Timer Dialog ]
       ├──> [ Add to Spotify Sheet ]
       └──> [ Station Menu Sheet ]
```

---

## ✨ Animations & Transitions

### Screen Transitions

```kotlin
// Navigation animations
val enterTransition = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeIn(animationSpec = tween(300))

val exitTransition = slideOutHorizontally(
    targetOffsetX = { -it / 3 },
    animationSpec = tween(300, easing = FastOutSlowInEasing)
) + fadeOut(animationSpec = tween(300))
```

### Button Press (Ripple)

```kotlin
// Material ripple with bounded = true (default)
// Duration: 150ms
```

### Spotify Button States

```kotlin
@Composable
fun SpotifyButton(
    state: SpotifyButtonState,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (state == SpotifyButtonState.LOADING) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium)
    )

    val color by animateColorAsState(
        targetValue = when (state) {
            SpotifyButtonState.READY -> MaterialTheme.colorScheme.primary
            SpotifyButtonState.SUCCESS -> Color(0xFF1DB954)
            SpotifyButtonState.ERROR -> Color(0xFFFFB800)
            else -> MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(300)
    )

    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(48.dp)
            .scale(scale),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = color
        )
    ) {
        when (state) {
            SpotifyButtonState.READY -> Icon(painter = painterResource(R.drawable.ic_spotify), null)
            SpotifyButtonState.LOADING -> CircularProgressIndicator(Modifier.size(24.dp))
            SpotifyButtonState.SUCCESS -> Icon(Icons.Default.Check, null)
            SpotifyButtonState.ERROR -> Icon(Icons.Default.Warning, null)
        }
    }
}
```

### Waveform Visualizer

```kotlin
@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = modifier) {
        if (!isPlaying) return@Canvas

        val barWidth = 4.dp.toPx()
        val spacing = 8.dp.toPx()
        val barCount = (size.width / (barWidth + spacing)).toInt()

        for (i in 0 until barCount) {
            val height = sin((i + offset * barCount) * 0.5f) * size.height * 0.5f + size.height * 0.5f
            drawRoundRect(
                color = Color(0xFF6750A4),
                topLeft = Offset(i * (barWidth + spacing), (size.height - height) / 2),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2)
            )
        }
    }
}
```

---

## 📐 Responsive Design

### Breakpoints

```kotlin
enum class WindowSize {
    Compact,    // < 600dp
    Medium,     // 600-840dp
    Expanded    // > 840dp
}

@Composable
fun calculateWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    return when {
        configuration.screenWidthDp < 600 -> WindowSize.Compact
        configuration.screenWidthDp < 840 -> WindowSize.Medium
        else -> WindowSize.Expanded
    }
}
```

### Adaptive Layouts

**Compact (Phone):** Single column, bottom navigation
**Medium (Tablet portrait):** Single column, larger cards
**Expanded (Tablet landscape, Desktop):** Two columns, navigation rail

---

## 📝 Implementation Checklist

- [ ] Set up Material 3 theme with dynamic colors
- [ ] Implement custom color scheme (Violet)
- [ ] Create typography scale
- [ ] Build component library (cards, buttons, bars)
- [ ] Implement Home screen layout
- [ ] Implement Player screen with animations
- [ ] Implement Browse/Search screen
- [ ] Implement Settings screen
- [ ] Add navigation (NavHost)
- [ ] Implement transitions & animations
- [ ] Add responsive breakpoints
- [ ] Test on different screen sizes
- [ ] Accessibility (TalkBack, contrast)
- [ ] Dark mode testing

---

**Document Version:** 1.0
**Last Updated:** 2025-11-14

💜 **Violet Radio** - Beautiful UI, Ready to Code!
