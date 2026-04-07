package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Arcanum Audio Dark Color Scheme
private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    primaryContainer = ArcanumGoldDim,
    onPrimaryContainer = ArcanumBlack,
    secondary = ArcanumPurple,
    onSecondary = ArcanumBlack,
    secondaryContainer = ArcanumPurple,
    onSecondaryContainer = ArcanumBlack,
    tertiary = ArcanumPink,
    onTertiary = ArcanumBlack,
    tertiaryContainer = ArcanumPink,
    onTertiaryContainer = ArcanumBlack,
    background = ArcanumBlack,
    onBackground = ArcanumGold,
    surface = ArcanumSurface,
    onSurface = ArcanumGold,
    surfaceVariant = ArcanumCard,
    onSurfaceVariant = ArcanumGold,
    error = ArcanumError,
    onError = ArcanumOnError
)

@Composable
fun ArcanumTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

// Alias for backward compatibility
@Composable
fun RPGAudioMixerTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ArcanumTheme(content = content)
}