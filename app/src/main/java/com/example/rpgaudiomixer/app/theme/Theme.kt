package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Arcanum Audio color scheme (dark-only)
private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    primaryContainer = ArcanumGoldDim,
    onPrimaryContainer = ArcanumTextPrimary,

    secondary = ArcanumPurple,
    onSecondary = ArcanumTextPrimary,
    secondaryContainer = ArcanumPurple,
    onSecondaryContainer = ArcanumTextPrimary,

    tertiary = ArcanumPink,
    onTertiary = ArcanumTextPrimary,

    background = ArcanumBlack,
    onBackground = ArcanumTextPrimary,

    surface = ArcanumSurfaceDark,
    onSurface = ArcanumTextPrimary,
    surfaceVariant = ArcanumCardDark,
    onSurfaceVariant = ArcanumTextSecondary,

    error = ArcanumError,
    onError = ArcanumBlack,

    outline = ArcanumTextMuted,
    outlineVariant = ArcanumGoldDim
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