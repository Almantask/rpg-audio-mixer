package com.example.rpgaudiomixer.ui.activescene

import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

sealed interface ActiveSceneSoundscapesUiState {
    data object Loading : ActiveSceneSoundscapesUiState
    data class Success(
        val categoryStates: List<CategoryState>,
        val masterVolume: Float = 1.0f,
    ) : ActiveSceneSoundscapesUiState
    data class Error(val message: String) : ActiveSceneSoundscapesUiState
}

data class CategoryState(
    val category: SoundscapeCategory,
    val isPlaying: Boolean = false,
    val currentTrackName: String? = null,
    val mixVolume: Float = 1.0f,
    val intensity: IntensityLevel = IntensityLevel.I,
    val displayOrder: Int = 0,
) {
    val tracksForCurrentIntensity: List<SoundscapeTrack>
        get() = category.tracks.filter { it.intensityLevel == intensity }

    val availableIntensities: Set<IntensityLevel>
        get() = category.tracks.map { it.intensityLevel }.toSet()
}
