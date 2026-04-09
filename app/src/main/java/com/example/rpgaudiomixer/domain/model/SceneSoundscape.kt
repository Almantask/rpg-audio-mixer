package com.example.rpgaudiomixer.domain.model

/**
 * Domain model representing a soundscape category assigned to a scene.
 *
 * Includes the category details, intensity level, mix volume, and display order.
 */
data class SceneSoundscape(
    val sceneId: Long,
    val category: SoundscapeCategory,
    val intensityLevel: IntensityLevel,
    val mixVolumePercent: Int, // 0-100
    val displayOrder: Int
)
