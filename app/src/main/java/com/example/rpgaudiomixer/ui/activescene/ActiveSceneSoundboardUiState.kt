package com.example.rpgaudiomixer.ui.activescene

import com.example.rpgaudiomixer.domain.model.FxTrack

sealed interface ActiveSceneSoundboardUiState {
    data object Loading : ActiveSceneSoundboardUiState
    data class Success(
        val fxTracks: List<FxTrack>,
        val masterVolume: Float = 1.0f,
    ) : ActiveSceneSoundboardUiState
    data class Error(val message: String) : ActiveSceneSoundboardUiState
}
