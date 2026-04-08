package com.example.rpgaudiomixer.ui.fx

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for FX Edit screen.
 */
sealed class FxEditUiState {
    object Loading : FxEditUiState()
    data class Success(val track: FxTrack) : FxEditUiState()
    data class Error(val message: String) : FxEditUiState()
}

/**
 * ViewModel for FX Edit screen.
 *
 * Manages editing FX track name, tags, and deletion.
 */
@HiltViewModel
class FxEditViewModel @Inject constructor(
    private val fxRepository: FxRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val trackId: Long = checkNotNull(savedStateHandle.get<Long>("trackId")) {
        "trackId is required"
    }

    private val _uiState = MutableStateFlow<FxEditUiState>(FxEditUiState.Loading)
    val uiState: StateFlow<FxEditUiState> = _uiState.asStateFlow()

    init {
        loadTrack()
    }

    private fun loadTrack() {
        viewModelScope.launch {
            try {
                val track = fxRepository.getById(trackId)
                if (track == null) {
                    _uiState.value = FxEditUiState.Error("FX track not found")
                } else {
                    _uiState.value = FxEditUiState.Success(track)
                }
            } catch (e: Exception) {
                _uiState.value = FxEditUiState.Error(
                    e.message ?: "Failed to load FX track"
                )
            }
        }
    }

    /**
     * Update track name.
     */
    fun updateName(newName: String) {
        val currentState = _uiState.value
        if (currentState is FxEditUiState.Success) {
            val updatedTrack = currentState.track.copy(name = newName)
            _uiState.value = FxEditUiState.Success(updatedTrack)
        }
    }

    /**
     * Add a tag to the track.
     */
    fun addTag(tag: String) {
        val currentState = _uiState.value
        if (currentState is FxEditUiState.Success) {
            val currentTags = currentState.track.tags
            if (!currentTags.contains(tag)) {
                val updatedTrack = currentState.track.copy(tags = currentTags + tag)
                _uiState.value = FxEditUiState.Success(updatedTrack)
            }
        }
    }

    /**
     * Remove a tag from the track.
     */
    fun removeTag(tag: String) {
        val currentState = _uiState.value
        if (currentState is FxEditUiState.Success) {
            val updatedTrack = currentState.track.copy(
                tags = currentState.track.tags.filter { it != tag }
            )
            _uiState.value = FxEditUiState.Success(updatedTrack)
        }
    }

    /**
     * Save changes to the track.
     */
    fun save() {
        val currentState = _uiState.value
        if (currentState is FxEditUiState.Success) {
            viewModelScope.launch {
                try {
                    fxRepository.update(currentState.track)
                } catch (e: Exception) {
                    _uiState.value = FxEditUiState.Error(
                        e.message ?: "Failed to save FX track"
                    )
                }
            }
        }
    }

    /**
     * Delete the track (soft-delete).
     */
    fun delete() {
        viewModelScope.launch {
            try {
                fxRepository.delete(trackId)
            } catch (e: Exception) {
                _uiState.value = FxEditUiState.Error(
                    e.message ?: "Failed to delete FX track"
                )
            }
        }
    }

    /**
     * Clear the error state.
     */
    fun clearError() {
        if (_uiState.value is FxEditUiState.Error) {
            viewModelScope.launch {
                loadTrack()
            }
        }
    }
}
