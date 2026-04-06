package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    primaryContainer = ArcanumGoldDim,
    onPrimaryContainer = ArcanumGold,
    secondary = ArcanumPurple,
    onSecondary = ArcanumBlack,
    secondaryContainer = ArcanumPurpleMuted,
    onSecondaryContainer = ArcanumOnSurface,
    tertiary = ArcanumPink,
    onTertiary = ArcanumBlack,
    background = ArcanumBlack,
    onBackground = ArcanumOnSurface,
    surface = ArcanumSurface,
    onSurface = ArcanumOnSurface,
    surfaceVariant = ArcanumCard,
    onSurfaceVariant = ArcanumOnSurfaceMuted,
    error = ArcanumError,
    onError = ArcanumOnError,
    errorContainer = ArcanumErrorContainer,
    onErrorContainer = ArcanumError,
    outline = ArcanumGoldDim,
    outlineVariant = ArcanumCardElevated,
)

@Composable
fun RPGAudioMixerTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = ArcanumTypography,
        shapes = ArcanumShapes,
        content = content,
    )
}