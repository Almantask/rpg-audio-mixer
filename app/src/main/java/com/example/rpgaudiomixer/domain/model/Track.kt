package com.example.rpgaudiomixer.domain.model

data class Track(
    val id: Long = 0L,
    val categoryId: Long,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float = 1.0f,
)
