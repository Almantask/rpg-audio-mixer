package com.example.rpgaudiomixer.domain.model

/**
 * Represents a single soundscape track within a category.
 * Each track has an intensity level and mix volume for composition.
 */
data class SoundscapeTrack(
    val id: String,
    val categoryId: String,
    val name: String,
    val filePath: String,
    val intensityLevel: IntensityLevel,
    val mixVolume: Float = 1.0f // Range: 0.0 to 1.0
)
