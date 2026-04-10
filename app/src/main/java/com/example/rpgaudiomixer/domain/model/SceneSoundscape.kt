package com.example.rpgaudiomixer.domain.model

data class SceneSoundscape(
    val sceneId: Long,
    val categoryId: Long,
    val categoryName: String,
    val themeLabel: String?,
    val iconResId: Int?,
    val isDemoContent: Boolean,
    val mixVolume: Float,
    val intensityLevel: IntensityLevel,
    val displayOrder: Int,
    val levelOneCount: Int,
    val levelTwoCount: Int,
    val levelThreeCount: Int,
)
