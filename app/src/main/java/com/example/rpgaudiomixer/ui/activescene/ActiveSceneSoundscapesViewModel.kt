package com.example.rpgaudiomixer.ui.activescene

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.data.activescene.SceneAudioDao
import com.example.rpgaudiomixer.data.activescene.SceneSoundscapeCrossRef
import com.example.rpgaudiomixer.data.soundscape.SoundscapeTrackDao
import com.example.rpgaudiomixer.domain.audio.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    private val sceneAudioDao: SceneAudioDao,
    private val soundscapeRepository: SoundscapeRepository,
    private val audioEngine: SceneAudioEngine,
    private val soundscapeTrackDao: SoundscapeTrackDao,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _sceneId: Long = savedStateHandle["sceneId"] ?: 0L

    constructor(
        sceneId: Long,
        sceneAudioDao: SceneAudioDao,
        soundscapeRepository: SoundscapeRepository,
        audioEngine: SceneAudioEngine,
        soundscapeTrackDao: SoundscapeTrackDao,
    ) : this(
        sceneAudioDao = sceneAudioDao,
        soundscapeRepository = soundscapeRepository,
        audioEngine = audioEngine,
        soundscapeTrackDao = soundscapeTrackDao,
        savedStateHandle = SavedStateHandle(mapOf("sceneId" to sceneId)),
    )

    private val _uiState = MutableStateFlow<ActiveSceneSoundscapesUiState>(ActiveSceneSoundscapesUiState.Loading)
    val uiState: StateFlow<ActiveSceneSoundscapesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                sceneAudioDao.observeSoundscapesForScene(_sceneId),
                soundscapeRepository.observeAllCategories(),
            ) { refs, allCategories ->
                val categoryMap = allCategories.associateBy { it.id }
                refs.mapNotNull { ref ->
                    categoryMap[ref.categoryId]?.let { category ->
                        CategoryState(
                            category = category,
                            isPlaying = audioEngine.getPlayer(ref.categoryId)?.isPlaying?.value ?: false,
                            mixVolume = ref.mixVolume,
                            intensity = IntensityLevel.entries.first { it.value == ref.intensityLevel },
                            displayOrder = ref.displayOrder,
                        )
                    }
                }.sortedBy { it.displayOrder }
            }
                .catch { e -> _uiState.value = ActiveSceneSoundscapesUiState.Error(e.message ?: "Unknown error") }
                .collect { states ->
                    _uiState.value = ActiveSceneSoundscapesUiState.Success(
                        categoryStates = states,
                        masterVolume = audioEngine.masterVolume.value,
                    )
                }
        }
    }

    fun setMasterVolume(volume: Float) {
        audioEngine.setMasterVolume(volume)
        val current = _uiState.value
        if (current is ActiveSceneSoundscapesUiState.Success) {
            _uiState.value = current.copy(masterVolume = volume)
        }
    }

    fun playCategory(categoryId: Long) {
        val state = ((_uiState.value as? ActiveSceneSoundscapesUiState.Success)
            ?.categoryStates?.firstOrNull { it.category.id == categoryId }) ?: return
        val engine = audioEngine.getPlayer(categoryId) ?: run {
            audioEngine.addCategory(categoryId)
            audioEngine.getPlayer(categoryId)
        } ?: return
        val pool = state.tracksForCurrentIntensity
        if (pool.isEmpty()) engine.rollRandomTrack(state.category.tracks)
        else engine.rollRandomTrack(pool)
    }

    fun pauseCategory(categoryId: Long) {
        audioEngine.getPlayer(categoryId)?.pause()
    }

    fun rollRandom(categoryId: Long) {
        val state = ((_uiState.value as? ActiveSceneSoundscapesUiState.Success)
            ?.categoryStates?.firstOrNull { it.category.id == categoryId }) ?: return
        val engine = audioEngine.getPlayer(categoryId) ?: run {
            audioEngine.addCategory(categoryId)
            audioEngine.getPlayer(categoryId)
        } ?: return
        engine.rollRandomTrack(state.tracksForCurrentIntensity.ifEmpty { state.category.tracks })
    }

    fun setIntensity(categoryId: Long, level: IntensityLevel) {
        viewModelScope.launch {
            val current = (_uiState.value as? ActiveSceneSoundscapesUiState.Success)
                ?.categoryStates?.firstOrNull { it.category.id == categoryId } ?: return@launch
            sceneAudioDao.updateSoundscapeRef(
                SceneSoundscapeCrossRef(
                    sceneId = _sceneId,
                    categoryId = categoryId,
                    displayOrder = current.displayOrder,
                    mixVolume = current.mixVolume,
                    intensityLevel = level.value,
                )
            )
        }
    }

    fun setMix(categoryId: Long, volume: Float) {
        viewModelScope.launch {
            val current = (_uiState.value as? ActiveSceneSoundscapesUiState.Success)
                ?.categoryStates?.firstOrNull { it.category.id == categoryId } ?: return@launch
            sceneAudioDao.updateSoundscapeRef(
                SceneSoundscapeCrossRef(
                    sceneId = _sceneId,
                    categoryId = categoryId,
                    displayOrder = current.displayOrder,
                    mixVolume = volume,
                    intensityLevel = current.intensity.value,
                )
            )
            audioEngine.getPlayer(categoryId)?.setMixVolume(volume)
        }
    }

    fun removeCategory(categoryId: Long) {
        viewModelScope.launch {
            audioEngine.removeCategory(categoryId)
            sceneAudioDao.removeSoundscapeFromScene(_sceneId, categoryId)
        }
    }

    fun addCategory(categoryId: Long) {
        viewModelScope.launch {
            val order = (_uiState.value as? ActiveSceneSoundscapesUiState.Success)
                ?.categoryStates?.size ?: 0
            sceneAudioDao.addSoundscapeToScene(
                SceneSoundscapeCrossRef(
                    sceneId = _sceneId,
                    categoryId = categoryId,
                    displayOrder = order,
                )
            )
            audioEngine.addCategory(categoryId)
        }
    }

    fun playCategoryWithStats(categoryId: Long, trackId: Long) {
        viewModelScope.launch {
            playCategory(categoryId)
            runCatching { soundscapeTrackDao.incrementPlayCount(trackId) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.releaseAll()
    }
}
