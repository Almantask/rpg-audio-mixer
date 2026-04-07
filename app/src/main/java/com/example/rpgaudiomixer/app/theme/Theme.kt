package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    primaryContainer = ArcanumAmber,
    onPrimaryContainer = ArcanumBlack,

    secondary = ArcanumPurple,
    onSecondary = ArcanumTextPrimary,
    secondaryContainer = ArcanumPurple,
    onSecondaryContainer = ArcanumTextPrimary,

    tertiary = ArcanumPink,
    onTertiary = ArcanumTextPrimary,

    background = ArcanumBlack,
    onBackground = ArcanumTextPrimary,

    surface = ArcanumDarkSurface,
    onSurface = ArcanumTextPrimary,
    surfaceVariant = ArcanumCardSurface,
    onSurfaceVariant = ArcanumTextSecondary,

    error = ArcanumError,
    onError = ArcanumBlack,
    errorContainer = ArcanumErrorDark,
    onErrorContainer = ArcanumTextPrimary,

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