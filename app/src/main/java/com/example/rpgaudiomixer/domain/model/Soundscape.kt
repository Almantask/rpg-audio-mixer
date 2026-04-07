package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(val value: Int) {
    LEVEL_I(1),
    LEVEL_II(2),
    LEVEL_III(3);

    companion object {
        fun fromValue(value: Int) = entries.firstOrNull { it.value == value } ?: LEVEL_I
    }
}

data class SoundscapeCategory(
    val id: Long = 0,
    val name: String,
    val iconResId: Int? = null,
    val themeLabel: String? = null
)

data class SoundscapeTrack(
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float = 0.5f,
    val playCount: Int = 0
)
