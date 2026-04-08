package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    onPrimary = BackgroundBlack,
    primaryContainer = ArcanumGoldDim,
    onPrimaryContainer = TextGold,

    secondary = PurpleAccent,
    onSecondary = BackgroundBlack,
    secondaryContainer = PurpleAccent,
    onSecondaryContainer = TextWhite,

    tertiary = PinkAccent,
    onTertiary = BackgroundBlack,

    background = BackgroundBlack,
    onBackground = TextWhite,

    surface = SurfaceBlack,
    onSurface = TextWhite,
    surfaceVariant = CardSurface,
    onSurfaceVariant = TextGray,

    error = ErrorRed,
    onError = BackgroundBlack,
    errorContainer = ErrorDark,
    onErrorContainer = ErrorRed,

    outline = TextDim,
    outlineVariant = CardSurface
)

/**
 * Arcanum Audio theme - dark only, gold/amber accents
 */
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

// Backwards compatibility alias
@Composable
fun RPGAudioMixerTheme(
    content: @Composable () -> Unit
) = ArcanumTheme(content)