package com.example.rpgaudiomixer.domain.model

data class SoundscapeTrack(
    val id: Long = 0,
    val categoryId: Long = 0,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float,
)

data class SoundscapeTrackDraft(
    val id: Long? = null,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float,
)
