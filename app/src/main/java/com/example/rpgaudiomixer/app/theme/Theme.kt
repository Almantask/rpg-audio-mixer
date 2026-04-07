package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    primaryContainer = ArcanumGoldVariant,
    onPrimaryContainer = ArcanumBlack,

    secondary = ArcanumPurple,
    onSecondary = ArcanumBlack,
    secondaryContainer = ArcanumPurple,
    onSecondaryContainer = ArcanumTextPrimary,

    tertiary = ArcanumPink,
    onTertiary = ArcanumBlack,

    background = ArcanumBlack,
    onBackground = ArcanumTextPrimary,

    surface = ArcanumSurface,
    onSurface = ArcanumTextPrimary,
    surfaceVariant = ArcanumCard,
    onSurfaceVariant = ArcanumTextSecondary,

    error = ArcanumError,
    onError = ArcanumBlack,

    outline = ArcanumTextMuted,
    outlineVariant = ArcanumTextSecondary
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