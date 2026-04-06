package com.example.rpgaudiomixer.ui.library.soundscapes

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

sealed interface SoundscapeLibraryUiState {
    data object Loading : SoundscapeLibraryUiState
    data class Success(val categories: List<SoundscapeCategory>) : SoundscapeLibraryUiState
    data class Error(val message: String) : SoundscapeLibraryUiState
}
