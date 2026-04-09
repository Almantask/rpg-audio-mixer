package com.example.rpgaudiomixer.ui.sessions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionCoverArtSelectionRepository @Inject constructor() {
    private val _selectedCoverArtUri = MutableStateFlow<String?>(null)
    val selectedCoverArtUri: StateFlow<String?> = _selectedCoverArtUri.asStateFlow()

    fun updateSelectedCoverArt(uri: String?) {
        _selectedCoverArtUri.value = uri
    }

    fun reset() {
        _selectedCoverArtUri.value = null
    }
}
