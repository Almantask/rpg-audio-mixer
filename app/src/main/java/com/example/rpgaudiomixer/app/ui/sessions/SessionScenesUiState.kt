package com.example.rpgaudiomixer.app.ui.sessions

import com.example.rpgaudiomixer.domain.scene.Scene

sealed class SessionScenesUiState {
    object Loading : SessionScenesUiState()
    data class Success(
        val linkedScenes: List<Scene>,
        val allAvailableScenes: List<Scene> = emptyList()
    ) : SessionScenesUiState()
    data class Error(val message: String) : SessionScenesUiState()
}
