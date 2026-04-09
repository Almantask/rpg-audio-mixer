package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBackground,
    primaryContainer = ArcanumGoldMuted,
    onPrimaryContainer = ArcanumBackground,
    secondary = ArcanumAccent,
    onSecondary = ArcanumBackground,
    tertiary = ArcanumAccentSecondary,
    onTertiary = ArcanumBackground,
    background = ArcanumBackground,
    onBackground = ArcanumOnDark,
    surface = ArcanumSurface,
    onSurface = ArcanumOnDark,
    surfaceVariant = ArcanumSurfaceVariant,
    onSurfaceVariant = ArcanumOnSurfaceMuted,
    error = ArcanumError,
    onError = ArcanumBackground,
    errorContainer = ArcanumErrorContainer,
    onErrorContainer = ArcanumError,
)

@Composable
fun RPGAudioMixerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
