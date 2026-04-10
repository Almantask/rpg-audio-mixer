package com.example.rpgaudiomixer.domain.model

data class MostPlayedSoundscapeTrack(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolumePercent: Int,
    val displayOrder: Int,
    val playCount: Int,
)
