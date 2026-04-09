package com.example.rpgaudiomixer.app.ui.library

import com.example.rpgaudiomixer.domain.library.FxTrack

data class FxLibraryUiState(
    val isLoading: Boolean = false,
    val tracks: List<FxTrack> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = ""
)
