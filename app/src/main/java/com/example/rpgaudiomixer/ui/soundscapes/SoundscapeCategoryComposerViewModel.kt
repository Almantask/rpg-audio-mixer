package com.example.rpgaudiomixer.ui.soundscapes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SoundscapeCategoryComposerUiState {
    data object Loading : SoundscapeCategoryComposerUiState
    data class Success(
        val category: SoundscapeCategory,
        val tracks: List<SoundscapeTrack>
    ) : SoundscapeCategoryComposerUiState
    data class Error(val message: String) : SoundscapeCategoryComposerUiState
}

@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Long = savedStateHandle.get<String>("categoryId")?.toLongOrNull() ?: 0L

    private val _uiState = MutableStateFlow<SoundscapeCategoryComposerUiState>(
        SoundscapeCategoryComposerUiState.Loading
    )
    val uiState: StateFlow<SoundscapeCategoryComposerUiState> = _uiState.asStateFlow()

    private val _showAddTrackDialog = MutableStateFlow(false)
    val showAddTrackDialog: StateFlow<Boolean> = _showAddTrackDialog.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentCategory: SoundscapeCategory? = null

    init {
        loadCategoryAndTracks()
    }

    private fun loadCategoryAndTracks() {
        viewModelScope.launch {
            try {
                val category = soundscapeRepository.getCategoryById(categoryId)
                if (category == null) {
                    _uiState.value = SoundscapeCategoryComposerUiState.Error("Category not found")
                    return@launch
                }
                currentCategory = category

                soundscapeRepository.observeTracksByCategory(categoryId)
                    .catch { e ->
                        _uiState.value = SoundscapeCategoryComposerUiState.Error(
                            e.message ?: "Failed to load tracks"
                        )
                    }
                    .collect { tracks ->
                        _uiState.value = SoundscapeCategoryComposerUiState.Success(
                            category = category,
                            tracks = tracks
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = SoundscapeCategoryComposerUiState.Error(
                    e.message ?: "Failed to load category"
                )
            }
        }
    }

    fun showAddTrackDialog() {
        _showAddTrackDialog.value = true
    }

    fun hideAddTrackDialog() {
        _showAddTrackDialog.value = false
    }

    fun addTrack(
        name: String,
        filePath: String,
        intensityLevel: IntensityLevel,
        mixVolume: Float = 0.5f
    ) {
        viewModelScope.launch {
            try {
                soundscapeRepository.createTrack(
                    categoryId = categoryId,
                    name = name,
                    filePath = filePath,
                    intensityLevel = intensityLevel,
                    mixVolume = mixVolume
                )
                hideAddTrackDialog()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to add track"
            }
        }
    }

    fun updateTrackIntensity(track: SoundscapeTrack, newIntensity: IntensityLevel) {
        viewModelScope.launch {
            try {
                soundscapeRepository.updateTrack(track.copy(intensityLevel = newIntensity))
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to update track intensity"
            }
        }
    }

    fun updateTrackMixVolume(track: SoundscapeTrack, newVolume: Float) {
        viewModelScope.launch {
            try {
                soundscapeRepository.updateTrack(track.copy(mixVolume = newVolume))
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to update track volume"
            }
        }
    }

    fun deleteTrack(track: SoundscapeTrack) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteTrack(track)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to delete track"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
