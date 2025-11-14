# 🎨 Spotify UI Mockups

## Player Screen z Spotify Button

```
┌─────────────────────────────────────┐
│  ← Violet Radio              ⋮      │
├─────────────────────────────────────┤
│         ┌─────────────────┐         │
│         │   🎵 LOGO       │         │
│         │   Radio 357     │         │
│         └─────────────────┘         │
│                                     │
│        Radio 357                    │
│        Pink Floyd                   │
│        Comfortably Numb             │
│                                     │
│     ═══════════○═══════════         │
│                                     │
│        ⏮️     ⏸️      ⏭️            │
│                                     │
│    ❤️       💜        ⏲️      🔗    │
│          SPOTIFY                    │
└─────────────────────────────────────┘
```

## Button States

### 1. Disabled (brak metadanych)
```
┌──────┐
│  💜  │  Szary (#E7E0EC)
└──────┘  Alpha: 0.38
```

### 2. Ready (gotowy)
```
┌──────┐
│  💜  │  Fioletowy (#6750A4)
└──────┘  Subtle pulse animation
```

### 3. Loading
```
┌──────┐
│  ⏳  │  Progress indicator
└──────┘
```

### 4. Success (2 sekundy)
```
┌──────┐
│  ✓   │  Spotify Green (#1DB954)
└──────┘  Scale + fade animation
```

### 5. Error
```
┌──────┐
│  ⚠️  │  Warning Yellow (#FFB800)
└──────┘  Shake animation
```

## Spotify Auth Flow

### Step 1: Onboarding
```
┌─────────────────────────────────────┐
│  🎵 Odkryj z Violet Radio           │
│                                     │
│  Dodawaj utwory z radia             │
│  bezpośrednio do Spotify!           │
│                                     │
│  [  Połącz ze Spotify  ]            │
│  [ Może później ]                   │
└─────────────────────────────────────┘
```

### Step 2: Success
```
┌─────────────────────────────────────┐
│         ✓                           │
│    Połączono ze Spotify!            │
│                                     │
│  Playlista:                         │
│  💜 Violet Radio Discoveries        │
│                                     │
│  [ Zamknij ]                        │
└─────────────────────────────────────┘
```

## Settings - Spotify Section

```
┌─────────────────────────────────────┐
│  Spotify                            │
│                                     │
│  🟢 Połączono: hexrox@gmail.com     │
│  [ Rozłącz ]                        │
│                                     │
│  Playlista docelowa                 │
│  💜 Violet Radio Discoveries    >   │
│                                     │
│  Auto-sync                     ○─   │
│  Tylko Wi-Fi                   ●─   │
│  Powiadomienia                 ●─   │
│                                     │
│  Historia: 47 utworów          >    │
└─────────────────────────────────────┘
```

## Snackbar Messages

### Success
```
┌───────────────────────────────┐
│ ✓ Dodano do Spotify!      [↗] │
└───────────────────────────────┘
Action: Otwórz w Spotify
```

### Error - Not Found
```
┌───────────────────────────────┐
│ ⚠️ Nie znaleziono na Spotify  │
└───────────────────────────────┘
```

### Not Authenticated
```
┌───────────────────────────────┐
│ 🔒 Zaloguj się do Spotify  [>]│
└───────────────────────────────┘
Action: Otwórz login
```

## Animations

### Button Click Sequence
```
1. Normal (💜)
2. Scale down 0.9x → 100ms
3. Loading spinner → 1-3s
4. Check mark (✓) → 300ms
5. Green color → 300ms
6. Hold 2s
7. Fade to normal → 500ms
```

### Error Shake
```
1. Center
2. Left 8dp → 50ms
3. Right 8dp → 50ms
4. Left 4dp → 50ms
5. Center → 50ms
Total: 200ms
```

## Color Specs

```kotlin
// Spotify Button
val SpotifyReady = Color(0xFF6750A4)
val SpotifyDisabled = Color(0xFFE7E0EC)
val SpotifyLoading = Color(0xFF79747E)
val SpotifySuccess = Color(0xFF1DB954)
val SpotifyError = Color(0xFFFFB800)
```

---

*UI Mockups z 💜*
