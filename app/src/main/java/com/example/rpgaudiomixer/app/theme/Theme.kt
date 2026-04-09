package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    secondary = ArcanumPurple,
    onSecondary = ArcanumOnDark,
    tertiary = ArcanumPink,
    onTertiary = ArcanumBlack,
    background = ArcanumBlack,
    onBackground = ArcanumOnDark,
    surface = ArcanumSurface,
    onSurface = ArcanumOnDark,
    surfaceVariant = ArcanumSurfaceVariant,
    onSurfaceVariant = ArcanumAmber,
    outline = ArcanumOutline,
    error = ArcanumError,
    onError = ArcanumOnError,
)

@Composable
fun RPGAudioMixerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = Typography,
        shapes = ArcanumShapes,
        content = content
    )
}
