package com.example.rpgaudiomixer.domain.model

data class SoundscapeTrack(
    val id: Long = 0L,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel = IntensityLevel.I,
    val mixVolumePercent: Int = 100,
)
