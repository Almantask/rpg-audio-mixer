package com.example.rpgaudiomixer.app.ui.active_scene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.scene.SceneActiveFx
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FxItemState(
    val fx: FxTrack,
    val displayOrder: Int,
    val isPlaying: Boolean = false // Simple state, SoundboardPlayer handles overlap
)

data class ActiveSceneSoundboardUiState(
    val effects: List<FxItemState> = emptyList(),
    val masterVolume: Float = 1f,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val soundboardPlayer: SoundboardPlayer
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])

    private val _uiState = MutableStateFlow(ActiveSceneSoundboardUiState())
    val uiState: StateFlow<ActiveSceneSoundboardUiState> = _uiState.asStateFlow()

    init {
        loadEffects()
    }

    private fun loadEffects() {
        viewModelScope.launch {
            sceneRepository.observeSceneActiveFx(sceneId)
                .onEach { effects ->
                    _uiState.update { it.copy(
                        effects = effects.map { it.toItemState() },
                        isLoading = false
                    ) }
                }
                .catch { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect()
        }
    }

    fun setMasterVolume(volume: Float) {
        _uiState.update { it.copy(masterVolume = volume) }
        soundboardPlayer.setMasterVolume(volume)
    }

    fun triggerFx(fx: FxTrack) {
        soundboardPlayer.triggerFx(fx)
        // In a real app we might track playing counts for animation, 
        // but for now we'll just fire and forget as per SoundboardPlayer impl.
    }

    fun stopFx(fxId: Long) {
        soundboardPlayer.stopFx(fxId)
    }

    fun addFx(fxId: Long) {
        viewModelScope.launch {
            val nextOrder = (_uiState.value.effects.maxOfOrNull { it.displayOrder } ?: -1) + 1
            sceneRepository.addFxToScene(sceneId, fxId, nextOrder)
        }
    }

    fun removeFx(fxId: Long) {
        viewModelScope.launch {
            sceneRepository.removeFxFromScene(sceneId, fxId)
        }
    }

    fun reorder(effects: List<FxItemState>) {
        // Implementation for reorder persistence would go here
    }

    private fun SceneActiveFx.toItemState() = FxItemState(
        fx = fxTrack,
        displayOrder = displayOrder
    )
}
