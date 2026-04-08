package com.example.rpgaudiomixer.ui.library.soundscapes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.repository.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrackEditState(
    val track: SoundscapeTrack,
    val isModified: Boolean = false
)

@HiltViewModel
class SoundscapeComposerViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val categoryId: Long = savedStateHandle.get<String>("categoryId")?.toLongOrNull() ?: 0L

    private val _category = MutableStateFlow<SoundscapeCategory?>(null)
    val category: StateFlow<SoundscapeCategory?> = _category.asStateFlow()

    private val _tracks = MutableStateFlow<UiState<List<TrackEditState>>>(UiState.Loading)
    val tracks: StateFlow<UiState<List<TrackEditState>>> = _tracks.asStateFlow()

    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges: StateFlow<Boolean> = _hasUnsavedChanges.asStateFlow()

    init {
        loadCategory()
        loadTracks()
    }

    private fun loadCategory() {
        viewModelScope.launch {
            try {
                _category.value = soundscapeRepository.getCategoryById(categoryId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun loadTracks() {
        viewModelScope.launch {
            try {
                soundscapeRepository.observeTracksByCategory(categoryId).collect { trackList ->
                    val trackStates = trackList.map { TrackEditState(it, false) }
                    _tracks.value = UiState.Success(trackStates)
                }
            } catch (e: Exception) {
                _tracks.value = UiState.Error(e.message ?: "Failed to load tracks")
            }
        }
    }

    fun updateTrackIntensity(trackId: Long, newIntensity: IntensityLevel) {
        viewModelScope.launch {
            try {
                val currentTracks = (_tracks.value as? UiState.Success)?.data ?: return@launch
                val updatedTracks = currentTracks.map { state ->
                    if (state.track.id == trackId) {
                        state.copy(
                            track = state.track.copy(intensityLevel = newIntensity),
                            isModified = true
                        )
                    } else state
                }
                _tracks.value = UiState.Success(updatedTracks)
                _hasUnsavedChanges.value = true
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateTrackMixVolume(trackId: Long, newVolume: Float) {
        viewModelScope.launch {
            try {
                val currentTracks = (_tracks.value as? UiState.Success)?.data ?: return@launch
                val updatedTracks = currentTracks.map { state ->
                    if (state.track.id == trackId) {
                        state.copy(
                            track = state.track.copy(mixVolume = newVolume),
                            isModified = true
                        )
                    } else state
                }
                _tracks.value = UiState.Success(updatedTracks)
                _hasUnsavedChanges.value = true
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addTrack(name: String, filePath: String, intensity: IntensityLevel) {
        viewModelScope.launch {
            try {
                soundscapeRepository.createTrack(
                    categoryId = categoryId,
                    name = name,
                    filePath = filePath,
                    intensityLevel = intensity,
                    mixVolume = 1.0f
                )
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteTrack(trackId: Long) {
        viewModelScope.launch {
            try {
                soundscapeRepository.deleteTrack(trackId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun saveAllChanges() {
        viewModelScope.launch {
            try {
                val currentTracks = (_tracks.value as? UiState.Success)?.data ?: return@launch
                currentTracks.filter { it.isModified }.forEach { state ->
                    soundscapeRepository.updateTrack(state.track)
                }
                _hasUnsavedChanges.value = false
                // Reload to reset modified flags
                loadTracks()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
