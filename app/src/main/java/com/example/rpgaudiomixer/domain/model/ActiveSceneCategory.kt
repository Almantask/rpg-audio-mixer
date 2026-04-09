package com.example.rpgaudiomixer.domain.model

data class ActiveSceneCategory(
    val categoryId: Long,
    val categoryName: String,
    val displayOrder: Int,
    val mixVolume: Float,
    val intensityLevel: IntensityLevel,
    val isPlaying: Boolean = false,
    val currentTrackName: String? = null,
    val availableTracks: List<SoundscapeTrack> = emptyList()
)
