package com.example.rpgaudiomixer.ui.sessionscenes

import com.example.rpgaudiomixer.domain.model.Scene

sealed class SessionScenesUiState {
    data object Loading : SessionScenesUiState()
    data class Success(
        val linkedScenes: List<Scene>,
        val availableScenes: List<Scene>
    ) : SessionScenesUiState()
    data class Error(val message: String) : SessionScenesUiState()
}
