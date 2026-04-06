package com.example.rpgaudiomixer.ui.scenes

import com.example.rpgaudiomixer.domain.model.Scene

sealed interface ScenesUiState {
    data object Loading : ScenesUiState
    data class Success(val scenes: List<Scene>, val activeTagFilter: String? = null) : ScenesUiState
    data class Error(val message: String) : ScenesUiState
}
