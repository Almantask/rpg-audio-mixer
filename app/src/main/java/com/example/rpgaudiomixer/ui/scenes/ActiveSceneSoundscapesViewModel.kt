package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.domain.repository.SceneSoundscapeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * UI State for Active Scene - Soundscapes tab.
 */
sealed class ActiveSceneSoundscapesUiState {
    object Loading : ActiveSceneSoundscapesUiState()
    data class Success(
        val sceneName: String,
        val atmosphereVolumePercent: Int,
        val soundscapes: List<SceneSoundscape>
    ) : ActiveSceneSoundscapesUiState()
    data class Error(val message: String) : ActiveSceneSoundscapesUiState()
}

/**
 * ViewModel for Active Scene - Soundscapes tab.
 *
 * Manages soundscape playback, intensity control, and master volume for a scene.
 */
@HiltViewModel(assistedFactory = ActiveSceneSoundscapesViewModel.Factory::class)
class ActiveSceneSoundscapesViewModel @AssistedInject constructor(
    @Assisted private val sceneId: Long,
    private val sceneRepository: SceneRepository,
    private val sceneSoundscapeRepository: SceneSoundscapeRepository,
    private val sceneAudioEngine: SceneAudioEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<ActiveSceneSoundscapesUiState>(ActiveSceneSoundscapesUiState.Loading)
    val uiState: StateFlow<ActiveSceneSoundscapesUiState> = _uiState.asStateFlow()

    private var currentScene: Scene? = null

    @AssistedFactory
    interface Factory {
        fun create(sceneId: Long): ActiveSceneSoundscapesViewModel
    }

    init {
        loadScene()
    }

    private fun loadScene() {
        viewModelScope.launch {
            try {
                val scene = sceneRepository.getById(sceneId)
                if (scene == null) {
                    _uiState.value = ActiveSceneSoundscapesUiState.Error("Scene not found")
                    return@launch
                }

                currentScene = scene

                sceneSoundscapeRepository.observeByScene(sceneId)
                    .catch { e ->
                        _uiState.value = ActiveSceneSoundscapesUiState.Error(
                            e.message ?: "Failed to load scene soundscapes"
                        )
                    }
                    .collect { soundscapes ->
                        _uiState.value = ActiveSceneSoundscapesUiState.Success(
                            sceneName = scene.name,
                            atmosphereVolumePercent = scene.atmosphereVolumePercent,
                            soundscapes = soundscapes
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to load scene"
                )
            }
        }
    }

    /**
     * Set the master atmosphere volume (0-100).
     */
    fun setMasterVolume(volumePercent: Int) {
        viewModelScope.launch {
            try {
                val scene = currentScene ?: return@launch
                val updatedScene = scene.copy(atmosphereVolumePercent = volumePercent)
                sceneRepository.update(updatedScene)
                currentScene = updatedScene

                // Update audio engine
                sceneAudioEngine.setMasterVolume(volumePercent / 100f)
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to update master volume"
                )
            }
        }
    }

    /**
     * Add a soundscape category to the scene.
     */
    fun addSoundscape(categoryId: Long, intensityLevel: IntensityLevel) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.addToScene(
                    sceneId = sceneId,
                    categoryId = categoryId,
                    intensityLevel = intensityLevel,
                    mixVolumePercent = 100
                )
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to add soundscape"
                )
            }
        }
    }

    /**
     * Update the intensity level for a soundscape.
     */
    fun updateIntensity(categoryId: Long, intensityLevel: IntensityLevel) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.updateIntensity(sceneId, categoryId, intensityLevel)
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to update intensity"
                )
            }
        }
    }

    /**
     * Update the mix volume for a soundscape.
     */
    fun updateMixVolume(categoryId: Long, mixVolumePercent: Int) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.updateMixVolume(sceneId, categoryId, mixVolumePercent)
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to update mix volume"
                )
            }
        }
    }

    /**
     * Remove a soundscape from the scene.
     */
    fun removeSoundscape(categoryId: Long) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.removeFromScene(sceneId, categoryId)
                sceneAudioEngine.removeCategory(categoryId)
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to remove soundscape"
                )
            }
        }
    }

    /**
     * Reorder soundscapes via drag-and-drop.
     */
    fun reorderSoundscapes(soundscapes: List<SceneSoundscape>) {
        viewModelScope.launch {
            try {
                sceneSoundscapeRepository.updateDisplayOrders(sceneId, soundscapes)
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to reorder soundscapes"
                )
            }
        }
    }

    /**
     * Start playing the scene with all soundscapes.
     */
    fun playScene() {
        val state = _uiState.value
        if (state !is ActiveSceneSoundscapesUiState.Success) return

        viewModelScope.launch {
            try {
                // Set master volume
                sceneAudioEngine.setMasterVolume(state.atmosphereVolumePercent / 100f)

                // Add all soundscape categories to the audio engine
                state.soundscapes.forEach { soundscape ->
                    sceneAudioEngine.addCategory(
                        categoryId = soundscape.category.id,
                        intensityLevel = soundscape.intensityLevel,
                        mixVolumePercent = soundscape.mixVolumePercent / 100f
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to play scene"
                )
            }
        }
    }

    /**
     * Pause/stop scene playback.
     */
    fun pauseScene() {
        viewModelScope.launch {
            try {
                sceneAudioEngine.releaseAll()
            } catch (e: Exception) {
                _uiState.value = ActiveSceneSoundscapesUiState.Error(
                    e.message ?: "Failed to pause scene"
                )
            }
        }
    }

    /**
     * Clear the error state and reload.
     */
    fun clearError() {
        if (_uiState.value is ActiveSceneSoundscapesUiState.Error) {
            viewModelScope.launch {
                _uiState.value = ActiveSceneSoundscapesUiState.Loading
                loadScene()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Release audio engine resources when ViewModel is cleared
        sceneAudioEngine.releaseAll()
    }
}
