package com.example.rpgaudiomixer.domain.model

data class SoundscapeTrack(
    val id: Long,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolumePercent: Int,
    val displayOrder: Int,
)
