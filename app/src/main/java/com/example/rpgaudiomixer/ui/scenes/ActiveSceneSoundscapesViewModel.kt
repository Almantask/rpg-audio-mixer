package com.example.rpgaudiomixer.ui.scenes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.MultiSelectOption
import com.example.rpgaudiomixer.app.components.SoundscapeCategoryCardModel
import com.example.rpgaudiomixer.app.navigation.MainNavDestination
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ActiveSceneSoundscapesContent(
    val sceneName: String,
    val masterVolume: Float,
    val categories: List<SoundscapeCategoryCardModel>,
    val availableCategoryOptions: List<MultiSelectOption>,
)

private data class CategoryPlaybackSnapshot(
    val currentTrack: SoundscapeTrack? = null,
    val isPlaying: Boolean = false,
)

@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val sceneAudioEngine: SceneAudioEngine,
) : ViewModel() {

    private val sceneId: Long = checkNotNull(savedStateHandle[MainNavDestination.SCENE_ID_ARG])
    private val autoplay: Boolean = savedStateHandle[MainNavDestination.AUTOPLAY_ARG] ?: false

    private val _uiState =
        MutableStateFlow<UiState<ActiveSceneSoundscapesContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<ActiveSceneSoundscapesContent>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var scene: Scene? = null
    private var masterVolume: Float = 1f
    private var latestAssignments: List<SceneSoundscape> = emptyList()
    private var latestTracksByCategory: Map<Long, List<SoundscapeTrack>> = emptyMap()
    private var latestAvailableOptions: List<MultiSelectOption> = emptyList()
    private var playbackSnapshots: Map<Long, CategoryPlaybackSnapshot> = emptyMap()
    private var didAutoplay = false

    init {
        loadScene()
        observeSoundscapes()
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        sceneAudioEngine.setMasterVolume(masterVolume)
        publishState()
    }

    fun toggleCategoryPlayback(categoryId: Long) {
        val assignment = latestAssignments.firstOrNull { it.categoryId == categoryId } ?: return
        val snapshot = playbackSnapshots[categoryId]
        if (snapshot?.isPlaying == true) {
            sceneAudioEngine.pauseCategory(categoryId)
            playbackSnapshots = playbackSnapshots + (categoryId to snapshot.copy(isPlaying = false))
            publishState()
            return
        }

        if (snapshot?.currentTrack != null) {
            sceneAudioEngine.resumeCategory(categoryId)
            playbackSnapshots = playbackSnapshots + (categoryId to snapshot.copy(isPlaying = true))
            publishState()
            return
        }

        rollRandom(categoryId = categoryId, intensityLevel = assignment.intensityLevel)
    }

    fun rollRandom(categoryId: Long) {
        val assignment = latestAssignments.firstOrNull { it.categoryId == categoryId } ?: return
        rollRandom(categoryId = categoryId, intensityLevel = assignment.intensityLevel)
    }

    fun setIntensity(categoryId: Long, intensityLevel: IntensityLevel) {
        val assignment = latestAssignments.firstOrNull { it.categoryId == categoryId } ?: return
        val enabledLevels = assignment.availableLevels()
        if (intensityLevel !in enabledLevels) return

        viewModelScope.launch {
            runCatching {
                sceneRepository.updateSoundscapeIntensity(sceneId, categoryId, intensityLevel)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to update intensity."
            }
        }
    }

    fun setMix(categoryId: Long, mixVolume: Float) {
        val resolvedVolume = mixVolume.coerceIn(0f, 1f)
        sceneAudioEngine.setCategoryMixVolume(categoryId, resolvedVolume)
        viewModelScope.launch {
            runCatching {
                sceneRepository.updateSoundscapeMix(sceneId, categoryId, resolvedVolume)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to update mix volume."
            }
        }
    }

    fun removeCategory(categoryId: Long) {
        sceneAudioEngine.removeCategory(categoryId)
        playbackSnapshots = playbackSnapshots - categoryId
        viewModelScope.launch {
            runCatching {
                sceneRepository.removeSoundscape(sceneId, categoryId)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to remove soundscape."
            }
        }
    }

    fun addCategories(categoryIds: List<Long>) {
        if (categoryIds.isEmpty()) return
        viewModelScope.launch {
            runCatching {
                sceneRepository.addSoundscapes(sceneId, categoryIds)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to add soundscapes."
            }
        }
    }

    fun moveCategory(categoryId: Long, direction: Int) {
        val currentIndex = latestAssignments.indexOfFirst { it.categoryId == categoryId }
        if (currentIndex == -1) return
        val targetIndex = (currentIndex + direction).coerceIn(0, latestAssignments.lastIndex)
        if (targetIndex == currentIndex) return

        val reorderedIds = latestAssignments.map { it.categoryId }.toMutableList().apply {
            add(targetIndex, removeAt(currentIndex))
        }

        viewModelScope.launch {
            runCatching {
                sceneRepository.reorderSoundscapes(sceneId, reorderedIds)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to reorder soundscapes."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        sceneAudioEngine.releaseAll()
        super.onCleared()
    }

    private fun loadScene() {
        viewModelScope.launch {
            scene = sceneRepository.getScene(sceneId)
            publishState()
        }
    }

    private fun observeSoundscapes() {
        viewModelScope.launch {
            sceneRepository.observeSoundscapes(sceneId)
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.message ?: "Unable to load active scene soundscapes.",
                    )
                }
                .collect { assignments ->
                    latestAssignments = assignments
                    latestTracksByCategory = assignments.associate { assignment ->
                        assignment.categoryId to soundscapeRepository.getTracks(assignment.categoryId)
                    }
                    latestAvailableOptions = soundscapeRepository.observeCategories()
                        .first()
                        .filter { category ->
                            category.totalTrackCount() > 0 &&
                                assignments.none { assignment -> assignment.categoryId == category.id }
                        }
                        .map { category ->
                            MultiSelectOption(
                                id = category.id,
                                title = category.name,
                                subtitle = "${category.totalTrackCount()} tracks ready",
                            )
                        }
                    syncAudioEngine(assignments)
                    publishState()
                    autoplayIfNeeded()
                }
        }
    }

    private suspend fun autoplayIfNeeded() {
        if (!autoplay || didAutoplay || latestAssignments.isEmpty()) return
        latestAssignments.forEach { assignment ->
            rollRandom(categoryId = assignment.categoryId, intensityLevel = assignment.intensityLevel)
        }
        didAutoplay = true
    }

    private fun syncAudioEngine(assignments: List<SceneSoundscape>) {
        sceneAudioEngine.setMasterVolume(masterVolume)

        val assignmentIds = assignments.map { it.categoryId }.toSet()
        playbackSnapshots.keys
            .filterNot { categoryId -> categoryId in assignmentIds }
            .forEach { categoryId ->
                sceneAudioEngine.removeCategory(categoryId)
                playbackSnapshots = playbackSnapshots - categoryId
            }

        assignments.forEach { assignment ->
            sceneAudioEngine.setCategoryMixVolume(assignment.categoryId, assignment.mixVolume)
            val snapshot = playbackSnapshots[assignment.categoryId]
            if (snapshot != null && snapshot.currentTrack != null) {
                playbackSnapshots = playbackSnapshots + (
                    assignment.categoryId to snapshot.copy(
                        currentTrack = snapshot.currentTrack.copy(mixVolume = assignment.mixVolume),
                    )
                )
            }
        }
    }

    private fun rollRandom(categoryId: Long, intensityLevel: IntensityLevel) {
        val pool = latestTracksByCategory[categoryId]
            .orEmpty()
            .filter { track -> track.intensityLevel == intensityLevel }
        if (pool.isEmpty()) {
            _errorMessage.value = "No tracks are available for intensity ${intensityLevel.label}."
            return
        }

        runCatching {
            sceneAudioEngine.rollRandomTrack(categoryId, pool)
        }.onSuccess { selectedTrack ->
            if (selectedTrack != null) {
                playbackSnapshots = playbackSnapshots + (
                    categoryId to CategoryPlaybackSnapshot(
                        currentTrack = selectedTrack,
                        isPlaying = true,
                    )
                )
                publishState()
            }
        }.onFailure { throwable ->
            _errorMessage.value = throwable.message ?: "Unable to play soundscape."
        }
    }

    private fun publishState() {
        val sceneName = scene?.name ?: "Active Scene"
        val categoryModels = latestAssignments.mapIndexed { index, assignment ->
            val snapshot = playbackSnapshots[assignment.categoryId]
            SoundscapeCategoryCardModel(
                categoryId = assignment.categoryId,
                name = assignment.category.name,
                currentTrackName = snapshot?.currentTrack?.name,
                mixVolume = assignment.mixVolume,
                selectedIntensity = assignment.intensityLevel,
                enabledLevels = assignment.availableLevels(),
                isPlaying = snapshot?.isPlaying == true,
                canPlay = latestTracksByCategory[assignment.categoryId].orEmpty().isNotEmpty(),
                canMoveUp = index > 0,
                canMoveDown = index < latestAssignments.lastIndex,
            )
        }

        _uiState.value = UiState.Success(
            ActiveSceneSoundscapesContent(
                sceneName = sceneName,
                masterVolume = masterVolume,
                categories = categoryModels,
                availableCategoryOptions = latestAvailableOptions,
            ),
        )
    }
}

private fun SceneSoundscape.availableLevels(): Set<IntensityLevel> {
    return buildSet {
        if (category.levelOneTrackCount > 0) add(IntensityLevel.I)
        if (category.levelTwoTrackCount > 0) add(IntensityLevel.II)
        if (category.levelThreeTrackCount > 0) add(IntensityLevel.III)
    }
}

private fun com.example.rpgaudiomixer.domain.model.SoundscapeCategory.totalTrackCount(): Int {
    return levelOneTrackCount + levelTwoTrackCount + levelThreeTrackCount
}
