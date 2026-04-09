package com.example.rpgaudiomixer.app.ui.scenes

import com.example.rpgaudiomixer.domain.scene.Scene

sealed class ScenesUiState {
    object Loading : ScenesUiState()
    data class Success(val scenes: List<Scene>) : ScenesUiState()
    data class Error(val message: String) : ScenesUiState()
}
