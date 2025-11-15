package com.violetradio.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light Color Scheme for Violet Radio
 */
private val LightColorScheme = lightColorScheme(
    primary = VioletPrimary,
    onPrimary = OnPrimaryLight,
    primaryContainer = VioletSecondary,
    onPrimaryContainer = Gray900,
    secondary = VioletSecondary,
    onSecondary = OnPrimaryLight,
    secondaryContainer = Gray100,
    onSecondaryContainer = Gray900,
    tertiary = SpotifyGreen,
    onTertiary = Color.White,
    tertiaryContainer = Gray100,
    onTertiaryContainer = Gray900,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFF0EE),
    onErrorContainer = Color(0xFF8B0000),
    background = LightBackground,
    onBackground = Gray900,
    surface = LightSurface,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray700,
    outline = Gray400,
    outlineVariant = Gray200,
    scrim = Gray900.copy(alpha = 0.32f),
    inverseSurface = Gray800,
    inverseOnSurface = Gray100,
    inversePrimary = VioletPrimaryDark
)

/**
 * Dark Color Scheme for Violet Radio
 */
private val DarkColorScheme = darkColorScheme(
    primary = VioletPrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = VioletSecondaryDark,
    onPrimaryContainer = Gray100,
    secondary = VioletSecondaryDark,
    onSecondary = OnPrimaryDark,
    secondaryContainer = Gray700,
    onSecondaryContainer = Gray100,
    tertiary = SpotifyGreen,
    onTertiary = Gray900,
    tertiaryContainer = Gray700,
    onTertiaryContainer = Gray100,
    error = ErrorRed,
    onError = Gray900,
    errorContainer = Color(0xFF5C0000),
    onErrorContainer = Color(0xFFFFB4AB),
    background = DarkBackground,
    onBackground = Gray100,
    surface = DarkSurface,
    onSurface = Gray100,
    surfaceVariant = Gray700,
    onSurfaceVariant = Gray300,
    outline = Gray500,
    outlineVariant = Gray700,
    scrim = Color.Black.copy(alpha = 0.32f),
    inverseSurface = Gray100,
    inverseOnSurface = Gray800,
    inversePrimary = VioletPrimary
)

/**
 * Main theme for Violet Radio
 *
 * @param darkTheme Whether to use dark theme
 * @param dynamicColor Whether to use Material You dynamic colors (Android 12+)
 * @param content The composable content
 */
@Composable
fun VioletRadioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color is available on Android 12+
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Use predefined dark or light color scheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Update system bars
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VioletTypography,
        content = content
    )
}
