package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.model.Scene

sealed class ScenesUiState {
    data object Loading : ScenesUiState()
    data class Success(val scenes: List<Scene>) : ScenesUiState()
    data class Error(val message: String) : ScenesUiState()
}
