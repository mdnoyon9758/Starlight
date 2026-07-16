package com.barcodereader.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// iOS 26 Light Theme
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    errorContainer = ErrorContainer,
    background = BackgroundLight,
    onBackground = Color(0xFF000000),
    surface = SurfaceContainerLight,
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFE5E5EA),
    onSurfaceVariant = Color(0xFF3C3C43),
    outline = Color(0xFFC6C6C8),
    outlineVariant = Color(0xFFD1D1D6)
)

// iOS 26 Dark Theme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0A84FF),           // iOS Blue Dark
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF0040DD),
    onPrimaryContainer = Color(0xFFD6E3FF),
    secondary = Color(0xFF5E5CE6),         // iOS Purple Dark
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF3634A3),
    onSecondaryContainer = Color(0xFFD9D7FF),
    tertiary = Color(0xFFFF9F0A),          // iOS Orange Dark
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFF7C5800),
    onTertiaryContainer = Color(0xFFFFDDB3),
    error = Color(0xFFFF453A),             // iOS Red Dark
    errorContainer = Color(0xFF890001),
    background = Color(0xFF000000),         // True Black
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1C1C1E),           // iOS Dark Gray
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFE5E5EA),
    outline = Color(0xFF48484A),
    outlineVariant = Color(0xFF3A3A3C)
)

@Composable
fun BarcodeReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            
            // iOS-style navigation bar
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
