package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    primaryContainer = ArcanumGoldDark,
    onPrimaryContainer = ArcanumOffWhite,
    secondary = ArcanumAmber,
    onSecondary = ArcanumBlack,
    secondaryContainer = ArcanumGoldDim,
    onSecondaryContainer = ArcanumOffWhite,
    tertiary = ArcanumSliderPurple,
    onTertiary = ArcanumWhite,
    background = ArcanumBlack,
    onBackground = ArcanumWhite,
    surface = ArcanumDarkSurface,
    onSurface = ArcanumWhite,
    surfaceVariant = ArcanumCardSurface,
    onSurfaceVariant = ArcanumGrayLight,
    outline = ArcanumBorder,
    outlineVariant = ArcanumGoldDim,
    scrim = ArcanumBlack,
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