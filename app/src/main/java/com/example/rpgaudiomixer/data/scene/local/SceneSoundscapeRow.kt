package com.example.rpgaudiomixer.data.scene.local

data class SceneSoundscapeRow(
    val sceneId: Long,
    val categoryId: Long,
    val categoryName: String,
    val themeLabel: String?,
    val iconResId: Int?,
    val isDemoContent: Boolean,
    val mixVolume: Float,
    val intensityLevel: Int,
    val displayOrder: Int,
    val levelOneCount: Int,
    val levelTwoCount: Int,
    val levelThreeCount: Int,
)
