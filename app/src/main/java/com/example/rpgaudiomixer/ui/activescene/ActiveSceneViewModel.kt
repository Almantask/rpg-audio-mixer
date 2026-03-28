package com.example.rpgaudiomixer.ui.activescene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.storage.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveSceneUiState(
    val scene: Scene? = null,
    val soundscapes: List<SceneSoundscape> = emptyList(),
    val fx: List<SceneFx> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ActiveSceneViewModel @Inject constructor(
    private val sceneRepository: SceneRepository,
) : ViewModel() {

    fun uiState(sceneId: Long): StateFlow<ActiveSceneUiState> = combine(
        sceneRepository.getSceneById(sceneId),
        sceneRepository.getSoundscapesForScene(sceneId),
        sceneRepository.getFxForScene(sceneId),
    ) { scene, soundscapes, fx ->
        ActiveSceneUiState(
            scene = scene,
            soundscapes = soundscapes.sortedBy { it.order },
            fx = fx.sortedBy { it.order },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ActiveSceneUiState(),
    )

    fun updateAtmosphereMasterVolume(scene: Scene, volume: Float) {
        viewModelScope.launch {
            sceneRepository.update(scene.copy(atmosphereMasterVolume = volume))
        }
    }

    fun updateSoundboardMasterVolume(scene: Scene, volume: Float) {
        viewModelScope.launch {
            sceneRepository.update(scene.copy(soundboardMasterVolume = volume))
        }
    }

    fun updateSoundscapeMix(sceneId: Long, categoryId: Long, mix: Float) {
        viewModelScope.launch {
            sceneRepository.updateSceneSoundscapeMix(sceneId, categoryId, mix)
        }
    }

    fun updateSoundscapeIntensity(sceneId: Long, categoryId: Long, intensity: Int) {
        viewModelScope.launch {
            sceneRepository.updateSceneSoundscapeIntensity(sceneId, categoryId, intensity)
        }
    }

    fun removeSoundscape(sceneId: Long, categoryId: Long) {
        viewModelScope.launch {
            sceneRepository.removeSoundscapeFromScene(sceneId, categoryId)
        }
    }

    fun removeFx(sceneId: Long, fxEffectId: Long) {
        viewModelScope.launch {
            sceneRepository.removeFxFromScene(sceneId, fxEffectId)
        }
    }
}
