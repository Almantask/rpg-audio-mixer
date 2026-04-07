package com.example.rpgaudiomixer.domain.model

data class SoundscapeTrack(
    val id: Long = 0,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float = 1.0f,
    val playCount: Int = 0
)
