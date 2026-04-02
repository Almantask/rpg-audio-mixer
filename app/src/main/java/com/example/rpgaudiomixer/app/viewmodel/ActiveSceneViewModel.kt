package com.example.rpgaudiomixer.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.audio.SceneAudioController
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneFXTrack
import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory
import com.example.rpgaudiomixer.domain.repository.LibraryRepository
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import com.example.rpgaudiomixer.infra.audio.ExoSceneAudioController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryPlayState(
    val isPlaying: Boolean = false,
    val currentTrackName: String? = null,
    val selectedIntensity: IntensityLevel = IntensityLevel.I,
)

data class ActiveSceneUiState(
    val scene: Scene = Scene(name = ""),
    val soundscapeCategories: List<SceneSoundscapeCategory> = emptyList(),
    val fxTracks: List<SceneFXTrack> = emptyList(),
    val categoryPlayStates: Map<Long, CategoryPlayState> = emptyMap(),
    val playingFxIds: Set<Long> = emptySet(),
    val masterAtmosphereVolume: Float = 0.8f,
    val masterSoundboardVolume: Float = 0.8f,
    val isLoaded: Boolean = false,
)

@HiltViewModel
class ActiveSceneViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val libraryRepository: LibraryRepository,
    private val audioController: ExoSceneAudioController,
) : ViewModel() {

    val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])
    val autoPlay: Boolean = savedStateHandle["autoPlay"] ?: false

    private val _uiState = MutableStateFlow(ActiveSceneUiState())
    val uiState: StateFlow<ActiveSceneUiState> = _uiState.asStateFlow()

    private val pollingJobs = mutableMapOf<Long, Job>()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val scene = sceneRepository.getSceneById(sceneId) ?: return@launch
            _uiState.value = _uiState.value.copy(
                scene = scene,
                masterAtmosphereVolume = scene.masterAtmosphereVolume,
                masterSoundboardVolume = scene.masterSoundboardVolume,
            )
            audioController.setMasterSoundscapeVolume(scene.masterAtmosphereVolume)
            audioController.setMasterFXVolume(scene.masterSoundboardVolume)
        }
        viewModelScope.launch {
            sceneRepository.getSceneSoundscapeCategories(sceneId).collect { categories ->
                _uiState.value = _uiState.value.copy(soundscapeCategories = categories, isLoaded = true)
                // Initialise play states for any new categories
                val currentStates = _uiState.value.categoryPlayStates.toMutableMap()
                categories.forEach { ssc ->
                    if (!currentStates.containsKey(ssc.id)) {
                        currentStates[ssc.id] = CategoryPlayState()
                    }
                }
                _uiState.value = _uiState.value.copy(categoryPlayStates = currentStates)
            }
        }
        viewModelScope.launch {
            sceneRepository.getSceneFXTracks(sceneId).collect { fxTracks ->
                _uiState.value = _uiState.value.copy(fxTracks = fxTracks)
            }
        }
    }

    // ── Soundscape controls ───────────────────────────────────────────────────

    fun toggleCategoryPlayback(sceneCategoryId: Long) {
        val ssc = _uiState.value.soundscapeCategories.find { it.id == sceneCategoryId } ?: return
        val state = _uiState.value.categoryPlayStates[sceneCategoryId] ?: CategoryPlayState()

        if (audioController.isCategoryPlaying(ssc.category.id)) {
            audioController.stopCategory(ssc.category.id)
            updateCategoryPlayState(sceneCategoryId) { it.copy(isPlaying = false, currentTrackName = null) }
        } else {
            playRandomTrack(sceneCategoryId, state.selectedIntensity)
        }
    }

    fun playRandomTrack(sceneCategoryId: Long, intensity: IntensityLevel? = null) {
        val ssc = _uiState.value.soundscapeCategories.find { it.id == sceneCategoryId } ?: return
        val state = _uiState.value.categoryPlayStates[sceneCategoryId] ?: CategoryPlayState()
        val targetIntensity = intensity ?: state.selectedIntensity
        val tracks = ssc.category.tracksFor(targetIntensity)
        if (tracks.isEmpty()) return

        audioController.playCategory(ssc.category.id, targetIntensity, tracks.map { it.filePath })
        audioController.setCategoryVolume(ssc.category.id, ssc.mixVolume)

        val trackName = audioController.currentTrackForCategory(ssc.category.id)
        updateCategoryPlayState(sceneCategoryId) {
            it.copy(isPlaying = true, currentTrackName = trackName, selectedIntensity = targetIntensity)
        }

        viewModelScope.launch {
            libraryRepository.incrementTrackPlayCount(tracks.first().id)
        }
    }

    fun setIntensityLevel(sceneCategoryId: Long, level: IntensityLevel) {
        val isPlaying = _uiState.value.categoryPlayStates[sceneCategoryId]?.isPlaying == true
        updateCategoryPlayState(sceneCategoryId) { it.copy(selectedIntensity = level) }
        if (isPlaying) {
            playRandomTrack(sceneCategoryId, level)
        }
    }

    fun setCategoryMixVolume(sceneCategoryId: Long, volume: Float) {
        val ssc = _uiState.value.soundscapeCategories.find { it.id == sceneCategoryId } ?: return
        audioController.setCategoryVolume(ssc.category.id, volume)
        viewModelScope.launch {
            sceneRepository.updateCategoryMixVolume(sceneCategoryId, volume)
        }
    }

    fun setMasterAtmosphereVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(masterAtmosphereVolume = volume)
        audioController.setMasterSoundscapeVolume(volume)
        viewModelScope.launch {
            sceneRepository.updateSceneMasterAtmosphereVolume(sceneId, volume)
        }
    }

    fun removeSoundscapeCategory(sceneCategoryId: Long) {
        val ssc = _uiState.value.soundscapeCategories.find { it.id == sceneCategoryId } ?: return
        audioController.stopCategory(ssc.category.id)
        viewModelScope.launch {
            sceneRepository.removeCategoryFromScene(sceneCategoryId)
        }
    }

    // ── Soundboard controls ───────────────────────────────────────────────────

    fun toggleFX(sceneFxId: Long) {
        val sceneFX = _uiState.value.fxTracks.find { it.id == sceneFxId } ?: return
        val fxId = sceneFX.fxTrack.id

        if (audioController.isFxPlaying(fxId)) {
            audioController.stopFX(fxId)
            _uiState.value = _uiState.value.copy(
                playingFxIds = _uiState.value.playingFxIds - fxId
            )
        } else {
            audioController.playFX(fxId, sceneFX.fxTrack.filePath)
            _uiState.value = _uiState.value.copy(
                playingFxIds = _uiState.value.playingFxIds + fxId
            )
            viewModelScope.launch {
                libraryRepository.incrementFXPlayCount(fxId)
                // Auto-remove from playing set when done (poll every 500ms)
                while (audioController.isFxPlaying(fxId)) {
                    delay(500)
                }
                _uiState.value = _uiState.value.copy(
                    playingFxIds = _uiState.value.playingFxIds - fxId
                )
            }
        }
    }

    fun playFXRetrigger(sceneFxId: Long) {
        val sceneFX = _uiState.value.fxTracks.find { it.id == sceneFxId } ?: return
        val fxId = sceneFX.fxTrack.id
        audioController.playFX(fxId, sceneFX.fxTrack.filePath)
        if (fxId !in _uiState.value.playingFxIds) {
            _uiState.value = _uiState.value.copy(playingFxIds = _uiState.value.playingFxIds + fxId)
        }
        viewModelScope.launch {
            libraryRepository.incrementFXPlayCount(fxId)
            while (audioController.isFxPlaying(fxId)) { delay(500) }
            _uiState.value = _uiState.value.copy(playingFxIds = _uiState.value.playingFxIds - fxId)
        }
    }

    fun setMasterSoundboardVolume(volume: Float) {
        _uiState.value = _uiState.value.copy(masterSoundboardVolume = volume)
        audioController.setMasterFXVolume(volume)
        viewModelScope.launch {
            sceneRepository.updateSceneMasterSoundboardVolume(sceneId, volume)
        }
    }

    fun removeFX(sceneFxId: Long) {
        val sceneFX = _uiState.value.fxTracks.find { it.id == sceneFxId } ?: return
        audioController.stopFX(sceneFX.fxTrack.id)
        viewModelScope.launch {
            sceneRepository.removeFXFromScene(sceneFxId)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun updateCategoryPlayState(
        sceneCategoryId: Long,
        update: (CategoryPlayState) -> CategoryPlayState,
    ) {
        val states = _uiState.value.categoryPlayStates.toMutableMap()
        states[sceneCategoryId] = update(states[sceneCategoryId] ?: CategoryPlayState())
        _uiState.value = _uiState.value.copy(categoryPlayStates = states)
    }

    override fun onCleared() {
        super.onCleared()
        audioController.release()
    }
}
