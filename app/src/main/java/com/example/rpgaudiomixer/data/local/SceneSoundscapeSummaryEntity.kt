package com.example.rpgaudiomixer.data.local

data class SceneSoundscapeSummaryEntity(
    val sceneId: Long,
    val categoryId: Long,
    val categoryName: String,
    val iconResId: Int?,
    val themeLabel: String?,
    val levelOneTrackCount: Int,
    val levelTwoTrackCount: Int,
    val levelThreeTrackCount: Int,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: Int,
)
