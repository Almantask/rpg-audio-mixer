package com.example.rpgaudiomixer.domain.model

data class SceneSoundscape(
    val sceneId: Long,
    val categoryId: Long,
    val categoryName: String,
    val displayOrder: Int,
    val mixVolumePercent: Int = 100,
    val intensityLevel: IntensityLevel = IntensityLevel.I,
)
