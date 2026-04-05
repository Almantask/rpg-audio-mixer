package com.example.rpgaudiomixer.domain.model

/**
 * Represents a soundscape category as instantiated within a specific scene.
 * Combines the base category metadata with scene-specific playback settings.
 */
data class SceneSoundscapeCategory(
    val sceneId: Long,
    val category: SoundscapeCategory,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: IntensityLevel
)
