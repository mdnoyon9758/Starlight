package com.measuremate.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkScheme = darkColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = Background,
    surface = Card,
    error = Error,
    onPrimary = Text,
    onSecondary = Text,
    onBackground = Text,
    onSurface = Text
)

private val LightScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    tertiary = Accent,
    background = androidx.compose.ui.graphics.Color(0xFFF4F6F8),
    surface = androidx.compose.ui.graphics.Color.White,
    error = Error
)

@Composable
fun MeasureMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

fun industrialScheme(): ColorScheme = DarkScheme
