package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.scene.SceneFxRepository
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ActiveSceneSoundboardUiState {
    data object Loading : ActiveSceneSoundboardUiState

    data class Success(
        val sceneName: String,
        val masterVolume: Float,
        val fxButtons: List<ActiveSceneFxUiModel>,
        val availableFxToAdd: List<FxTrack>,
    ) : ActiveSceneSoundboardUiState

    data class Error(val message: String) : ActiveSceneSoundboardUiState
}

data class ActiveSceneFxUiModel(
    val fxTrackId: Long,
    val name: String,
    val playingInstanceCount: Int,
) {
    val isPlaying: Boolean
        get() = playingInstanceCount > 0
}

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val sceneFxRepository: SceneFxRepository,
    private val soundboardPlayer: SoundboardPlayer,
) : ViewModel() {
    private val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])
    private val masterVolume = MutableStateFlow(1f)
    private val playbackInstanceIds = MutableStateFlow<Map<Long, List<Long>>>(emptyMap())
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    private val sceneFxItems = sceneFxRepository.observeSceneFx(sceneId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList(),
    )

    val uiState: StateFlow<ActiveSceneSoundboardUiState> = combine(
        sceneRepository.observeScene(sceneId),
        sceneFxItems,
        sceneFxRepository.observeAvailableFx(sceneId),
        masterVolume,
        playbackInstanceIds,
    ) { scene, sceneFx, availableFx, master, playback ->
        ActiveSceneSoundboardUiState.Success(
            sceneName = scene?.name.orEmpty(),
            masterVolume = master,
            fxButtons = sceneFx.sortedBy { item -> item.displayOrder }.map { fx ->
                ActiveSceneFxUiModel(
                    fxTrackId = fx.fxTrackId,
                    name = fx.name,
                    playingInstanceCount = playback[fx.fxTrackId]?.size ?: 0,
                )
            },
            availableFxToAdd = availableFx,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ActiveSceneSoundboardUiState.Loading,
    )

    constructor(
        sceneId: Long,
        sceneRepository: SceneRepository,
        sceneFxRepository: SceneFxRepository,
        soundboardPlayer: SoundboardPlayer,
    ) : this(
        savedStateHandle = SavedStateHandle(mapOf("sceneId" to sceneId)),
        sceneRepository = sceneRepository,
        sceneFxRepository = sceneFxRepository,
        soundboardPlayer = soundboardPlayer,
    )

    fun setMasterVolume(volume: Float) {
        val normalizedVolume = volume.coerceIn(0f, 1f)
        masterVolume.value = normalizedVolume
        soundboardPlayer.setMasterVolume(normalizedVolume)
    }

    fun triggerFx(fxTrackId: Long) {
        val sceneFx = sceneFxItems.value.firstOrNull { item -> item.fxTrackId == fxTrackId } ?: return
        val instanceId = soundboardPlayer.triggerFx(sceneFx.toFxTrack())
        viewModelScope.launch {
            sceneFxRepository.incrementTrackPlayCount(fxTrackId)
        }
        playbackInstanceIds.value = playbackInstanceIds.value + (
            fxTrackId to soundboardPlayer.activeInstanceIdsForTrack(fxTrackId).ifEmpty { listOf(instanceId) }
        )
        refreshPlaybackState(sceneFxItems.value)
    }

    fun stopFx(fxTrackId: Long) {
        val instanceIds = playbackInstanceIds.value[fxTrackId].orEmpty()
        val instanceId = instanceIds.lastOrNull() ?: return
        soundboardPlayer.stopFx(instanceId)
        playbackInstanceIds.value = playbackInstanceIds.value + (
            fxTrackId to soundboardPlayer.activeInstanceIdsForTrack(fxTrackId)
        )
    }

    fun addFx(fxTrackId: Long) {
        viewModelScope.launch {
            sceneFxRepository.addFxToScene(sceneId, fxTrackId)
        }
    }

    fun removeFx(fxTrackId: Long) {
        playbackInstanceIds.value[fxTrackId].orEmpty().reversed().forEach { instanceId ->
            soundboardPlayer.stopFx(instanceId)
        }
        playbackInstanceIds.value = playbackInstanceIds.value - fxTrackId
        viewModelScope.launch {
            sceneFxRepository.removeFxFromScene(sceneId, fxTrackId)
        }
    }

    fun reorderFx(orderedFxTrackIds: List<Long>) {
        if (orderedFxTrackIds.isEmpty()) {
            return
        }
        viewModelScope.launch {
            sceneFxRepository.reorderFx(sceneId, orderedFxTrackIds)
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        soundboardPlayer.releaseAll()
        super.onCleared()
    }

    private fun refreshPlaybackState(sceneFx: List<SceneFx>) {
        playbackInstanceIds.value = sceneFx.associate { fx ->
            fx.fxTrackId to soundboardPlayer.activeInstanceIdsForTrack(fx.fxTrackId)
        }
    }

}

private fun SceneFx.toFxTrack(): FxTrack {
    return FxTrack(
        id = fxTrackId,
        name = name,
        filePath = filePath,
        tags = tags,
        durationMs = durationMs,
        playCount = playCount,
        isDemoContent = isDemoContent,
    )
}
