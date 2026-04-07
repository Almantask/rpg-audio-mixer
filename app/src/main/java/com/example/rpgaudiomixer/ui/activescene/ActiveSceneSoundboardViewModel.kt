package com.example.rpgaudiomixer.ui.activescene

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.repository.FxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ActiveSceneSoundboardUiState(
    val effects: List<SceneFxState> = emptyList(),
    val masterVolume: Float = 1.0f
)

data class SceneFxState(
    val fx: FxTrack,
    val displayOrder: Int,
    val isPlaying: Boolean = false
)

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    private val fxRepository: FxRepository,
    private val musicPlayer: MixedMusicPlayer
) : ViewModel() {

    private val _sceneId = MutableStateFlow<Long?>(null)

    private val _uiState = MutableStateFlow<UiState<ActiveSceneSoundboardUiState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ActiveSceneSoundboardUiState>> = _uiState.asStateFlow()

    private val playingEffects = mutableMapOf<Long, Boolean>()

    fun loadScene(sceneId: Long) {
        _sceneId.value = sceneId

        viewModelScope.launch {
            try {
                // For now, load all FX tracks from the library
                // In the future, this would load scene-specific FX assignments
                fxRepository.observeAll().collect { fxTracks ->
                    val effectStates = fxTracks.mapIndexed { index, fx ->
                        SceneFxState(
                            fx = fx,
                            displayOrder = index,
                            isPlaying = playingEffects[fx.id] ?: false
                        )
                    }

                    _uiState.value = UiState.Success(
                        ActiveSceneSoundboardUiState(
                            effects = effectStates,
                            masterVolume = 1.0f
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to load soundboard effects")
            }
        }
    }

    fun setMasterVolume(volume: Float) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        _uiState.value = UiState.Success(
            currentState.copy(masterVolume = volume)
        )
    }

    fun playEffect(fxId: Long) {
        viewModelScope.launch {
            val currentState = (_uiState.value as? UiState.Success)?.data ?: return@launch
            val fx = currentState.effects.find { it.fx.id == fxId }?.fx ?: return@launch

            try {
                // Play the effect using the track ID from the FX
                musicPlayer.playSingleSound(fx.trackId)

                // Mark as playing temporarily
                playingEffects[fxId] = true
                updateEffectPlayingState(fxId, true)

                // Reset playing state after a delay (sound effects are typically short)
                // This is a simple approach - a more sophisticated implementation would
                // track actual playback completion
                kotlinx.coroutines.delay(1000)
                playingEffects[fxId] = false
                updateEffectPlayingState(fxId, false)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Failed to play effect")
            }
        }
    }

    fun removeEffect(fxId: Long) {
        viewModelScope.launch {
            val sceneId = _sceneId.value ?: return@launch
            // TODO: Implement scene-specific FX removal when scene soundboard repository is available
            // For now, this is a no-op since we're showing all FX from the library
        }
    }

    private fun updateEffectPlayingState(fxId: Long, isPlaying: Boolean) {
        val currentState = (_uiState.value as? UiState.Success)?.data ?: return
        val updatedEffects = currentState.effects.map { effectState ->
            if (effectState.fx.id == fxId) {
                effectState.copy(isPlaying = isPlaying)
            } else {
                effectState
            }
        }
        _uiState.value = UiState.Success(
            currentState.copy(effects = updatedEffects)
        )
    }
}
