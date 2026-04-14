package com.example.rpgaudiomixer.app.screens.activescene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.domain.repository.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ActiveSceneUiState {
    data object Loading : ActiveSceneUiState
    data class Ready(
        val sceneName: String,
        val sceneDescription: String?,
        val categories: List<CategoryUiModel>,
        val fxButtons: List<FxButtonUiModel>,
        val masterAtmosphereVolume: Float,
        val masterFxVolume: Float,
        val isSessionLocked: Boolean,
        val sceneNotes: String?,
        val masterIntensityLevel: Int = 1,
    ) : ActiveSceneUiState

    data class Error(val message: String) : ActiveSceneUiState
}

data class CategoryUiModel(
    val id: Long,
    val name: String,
    val isPlaying: Boolean,
    val currentTrackName: String?,
    val mixVolume: Float,
    val intensityLevel: Int,
    val availableIntensities: Set<Int>,
)

data class FxButtonUiModel(
    val trackId: Long,
    val name: String,
    val isPlaying: Boolean,
)

@HiltViewModel
class ActiveSceneViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])

    private val _uiState = MutableStateFlow<ActiveSceneUiState>(ActiveSceneUiState.Loading)
    val uiState: StateFlow<ActiveSceneUiState> = _uiState.asStateFlow()

    init {
        loadScene()
    }

    private fun loadScene() {
        viewModelScope.launch {
            runCatching {
                sceneRepository.getById(sceneId)
            }.onSuccess { scene ->
                if (scene == null) {
                    _uiState.value = ActiveSceneUiState.Error("Scene not found")
                } else {
                    _uiState.value = ActiveSceneUiState.Ready(
                        sceneName = scene.name,
                        sceneDescription = scene.description,
                        categories = emptyList(),
                        fxButtons = emptyList(),
                        masterAtmosphereVolume = 1.0f,
                        masterFxVolume = 1.0f,
                        isSessionLocked = false,
                        sceneNotes = scene.notes,
                    )
                }
            }.onFailure { e ->
                _uiState.value = ActiveSceneUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun playCategory(id: Long) {
        updateReadyState { state ->
            state.copy(
                categories = state.categories.map { cat ->
                    if (cat.id == id) cat.copy(isPlaying = true) else cat
                },
            )
        }
    }

    fun pauseCategory(id: Long) {
        updateReadyState { state ->
            state.copy(
                categories = state.categories.map { cat ->
                    if (cat.id == id) cat.copy(isPlaying = false) else cat
                },
            )
        }
    }

    fun setCategoryIntensity(id: Long, level: Int) {
        updateReadyState { state ->
            state.copy(
                categories = state.categories.map { cat ->
                    if (cat.id == id && level in cat.availableIntensities) {
                        cat.copy(intensityLevel = level)
                    } else {
                        cat
                    }
                },
            )
        }
    }

    fun setCategoryMix(id: Long, volume: Float) {
        updateReadyState { state ->
            state.copy(
                categories = state.categories.map { cat ->
                    if (cat.id == id) cat.copy(mixVolume = volume.coerceIn(0.0f, 1.0f)) else cat
                },
            )
        }
    }

    fun setMasterAtmosphereVolume(volume: Float) {
        updateReadyState { state ->
            state.copy(masterAtmosphereVolume = volume.coerceIn(0.0f, 1.0f))
        }
    }

    fun setMasterFxVolume(volume: Float) {
        updateReadyState { state ->
            state.copy(masterFxVolume = volume.coerceIn(0.0f, 1.0f))
        }
    }

    fun playFx(trackId: Long) {
        updateReadyState { state ->
            state.copy(
                fxButtons = state.fxButtons.map { fx ->
                    if (fx.trackId == trackId) fx.copy(isPlaying = true) else fx
                },
            )
        }
    }

    fun stopAll() {
        updateReadyState { state ->
            state.copy(
                categories = state.categories.map { it.copy(isPlaying = false) },
                fxButtons = state.fxButtons.map { it.copy(isPlaying = false) },
            )
        }
    }

    fun updateSceneNotes(notes: String) {
        updateReadyState { state -> state.copy(sceneNotes = notes) }
        viewModelScope.launch {
            val scene = sceneRepository.getById(sceneId) ?: return@launch
            sceneRepository.updateScene(scene.copy(notes = notes))
        }
    }

    fun toggleSessionLock() {
        updateReadyState { state ->
            state.copy(isSessionLocked = !state.isSessionLocked)
        }
    }

    fun setMasterIntensity(level: Int) {
        updateReadyState { state ->
            state.copy(
                masterIntensityLevel = level,
                categories = state.categories.map { cat ->
                    if (level in cat.availableIntensities) cat.copy(intensityLevel = level) else cat
                },
            )
        }
    }

    private fun updateReadyState(transform: (ActiveSceneUiState.Ready) -> ActiveSceneUiState.Ready) {
        _uiState.update { current ->
            if (current is ActiveSceneUiState.Ready) transform(current) else current
        }
    }
}
