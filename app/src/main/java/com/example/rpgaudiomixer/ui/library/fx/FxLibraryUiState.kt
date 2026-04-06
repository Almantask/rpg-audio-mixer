package com.example.rpgaudiomixer.ui.library.fx

import com.example.rpgaudiomixer.domain.model.FxTrack

sealed interface FxLibraryUiState {
    data object Loading : FxLibraryUiState
    data class Success(val tracks: List<FxTrack>, val searchQuery: String = "") : FxLibraryUiState
    data class Error(val message: String) : FxLibraryUiState
}
