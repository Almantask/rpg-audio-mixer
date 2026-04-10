package com.example.rpgaudiomixer.data.soundscape.local

data class SoundscapeMostPlayedTrackRow(
    val id: Long,
    val categoryId: Long,
    val categoryName: String,
    val name: String,
    val filePath: String,
    val intensityLevel: Int,
    val mixVolumePercent: Int,
    val displayOrder: Int,
    val playCount: Int,
)
