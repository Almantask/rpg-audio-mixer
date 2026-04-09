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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.AudioFilePickerButton
import com.example.rpgaudiomixer.app.components.MasterSlider
import com.example.rpgaudiomixer.app.components.glowBorder
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ActiveSceneFxButtonUiState(
    val track: FxTrack,
    val displayOrder: Int,
    val playingInstanceCount: Int = 0,
    val isPlaying: Boolean = false,
)

data class ActiveSceneFxSelectionOptionUiState(
    val track: FxTrack,
    val isAdded: Boolean,
)

data class ActiveSceneSoundboardUiState(
    val isLoading: Boolean = true,
    val sceneName: String = "Scene",
    val masterVolume: Float = 1f,
    val fxButtons: List<ActiveSceneFxButtonUiState> = emptyList(),
    val isSelectionSheetVisible: Boolean = false,
    val selectionOptions: List<ActiveSceneFxSelectionOptionUiState> = emptyList(),
    val errorMessage: String? = null,
)

@Composable
fun ActiveSceneSoundboardTabRoute(
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundboardViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    ActiveSceneSoundboardTab(
        uiState = uiState.value,
        onSetMasterVolume = viewModel::setMasterVolume,
        onTriggerFx = viewModel::triggerFx,
        onStopFx = viewModel::stopFx,
        onShowAddFxSheet = viewModel::showAddFxSheet,
        onHideAddFxSheet = viewModel::hideAddFxSheet,
        onAddFx = viewModel::addFx,
        onImportNewFx = viewModel::importNewFx,
        onRemoveFx = viewModel::removeFx,
        modifier = modifier,
    )
}

@Composable
fun ActiveSceneSoundboardTab(
    uiState: ActiveSceneSoundboardUiState,
    onSetMasterVolume: (Float) -> Unit,
    onTriggerFx: (Long) -> Unit,
    onStopFx: (Long) -> Unit,
    onShowAddFxSheet: () -> Unit,
    onHideAddFxSheet: () -> Unit,
    onAddFx: (Long) -> Unit,
    onImportNewFx: (String) -> Unit,
    onRemoveFx: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MasterSlider(
            label = "Master FX",
            value = uiState.masterVolume,
            onValueChange = onSetMasterVolume,
        )
        if (uiState.fxButtons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No effects in this scene yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                gridItems(uiState.fxButtons, key = { it.track.id }) { fxButton ->
                    FxButton(
                        fxButton = fxButton,
                        onPlayPause = {
                            if (fxButton.isPlaying) {
                                onStopFx(fxButton.track.id)
                            } else {
                                onTriggerFx(fxButton.track.id)
                            }
                        },
                        onRemove = { onRemoveFx(fxButton.track.id) },
                    )
                }
            }
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onShowAddFxSheet,
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = "Add New Effect",
            )
        }
    }

    if (uiState.isSelectionSheetVisible) {
        FxSelectionDialog(
            options = uiState.selectionOptions,
            onDismiss = onHideAddFxSheet,
            onAddFx = onAddFx,
            onImportNewFx = onImportNewFx,
        )
    }
}

@Composable
private fun FxButton(
    fxButton: ActiveSceneFxButtonUiState,
    onPlayPause: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .glowBorder(
                isPlaying = fxButton.isPlaying,
                color = MaterialTheme.colorScheme.primary,
            )
            .testTag("ActiveSceneFxButton_${fxButton.track.id}"),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (fxButton.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (fxButton.isPlaying) "Pause ${fxButton.track.name}" else "Play ${fxButton.track.name}",
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove ${fxButton.track.name}",
                    )
                }
            }
            Text(
                text = fxButton.track.name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = if (fxButton.playingInstanceCount > 0) {
                    "${fxButton.playingInstanceCount} playing"
                } else {
                    "Idle"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FxSelectionDialog(
    options: List<ActiveSceneFxSelectionOptionUiState>,
    onDismiss: () -> Unit,
    onAddFx: (Long) -> Unit,
    onImportNewFx: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "FX Selection") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(options, key = { it.track.id }) { option ->
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
                            Text(text = option.track.name)
                            if (option.isAdded) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Already added",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            } else {
                                IconButton(onClick = { onAddFx(option.track.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add ${option.track.name}",
                                    )
                                }
                            }
                        }
                    }
                }
                AudioFilePickerButton(
                    text = "Import New",
                    onAudioPicked = onImportNewFx,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val fxRepository: FxRepository,
    private val soundboardPlayer: SoundboardPlayer,
) : ViewModel() {
    private val sceneId: Long = requireNotNull(savedStateHandle[AppRoute.SCENE_ID_ARG])
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    private var pendingAddedFxIds: Set<Long> = emptySet()
    private val activeInstanceIdsByTrack = mutableMapOf<Long, MutableList<Long>>()
    private val trackIdByInstanceId = mutableMapOf<Long, Long>()
    private val completionJobsByInstanceId = mutableMapOf<Long, Job>()

    internal constructor(
        sceneId: Long,
        sceneRepository: SceneRepository,
        fxRepository: FxRepository,
        soundboardPlayer: SoundboardPlayer,
        mainDispatcher: CoroutineDispatcher,
    ) : this(
        savedStateHandle = SavedStateHandle(mapOf(AppRoute.SCENE_ID_ARG to sceneId)),
        sceneRepository = sceneRepository,
        fxRepository = fxRepository,
        soundboardPlayer = soundboardPlayer,
    ) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(ActiveSceneSoundboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            combine(
                sceneRepository.observeScene(sceneId),
                sceneRepository.observeFxForScene(sceneId),
                fxRepository.observeFxTracks(),
            ) { scene, sceneFx, allFx ->
                buildUiState(scene = scene, sceneFx = sceneFx, allFx = allFx)
            }
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load soundboard.",
                    )
                }
                .collect { state ->
                    _uiState.value = state
                    soundboardPlayer.setMasterVolume(state.masterVolume)
                }
        }
    }

    fun setMasterVolume(volume: Float) {
        val normalized = volume.coerceIn(0f, 1f)
        _uiState.value = _uiState.value.copy(masterVolume = normalized)
        soundboardPlayer.setMasterVolume(normalized)
    }

    fun triggerFx(trackId: Long) {
        val track = _uiState.value.fxButtons.firstOrNull { it.track.id == trackId }?.track ?: return
        viewModelScope.launch(mainDispatcher) {
            runCatching { soundboardPlayer.triggerFx(track) }
                .onSuccess { result ->
                    registerStartedInstance(track = track, instanceId = result.startedInstanceId)
                    result.evictedInstanceId?.let(::clearInstance)
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = throwable.message ?: "Unable to play ${track.name}.",
                    )
                }
        }
    }

    fun stopFx(trackId: Long) {
        val instanceId = activeInstanceIdsByTrack[trackId]?.lastOrNull() ?: return
        soundboardPlayer.stopFx(instanceId)
        clearInstance(instanceId)
    }

    fun showAddFxSheet() {
        _uiState.value = _uiState.value.copy(isSelectionSheetVisible = true)
    }

    fun hideAddFxSheet() {
        _uiState.value = _uiState.value.copy(isSelectionSheetVisible = false)
    }

    fun addFx(trackId: Long) {
        pendingAddedFxIds += trackId
        _uiState.value = _uiState.value.copy(
            selectionOptions = _uiState.value.selectionOptions.map { option ->
                if (option.track.id == trackId) option.copy(isAdded = true) else option
            },
        )
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.addFxToScene(sceneId = sceneId, fxTrackId = trackId)
        }
    }

    fun importNewFx(sourceUri: String) {
        viewModelScope.launch(mainDispatcher) {
            runCatching { fxRepository.importFxTrack(sourceUri) }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = throwable.message ?: "Unable to import effect.",
                    )
                }
        }
    }

    fun reorderFx(orderedFxTrackIds: List<Long>) {
        val reorderedButtons = orderedFxTrackIds.mapIndexedNotNull { index, trackId ->
            _uiState.value.fxButtons.firstOrNull { it.track.id == trackId }?.copy(displayOrder = index)
        }
        _uiState.value = _uiState.value.copy(fxButtons = reorderedButtons)
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.reorderFx(sceneId = sceneId, orderedFxTrackIds = orderedFxTrackIds)
        }
    }

    fun removeFx(trackId: Long) {
        activeInstanceIdsByTrack[trackId].orEmpty().toList().forEach { instanceId ->
            soundboardPlayer.stopFx(instanceId)
            clearInstance(instanceId)
        }
        pendingAddedFxIds -= trackId
        _uiState.value = _uiState.value.copy(
            fxButtons = _uiState.value.fxButtons.filterNot { it.track.id == trackId },
            selectionOptions = _uiState.value.selectionOptions.map { option ->
                if (option.track.id == trackId) option.copy(isAdded = false) else option
            },
        )
        viewModelScope.launch(mainDispatcher) {
            sceneRepository.removeFxFromScene(sceneId = sceneId, fxTrackId = trackId)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        completionJobsByInstanceId.values.forEach(Job::cancel)
        soundboardPlayer.releaseAll()
    }

    private fun registerStartedInstance(track: FxTrack, instanceId: Long) {
        activeInstanceIdsByTrack.getOrPut(track.id) { mutableListOf() }.add(instanceId)
        trackIdByInstanceId[instanceId] = track.id
        updatePlayingState(track.id)
        completionJobsByInstanceId[instanceId]?.cancel()
        completionJobsByInstanceId[instanceId] = viewModelScope.launch(mainDispatcher) {
            delay(track.durationMs)
            clearInstance(instanceId)
        }
    }

    private fun clearInstance(instanceId: Long) {
        completionJobsByInstanceId.remove(instanceId)?.cancel()
        val trackId = trackIdByInstanceId.remove(instanceId) ?: return
        val remaining = activeInstanceIdsByTrack[trackId].orEmpty().filterNot { it == instanceId }
        if (remaining.isEmpty()) {
            activeInstanceIdsByTrack.remove(trackId)
        } else {
            activeInstanceIdsByTrack[trackId] = remaining.toMutableList()
        }
        updatePlayingState(trackId)
    }

    private fun updatePlayingState(trackId: Long) {
        val activeCount = activeInstanceIdsByTrack[trackId]?.size ?: 0
        _uiState.value = _uiState.value.copy(
            fxButtons = _uiState.value.fxButtons.map { button ->
                if (button.track.id == trackId) {
                    button.copy(
                        playingInstanceCount = activeCount,
                        isPlaying = activeCount > 0,
                    )
                } else {
                    button
                }
            },
        )
    }

    private fun buildUiState(
        scene: com.example.rpgaudiomixer.domain.model.Scene?,
        sceneFx: List<SceneFx>,
        allFx: List<FxTrack>,
    ): ActiveSceneSoundboardUiState {
        val currentState = _uiState.value
        val buttons = sceneFx
            .sortedBy(SceneFx::displayOrder)
            .map { sceneFxItem ->
                val activeCount = activeInstanceIdsByTrack[sceneFxItem.track.id]?.size ?: 0
                ActiveSceneFxButtonUiState(
                    track = sceneFxItem.track,
                    displayOrder = sceneFxItem.displayOrder,
                    playingInstanceCount = activeCount,
                    isPlaying = activeCount > 0,
                )
            }
        val addedIds = buttons.map { it.track.id }.toSet() + pendingAddedFxIds
        val selectionOptions = allFx
            .sortedBy(FxTrack::name)
            .map { track ->
                ActiveSceneFxSelectionOptionUiState(
                    track = track,
                    isAdded = addedIds.contains(track.id),
                )
            }
        return currentState.copy(
            isLoading = false,
            sceneName = scene?.name ?: "Scene",
            fxButtons = buttons,
            selectionOptions = selectionOptions,
        )
    }
}
