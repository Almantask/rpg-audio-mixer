package com.example.rpgaudiomixer.domain.model

data class SoundscapeTrack(
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float = 0.5f // 0.0 to 1.0
)
