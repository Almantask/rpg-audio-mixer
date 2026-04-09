package com.example.rpgaudiomixer.ui.soundscapecomposer

import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

sealed interface SoundscapeComposerUiState {
    data object Loading : SoundscapeComposerUiState
    data class Success(
        val category: SoundscapeCategory,
        val tracks: List<SoundscapeTrack>
    ) : SoundscapeComposerUiState
    data class Error(val message: String) : SoundscapeComposerUiState
}
