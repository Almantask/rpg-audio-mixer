package com.example.rpgaudiomixer.ui.activescene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.data.activescene.SceneAudioDao
import com.example.rpgaudiomixer.data.activescene.SceneFxCrossRef
import com.example.rpgaudiomixer.domain.audio.SoundboardPlayer
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    private val sceneAudioDao: SceneAudioDao,
    private val fxRepository: FxRepository,
    private val soundboardPlayer: SoundboardPlayer,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _sceneId: Long = savedStateHandle["sceneId"] ?: 0L

    constructor(
        sceneId: Long,
        sceneAudioDao: SceneAudioDao,
        fxRepository: FxRepository,
        soundboardPlayer: SoundboardPlayer,
    ) : this(
        sceneAudioDao = sceneAudioDao,
        fxRepository = fxRepository,
        soundboardPlayer = soundboardPlayer,
        savedStateHandle = SavedStateHandle(mapOf("sceneId" to sceneId)),
    )

    private val _uiState = MutableStateFlow<ActiveSceneSoundboardUiState>(ActiveSceneSoundboardUiState.Loading)
    val uiState: StateFlow<ActiveSceneSoundboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                sceneAudioDao.observeFxForScene(_sceneId),
                fxRepository.observeAll(),
            ) { refs, allTracks ->
                val trackMap = allTracks.associateBy { it.id }
                refs.sortedBy { it.displayOrder }
                    .mapNotNull { trackMap[it.fxTrackId] }
            }
                .catch { e -> _uiState.value = ActiveSceneSoundboardUiState.Error(e.message ?: "Unknown error") }
                .collect { tracks ->
                    _uiState.value = ActiveSceneSoundboardUiState.Success(
                        fxTracks = tracks,
                        masterVolume = (_uiState.value as? ActiveSceneSoundboardUiState.Success)?.masterVolume ?: 1.0f,
                    )
                }
        }
    }

    fun triggerFx(fxTrack: FxTrack) {
        soundboardPlayer.triggerFx(fxTrack)
    }

    fun triggerFxWithStats(fxTrack: FxTrack) {
        soundboardPlayer.triggerFx(fxTrack)
        viewModelScope.launch {
            runCatching { fxRepository.incrementPlayCount(fxTrack.id) }
        }
    }

    fun stopAll() {
        soundboardPlayer.stopAll()
    }

    fun setMasterVolume(volume: Float) {
        soundboardPlayer.setMasterVolume(volume)
        val current = _uiState.value
        if (current is ActiveSceneSoundboardUiState.Success) {
            _uiState.value = current.copy(masterVolume = volume)
        }
    }

    fun addFx(fxTrackId: Long) {
        viewModelScope.launch {
            val order = (_uiState.value as? ActiveSceneSoundboardUiState.Success)?.fxTracks?.size ?: 0
            sceneAudioDao.addFxToScene(SceneFxCrossRef(sceneId = _sceneId, fxTrackId = fxTrackId, displayOrder = order))
        }
    }

    fun removeFx(fxTrackId: Long) {
        viewModelScope.launch {
            sceneAudioDao.removeFxFromScene(sceneId = _sceneId, fxTrackId = fxTrackId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        soundboardPlayer.stopAll()
    }
}
