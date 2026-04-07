package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Arcanum Audio Dark Color Scheme
 *
 * This app is dark-only with no dynamic color support.
 * Gold (#F2CA50) is the primary accent, black (#0A0A0A) is the background.
 */
private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = ArcanumBlack,
    primaryContainer = ArcanumGoldDark,
    onPrimaryContainer = ArcanumBlack,

    secondary = ArcanumPurple,
    onSecondary = ArcanumTextPrimary,
    secondaryContainer = ArcanumPurple,
    onSecondaryContainer = ArcanumTextPrimary,

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
        typography = ArcanumTypography,
        shapes = ArcanumShapes,
        content = content
    )
}