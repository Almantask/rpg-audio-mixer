package com.example.rpgaudiomixer.app.ui.library

import com.example.rpgaudiomixer.domain.library.SoundscapeCategory
import com.example.rpgaudiomixer.domain.library.SoundscapeTrack

sealed class SoundscapeCategoryComposerUiState {
    object Loading : SoundscapeCategoryComposerUiState()
    data class Success(
        val category: SoundscapeCategory,
        val tracks: List<SoundscapeTrack>
    ) : SoundscapeCategoryComposerUiState()
    data class Error(val message: String) : SoundscapeCategoryComposerUiState()
}
