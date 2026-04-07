package com.example.rpgaudiomixer.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArcanumColorScheme = darkColorScheme(
    primary = ArcanumGold,
    secondary = ArcanumPurple,
    tertiary = ArcanumPink,
    background = BackgroundBlack,
    surface = SurfaceDark,
    surfaceVariant = CardSurface,
    error = ErrorRed,
    onPrimary = BackgroundBlack,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    onError = BackgroundBlack
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

// Backward compatibility alias
@Composable
fun RPGAudioMixerTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    ArcanumTheme(content = content)
}