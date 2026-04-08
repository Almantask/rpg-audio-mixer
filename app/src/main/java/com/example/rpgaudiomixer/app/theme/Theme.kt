package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    secondary = ArcanumPurple,
    onSecondary = ArcanumTextPrimary,
    tertiary = ArcanumPink,
    onTertiary = ArcanumTextPrimary,
    background = ArcanumBlack,
    onBackground = ArcanumTextPrimary,
    surface = ArcanumSurface,
    onSurface = ArcanumTextPrimary,
    surfaceVariant = ArcanumCard,
    onSurfaceVariant = ArcanumTextSecondary,
    error = ArcanumError,
    onError = ArcanumBlack,
    outline = ArcanumTextDisabled
)

@Composable
fun ArcanumTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = Typography,
        shapes = ArcanumShapes,
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