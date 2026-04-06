package com.example.rpgaudiomixer.domain.model

enum class IntensityLevel(val value: Int, val label: String) {
    I(1, "I"),
    II(2, "II"),
    III(3, "III"),
}

data class SoundscapeCategory(
    val id: Long = 0,
    val name: String,
    val tracks: List<SoundscapeTrack> = emptyList(),
)

data class SoundscapeTrack(
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel = IntensityLevel.I,
    val mixVolume: Float = 1.0f,
)
