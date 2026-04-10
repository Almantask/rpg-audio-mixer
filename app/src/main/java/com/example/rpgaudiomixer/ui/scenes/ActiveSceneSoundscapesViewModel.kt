package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.scene.SceneSoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface ActiveSceneSoundscapesUiState {
    data object Loading : ActiveSceneSoundscapesUiState

    data class Success(
        val sceneName: String,
        val masterVolume: Float,
        val soundscapes: List<ActiveSceneSoundscapeUiModel>,
        val availableCategoriesToAdd: List<SoundscapeCategory>,
    ) : ActiveSceneSoundscapesUiState

    data class Error(val message: String) : ActiveSceneSoundscapesUiState
}

data class ActiveSceneSoundscapeUiModel(
    val categoryId: Long,
    val categoryName: String,
    val themeLabel: String?,
    val currentTrackName: String?,
    val mixVolume: Float,
    val selectedIntensity: IntensityLevel,
    val availableIntensityLevels: Set<IntensityLevel>,
    val isPlaying: Boolean,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val sceneSoundscapeRepository: SceneSoundscapeRepository,
    private val sceneAudioEngine: SceneAudioEngine,
) : ViewModel() {
    private val sceneId: Long = checkNotNull(savedStateHandle["sceneId"])
    private val masterVolume = MutableStateFlow(1f)
    private val playbackSnapshots = MutableStateFlow<Map<Long, PlaybackSnapshot>>(emptyMap())
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val soundscapeSources: StateFlow<List<SceneSoundscapeSource>> =
        sceneSoundscapeRepository.observeSceneSoundscapes(sceneId)
            .flatMapLatest { soundscapes ->
                if (soundscapes.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(
                        soundscapes.map { soundscape ->
                            sceneSoundscapeRepository.observeTracks(soundscape.categoryId).mapToSource(soundscape)
                        },
                    ) { sources -> sources.toList() }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    val uiState: StateFlow<ActiveSceneSoundscapesUiState> = combine(
        sceneRepository.observeScene(sceneId),
        soundscapeSources,
        sceneSoundscapeRepository.observeAvailableSoundscapes(sceneId),
        masterVolume,
        playbackSnapshots,
    ) { scene, soundscapes, availableCategories, master, playback ->
        ActiveSceneSoundscapesUiState.Success(
            sceneName = scene?.name.orEmpty(),
            masterVolume = master,
            soundscapes = soundscapes.map { source ->
                source.toUiModel(playback[source.soundscape.categoryId])
            },
            availableCategoriesToAdd = availableCategories,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ActiveSceneSoundscapesUiState.Loading,
        )

    constructor(
        sceneId: Long,
        sceneRepository: SceneRepository,
        sceneSoundscapeRepository: SceneSoundscapeRepository,
        sceneAudioEngine: SceneAudioEngine,
    ) : this(
        savedStateHandle = SavedStateHandle(mapOf("sceneId" to sceneId)),
        sceneRepository = sceneRepository,
        sceneSoundscapeRepository = sceneSoundscapeRepository,
        sceneAudioEngine = sceneAudioEngine,
    )

    fun setMasterVolume(volume: Float) {
        val normalizedVolume = volume.coerceIn(0f, 1f)
        masterVolume.value = normalizedVolume
        sceneAudioEngine.setMasterVolume(normalizedVolume)
    }

    fun playCategory(categoryId: Long) {
        val source = soundscapeSources.value.firstOrNull { it.soundscape.categoryId == categoryId } ?: return
        val snapshot = playbackSnapshots.value[categoryId]
        val categoryPlayer = sceneAudioEngine.getCategoryPlayer(categoryId)

        if (snapshot?.currentTrackName != null && snapshot.isPlaying.not() && categoryPlayer != null) {
            categoryPlayer.resume()
            updatePlaybackSnapshot(categoryId) { it.copy(isPlaying = true) }
            return
        }

        val selectedPool = source.tracks.filter { track ->
            track.intensityLevel == source.soundscape.intensityLevel
        }
        if (selectedPool.isEmpty()) {
            _errorMessage.value = "No tracks are available at intensity ${source.soundscape.intensityLevel.label} for ${source.soundscape.categoryName}."
            return
        }

        sceneAudioEngine.setCategoryMix(categoryId, source.soundscape.mixVolume)
        sceneAudioEngine.rollRandomTrack(categoryId, selectedPool)
            .onSuccess { track ->
                updatePlaybackSnapshot(categoryId) {
                    PlaybackSnapshot(
                        currentTrackName = track.name,
                        isPlaying = true,
                    )
                }
            }
            .onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to play ${source.soundscape.categoryName}."
            }
    }

    fun pauseCategory(categoryId: Long) {
        sceneAudioEngine.pauseCategory(categoryId)
        updatePlaybackSnapshot(categoryId) { snapshot ->
            snapshot.copy(isPlaying = false)
        }
    }

    fun setMix(categoryId: Long, mixVolume: Float) {
        val normalizedVolume = mixVolume.coerceIn(0f, 1f)
        viewModelScope.launch {
            sceneSoundscapeRepository.updateMixVolume(sceneId, categoryId, normalizedVolume)
        }
        sceneAudioEngine.setCategoryMix(categoryId, normalizedVolume)
    }

    fun setIntensity(categoryId: Long, intensityLevel: IntensityLevel) {
        val source = soundscapeSources.value.firstOrNull { it.soundscape.categoryId == categoryId } ?: return
        if (intensityLevel !in source.availableIntensityLevels) {
            return
        }

        viewModelScope.launch {
            sceneSoundscapeRepository.updateIntensityLevel(sceneId, categoryId, intensityLevel)
        }
    }

    fun addCategory(categoryId: Long) {
        viewModelScope.launch {
            sceneSoundscapeRepository.addSoundscapeToScene(sceneId, categoryId)
        }
    }

    fun removeCategory(categoryId: Long) {
        sceneAudioEngine.stopCategory(categoryId)
        viewModelScope.launch {
            sceneSoundscapeRepository.removeSoundscapeFromScene(sceneId, categoryId)
        }
        playbackSnapshots.value = playbackSnapshots.value - categoryId
    }

    fun reorderCategories(orderedCategoryIds: List<Long>) {
        if (orderedCategoryIds.isEmpty()) {
            return
        }

        viewModelScope.launch {
            sceneSoundscapeRepository.reorderSoundscapes(sceneId, orderedCategoryIds)
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        sceneAudioEngine.releaseAll()
        super.onCleared()
    }

    private fun updatePlaybackSnapshot(
        categoryId: Long,
        transform: (PlaybackSnapshot) -> PlaybackSnapshot,
    ) {
        val currentSnapshot = playbackSnapshots.value[categoryId] ?: PlaybackSnapshot()
        playbackSnapshots.value = playbackSnapshots.value + (categoryId to transform(currentSnapshot))
    }
}

private data class SceneSoundscapeSource(
    val soundscape: SceneSoundscape,
    val tracks: List<SoundscapeTrack>,
) {
    val availableIntensityLevels: Set<IntensityLevel>
        get() = tracks.map { it.intensityLevel }.toSet()

    fun toUiModel(playbackSnapshot: PlaybackSnapshot?): ActiveSceneSoundscapeUiModel {
        return ActiveSceneSoundscapeUiModel(
            categoryId = soundscape.categoryId,
            categoryName = soundscape.categoryName,
            themeLabel = soundscape.themeLabel,
            currentTrackName = playbackSnapshot?.currentTrackName,
            mixVolume = soundscape.mixVolume,
            selectedIntensity = soundscape.intensityLevel,
            availableIntensityLevels = availableIntensityLevels,
            isPlaying = playbackSnapshot?.isPlaying == true,
        )
    }
}

private data class PlaybackSnapshot(
    val currentTrackName: String? = null,
    val isPlaying: Boolean = false,
)

private fun Flow<List<SoundscapeTrack>>.mapToSource(
    soundscape: SceneSoundscape,
): Flow<SceneSoundscapeSource> {
    return map { tracks ->
        SceneSoundscapeSource(soundscape = soundscape, tracks = tracks)
    }
}
