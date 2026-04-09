package com.example.rpgaudiomixer.ui.scenes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.playback.ScenePlaybackController
import com.example.rpgaudiomixer.app.components.ArcanumTopBarTestTags
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.media.SceneAudioEngine
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeAudioFileImporter
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeAudioPickerMode
import com.example.rpgaudiomixer.ui.soundscapes.SoundscapeAudioSelectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import java.util.Locale

object ActiveSceneSoundscapesTestTags {
    const val MASTER_SLIDER = "ActiveScene_Soundscapes_MasterSlider"
    const val ADD_BUTTON = "ActiveScene_Soundscapes_AddButton"
    const val EMPTY_STATE = "ActiveScene_Soundscapes_Empty"
    const val SELECTION_SHEET = "ActiveScene_Soundscapes_SelectionSheet"
    const val IMPORT_NEW_BUTTON = "ActiveScene_Soundscapes_ImportNew"
    const val SELECTION_BACK = ArcanumTopBarTestTags.BACK_ARROW

    fun card(name: String): String = "ActiveScene_Soundscape_${name.asTagSuffix()}"
    fun playButton(name: String): String = "ActiveScene_Soundscape_Play_${name.asTagSuffix()}"
    fun randomButton(name: String): String = "ActiveScene_Soundscape_Random_${name.asTagSuffix()}"
    fun mixSlider(name: String): String = "ActiveScene_Soundscape_Mix_${name.asTagSuffix()}"
    fun removeButton(name: String): String = "ActiveScene_Soundscape_Remove_${name.asTagSuffix()}"
    fun intensity(name: String, intensityLevel: IntensityLevel): String =
        "ActiveScene_Soundscape_Intensity_${name.asTagSuffix()}_${intensityLevel.label}"
    fun selectionAdd(name: String): String = "ActiveScene_Soundscape_Selection_Add_${name.asTagSuffix()}"
    fun selectionAdded(name: String): String = "ActiveScene_Soundscape_Selection_Added_${name.asTagSuffix()}"
}

data class ActiveSceneSoundscapeCardUiState(
    val categoryId: Long,
    val categoryName: String,
    val currentTrackName: String? = null,
    val mixVolumePercent: Int = 100,
    val intensityLevel: IntensityLevel = IntensityLevel.I,
    val availableIntensities: Set<IntensityLevel> = emptySet(),
    val isPlaying: Boolean = false,
) {
    val canPlay: Boolean = availableIntensities.isNotEmpty()
}

data class SelectableSoundscapeUiState(
    val categoryId: Long,
    val categoryName: String,
    val trackCount: Int,
    val isAlreadyAdded: Boolean,
)

data class ActiveSceneSoundscapesUiState(
    val isLoading: Boolean = true,
    val masterVolumePercent: Int = 100,
    val cards: List<ActiveSceneSoundscapeCardUiState> = emptyList(),
    val selectionItems: List<SelectableSoundscapeUiState> = emptyList(),
    val isSelectionOpen: Boolean = false,
    val navigateToComposerCategoryId: Long? = null,
    val errorMessage: String? = null,
)

private data class PlaybackSnapshot(
    val track: SoundscapeTrack? = null,
    val isPlaying: Boolean = false,
)

@Composable
fun ActiveSceneSoundscapesRoute(
    modifier: Modifier = Modifier,
    onOpenSoundscapeComposer: (Long) -> Unit,
    viewModel: ActiveSceneSoundscapesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri == null) {
            viewModel.closeImportPicker()
        } else {
            viewModel.onImportAudioPicked(uri)
        }
    }

    LaunchedEffect(uiState.navigateToComposerCategoryId) {
        uiState.navigateToComposerCategoryId?.let { categoryId ->
            onOpenSoundscapeComposer(categoryId)
            viewModel.onComposerNavigationHandled()
        }
    }

    ActiveSceneSoundscapesScreen(
        modifier = modifier,
        uiState = uiState,
        onMasterVolumeChange = viewModel::setMasterVolume,
        onPlayToggle = { card ->
            if (card.isPlaying) {
                viewModel.pauseCategory(card.categoryId)
            } else {
                viewModel.playCategory(card.categoryId)
            }
        },
        onRollRandom = viewModel::rollRandom,
        onMixChange = viewModel::setMix,
        onIntensitySelected = viewModel::setIntensity,
        onMoveCategory = viewModel::moveCategory,
        onRemoveCategory = viewModel::removeCategory,
        onOpenSelection = viewModel::openSelection,
        onCloseSelection = viewModel::closeSelection,
        onAddCategory = viewModel::addCategory,
        onImportNew = {
            viewModel.requestImport()
            if (viewModel.useSystemAudioPicker) {
                audioPickerLauncher.launch(arrayOf("audio/*"))
            }
        },
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun ActiveSceneSoundscapesScreen(
    uiState: ActiveSceneSoundscapesUiState,
    modifier: Modifier = Modifier,
    onMasterVolumeChange: (Int) -> Unit,
    onPlayToggle: (ActiveSceneSoundscapeCardUiState) -> Unit,
    onRollRandom: (Long) -> Unit,
    onMixChange: (Long, Int) -> Unit,
    onIntensitySelected: (Long, IntensityLevel) -> Unit,
    onMoveCategory: (Long, Int) -> Unit,
    onRemoveCategory: (Long) -> Unit,
    onOpenSelection: () -> Unit,
    onCloseSelection: () -> Unit,
    onAddCategory: (Long) -> Unit,
    onImportNew: () -> Unit,
    onDismissError: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.cards.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .testTag(ActiveSceneSoundscapesTestTags.EMPTY_STATE),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("No soundscapes yet")
                    TextButton(
                        modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.ADD_BUTTON),
                        onClick = onOpenSelection,
                    ) {
                        Text("Add New Soundscape")
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Master Atmosphere", fontWeight = FontWeight.SemiBold)
                            Slider(
                                modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.MASTER_SLIDER),
                                value = uiState.masterVolumePercent.toFloat(),
                                valueRange = 0f..100f,
                                onValueChange = { value -> onMasterVolumeChange(value.roundToInt()) },
                            )
                            Text("${uiState.masterVolumePercent}%")
                        }
                    }
                    itemsIndexed(uiState.cards, key = { _, card -> card.categoryId }) { index, card ->
                        SoundscapeCategoryCard(
                            card = card,
                            onPlayToggle = { onPlayToggle(card) },
                            onRollRandom = { onRollRandom(card.categoryId) },
                            onMixChange = { value -> onMixChange(card.categoryId, value) },
                            onIntensitySelected = { intensity -> onIntensitySelected(card.categoryId, intensity) },
                            onMove = { offset -> onMoveCategory(card.categoryId, offset) },
                            onRemove = { onRemoveCategory(card.categoryId) },
                            isFirst = index == 0,
                            isLast = index == uiState.cards.lastIndex,
                        )
                    }
                    item {
                        TextButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(ActiveSceneSoundscapesTestTags.ADD_BUTTON),
                            onClick = onOpenSelection,
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add New Soundscape")
                        }
                    }
                }
            }
        }

        if (uiState.isSelectionOpen) {
            SoundscapeSelectionSheet(
                items = uiState.selectionItems,
                onClose = onCloseSelection,
                onAddCategory = onAddCategory,
                onImportNew = onImportNew,
            )
        }

        ErrorDialog(message = uiState.errorMessage, onDismiss = onDismissError)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SoundscapeCategoryCard(
    card: ActiveSceneSoundscapeCardUiState,
    onPlayToggle: () -> Unit,
    onRollRandom: () -> Unit,
    onMixChange: (Int) -> Unit,
    onIntensitySelected: (IntensityLevel) -> Unit,
    onMove: (Int) -> Unit,
    onRemove: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
) {
    var dragDelta by remember(card.categoryId) { mutableFloatStateOf(0f) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(card.categoryId) {
                detectDragGesturesAfterLongPress(
                    onDragEnd = {
                        when {
                            dragDelta < -80f && !isFirst -> onMove(-1)
                            dragDelta > 80f && !isLast -> onMove(1)
                        }
                        dragDelta = 0f
                    },
                    onDragCancel = { dragDelta = 0f },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragDelta += dragAmount.y
                    },
                )
            }
            .testTag(ActiveSceneSoundscapesTestTags.card(card.categoryName)),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (card.isPlaying) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)) else Modifier,
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(card.categoryName, fontWeight = FontWeight.Bold)
                    Text(card.currentTrackName ?: "No track loaded", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(
                    modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.randomButton(card.categoryName)),
                    onClick = onRollRandom,
                    enabled = card.canPlay,
                ) {
                    Icon(Icons.Default.Casino, contentDescription = "Roll random track")
                }
                IconButton(
                    modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.playButton(card.categoryName)),
                    onClick = onPlayToggle,
                    enabled = card.canPlay || card.isPlaying,
                ) {
                    Icon(
                        imageVector = if (card.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (card.isPlaying) "Pause" else "Play",
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("MIX ${card.mixVolumePercent}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.mixSlider(card.categoryName)),
                    value = card.mixVolumePercent.toFloat(),
                    valueRange = 0f..100f,
                    onValueChange = { value -> onMixChange(value.roundToInt()) },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                IntensityLevel.entries.forEach { intensity ->
                    val isAvailable = intensity in card.availableIntensities
                    FilterChip(
                        modifier = Modifier
                            .testTag(ActiveSceneSoundscapesTestTags.intensity(card.categoryName, intensity))
                            .graphicsLayer { alpha = if (isAvailable) 1f else 0.35f },
                        selected = card.intensityLevel == intensity,
                        onClick = { onIntensitySelected(intensity) },
                        label = { Text(intensity.label) },
                        enabled = true,
                    )
                }
            }
            TextButton(
                modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.removeButton(card.categoryName)),
                onClick = onRemove,
            ) {
                Text("Remove soundscape")
            }
        }
    }
}

@Composable
private fun SoundscapeSelectionSheet(
    items: List<SelectableSoundscapeUiState>,
    onClose: () -> Unit,
    onAddCategory: (Long) -> Unit,
    onImportNew: () -> Unit,
) {
    ModalBottomSheet(
        modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.SELECTION_SHEET),
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
                    modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.SELECTION_BACK),
                    onClick = onClose,
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Imported Soundscapes", fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.padding(24.dp))
            }
            if (items.isEmpty()) {
                Text("No soundscape categories available yet")
            } else {
                items.forEachIndexed { index, item ->
                    SoundscapeSelectionRow(item = item, onAddCategory = onAddCategory)
                    if (index != items.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Need a custom resonance?", fontWeight = FontWeight.Bold)
                    Text("Summon a new soundscape category from your scrolls.")
                    TextButton(
                        modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.IMPORT_NEW_BUTTON),
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
private fun SoundscapeSelectionRow(
    item: SelectableSoundscapeUiState,
    onAddCategory: (Long) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(item.categoryName, fontWeight = FontWeight.SemiBold)
            Text("${item.trackCount} TRACKS", style = MaterialTheme.typography.bodySmall)
        }
        if (item.isAlreadyAdded) {
            Surface(
                modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.selectionAdded(item.categoryName)),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(999.dp),
            ) {
                Icon(Icons.Default.Check, contentDescription = "Already added", modifier = Modifier.padding(8.dp))
            }
        } else {
            IconButton(
                modifier = Modifier.testTag(ActiveSceneSoundscapesTestTags.selectionAdd(item.categoryName)),
                onClick = { onAddCategory(item.categoryId) },
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add ${item.categoryName}")
            }
        }
    }
}

@HiltViewModel
class ActiveSceneSoundscapesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sceneRepository: SceneRepository,
    private val soundscapeRepository: SoundscapeRepository,
    private val sceneAudioEngine: SceneAudioEngine,
    private val scenePlaybackController: ScenePlaybackController,
    private val audioSelectionRepository: SoundscapeAudioSelectionRepository,
    private val audioFileImporter: SoundscapeAudioFileImporter,
    audioPickerMode: SoundscapeAudioPickerMode,
) : ViewModel() {
    private val sceneId = requireNotNull(savedStateHandle.get<String>("sceneId")) {
        "Navigation argument 'sceneId' is missing."
    }.toLongOrNull() ?: error("Navigation argument 'sceneId' must be a valid numeric value.")

    val useSystemAudioPicker: Boolean = audioPickerMode.useSystemAudioPicker

    private val masterVolumePercent = MutableStateFlow(100)
    private val isSelectionOpen = MutableStateFlow(false)
    private val playback = MutableStateFlow<Map<Long, PlaybackSnapshot>>(emptyMap())
    private val errorMessage = MutableStateFlow<String?>(null)
    private val navigateToComposerCategoryId = MutableStateFlow<Long?>(null)

    private val categoryDetails: Flow<Map<Long, SoundscapeCategory>> = sceneRepository.observeSceneSoundscapes(sceneId)
        .flatMapLatest { soundscapes ->
            if (soundscapes.isEmpty()) {
                flowOf(emptyMap())
            } else {
                combine(soundscapes.map { soundscape -> soundscapeRepository.observeCategory(soundscape.categoryId) }) { categories ->
                    categories.filterNotNull().associateBy(SoundscapeCategory::id)
                }
            }
        }

    val uiState: StateFlow<ActiveSceneSoundscapesUiState> = combine(
        sceneRepository.observeScene(sceneId),
        sceneRepository.observeSceneSoundscapes(sceneId),
        soundscapeRepository.observeCategories(),
        categoryDetails,
        masterVolumePercent,
        isSelectionOpen,
        playback,
        navigateToComposerCategoryId,
        errorMessage,
    ) { scene, soundscapes, categories, categoryDetails, master, selectionOpen, playbackSnapshots, composerId, error ->
        val cards = soundscapes.map { soundscape ->
            val category = categoryDetails[soundscape.categoryId]
            val snapshot = playbackSnapshots[soundscape.categoryId]
            ActiveSceneSoundscapeCardUiState(
                categoryId = soundscape.categoryId,
                categoryName = soundscape.categoryName,
                currentTrackName = snapshot?.track?.name,
                mixVolumePercent = soundscape.mixVolumePercent,
                intensityLevel = soundscape.intensityLevel,
                availableIntensities = category?.tracks?.map(SoundscapeTrack::intensityLevel)?.toSet().orEmpty(),
                isPlaying = snapshot?.isPlaying == true,
            )
        }
        ActiveSceneSoundscapesUiState(
            isLoading = scene == null,
            masterVolumePercent = master,
            cards = cards,
            selectionItems = categories
                .filter { category -> category.countFor(IntensityLevel.I) + category.countFor(IntensityLevel.II) + category.countFor(IntensityLevel.III) > 0 }
                .map { category ->
                    SelectableSoundscapeUiState(
                        categoryId = category.id,
                        categoryName = category.name,
                        trackCount = category.countFor(IntensityLevel.I) + category.countFor(IntensityLevel.II) + category.countFor(IntensityLevel.III),
                        isAlreadyAdded = soundscapes.any { soundscape -> soundscape.categoryId == category.id },
                    )
                },
            isSelectionOpen = selectionOpen,
            navigateToComposerCategoryId = composerId,
            errorMessage = error,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ActiveSceneSoundscapesUiState(),
    )

    init {
        viewModelScope.launch {
            sceneRepository.observeScene(sceneId).collect { scene ->
                val persistedVolume = scene?.atmosphereVolumePercent ?: 100
                if (masterVolumePercent.value != persistedVolume) {
                    masterVolumePercent.value = persistedVolume
                    if (scenePlaybackController.state.value.currentSceneId == sceneId) {
                        sceneAudioEngine.setMasterVolume(persistedVolume / 100f)
                    }
                }
            }
        }
    }

    fun setMasterVolume(volumePercent: Int) {
        masterVolumePercent.value = volumePercent.coerceIn(0, 100)
        if (scenePlaybackController.state.value.currentSceneId == sceneId) {
            sceneAudioEngine.setMasterVolume(masterVolumePercent.value / 100f)
            scenePlaybackController.syncAtmosphereVolume(sceneId, masterVolumePercent.value)
        }
        viewModelScope.launch {
            sceneRepository.updateSceneAtmosphereVolume(sceneId, masterVolumePercent.value)
        }
    }

    fun playCategory(categoryId: Long) {
        viewModelScope.launch {
            val soundscape = sceneRepository.observeSceneSoundscapes(sceneId).first().firstOrNull { it.categoryId == categoryId } ?: return@launch
            val category = soundscapeRepository.observeCategory(categoryId).first() ?: return@launch
            val snapshot = playback.value[categoryId]
            val pool = category.tracks.filter { track -> track.intensityLevel == soundscape.intensityLevel }
            if (pool.isEmpty()) {
                errorMessage.value = "No tracks are available at ${soundscape.intensityLevel.label} intensity for ${soundscape.categoryName}."
                return@launch
            }
            sceneRepository.updateSoundscapeMix(sceneId, categoryId, soundscape.mixVolumePercent)
            sceneAudioEngine.addCategory(categoryId, soundscape.mixVolumePercent / 100f)
            if (snapshot?.track != null && snapshot.isPlaying.not() && snapshot.track.intensityLevel == soundscape.intensityLevel) {
                sceneAudioEngine.resumeCategory(categoryId)
                playback.update { it + (categoryId to snapshot.copy(isPlaying = true)) }
            } else {
                val selectedTrack = sceneAudioEngine.rollRandomTrack(categoryId, pool) ?: return@launch
                soundscapeRepository.incrementPlayCount(selectedTrack.id)
                playback.update { it + (categoryId to PlaybackSnapshot(track = selectedTrack, isPlaying = true)) }
            }
        }
    }

    fun pauseCategory(categoryId: Long) {
        sceneAudioEngine.pauseCategory(categoryId)
        playback.update { current ->
            current[categoryId]?.let { snapshot -> current + (categoryId to snapshot.copy(isPlaying = false)) } ?: current
        }
    }

    fun rollRandom(categoryId: Long) {
        viewModelScope.launch {
            val soundscape = sceneRepository.observeSceneSoundscapes(sceneId).first().firstOrNull { it.categoryId == categoryId } ?: return@launch
            val category = soundscapeRepository.observeCategory(categoryId).first() ?: return@launch
            val pool = category.tracks.filter { track -> track.intensityLevel == soundscape.intensityLevel }
            if (pool.isEmpty()) {
                errorMessage.value = "No tracks are available at ${soundscape.intensityLevel.label} intensity for ${soundscape.categoryName}."
                return@launch
            }
            sceneAudioEngine.addCategory(categoryId, soundscape.mixVolumePercent / 100f)
            val selectedTrack = sceneAudioEngine.rollRandomTrack(categoryId, pool) ?: return@launch
            soundscapeRepository.incrementPlayCount(selectedTrack.id)
            playback.update { it + (categoryId to PlaybackSnapshot(track = selectedTrack, isPlaying = true)) }
        }
    }

    fun setMix(categoryId: Long, mixVolumePercent: Int) {
        viewModelScope.launch {
            sceneRepository.updateSoundscapeMix(sceneId, categoryId, mixVolumePercent)
            sceneAudioEngine.addCategory(categoryId, mixVolumePercent.coerceIn(0, 100) / 100f)
        }
    }

    fun setIntensity(categoryId: Long, intensityLevel: IntensityLevel) {
        viewModelScope.launch {
            val category = soundscapeRepository.observeCategory(categoryId).first() ?: return@launch
            val pool = category.tracks.filter { track -> track.intensityLevel == intensityLevel }
            if (pool.isEmpty()) {
                errorMessage.value = "No tracks are available at ${intensityLevel.label} intensity for ${category.name}."
                return@launch
            }
            sceneRepository.updateSoundscapeIntensity(sceneId, categoryId, intensityLevel)
            val snapshot = playback.value[categoryId]
            if (snapshot?.isPlaying == true) {
                val selectedTrack = sceneAudioEngine.rollRandomTrack(categoryId, pool) ?: return@launch
                soundscapeRepository.incrementPlayCount(selectedTrack.id)
                playback.update { it + (categoryId to PlaybackSnapshot(track = selectedTrack, isPlaying = true)) }
            }
        }
    }

    fun moveCategory(categoryId: Long, offset: Int) {
        viewModelScope.launch {
            val current = sceneRepository.observeSceneSoundscapes(sceneId).first().toMutableList()
            val index = current.indexOfFirst { soundscape -> soundscape.categoryId == categoryId }
            if (index == -1) return@launch
            val targetIndex = (index + offset).coerceIn(0, current.lastIndex)
            if (index == targetIndex) return@launch
            val item = current.removeAt(index)
            current.add(targetIndex, item)
            sceneRepository.reorderSoundscapes(sceneId, current.map(SceneSoundscape::categoryId))
        }
    }

    fun removeCategory(categoryId: Long) {
        viewModelScope.launch {
            val soundscape = sceneRepository.observeSceneSoundscapes(sceneId).first().firstOrNull { it.categoryId == categoryId } ?: return@launch
            sceneRepository.removeSoundscapeCategory(sceneId, soundscape.categoryName)
            sceneAudioEngine.removeCategory(categoryId)
            playback.update { it - categoryId }
        }
    }

    fun openSelection() {
        isSelectionOpen.value = true
    }

    fun closeSelection() {
        isSelectionOpen.value = false
    }

    fun addCategory(categoryId: Long) {
        viewModelScope.launch {
            val category = soundscapeRepository.observeCategory(categoryId).first() ?: return@launch
            sceneRepository.addSoundscapeCategory(sceneId, category.name)
        }
    }

    fun requestImport() {
        audioSelectionRepository.requestPicker(IMPORT_CATEGORY_ID)
    }

    fun onImportAudioPicked(uri: Uri) {
        viewModelScope.launch {
            val imported = audioFileImporter.importAudio(uri)
            if (imported == null) {
                errorMessage.value = "Unable to import the selected soundscape audio file."
                return@launch
            }
            val (displayName, filePath) = imported
            val categoryId = soundscapeRepository.createCategory(displayName)
            soundscapeRepository.upsertTrack(
                SoundscapeTrack(
                    categoryId = categoryId,
                    name = displayName,
                    filePath = filePath,
                    intensityLevel = IntensityLevel.I,
                    mixVolumePercent = 100,
                ),
            )
            sceneRepository.addSoundscapeCategory(sceneId, displayName)
            navigateToComposerCategoryId.value = categoryId
            audioSelectionRepository.closePicker()
        }
    }

    fun closeImportPicker() {
        audioSelectionRepository.closePicker()
    }

    fun onComposerNavigationHandled() {
        navigateToComposerCategoryId.value = null
    }

    fun clearError() {
        errorMessage.value = null
    }
    companion object {
        private const val IMPORT_CATEGORY_ID = -1L
    }
}

private fun String.asTagSuffix(): String = lowercase(Locale.US)
