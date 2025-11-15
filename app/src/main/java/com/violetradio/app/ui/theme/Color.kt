package com.violetradio.app.ui.theme

import androidx.compose.ui.graphics.Color

// Brand Colors - Light Theme
val VioletPrimary = Color(0xFF6750A4)
val VioletPrimaryVariant = Color(0xFF7F67BE)
val VioletSecondary = Color(0xFF7F67BE)
val LightBackground = Color(0xFFFDFCFF)
val LightSurface = Color(0xFFFFFFFF)
val OnPrimaryLight = Color(0xFFFFFFFF)

// Brand Colors - Dark Theme
val VioletPrimaryDark = Color(0xFF7F67BE)
val VioletSecondaryDark = Color(0xFF9D7FC8)
val DarkBackground = Color(0xFF1C1B1F)
val DarkSurface = Color(0xFF2B2930)
val OnPrimaryDark = Color(0xFF1C1B1F)

// Semantic Colors
val SuccessGreen = Color(0xFF4CAF50)   // Live indicator
val ErrorRed = Color(0xFFF44336)       // Offline/Error
val WarningOrange = Color(0xFFFF9800)  // Alerts
val SpotifyGreen = Color(0xFF1DB954)   // Spotify branding
val YouTubeRed = Color(0xFFFF0000)     // YouTube branding

// Neutral Colors
val Gray50 = Color(0xFFF5F5F5)
val Gray100 = Color(0xFFE7E0EC)
val Gray200 = Color(0xFFCCC2DC)
val Gray300 = Color(0xFFB0A7C0)
val Gray400 = Color(0xFF958DA5)
val Gray500 = Color(0xFF7A7289)
val Gray600 = Color(0xFF625B71)
val Gray700 = Color(0xFF49454F)
val Gray800 = Color(0xFF332D41)
val Gray900 = Color(0xFF1C1B1F)

// Status Colors for Spotify Button
val SpotifyDisabled = Color(0xFFE7E0EC)
val SpotifyReady = VioletPrimary
val SpotifySuccess = SpotifyGreen
val SpotifyError = Color(0xFFFFB800)

// Status Colors for YouTube Button
val YouTubeDisabled = Color(0xFFE7E0EC)
val YouTubeReady = YouTubeRed
val YouTubeSuccess = SuccessGreen
val YouTubeNotFound = WarningOrange
