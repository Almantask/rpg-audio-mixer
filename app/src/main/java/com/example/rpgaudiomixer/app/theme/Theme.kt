package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumOnGold,
    primaryContainer = ArcanumGoldDim,
    onPrimaryContainer = ArcanumOnSurface,
    secondary = ArcanumPurple,
    onSecondary = Color.White,
    secondaryContainer = ArcanumPurpleLight.copy(alpha = 0.2f),
    onSecondaryContainer = ArcanumPurpleLight,
    tertiary = ArcanumPink,
    onTertiary = Color.White,
    tertiaryContainer = ArcanumPink.copy(alpha = 0.15f),
    onTertiaryContainer = ArcanumPinkLight,
    background = ArcanumBlack,
    onBackground = ArcanumOnSurface,
    surface = ArcanumSurface,
    onSurface = ArcanumOnSurface,
    surfaceVariant = ArcanumSurfaceVariant,
    onSurfaceVariant = ArcanumOnSurfaceVariant,
    error = Color(0xFFCF6679),
    onError = Color.Black,
    outline = ArcanumGoldDim.copy(alpha = 0.5f),
    outlineVariant = ArcanumDisabled,
)

@Composable
fun RPGAudioMixerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = ArcanumTypography,
        content = content
    )
}