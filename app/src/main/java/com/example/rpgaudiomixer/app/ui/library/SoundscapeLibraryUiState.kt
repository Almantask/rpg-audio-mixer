package com.example.rpgaudiomixer.app.ui.library

import com.example.rpgaudiomixer.domain.library.SoundscapeCategory

sealed class SoundscapeLibraryUiState {
    object Loading : SoundscapeLibraryUiState()
    data class Success(val categories: List<SoundscapeCategory>) : SoundscapeLibraryUiState()
    data class Error(val message: String) : SoundscapeLibraryUiState()
}
