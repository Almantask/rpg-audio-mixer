package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBackground,
    secondary = ArcanumAccent,
    tertiary = ArcanumAccentSecondary,
    background = ArcanumBackground,
    onBackground = ArcanumOnDark,
    surface = ArcanumSurface,
    onSurface = ArcanumOnDark,
    surfaceVariant = ArcanumSurfaceVariant,
    onSurfaceVariant = ArcanumMutedText,
    error = ArcanumError,
)

@Composable
fun RPGAudioMixerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
