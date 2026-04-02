package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.LibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val categories: List<SoundscapeCategory> = emptyList(),
    val fxTracks: List<FXTrack> = emptyList(),
    val playingFxId: Long? = null,
    val editingFxTrack: FXTrack? = null,
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            libraryRepository.getAllCategories().collect { cats ->
                _uiState.value = _uiState.value.copy(categories = cats)
            }
        }
        viewModelScope.launch {
            libraryRepository.getAllFXTracks().collect { fx ->
                _uiState.value = _uiState.value.copy(fxTracks = fx)
            }
        }
    }

    fun importFXTrack(name: String, filePath: String) {
        viewModelScope.launch {
            libraryRepository.upsertFXTrack(FXTrack(name = name, filePath = filePath))
        }
    }

    fun openEditFX(fxTrack: FXTrack) {
        _uiState.value = _uiState.value.copy(editingFxTrack = fxTrack)
    }

    fun closeEditFX() {
        _uiState.value = _uiState.value.copy(editingFxTrack = null)
    }

    fun saveEditFX(fxTrack: FXTrack) {
        viewModelScope.launch {
            libraryRepository.updateFXTrack(fxTrack)
            closeEditFX()
        }
    }

    fun deleteFXTrack(id: Long) {
        viewModelScope.launch {
            libraryRepository.deleteFXTrack(id)
        }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            libraryRepository.upsertCategory(SoundscapeCategory(name = name))
        }
    }

    fun deleteCategory(id: Long) {
        viewModelScope.launch {
            libraryRepository.deleteCategory(id)
        }
    }
}
