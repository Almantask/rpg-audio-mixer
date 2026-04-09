package com.example.rpgaudiomixer.ui.scenes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.ArcanumTopBarTestTags
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.SoundboardPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.ui.fx.FxAudioFileImporter
import com.example.rpgaudiomixer.ui.fx.FxAudioImportResult
import com.example.rpgaudiomixer.ui.fx.FxAudioPickerMode
import com.example.rpgaudiomixer.ui.fx.FxAudioSelectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

object ActiveSceneSoundboardTestTags {
    const val MASTER_SLIDER = "ActiveScene_Soundboard_MasterSlider"
    const val ADD_BUTTON = "ActiveScene_Soundboard_AddButton"
    const val EMPTY_STATE = "ActiveScene_Soundboard_Empty"
    const val GRID = "ActiveScene_Soundboard_Grid"
    const val DELETE_ZONE = "ActiveScene_Soundboard_DeleteZone"
    const val SELECTION_SHEET = "ActiveScene_Soundboard_SelectionSheet"
    const val SELECTION_BACK = ArcanumTopBarTestTags.BACK_ARROW
    const val IMPORT_NEW_BUTTON = "ActiveScene_Soundboard_ImportNew"

    fun button(name: String): String = "ActiveScene_Soundboard_Button_${name.asTagSuffix()}"
    fun trigger(name: String): String = "ActiveScene_Soundboard_Trigger_${name.asTagSuffix()}"
    fun pause(name: String): String = "ActiveScene_Soundboard_Pause_${name.asTagSuffix()}"
    fun selectionAdd(name: String): String = "ActiveScene_Soundboard_Selection_Add_${name.asTagSuffix()}"
    fun selectionAdded(name: String): String = "ActiveScene_Soundboard_Selection_Added_${name.asTagSuffix()}"
}

data class ActiveSceneFxButtonUiState(
    val fxTrackId: Long,
    val name: String,
    val activeInstances: Int = 0,
) {
    val isPlaying: Boolean = activeInstances > 0
}

data class SelectableFxUiState(
    val fxTrackId: Long,
    val name: String,
    val durationMs: Long,
    val playCount: Int,
    val isAlreadyAdded: Boolean,
)

data class ActiveSceneSoundboardUiState(
    val isLoading: Boolean = true,
    val masterVolumePercent: Int = 100,
    val buttons: List<ActiveSceneFxButtonUiState> = emptyList(),
    val selectionItems: List<SelectableFxUiState> = emptyList(),
    val isSelectionOpen: Boolean = false,
    val isDeleteMode: Boolean = false,
    val errorMessage: String? = null,
)

@Composable
fun ActiveSceneSoundboardRoute(
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri == null) {
            viewModel.closePicker()
        } else {
            viewModel.onAudioPicked(uri)
        }
    }

    ActiveSceneSoundboardScreen(
        modifier = modifier,
        uiState = uiState,
        onMasterVolumeChange = viewModel::setMasterVolume,
        onTriggerFx = viewModel::triggerFx,
        onStopFx = viewModel::stopFx,
        onOpenSelection = viewModel::openSelection,
        onCloseSelection = viewModel::closeSelection,
        onAddFx = viewModel::addFx,
        onMoveFx = viewModel::moveFx,
        onRemoveFx = viewModel::removeFx,
        onImportNew = {
            viewModel.requestImport()
            if (viewModel.useSystemAudioPicker) {
                audioPickerLauncher.launch(arrayOf("audio/*"))
            }
        },
        onDeleteModeChanged = viewModel::setDeleteMode,
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun ActiveSceneSoundboardScreen(
    uiState: ActiveSceneSoundboardUiState,
    modifier: Modifier = Modifier,
    onMasterVolumeChange: (Int) -> Unit,
    onTriggerFx: (Long) -> Unit,
    onStopFx: (Long) -> Unit,
    onOpenSelection: () -> Unit,
    onCloseSelection: () -> Unit,
    onAddFx: (Long) -> Unit,
    onMoveFx: (Long, Int) -> Unit,
    onRemoveFx: (Long) -> Unit,
    onImportNew: () -> Unit,
    onDeleteModeChanged: (Boolean) -> Unit,
    onDismissError: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            uiState.buttons.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .testTag(ActiveSceneSoundboardTestTags.EMPTY_STATE),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No soundboard effects yet")
                    TextButton(
                        modifier = Modifier.testTag(ActiveSceneSoundboardTestTags.ADD_BUTTON),
                        onClick = onOpenSelection,
                    ) {
                        Text("Add New Effect")
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .testTag(ActiveSceneSoundboardTestTags.GRID),
                    columns = GridCells.Fixed(4),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Master Volume", fontWeight = FontWeight.SemiBold)
                            Slider(
                                modifier = Modifier.testTag(ActiveSceneSoundboardTestTags.MASTER_SLIDER),
                                value = uiState.masterVolumePercent.toFloat(),
                                valueRange = 0f..100f,
                                onValueChange = { value -> onMasterVolumeChange(value.roundToInt()) },
                            )
                            Text("${uiState.masterVolumePercent}%")
                        }
                    }
                    itemsIndexed(uiState.buttons, key = { _, item -> item.fxTrackId }) { index, item ->
                        FxButton(
                            item = item,
                            isDeleteMode = uiState.isDeleteMode,
                            onTriggerFx = { onTriggerFx(item.fxTrackId) },
                            onStopFx = { onStopFx(item.fxTrackId) },
                            onMoveFx = { offset -> onMoveFx(item.fxTrackId, offset) },
                            onRemoveFx = { onRemoveFx(item.fxTrackId) },
                            onDeleteModeChanged = onDeleteModeChanged,
                            isFirst = index == 0,
                            isLast = index == uiState.buttons.lastIndex,
                        )
                    }
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(ActiveSceneSoundboardTestTags.ADD_BUTTON),
                            onClick = onOpenSelection,
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add New Effect")
                        }
                    }
                }
            }
        }

        if (uiState.isDeleteMode) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(24.dp)
                    .testTag(ActiveSceneSoundboardTestTags.DELETE_ZONE),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null)
                    Text("Drop here to remove effect")
                }
            }
        }

        if (uiState.isSelectionOpen) {
            FxSelectionSheet(
                items = uiState.selectionItems,
                onClose = onCloseSelection,
                onAddFx = onAddFx,
                onImportNew = onImportNew,
            )
        }

        ErrorDialog(message = uiState.errorMessage, onDismiss = onDismissError)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FxButton(
    item: ActiveSceneFxButtonUiState,
    isDeleteMode: Boolean,
    onTriggerFx: () -> Unit,
    onStopFx: () -> Unit,
    onMoveFx: (Int) -> Unit,
    onRemoveFx: () -> Unit,
    onDeleteModeChanged: (Boolean) -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
) {
    var dragX by remember(item.fxTrackId) { mutableFloatStateOf(0f) }
    var dragY by remember(item.fxTrackId) { mutableFloatStateOf(0f) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onTriggerFx)
            .pointerInput(item.fxTrackId) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onDeleteModeChanged(true) },
                    onDragCancel = {
                        dragX = 0f
                        dragY = 0f
                        onDeleteModeChanged(false)
                    },
                    onDragEnd = {
                        when {
                            dragY > 160f -> onRemoveFx()
                            dragX < -80f && !isFirst -> onMoveFx(-1)
                            dragX > 80f && !isLast -> onMoveFx(1)
                            dragY < -80f -> onMoveFx(-4)
                            dragY > 80f -> onMoveFx(4)
                        }
                        dragX = 0f
                        dragY = 0f
                        onDeleteModeChanged(false)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragX += dragAmount.x
                        dragY += dragAmount.y
                    },
                )
            }
            .testTag(ActiveSceneSoundboardTestTags.button(item.name)),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (item.isPlaying) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) else Modifier)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = item.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                if (item.isPlaying) {
                    IconButton(
                        modifier = Modifier
                            .size(24.dp)
                            .testTag(ActiveSceneSoundboardTestTags.pause(item.name)),
                        onClick = onStopFx,
                    ) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause ${item.name}")
                    }
                } else {
                    Icon(
                        modifier = Modifier
                            .size(20.dp)
                            .testTag(ActiveSceneSoundboardTestTags.trigger(item.name)),
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                    )
                }
            }
            if (item.activeInstances > 1 || isDeleteMode) {
                Text(
                    text = if (isDeleteMode) "Drag to flames" else "${item.activeInstances} playing",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.graphicsLayer { alpha = 0.8f },
                )
            }
        }
    }
}

@Composable
private fun FxSelectionSheet(
    items: List<SelectableFxUiState>,
    onClose: () -> Unit,
    onAddFx: (Long) -> Unit,
    onImportNew: () -> Unit,
) {
    ModalBottomSheet(
        modifier = Modifier.testTag(ActiveSceneSoundboardTestTags.SELECTION_SHEET),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onClose,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(
                    modifier = Modifier.testTag(ActiveSceneSoundboardTestTags.SELECTION_BACK),
                    onClick = onClose,
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Imported FX", fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.size(48.dp))
            }
            if (items.isEmpty()) {
                Text("No FX tracks available yet")
            } else {
                items.forEachIndexed { index, item ->
                    FxSelectionRow(item = item, onAddFx = onAddFx)
                    if (index != items.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Need a custom resonance?", fontWeight = FontWeight.Bold)
                    Text("Summon a new effect from your scrolls.")
                    TextButton(
                        modifier = Modifier.testTag(ActiveSceneSoundboardTestTags.IMPORT_NEW_BUTTON),
                        onClick = onImportNew,
                    ) {
                        Text("Import New")
                    }
                }
            }
        }
    }
}

@Composable
private fun FxSelectionRow(
    item: SelectableFxUiState,
    onAddFx: (Long) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.name, fontWeight = FontWeight.SemiBold)
            Text("${item.durationMs}ms  PLAYED ${item.playCount}×", style = MaterialTheme.typography.bodySmall)
        }
        if (item.isAlreadyAdded) {
            Surface(
                modifier = Modifier.testTag(ActiveSceneSoundboardTestTags.selectionAdded(item.name)),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(999.dp),
            ) {
                Icon(Icons.Default.Check, contentDescription = "Already added", modifier = Modifier.padding(8.dp))
            }
        } else {
            IconButton(
                modifier = Modifier.testTag(ActiveSceneSoundboardTestTags.selectionAdd(item.name)),
                onClick = { onAddFx(item.fxTrackId) },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add ${item.name}")
            }
        }
    }
}

@HiltViewModel
class ActiveSceneSoundboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val fxRepository: FxRepository,
    private val soundboardPlayer: SoundboardPlayer,
    private val audioSelectionRepository: FxAudioSelectionRepository,
    private val audioFileImporter: FxAudioFileImporter,
    audioPickerMode: FxAudioPickerMode,
) : ViewModel() {
    private val sceneId = requireNotNull(savedStateHandle.get<String>("sceneId")) {
        "Navigation argument 'sceneId' is missing."
    }.toLongOrNull() ?: error("Navigation argument 'sceneId' must be a valid numeric value.")

    val useSystemAudioPicker: Boolean = audioPickerMode.useSystemAudioPicker

    private val masterVolumePercent = MutableStateFlow(100)
    private val isSelectionOpen = MutableStateFlow(false)
    private val activeInstances = MutableStateFlow<Map<Long, List<Long>>>(emptyMap())
    private val isDeleteMode = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ActiveSceneSoundboardUiState> = combine(
        sceneRepository.observeScene(sceneId),
        sceneRepository.observeSceneFx(sceneId),
        fxRepository.observeTracks(),
        masterVolumePercent,
        isSelectionOpen,
        activeInstances,
        isDeleteMode,
        errorMessage,
    ) { scene, sceneFx, tracks, master, selectionOpen, active, deleteMode, error ->
        ActiveSceneSoundboardUiState(
            isLoading = scene == null,
            masterVolumePercent = master,
            buttons = sceneFx.map { fx ->
                ActiveSceneFxButtonUiState(
                    fxTrackId = fx.fxTrackId,
                    name = fx.name,
                    activeInstances = active[fx.fxTrackId]?.size ?: 0,
                )
            },
            selectionItems = tracks.map { track ->
                SelectableFxUiState(
                    fxTrackId = track.id,
                    name = track.name,
                    durationMs = track.durationMs,
                    playCount = track.playCount,
                    isAlreadyAdded = sceneFx.any { fx -> fx.fxTrackId == track.id },
                )
            },
            isSelectionOpen = selectionOpen,
            isDeleteMode = deleteMode,
            errorMessage = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ActiveSceneSoundboardUiState(),
    )

    init {
        viewModelScope.launch {
            sceneRepository.observeScene(sceneId).collect { scene ->
                val persistedVolume = scene?.soundboardVolumePercent ?: 100
                if (masterVolumePercent.value != persistedVolume) {
                    masterVolumePercent.value = persistedVolume
                    soundboardPlayer.setMasterVolume(persistedVolume / 100f)
                }
            }
        }
        viewModelScope.launch {
            audioSelectionRepository.selectedAudio.filterNotNull().collect { selection ->
                if (!selection.isValidAudio) {
                    errorMessage.value = "The file could not be read as audio."
                } else {
                    fxRepository.upsertTrack(
                        FxTrack(
                            name = selection.displayName,
                            filePath = selection.filePath,
                            durationMs = selection.durationMs,
                        ),
                    )
                }
                audioSelectionRepository.consumeSelection()
            }
        }
    }

    fun setMasterVolume(volumePercent: Int) {
        masterVolumePercent.value = volumePercent.coerceIn(0, 100)
        soundboardPlayer.setMasterVolume(masterVolumePercent.value / 100f)
        viewModelScope.launch {
            sceneRepository.updateSceneSoundboardVolume(sceneId, masterVolumePercent.value)
        }
    }

    fun triggerFx(fxTrackId: Long) {
        viewModelScope.launch {
            val fx = sceneRepository.observeSceneFx(sceneId).first().firstOrNull { item -> item.fxTrackId == fxTrackId } ?: return@launch
            val instanceId = soundboardPlayer.triggerFx(fx.toFxTrack())
            fxRepository.incrementPlayCount(fxTrackId)
            activeInstances.update { current -> current + (fxTrackId to (current[fxTrackId].orEmpty() + instanceId)) }
        }
    }

    fun stopFx(fxTrackId: Long) {
        val instanceId = activeInstances.value[fxTrackId].orEmpty().lastOrNull() ?: return
        soundboardPlayer.stopFx(instanceId)
        activeInstances.update { current ->
            val remaining = current[fxTrackId].orEmpty().dropLast(1)
            if (remaining.isEmpty()) current - fxTrackId else current + (fxTrackId to remaining)
        }
    }

    fun openSelection() {
        isSelectionOpen.value = true
    }

    fun closeSelection() {
        isSelectionOpen.value = false
    }

    fun addFx(fxTrackId: Long) {
        viewModelScope.launch {
            sceneRepository.addSoundboardEffect(sceneId, fxTrackId)
        }
    }

    fun moveFx(fxTrackId: Long, offset: Int) {
        viewModelScope.launch {
            val current = sceneRepository.observeSceneFx(sceneId).first().toMutableList()
            val index = current.indexOfFirst { fx -> fx.fxTrackId == fxTrackId }
            if (index == -1) return@launch
            val targetIndex = (index + offset).coerceIn(0, current.lastIndex)
            if (index == targetIndex) return@launch
            val item = current.removeAt(index)
            current.add(targetIndex, item)
            sceneRepository.reorderSoundboardEffects(sceneId, current.map(SceneFx::fxTrackId))
        }
    }

    fun removeFx(fxTrackId: Long) {
        viewModelScope.launch {
            val fx = sceneRepository.observeSceneFx(sceneId).first().firstOrNull { item -> item.fxTrackId == fxTrackId } ?: return@launch
            soundboardPlayer.stopTrack(fx.name)
            activeInstances.update { it - fxTrackId }
            sceneRepository.removeSoundboardEffect(sceneId, fxTrackId)
        }
    }

    fun requestImport() {
        audioSelectionRepository.requestPicker()
    }

    fun onAudioPicked(uri: Uri) {
        when (val importedAudio = audioFileImporter.importAudio(uri)) {
            FxAudioImportResult.UnsupportedType -> {
                audioSelectionRepository.closePicker()
                errorMessage.value = "Only audio files can be imported."
            }
            FxAudioImportResult.UnreadableAudio -> {
                audioSelectionRepository.closePicker()
                errorMessage.value = "The file could not be read as audio."
            }
            is FxAudioImportResult.Success -> {
                audioSelectionRepository.submitSelection(
                    displayName = importedAudio.audio.displayName,
                    filePath = importedAudio.audio.filePath,
                    durationMs = importedAudio.audio.durationMs,
                )
            }
        }
    }

    fun closePicker() {
        audioSelectionRepository.closePicker()
    }

    fun setDeleteMode(enabled: Boolean) {
        isDeleteMode.value = enabled
    }

    fun clearError() {
        errorMessage.value = null
    }
}

private fun SceneFx.toFxTrack(): FxTrack = FxTrack(
    id = fxTrackId,
    name = name,
    filePath = filePath,
    tags = tags,
    durationMs = durationMs,
    playCount = playCount,
)

private fun String.asTagSuffix(): String = lowercase(Locale.US)
