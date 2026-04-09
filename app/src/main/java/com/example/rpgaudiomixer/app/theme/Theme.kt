package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumOnGold,
    secondary = ArcanumPurple,
    onSecondary = Color.White,
    tertiary = ArcanumPink,
    onTertiary = Color.White,
    background = ArcanumBlack,
    onBackground = ArcanumOnSurface,
    surface = ArcanumSurface,
    onSurface = ArcanumOnSurface,
    surfaceVariant = ArcanumCard,
    onSurfaceVariant = ArcanumOnSurface,
    error = ArcanumErrorRed,
    onError = ArcanumBlack,
    outline = ArcanumMutedGold
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