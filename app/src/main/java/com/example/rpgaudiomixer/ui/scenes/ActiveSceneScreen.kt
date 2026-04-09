package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.AudioFilePickerButton
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.glowBorder
import com.example.rpgaudiomixer.app.components.IntensitySelector
import com.example.rpgaudiomixer.app.components.MasterSlider
import com.example.rpgaudiomixer.app.components.MixSlider
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.domain.media.SceneAudioController
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ActiveSceneSoundscapeCardUiState(
    val category: SoundscapeCategory,
    val mixVolume: Float,
    val selectedIntensityLevel: IntensityLevel,
    val availableIntensityLevels: Set<IntensityLevel>,
    val currentTrackName: String? = null,
    val isPlaying: Boolean = false,
    val displayOrder: Int = 0,
)

data class ActiveSceneSoundscapeSelectionOptionUiState(
    val category: SoundscapeCategory,
    val isAdded: Boolean,
)

data class ActiveSceneSoundscapesUiState(
    val isLoading: Boolean = true,
    val sceneName: String = "Scene",
    val masterVolume: Float = 1f,
    val soundscapes: List<ActiveSceneSoundscapeCardUiState> = emptyList(),
    val isSelectionSheetVisible: Boolean = false,
    val selectionOptions: List<ActiveSceneSoundscapeSelectionOptionUiState> = emptyList(),
    val errorMessage: String? = null,
    val pendingComposerCategoryId: Long? = null,
)

@Composable
fun ActiveSceneRoute(
    onOpenSoundscapeComposer: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundscapesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.pendingComposerCategoryId) {
        uiState.pendingComposerCategoryId?.let { categoryId ->
            onOpenSoundscapeComposer(categoryId)
            viewModel.onComposerOpened()
        }
    }

    ActiveSceneScreen(
        uiState = uiState,
        onSetMasterVolume = viewModel::setMasterVolume,
        onPlayCategory = viewModel::playCategory,
        onPauseCategory = viewModel::pauseCategory,
        onRollRandom = viewModel::rollRandom,
        onSetIntensity = viewModel::setIntensity,
        onSetMix = viewModel::setMix,
        onReorderCategories = viewModel::reorderCategories,
        onRemoveCategory = viewModel::removeCategory,
        onShowAddSoundscapeSheet = viewModel::showAddSoundscapeSheet,
        onHideAddSoundscapeSheet = viewModel::hideAddSoundscapeSheet,
        onAddCategory = viewModel::addCategory,
        onImportNewSoundscape = viewModel::importNewSoundscape,
        onDismissError = viewModel::clearError,
        modifier = modifier,
    )
}

@Composable
fun ActiveSceneScreen(
    uiState: ActiveSceneSoundscapesUiState,
    onSetMasterVolume: (Float) -> Unit,
    onPlayCategory: (Long) -> Unit,
    onPauseCategory: (Long) -> Unit,
    onRollRandom: (Long) -> Unit,
    onSetIntensity: (Long, IntensityLevel) -> Unit,
    onSetMix: (Long, Float) -> Unit,
    onReorderCategories: (List<Long>) -> Unit,
    onRemoveCategory: (Long) -> Unit,
    onShowAddSoundscapeSheet: () -> Unit,
    onHideAddSoundscapeSheet: () -> Unit,
    onAddCategory: (Long) -> Unit,
    onImportNewSoundscape: (String) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = uiState.sceneName,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        TabRow(selectedTabIndex = selectedTab) {
            listOf("Soundscapes", "Soundboard").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title) },
                )
            }
        }
        if (selectedTab == 0) {
            MasterSlider(
                label = "Master Atmosphere",
                value = uiState.masterVolume,
                onValueChange = onSetMasterVolume,
            )
            LazyColumn(
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.soundscapes, key = { it.category.id }) { soundscape ->
                    SwipeToDeleteContainer(
                        onDelete = { onRemoveCategory(soundscape.category.id) },
                    ) {
                        SoundscapeCategoryCard(
                            soundscape = soundscape,
                            onPlayPause = {
                                if (soundscape.isPlaying) {
                                    onPauseCategory(soundscape.category.id)
                                } else {
                                    onPlayCategory(soundscape.category.id)
                                }
                            },
                            onRollRandom = { onRollRandom(soundscape.category.id) },
                            onSetMix = { onSetMix(soundscape.category.id, it) },
                            onSetIntensity = { onSetIntensity(soundscape.category.id, it) },
                        )
                    }
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onShowAddSoundscapeSheet,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = "Add New Soundscape",
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Soundboard tab")
            }
        }
    }

    if (uiState.isSelectionSheetVisible) {
        SoundscapeSelectionDialog(
            options = uiState.selectionOptions,
            onDismiss = onHideAddSoundscapeSheet,
            onAddCategory = onAddCategory,
            onImportNewSoundscape = onImportNewSoundscape,
        )
    }

    ErrorDialog(
        message = uiState.errorMessage,
        onDismiss = onDismissError,
    )
}

@Composable
private fun SoundscapeCategoryCard(
    soundscape: ActiveSceneSoundscapeCardUiState,
    onPlayPause: () -> Unit,
    onRollRandom: () -> Unit,
    onSetMix: (Float) -> Unit,
    onSetIntensity: (IntensityLevel) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glowBorder(
                isPlaying = soundscape.isPlaying,
                color = MaterialTheme.colorScheme.primary,
            )
            .testTag("ActiveSceneSoundscapeCard_${soundscape.category.id}"),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = soundscape.category.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Row {
                    IconButton(onClick = onRollRandom) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Play random track",
                        )
                    }
                    IconButton(onClick = onPlayPause) {
                        Icon(
                            imageVector = if (soundscape.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (soundscape.isPlaying) "Pause" else "Play",
                        )
                    }
                }
            }
            Text(
                text = soundscape.currentTrackName ?: "No track loaded",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MixSlider(
                mixVolume = soundscape.mixVolume,
                onMixChanged = onSetMix,
            )
            IntensitySelector(
                selectedLevel = soundscape.selectedIntensityLevel,
                enabledLevels = soundscape.availableIntensityLevels,
                onSelectLevel = onSetIntensity,
            )
        }
    }
}

@Composable
private fun SoundscapeSelectionDialog(
    options: List<ActiveSceneSoundscapeSelectionOptionUiState>,
    onDismiss: () -> Unit,
    onAddCategory: (Long) -> Unit,
    onImportNewSoundscape: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Soundscape Selection") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(options, key = { it.category.id }) { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    shape = MaterialTheme.shapes.medium,
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = option.category.name)
                            if (option.isAdded) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Already added",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                IconButton(onClick = { onAddCategory(option.category.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add ${option.category.name}",
                                    )
                                }
                            }
                        }
                    }
                }
                AudioFilePickerButton(
                    text = "Import New",
                    onAudioPicked = onImportNewSoundscape,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val sceneAudioController: SceneAudioController,
) : ViewModel() {
    private val sceneId: Long = requireNotNull(savedStateHandle[AppRoute.SCENE_ID_ARG])
    private val autoplay: Boolean = savedStateHandle[AppRoute.AUTOPLAY_ARG] ?: false
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    private var currentCategoryIds: Set<Long> = emptySet()
    private var pendingAddedCategoryIds: Set<Long> = emptySet()

    internal constructor(
        sceneId: Long,
        autoplay: Boolean,
        sceneRepository: SceneRepository,
        soundscapeRepository: SoundscapeRepository,
        sceneAudioController: SceneAudioController,
        mainDispatcher: CoroutineDispatcher,
    ) : this(
        savedStateHandle = SavedStateHandle(
            mapOf(
                AppRoute.SCENE_ID_ARG to sceneId,
                AppRoute.AUTOPLAY_ARG to autoplay,
            )
        ),
        sceneRepository = sceneRepository,
        soundscapeRepository = soundscapeRepository,
        sceneAudioController = sceneAudioController,
    ) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(ActiveSceneSoundscapesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            val sceneSoundscapesFlow = sceneRepository.observeSoundscapesForScene(sceneId)
            val tracksByCategoryFlow = sceneSoundscapesFlow.flatMapLatest { soundscapes ->
                soundscapes.combineTrackFlows(soundscapeRepository)
            }
            combine(
                sceneRepository.observeScene(sceneId),
                soundscapeRepository.observeCategories(),
                sceneSoundscapesFlow,
                tracksByCategoryFlow,
            ) { scene, categories, sceneSoundscapes, tracksByCategory ->
                buildUiState(
                    scene = scene,
                    categories = categories,
                    sceneSoundscapes = sceneSoundscapes,
                    tracksByCategory = tracksByCategory,
                )
            }
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load soundscapes.",
                    )
                }
                .collect { state ->
                    syncAudioCategories(state)
                    _uiState.value = state
                }
        }
    }

    fun setMasterVolume(volume: Float) {
        val normalized = volume.coerceIn(0f, 1f)
        _uiState.value = _uiState.value.copy(masterVolume = normalized)
        sceneAudioController.setMasterVolume(normalized)
    }

    fun playCategory(categoryId: Long) {
        val soundscape = _uiState.value.soundscapes.firstOrNull { it.category.id == categoryId } ?: return
        if (!soundscape.isPlaying && soundscape.currentTrackName != null) {
            sceneAudioController.resume(categoryId)
            updateCard(categoryId) { it.copy(isPlaying = true) }
        } else {
            rollRandom(categoryId)
        }
    }

    fun pauseCategory(categoryId: Long) {
        sceneAudioController.pause(categoryId)
        updateCard(categoryId) { it.copy(isPlaying = false) }
    }

    fun rollRandom(categoryId: Long) {
        val soundscape = _uiState.value.soundscapes.firstOrNull { it.category.id == categoryId } ?: return
        viewModelScope.launch(mainDispatcher) {
            val tracks = soundscapeRepository.observeTracks(categoryId).first()
            val pool = tracks.filter { it.intensityLevel == soundscape.selectedIntensityLevel }
            if (pool.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "No tracks are available at intensity ${soundscape.selectedIntensityLevel.label}.",
                )
                return@launch
            }
            val selectedTrack = sceneAudioController.rollRandomTrack(categoryId, pool)
            if (selectedTrack != null) {
                updateCard(categoryId) {
                    it.copy(
                        currentTrackName = selectedTrack.name,
                        isPlaying = true,
                    )
                }
            }
        }
    }

    fun setIntensity(categoryId: Long, level: IntensityLevel) {
        val soundscape = _uiState.value.soundscapes.firstOrNull { it.category.id == categoryId } ?: return
        if (!soundscape.availableIntensityLevels.contains(level)) {
            return
        }
        updateCard(categoryId) { it.copy(selectedIntensityLevel = level) }
        persistSoundscape(categoryId)
    }

    fun setMix(categoryId: Long, mixVolume: Float) {
        val normalized = mixVolume.coerceIn(0f, 1f)
        updateCard(categoryId) { it.copy(mixVolume = normalized) }
        sceneAudioController.setCategoryMixVolume(categoryId, normalized)
        persistSoundscape(categoryId)
    }

    fun reorderCategories(categoryIds: List<Long>) {
        val updated = categoryIds.mapNotNull { id ->
            _uiState.value.soundscapes.firstOrNull { it.category.id == id }
        }.mapIndexed { index, item -> item.copy(displayOrder = index) }
        _uiState.value = _uiState.value.copy(soundscapes = updated)
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.reorderSoundscapes(sceneId = sceneId, orderedCategoryIds = categoryIds)
        }
    }

    fun removeCategory(categoryId: Long) {
        _uiState.value = _uiState.value.copy(
            soundscapes = _uiState.value.soundscapes.filterNot { it.category.id == categoryId },
            selectionOptions = _uiState.value.selectionOptions.map { option ->
                if (option.category.id == categoryId) option.copy(isAdded = false) else option
            },
        )
        pendingAddedCategoryIds -= categoryId
        sceneAudioController.removeCategory(categoryId)
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.removeSoundscapeFromScene(sceneId = sceneId, categoryId = categoryId)
        }
    }

    fun showAddSoundscapeSheet() {
        _uiState.value = _uiState.value.copy(isSelectionSheetVisible = true)
    }

    fun hideAddSoundscapeSheet() {
        _uiState.value = _uiState.value.copy(isSelectionSheetVisible = false)
    }

    fun addCategory(categoryId: Long) {
        pendingAddedCategoryIds += categoryId
        _uiState.value = _uiState.value.copy(
            selectionOptions = _uiState.value.selectionOptions.map { option ->
                if (option.category.id == categoryId) option.copy(isAdded = true) else option
            }
        )
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.addSoundscapeToScene(sceneId = sceneId, categoryId = categoryId)
        }
    }

    fun importNewSoundscape(sourceUri: String) {
        viewModelScope.launch(mainDispatcher) {
            val categoryName = sourceUri.substringAfterLast('/').ifBlank { "Imported Soundscape" }
            val categoryId = soundscapeRepository.createCategory(categoryName)
            soundscapeRepository.importTrack(categoryId = categoryId, sourceUri = sourceUri)
            _uiState.value = _uiState.value.copy(
                pendingComposerCategoryId = categoryId,
                isSelectionSheetVisible = false,
            )
        }
    }

    fun onComposerOpened() {
        _uiState.value = _uiState.value.copy(pendingComposerCategoryId = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun updateCard(categoryId: Long, transform: (ActiveSceneSoundscapeCardUiState) -> ActiveSceneSoundscapeCardUiState) {
        _uiState.value = _uiState.value.copy(
            soundscapes = _uiState.value.soundscapes.map { item ->
                if (item.category.id == categoryId) transform(item) else item
            }
        )
    }

    private fun persistSoundscape(categoryId: Long) {
        val soundscape = _uiState.value.soundscapes.firstOrNull { it.category.id == categoryId } ?: return
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.updateSoundscapeInScene(
                sceneId = sceneId,
                categoryId = categoryId,
                displayOrder = soundscape.displayOrder,
                mixVolume = soundscape.mixVolume,
                intensityLevel = soundscape.selectedIntensityLevel,
            )
        }
    }

    private fun buildUiState(
        scene: com.example.rpgaudiomixer.domain.model.Scene?,
        categories: List<SoundscapeCategory>,
        sceneSoundscapes: List<SceneSoundscape>,
        tracksByCategory: Map<Long, List<SoundscapeTrack>>,
    ): ActiveSceneSoundscapesUiState {
        val currentState = _uiState.value
        val soundscapeCards = sceneSoundscapes
            .sortedBy(SceneSoundscape::displayOrder)
            .map { soundscape ->
                val tracks = tracksByCategory[soundscape.category.id].orEmpty()
                val availableLevels = tracks.map(SoundscapeTrack::intensityLevel).toSet()
                val previous = currentState.soundscapes.firstOrNull { it.category.id == soundscape.category.id }
                ActiveSceneSoundscapeCardUiState(
                    category = soundscape.category,
                    mixVolume = previous?.mixVolume ?: soundscape.mixVolume,
                    selectedIntensityLevel = previous?.selectedIntensityLevel ?: soundscape.intensityLevel,
                    availableIntensityLevels = availableLevels,
                    currentTrackName = previous?.currentTrackName,
                    isPlaying = previous?.isPlaying == true || (autoplay && previous == null && soundscape.displayOrder == 0),
                    displayOrder = soundscape.displayOrder,
                )
            }
        val addedIds = soundscapeCards.map { it.category.id }.toSet() + pendingAddedCategoryIds
        val selectionOptions = categories
            .filter(SoundscapeCategory::hasTracks)
            .sortedBy(SoundscapeCategory::name)
            .map { category ->
                ActiveSceneSoundscapeSelectionOptionUiState(
                    category = category,
                    isAdded = addedIds.contains(category.id),
                )
            }
        return currentState.copy(
            isLoading = false,
            sceneName = scene?.name ?: "Scene",
            soundscapes = soundscapeCards,
            selectionOptions = selectionOptions,
        )
    }

    private fun syncAudioCategories(state: ActiveSceneSoundscapesUiState) {
        val categoryIds = state.soundscapes.map { it.category.id }.toSet()
        (categoryIds - currentCategoryIds).forEach(sceneAudioController::addCategory)
        (currentCategoryIds - categoryIds).forEach(sceneAudioController::removeCategory)
        state.soundscapes.forEach { soundscape ->
            sceneAudioController.setCategoryMixVolume(soundscape.category.id, soundscape.mixVolume)
        }
        sceneAudioController.setMasterVolume(state.masterVolume)
        currentCategoryIds = categoryIds
    }
}

private fun List<SceneSoundscape>.combineTrackFlows(
    soundscapeRepository: SoundscapeRepository,
): Flow<Map<Long, List<SoundscapeTrack>>> {
    if (isEmpty()) {
        return flowOf(emptyMap())
    }
    val soundscapes = this
    return combine(soundscapes.map { soundscape ->
        soundscapeRepository.observeTracks(soundscape.category.id)
    }) { trackLists ->
        soundscapes.mapIndexed { index, soundscape ->
            soundscape.category.id to (trackLists[index] as List<SoundscapeTrack>)
        }.toMap()
    }
}

private fun SoundscapeCategory.hasTracks(): Boolean =
    levelOneTrackCount > 0 || levelTwoTrackCount > 0 || levelThreeTrackCount > 0
