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

/**
 * UI State for Soundscape Category Composer screen.
 */
sealed class SoundscapeCategoryComposerUiState {
    object Loading : SoundscapeCategoryComposerUiState()
    data class Success(
        val category: SoundscapeCategory,
        val tracks: List<SoundscapeTrack>
    ) : SoundscapeCategoryComposerUiState()
    data class Error(val message: String) : SoundscapeCategoryComposerUiState()
}

/**
 * ViewModel for Soundscape Category Composer screen.
 *
 * Manages track CRUD operations within a soundscape category.
 */
@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Long = checkNotNull(savedStateHandle.get<Long>("categoryId")) {
        "categoryId is required"
    }

    private val _uiState = MutableStateFlow<SoundscapeCategoryComposerUiState>(
        SoundscapeCategoryComposerUiState.Loading
    )
    val uiState: StateFlow<SoundscapeCategoryComposerUiState> = _uiState.asStateFlow()

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

    /**
     * Add a new track to the category.
     */
    fun addTrack(name: String, filePath: String, intensityLevel: IntensityLevel, mixVolume: Float = 0.75f) {
        viewModelScope.launch {
            try {
                soundscapeRepository.createTrack(categoryId, name, filePath, intensityLevel, mixVolume)
            } catch (e: Exception) {
                _uiState.value = SoundscapeCategoryComposerUiState.Error(
                    e.message ?: "Failed to add track"
                )
            }
        }
    }

    /**
     * Update track intensity level.
     */
    fun updateTrackIntensity(track: SoundscapeTrack, newIntensity: IntensityLevel) {
        viewModelScope.launch {
            try {
                soundscapeRepository.updateTrack(track.copy(intensityLevel = newIntensity))
            } catch (e: Exception) {
                _uiState.value = SoundscapeCategoryComposerUiState.Error(
                    e.message ?: "Failed to update track intensity"
                )
            }
        }
    }

    /**
     * Update track mix volume.
     */
    fun updateTrackVolume(track: SoundscapeTrack, newVolume: Float) {
        viewModelScope.launch {
            try {
                soundscapeRepository.updateTrack(track.copy(mixVolume = newVolume))
            } catch (e: Exception) {
                _uiState.value = SoundscapeCategoryComposerUiState.Error(
                    e.message ?: "Failed to update track volume"
                )
            }
        }
    }

    /**
     * Delete a track by ID.
     */
    fun deleteTrack(id: Long) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteTrack(id)
            } catch (e: Exception) {
                _uiState.value = SoundscapeCategoryComposerUiState.Error(
                    e.message ?: "Failed to delete track"
                )
            }
        }
    }

    /**
     * Clear the error state.
     */
    fun clearError() {
        if (_uiState.value is SoundscapeCategoryComposerUiState.Error) {
            viewModelScope.launch {
                loadCategoryAndTracks()
            }
        }
    }
}
