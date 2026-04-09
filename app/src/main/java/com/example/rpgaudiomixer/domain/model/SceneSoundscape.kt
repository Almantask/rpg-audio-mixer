package com.example.rpgaudiomixer.domain.model

data class SceneSoundscape(
    val sceneId: Long,
    val category: SoundscapeCategory,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: IntensityLevel,
)
