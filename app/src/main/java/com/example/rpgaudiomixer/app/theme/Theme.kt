package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumPrimary,
    onPrimary = ArcanumOnPrimary,
    secondary = ArcanumSecondary,
    onSecondary = ArcanumOnSecondary,
    tertiary = ArcanumTertiary,
    onTertiary = ArcanumOnTertiary,
    error = ArcanumError,
    onError = ArcanumOnError,
    background = ArcanumBackground,
    surface = ArcanumSurface,
    surfaceVariant = ArcanumSurfaceVariant,
    onSurface = ArcanumOnSurface,
    onSurfaceVariant = ArcanumOnSurfaceVariant,
    outline = ArcanumOutline,
)

@Composable
fun ArcanumTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcanumColorScheme,
        typography = Typography,
        shapes = ArcanumShapes,
        content = content,
    )
}