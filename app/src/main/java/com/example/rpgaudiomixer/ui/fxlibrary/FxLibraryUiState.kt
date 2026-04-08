package com.example.rpgaudiomixer.ui.fxlibrary

import com.example.rpgaudiomixer.domain.model.FxTrack

sealed class FxLibraryUiState {
    data object Loading : FxLibraryUiState()
    data class Success(val tracks: List<FxTrack>) : FxLibraryUiState()
    data class Error(val message: String) : FxLibraryUiState()
}
