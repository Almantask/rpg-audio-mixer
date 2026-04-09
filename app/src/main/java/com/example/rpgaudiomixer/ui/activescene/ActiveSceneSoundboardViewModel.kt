package com.example.rpgaudiomixer.ui.activescene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.audio.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.ActiveSceneFx
import com.example.rpgaudiomixer.domain.repository.FxRepository
import com.example.rpgaudiomixer.domain.repository.SceneFxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ActiveSceneSoundboardUiState {
    data object Loading : ActiveSceneSoundboardUiState
    data class Success(
        val fxTracks: List<ActiveSceneFx>,
        val masterVolume: Float
    ) : ActiveSceneSoundboardUiState
    data class Error(val message: String) : ActiveSceneSoundboardUiState
}

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneFxRepository: SceneFxRepository,
    private val fxRepository: FxRepository,
    private val soundboardPlayer: SoundboardPlayer
) : ViewModel() {

    private val sceneId: Long = savedStateHandle.get<String>("sceneId")?.toLongOrNull()
        ?: throw IllegalArgumentException("sceneId is required")

    private val _masterVolume = MutableStateFlow(1.0f)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Track active FX instances by FX track ID
    private val _activeFxInstances = MutableStateFlow<Map<Long, List<String>>>(emptyMap())

    val uiState: StateFlow<ActiveSceneSoundboardUiState> = combine(
        sceneFxRepository.observeByScene(sceneId),
        _masterVolume,
        _activeFxInstances
    ) { crossRefs, masterVolume, activeFxInstances ->
        val fxTracks = crossRefs.mapNotNull { crossRef ->
            val fxTrack = fxRepository.getFxTrackById(crossRef.fxTrackId)
            fxTrack?.let {
                val instances = activeFxInstances[crossRef.fxTrackId] ?: emptyList()
                ActiveSceneFx(
                    fxTrackId = crossRef.fxTrackId,
                    name = it.name,
                    filePath = it.filePath,
                    displayOrder = crossRef.displayOrder,
                    isPlaying = instances.isNotEmpty(),
                    activeInstanceCount = instances.size
                )
            }
        }.sortedBy { it.displayOrder }

        ActiveSceneSoundboardUiState.Success(
            fxTracks = fxTracks,
            masterVolume = masterVolume
        )
    }
        .catch { e ->
            emit(ActiveSceneSoundboardUiState.Error(e.message ?: "Unknown error"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ActiveSceneSoundboardUiState.Loading
        )

    fun setMasterVolume(volume: Float) {
        _masterVolume.value = volume.coerceIn(0f, 1f)
        soundboardPlayer.setMasterVolume(volume)
    }

    fun triggerFx(fxTrackId: Long) {
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                if (currentState is ActiveSceneSoundboardUiState.Success) {
                    val fx = currentState.fxTracks.find { it.fxTrackId == fxTrackId }
                    if (fx != null) {
                        val fxTrack = fxRepository.getFxTrackById(fxTrackId)
                        if (fxTrack != null) {
                            val instanceId = soundboardPlayer.triggerFx(fxTrack)

                            // Update active instances
                            val currentInstances = _activeFxInstances.value[fxTrackId] ?: emptyList()
                            _activeFxInstances.value = _activeFxInstances.value.toMutableMap().apply {
                                put(fxTrackId, currentInstances + instanceId)
                            }

                            // Schedule instance removal after a reasonable duration
                            // In a real implementation, we'd listen to playback completion
                            // For now, we'll use a simple delay based on typical FX duration
                            launch {
                                kotlinx.coroutines.delay(5000) // 5 seconds default
                                removeInstance(fxTrackId, instanceId)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to trigger FX"
            }
        }
    }

    fun stopFx(fxTrackId: Long) {
        viewModelScope.launch {
            try {
                val instances = _activeFxInstances.value[fxTrackId] ?: emptyList()
                instances.forEach { instanceId ->
                    soundboardPlayer.stopFx(instanceId)
                }
                _activeFxInstances.value = _activeFxInstances.value.toMutableMap().apply {
                    remove(fxTrackId)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to stop FX"
            }
        }
    }

    private fun removeInstance(fxTrackId: Long, instanceId: String) {
        val currentInstances = _activeFxInstances.value[fxTrackId] ?: emptyList()
        val updatedInstances = currentInstances.filter { it != instanceId }
        _activeFxInstances.value = _activeFxInstances.value.toMutableMap().apply {
            if (updatedInstances.isEmpty()) {
                remove(fxTrackId)
            } else {
                put(fxTrackId, updatedInstances)
            }
        }
    }

    fun reorderFx(fxTrackIds: List<Long>) {
        viewModelScope.launch {
            try {
                sceneFxRepository.updateDisplayOrders(sceneId, fxTrackIds)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to reorder FX"
            }
        }
    }

    fun removeFx(fxTrackId: Long) {
        viewModelScope.launch {
            try {
                sceneFxRepository.removeFxFromScene(sceneId, fxTrackId)
                stopFx(fxTrackId)
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to remove FX"
            }
        }
    }

    fun addFx(fxTrackId: Long) {
        viewModelScope.launch {
            try {
                val currentState = uiState.value
                if (currentState is ActiveSceneSoundboardUiState.Success) {
                    val nextDisplayOrder = currentState.fxTracks.maxOfOrNull { it.displayOrder }?.plus(1) ?: 0
                    sceneFxRepository.addFxToScene(sceneId, fxTrackId, nextDisplayOrder)
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to add FX"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        soundboardPlayer.release()
    }
}
