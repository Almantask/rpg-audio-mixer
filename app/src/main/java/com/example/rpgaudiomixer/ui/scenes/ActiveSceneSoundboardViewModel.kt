package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.FxButtonModel
import com.example.rpgaudiomixer.app.components.MultiSelectOption
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ActiveSceneSoundboardContent(
    val sceneName: String,
    val masterVolume: Float,
    val effects: List<FxButtonModel>,
    val availableFxOptions: List<MultiSelectOption>,
)

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val fxRepository: FxRepository,
    private val soundboardPlayer: SoundboardPlayer,
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle[MainNavDestination.SCENE_ID_ARG])

    private val _uiState =
        MutableStateFlow<UiState<ActiveSceneSoundboardContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<ActiveSceneSoundboardContent>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var scene: Scene? = null
    private var masterVolume: Float = soundboardPlayer.masterVolume
    private var latestAssignments: List<SceneFx> = emptyList()
    private var latestTracksById: Map<Long, FxTrack> = emptyMap()
    private var latestAvailableOptions: List<MultiSelectOption> = emptyList()
    private var activeInstanceCounts: Map<Long, Int> = emptyMap()

    init {
        loadScene()
        observeSceneFx()
        observeActiveCounts()
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        soundboardPlayer.setMasterVolume(masterVolume)
        publishState()
    }

    fun triggerFx(fxTrackId: Long) {
        val fxTrack = latestTracksById[fxTrackId] ?: return
        runCatching {
            soundboardPlayer.triggerFx(fxTrack)
        }.onSuccess {
            refreshActiveCounts()
        }.onFailure { throwable ->
            _errorMessage.value = throwable.message ?: "Unable to trigger effect."
        }
    }

    fun stopFx(fxTrackId: Long) {
        soundboardPlayer.stopFxTrack(fxTrackId)
        refreshActiveCounts()
    }

    fun addFx(fxTrackIds: List<Long>) {
        if (fxTrackIds.isEmpty()) return
        viewModelScope.launch {
            runCatching {
                sceneRepository.addFx(sceneId, fxTrackIds)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to add effects."
            }
        }
    }

    fun removeFx(fxTrackId: Long) {
        soundboardPlayer.stopFxTrack(fxTrackId)
        refreshActiveCounts()
        viewModelScope.launch {
            runCatching {
                sceneRepository.removeFx(sceneId, fxTrackId)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to remove effect."
            }
        }
    }

    fun moveFx(fxTrackId: Long, direction: Int) {
        val currentIndex = latestAssignments.indexOfFirst { it.fxTrackId == fxTrackId }
        if (currentIndex == -1) return
        val targetIndex = (currentIndex + direction).coerceIn(0, latestAssignments.lastIndex)
        if (targetIndex == currentIndex) return

        val reorderedIds = latestAssignments.map { it.fxTrackId }.toMutableList().apply {
            add(targetIndex, removeAt(currentIndex))
        }

        viewModelScope.launch {
            runCatching {
                sceneRepository.reorderFx(sceneId, reorderedIds)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to reorder effects."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    private fun loadScene() {
        viewModelScope.launch {
            scene = sceneRepository.getScene(sceneId)
            publishState()
        }
    }

    private fun observeSceneFx() {
        viewModelScope.launch {
            sceneRepository.observeFx(sceneId)
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.message ?: "Unable to load scene effects.",
                    )
                }
                .collect { sceneFx ->
                    latestAssignments = sceneFx
                    latestTracksById = sceneFx.associate { assignment ->
                        assignment.fxTrackId to assignment.fxTrack
                    }
                    latestAvailableOptions = fxRepository.observeAll()
                        .first()
                        .filter { fxTrack ->
                            sceneFx.none { assignment -> assignment.fxTrackId == fxTrack.id }
                        }
                        .map { fxTrack ->
                            MultiSelectOption(
                                id = fxTrack.id,
                                title = fxTrack.name,
                                subtitle = "${fxTrack.playCount} plays",
                            )
                        }
                    refreshActiveCounts()
                    publishState()
                }
        }
    }

    private fun observeActiveCounts() {
        viewModelScope.launch {
            while (isActive) {
                refreshActiveCounts()
                delay(400)
            }
        }
    }

    private fun refreshActiveCounts() {
        val counts = soundboardPlayer.activeInstanceCounts()
        if (counts != activeInstanceCounts) {
            activeInstanceCounts = counts
            publishState()
        }
    }

    private fun publishState() {
        _uiState.value = UiState.Success(
            ActiveSceneSoundboardContent(
                sceneName = scene?.name ?: "Active Scene",
                masterVolume = masterVolume,
                effects = latestAssignments.mapIndexed { index, assignment ->
                    FxButtonModel(
                        fxTrackId = assignment.fxTrackId,
                        name = assignment.fxTrack.name,
                        playCount = assignment.fxTrack.playCount,
                        activeInstanceCount = activeInstanceCounts[assignment.fxTrackId] ?: 0,
                        canMoveUp = index > 0,
                        canMoveDown = index < latestAssignments.lastIndex,
                    )
                },
                availableFxOptions = latestAvailableOptions,
            ),
        )
    }
}
