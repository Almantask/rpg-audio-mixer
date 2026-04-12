package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Arcanum Audio – dark-only color scheme

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    primaryContainer = ArcanumGoldDark,
    onPrimaryContainer = ArcanumGold,
    secondary = ArcanumPink,
    onSecondary = ArcanumBlack,
    secondaryContainer = ArcanumSurfaceVariant,
    onSecondaryContainer = ArcanumPink,
    tertiary = ArcanumPurple,
    onTertiary = ArcanumBlack,
    tertiaryContainer = ArcanumSurfaceVariant,
    onTertiaryContainer = ArcanumPurple,
    error = ArcanumError,
    onError = ArcanumOnError,
    errorContainer = ArcanumErrorContainer,
    onErrorContainer = ArcanumError,
    background = ArcanumBlack,
    onBackground = ArcanumOnSurface,
    surface = ArcanumSurface,
    onSurface = ArcanumOnSurface,
    surfaceVariant = ArcanumSurfaceVariant,
    onSurfaceVariant = ArcanumOnSurfaceVariant,
    outline = ArcanumOnSurfaceVariant,
    outlineVariant = ArcanumSurfaceVariant,
)

@Composable
fun RPGAudioMixerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = Typography,
        content = content,
    )
}