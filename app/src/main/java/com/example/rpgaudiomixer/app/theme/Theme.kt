package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    secondary = ArcanumAccent,
    tertiary = ArcanumAccent,
    background = ArcanumBlack,
    surface = ArcanumSurface,
    surfaceVariant = ArcanumSurfaceVariant,
    onPrimary = ArcanumBlack,
    onSecondary = ArcanumBlack,
    onTertiary = ArcanumBlack,
    onBackground = ArcanumOnDark,
    onSurface = ArcanumOnDark,
    error = ArcanumError,
    onError = ArcanumBlack,
)

@Composable
fun RPGAudioMixerTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = Typography,
        content = content,
    )
}
