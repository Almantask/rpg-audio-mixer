package com.example.rpgaudiomixer.ui.soundscapecomposer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SoundscapeCategoryComposerViewModel.Factory::class)
class SoundscapeCategoryComposerViewModel @AssistedInject constructor(
    private val soundscapeRepository: SoundscapeRepository,
    @Assisted private val categoryId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<SoundscapeComposerUiState>(SoundscapeComposerUiState.Loading)
    val uiState: StateFlow<SoundscapeComposerUiState> = _uiState.asStateFlow()

    private val _showImportDialog = MutableStateFlow(false)
    val showImportDialog: StateFlow<Boolean> = _showImportDialog.asStateFlow()

    init {
        loadCategoryAndTracks()
    }

    private fun loadCategoryAndTracks() {
        viewModelScope.launch {
            try {
                val category = soundscapeRepository.getCategoryById(categoryId)
                if (category == null) {
                    _uiState.value = SoundscapeComposerUiState.Error("Category not found")
                    return@launch
                }

                soundscapeRepository.observeTracksByCategory(categoryId)
                    .catch { e ->
                        _uiState.value = SoundscapeComposerUiState.Error(e.message ?: "Unknown error")
                    }
                    .collect { tracks ->
                        _uiState.value = SoundscapeComposerUiState.Success(category, tracks)
                    }
            } catch (e: Exception) {
                _uiState.value = SoundscapeComposerUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createTrack(
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float = 1.0f
    ) {
        viewModelScope.launch {
            try {
                soundscapeRepository.createTrack(categoryId, name, filePath, intensityLevel, mixVolume)
                hideImportDialog()
            } catch (e: Exception) {
                _uiState.value = SoundscapeComposerUiState.Error(e.message ?: "Failed to create track")
            }
        }
    }

    fun updateTrack(track: SoundscapeTrack) {
        viewModelScope.launch {
            try {
                soundscapeRepository.updateTrack(track)
            } catch (e: Exception) {
                _uiState.value = SoundscapeComposerUiState.Error(e.message ?: "Failed to update track")
            }
        }
    }

    fun deleteTrack(id: String) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteTrack(id)
            } catch (e: Exception) {
                _uiState.value = SoundscapeComposerUiState.Error(e.message ?: "Failed to delete track")
            }
        }
    }

    fun showImportDialog() {
        _showImportDialog.value = true
    }

    fun hideImportDialog() {
        _showImportDialog.value = false
    }

    @AssistedFactory
    interface Factory {
        fun create(categoryId: String): SoundscapeCategoryComposerViewModel
    }
}
