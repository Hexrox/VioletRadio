# 🎨 Violet Radio - Design Guidelines

## Brand Identity

**Nazwa:** Violet Radio
**Tagline:** "Twoje fale, twój świat"
**Dlaczego Violet?** Fiolet = kreatywność, muzyka, elegancja

## Color Palette

### Light Theme
```kotlin
Primary = Color(0xFF6750A4)        // Violet
OnPrimary = Color(0xFFFFFFFF)
Secondary = Color(0xFF7F67BE)
Background = Color(0xFFFDFCFF)
Surface = Color(0xFFFFFFFF)
```

### Dark Theme
```kotlin
Primary = Color(0xFF7F67BE)
OnPrimary = Color(0xFF1C1B1F)
Secondary = Color(0xFF9D7FC8)
Background = Color(0xFF1C1B1F)
Surface = Color(0xFF2B2930)
```

### Semantic Colors
```kotlin
Success = Color(0xFF4CAF50)    // Live indicator
Error = Color(0xFFF44336)      // Offline
Warning = Color(0xFFFF9800)    // Alerts
SpotifyGreen = Color(0xFF1DB954)
```

## Typography

**Font:** Roboto (system font)

```kotlin
displayLarge: 57sp / Regular
headlineLarge: 32sp / Regular
titleLarge: 22sp / Medium
bodyLarge: 16sp / Regular
labelLarge: 14sp / Medium
```

## Components

### Material Cards
- Elevation: 1-3dp
- Border Radius: 12dp
- Padding: 16dp
- Glassmorphism effect dla discover cards

### Buttons
- FAB: 56x56dp, radius 16dp, elevation 6dp
- FAB Mini: 40x40dp, radius 12dp
- Touch target: minimum 48x48dp

### Icons
- Size: 24x24dp
- Style: Material Icons Rounded
- Touch target: 48x48dp

## Screens

### 1. Home Screen
```
- Top App Bar (64dp)
- Now Playing Card (sticky)
  - Station avatar (56dp)
  - Play controls + Spotify button
- Ulubione (horizontal scroll, chips 140x160dp)
- Odkryj (material cards)
- Ostatnio słuchane (list items)
- FAB (bottom right, 56dp)
```

### 2. Player Screen
```
- Top bar z back button
- Album art (280x280dp, rounded 28dp)
- Track info (centered)
- Waveform animation (48dp)
- Controls (⏮️ ⏸️ ⏭️)
- Secondary actions (❤️ 💜 ⏲️ 🔗)
```

### 3. Browse Screen
```
- Search bar (56dp height, rounded 28dp)
- Filter chips (horizontal scroll)
- Station list (cards z avatarami 56dp)
```

## Spotify Button States

1. **Disabled** - Szary (#E7E0EC), brak metadanych
2. **Ready** - Fioletowy (#6750A4), gotowy do dodania
3. **Loading** - Progress indicator
4. **Success** - Zielony (#1DB954), ✓ przez 2s
5. **Error** - Żółty (#FFB800), shake animation
6. **Not Authenticated** - Tertiary, 🔒

## Animations

- **Duration:** 300ms (default)
- **Easing:** FastOutSlowIn
- **Ripple:** 150ms
- **Waveform:** Continuous 1200ms cycle

## Accessibility

- Contrast ratio: 4.5:1 (normal), 3:1 (large text)
- Touch targets: 48x48dp minimum
- VoiceOver/TalkBack support
- Dynamic Type up to 200%

## App Icon

**Adaptive Icon:**
- Foreground: Biała fala radiowa "V" shape
- Background: Gradient #6750A4 → #7F67BE
- Safe zone: 66dp z 108dp

**Notification Icon:**
- 24x24dp, biały outline
- Prosty, rozpoznawalny w małym rozmiarze

---

*Design z 💜 dla Violet Radio*
