package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(val value: Int) {
    I(1), II(2), III(3)
}

data class SoundscapeCategory(
    val id: Long = 0,
    val name: String,
    val iconResId: Int? = null,
    val themeLabel: String? = null,
    val trackCounts: Map<IntensityLevel, Int> = emptyMap(),
    val deletedAt: Long? = null
)

data class SoundscapeTrack(
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float = 1.0f,
    val playCount: Int = 0
)
