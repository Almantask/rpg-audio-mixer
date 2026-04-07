package com.example.rpgaudiomixer.domain.model

data class SceneSoundscape(
    val sceneId: Long,
    val categoryId: Long,
    val categoryName: String,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: IntensityLevel
)
