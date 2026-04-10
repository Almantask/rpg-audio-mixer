package com.example.rpgaudiomixer.domain.model

data class SceneSoundscape(
    val sceneId: Long,
    val categoryId: Long,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: IntensityLevel,
    val category: SoundscapeCategory,
)
