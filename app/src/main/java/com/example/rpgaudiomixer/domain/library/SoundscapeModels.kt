package com.example.rpgaudiomixer.domain.library

enum class IntensityLevel(val value: Int) {
    I(1),
    II(2),
    III(3);

    companion object {
        fun fromInt(value: Int): IntensityLevel = values().first { it.value == value }
    }
}

data class SoundscapeTrack(
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float = 1.0f
)

data class SoundscapeCategory(
    val id: Long = 0,
    val name: String,
    val iconResId: Int? = null,
    val themeLabel: String? = null,
    val tracks: List<SoundscapeTrack> = emptyList()
)
