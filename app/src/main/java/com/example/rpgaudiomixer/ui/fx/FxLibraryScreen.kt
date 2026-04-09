package com.example.rpgaudiomixer.ui.fx

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.motion.MotionSystemStateRepository
import com.example.rpgaudiomixer.app.motion.MotionTransitionType
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.scene.SceneRepository
import com.example.rpgaudiomixer.domain.trash.FxTrackTrashRepository
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import java.util.Locale

object FxLibraryTestTags {
    const val EMPTY_ILLUSTRATION = "FxLibrary_Empty_Illustration"
    const val IMPORT_BUTTON = "FxLibrary_Import_Button"
    const val DEMO_BUTTON = "FxLibrary_Demo_Button"
    const val DEMO_LOADING = "FxLibrary_Demo_Loading"
    const val SEARCH_FIELD = "FxLibrary_Search_Field"
    const val EDIT_DIALOG = "FxLibrary_Edit_Dialog"
    const val NAME_INPUT = "FxLibrary_Name_Input"
    const val TAGS_SECTION = "FxLibrary_Tags_Section"
    const val MINI_PLAYER = "FxLibrary_Mini_Player"
    const val MINI_PLAYER_PAUSE = "FxLibrary_Mini_Player_Pause"
    const val MINI_PLAYER_PLAY = "FxLibrary_Mini_Player_Play"
    const val MINI_PLAYER_PREVIOUS = "FxLibrary_Mini_Player_Previous"
    const val MINI_PLAYER_NEXT = "FxLibrary_Mini_Player_Next"

    fun row(name: String): String = "FxLibrary_Row_${name.asTagSuffix()}"
    fun editButton(name: String): String = "FxLibrary_Edit_${name.asTagSuffix()}"
    fun playButton(name: String): String = "FxLibrary_Play_${name.asTagSuffix()}"
    fun tagChip(name: String, tag: String): String = "FxLibrary_Tag_${name.asTagSuffix()}_${tag.asTagSuffix()}"
}

private val PREDEFINED_FX_TAGS = listOf("Combat", "Magic", "Nature", "Creature", "Impact", "Weather")

data class FxLibraryUiState(
    val tracks: List<FxTrack> = emptyList(),
    val searchQuery: String = "",
    val showDemoButton: Boolean = true,
    val isDownloadingDemo: Boolean = false,
    val editTrackId: Long? = null,
    val editName: String = "",
    val editTags: Set<String> = emptySet(),
    val previewTrackId: Long? = null,
    val isPreviewPlaying: Boolean = false,
    val errorMessage: String? = null,
) {
    val previewTrack: FxTrack?
        get() = tracks.firstOrNull { it.id == previewTrackId }
}

@Composable
fun FxLibraryPane(
    modifier: Modifier = Modifier,
    viewModel: FxLibraryViewModel = hiltViewModel(),
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

    FxLibraryScreen(
        uiState = uiState,
        modifier = modifier,
        useSystemAudioPicker = viewModel.useSystemAudioPicker,
        onImportFx = {
            viewModel.requestImport()
            if (viewModel.useSystemAudioPicker) {
                audioPickerLauncher.launch(arrayOf("audio/*"))
            }
        },
        onSearchQueryChange = viewModel::updateSearchQuery,
        onDownloadDemo = viewModel::downloadDemoTracks,
        onOpenEdit = viewModel::openEdit,
        onDismissEdit = viewModel::dismissEdit,
        onEditNameChange = viewModel::updateEditName,
        onToggleEditTag = viewModel::toggleEditTag,
        onSaveEdit = viewModel::saveEdit,
        onDeleteTrack = viewModel::deleteEditedTrack,
        onPreviewTrack = viewModel::playPreview,
        onPausePreview = viewModel::pausePreview,
        onResumePreview = viewModel::resumePreview,
        onPreviousPreview = viewModel::playPreviousPreview,
        onNextPreview = viewModel::playNextPreview,
        onDismissError = viewModel::clearError,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FxLibraryScreen(
    uiState: FxLibraryUiState,
    modifier: Modifier = Modifier,
    useSystemAudioPicker: Boolean,
    onImportFx: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onDownloadDemo: () -> Unit,
    onOpenEdit: (FxTrack) -> Unit,
    onDismissEdit: () -> Unit,
    onEditNameChange: (String) -> Unit,
    onToggleEditTag: (String) -> Unit,
    onSaveEdit: () -> Unit,
    onDeleteTrack: () -> Unit,
    onPreviewTrack: (FxTrack) -> Unit,
    onPausePreview: () -> Unit,
    onResumePreview: () -> Unit,
    onPreviousPreview: () -> Unit,
    onNextPreview: () -> Unit,
    onDismissError: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = if (uiState.previewTrack != null) 180.dp else 112.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Sound Effects",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ArcanumGold,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("Action & Environmental FX")
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .testTag(FxLibraryTestTags.IMPORT_BUTTON),
                        onClick = onImportFx,
                    ) {
                        Text("Import FX")
                    }
                    if (uiState.showDemoButton) {
                        if (uiState.isDownloadingDemo) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(32.dp)
                                    .align(Alignment.CenterVertically)
                                    .testTag(FxLibraryTestTags.DEMO_LOADING),
                            )
                        } else {
                            FilledTonalButton(
                                modifier = Modifier.testTag(FxLibraryTestTags.DEMO_BUTTON),
                                onClick = onDownloadDemo,
                            ) {
                                Text("Get Demo FX")
                            }
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(FxLibraryTestTags.SEARCH_FIELD),
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    singleLine = true,
                    label = { Text("Search") },
                )
            }
            if (uiState.tracks.isEmpty()) {
                item {
                    FxEmptyState(onImportFx = onImportFx)
                }
            } else {
                items(uiState.tracks, key = FxTrack::id) { track ->
                    FxTrackRow(
                        track = track,
                        onPreviewTrack = onPreviewTrack,
                        onOpenEdit = onOpenEdit,
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.previewTrack != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding(),
            enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight / 2 }) + fadeOut(),
            label = "fx-mini-player",
        ) {
            uiState.previewTrack?.let { previewTrack ->
                MiniPlayerBar(
                    track = previewTrack,
                    isPlaying = uiState.isPreviewPlaying,
                    onPausePreview = onPausePreview,
                    onResumePreview = onResumePreview,
                    onPreviousPreview = onPreviousPreview,
                    onNextPreview = onNextPreview,
                )
            }
        }

        if (uiState.editTrackId != null) {
            AlertDialog(
                modifier = Modifier.testTag(FxLibraryTestTags.EDIT_DIALOG),
                onDismissRequest = onDismissEdit,
                title = { Text("Edit FX") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag(FxLibraryTestTags.NAME_INPUT),
                            value = uiState.editName,
                            onValueChange = onEditNameChange,
                            singleLine = true,
                            label = { Text("Name") },
                        )
                        Column(
                            modifier = Modifier.testTag(FxLibraryTestTags.TAGS_SECTION),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text("Tags", fontWeight = FontWeight.SemiBold)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                PREDEFINED_FX_TAGS.forEach { tag ->
                                    FilterChip(
                                        selected = tag in uiState.editTags,
                                        onClick = { onToggleEditTag(tag) },
                                        label = { Text(tag) },
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = onSaveEdit) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDeleteTrack) {
                            Text("Delete")
                        }
                        TextButton(onClick = onDismissEdit) {
                            Text("Cancel")
                        }
                    }
                },
            )
        }

        if (!useSystemAudioPicker) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 4.dp),
                text = "FX picker is managed by the acceptance harness.",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        ErrorDialog(message = uiState.errorMessage, onDismiss = onDismissError)
    }
}

@Composable
private fun FxEmptyState(
    onImportFx: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp),
                )
                .testTag(FxLibraryTestTags.EMPTY_ILLUSTRATION),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = ArcanumGold, modifier = Modifier.size(48.dp))
        }
        Text("No sound effects yet", fontWeight = FontWeight.Bold)
        Text("Import FX to build a library of one-shot sounds.")
        FilledTonalButton(onClick = onImportFx) {
            Text("Import FX")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FxTrackRow(
    track: FxTrack,
    onPreviewTrack: (FxTrack) -> Unit,
    onOpenEdit: (FxTrack) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(FxLibraryTestTags.row(track.name)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                modifier = Modifier.testTag(FxLibraryTestTags.playButton(track.name)),
                onClick = { onPreviewTrack(track) },
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play ${track.name}")
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(track.name, fontWeight = FontWeight.SemiBold)
                    Text(track.durationMs.toDurationLabel(), style = MaterialTheme.typography.bodySmall)
                }
                if (track.tags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        track.tags.forEach { tag ->
                            FilterChip(
                                modifier = Modifier.testTag(FxLibraryTestTags.tagChip(track.name, tag)),
                                selected = true,
                                onClick = {},
                                enabled = false,
                                label = { Text(tag) },
                            )
                        }
                    }
                }
            }
            IconButton(
                modifier = Modifier.testTag(FxLibraryTestTags.editButton(track.name)),
                onClick = { onOpenEdit(track) },
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit ${track.name}")
            }
        }
    }
}

@Composable
private fun MiniPlayerBar(
    modifier: Modifier = Modifier,
    track: FxTrack,
    isPlaying: Boolean,
    onPausePreview: () -> Unit,
    onResumePreview: () -> Unit,
    onPreviousPreview: () -> Unit,
    onNextPreview: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(FxLibraryTestTags.MINI_PLAYER),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Last Effect Played", style = MaterialTheme.typography.labelMedium)
                Text(track.name, color = ArcanumGold, fontWeight = FontWeight.Bold)
            }
            IconButton(
                modifier = Modifier.testTag(FxLibraryTestTags.MINI_PLAYER_PREVIOUS),
                onClick = onPreviousPreview,
            ) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
            }
            IconButton(
                modifier = Modifier.testTag(
                    if (isPlaying) FxLibraryTestTags.MINI_PLAYER_PAUSE else FxLibraryTestTags.MINI_PLAYER_PLAY,
                ),
                onClick = {
                    if (isPlaying) onPausePreview() else onResumePreview()
                },
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                )
            }
            IconButton(
                modifier = Modifier.testTag(FxLibraryTestTags.MINI_PLAYER_NEXT),
                onClick = onNextPreview,
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next")
            }
        }
    }
}

@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val fxRepository: FxRepository,
    private val sceneRepository: SceneRepository,
    private val fxTrackTrashRepository: FxTrackTrashRepository,
    private val trashVaultRepository: TrashVaultRepository,
    private val audioSelectionRepository: FxAudioSelectionRepository,
    private val audioFileImporter: FxAudioFileImporter,
    private val mixedMusicPlayer: MixedMusicPlayer,
    private val motionSystemStateRepository: MotionSystemStateRepository,
    audioPickerMode: FxAudioPickerMode,
) : ViewModel() {
    val useSystemAudioPicker: Boolean = audioPickerMode.useSystemAudioPicker

    private val query = MutableStateFlow("")
    private val editState = MutableStateFlow(FxEditState())
    private val previewState = MutableStateFlow(FxPreviewState())
    private val isDownloadingDemo = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val _uiState = MutableStateFlow(FxLibraryUiState())
    val uiState: StateFlow<FxLibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                query.flatMapLatest { searchQuery ->
                    if (searchQuery.isBlank()) fxRepository.observeTracks() else fxRepository.searchTracks(searchQuery)
                },
                query,
                fxRepository.hasDemoTracks(),
                isDownloadingDemo,
                editState,
                previewState,
                errorMessage,
            ) { tracks, searchQuery, hasDemoTracks, downloading, edit, preview, error ->
                FxLibraryUiState(
                    tracks = tracks,
                    searchQuery = searchQuery,
                    showDemoButton = !hasDemoTracks,
                    isDownloadingDemo = downloading,
                    editTrackId = edit.trackId,
                    editName = edit.name,
                    editTags = edit.tags,
                    previewTrackId = preview.trackId,
                    isPreviewPlaying = preview.isPlaying,
                    errorMessage = error,
                )
            }.collect { state ->
                _uiState.value = state
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

    fun updateSearchQuery(value: String) {
        query.value = value
    }

    fun downloadDemoTracks() {
        viewModelScope.launch {
            isDownloadingDemo.value = true
            delay(150)
            fxRepository.downloadDemoTracks()
            isDownloadingDemo.value = false
        }
    }

    fun openEdit(track: FxTrack) {
        editState.value = FxEditState(
            trackId = track.id,
            name = track.name,
            tags = track.tags.toSet(),
        )
    }

    fun dismissEdit() {
        editState.value = FxEditState()
    }

    fun updateEditName(name: String) {
        editState.update { it.copy(name = name) }
    }

    fun toggleEditTag(tag: String) {
        editState.update { state ->
            state.copy(tags = if (tag in state.tags) state.tags - tag else state.tags + tag)
        }
    }

    fun saveEdit() {
        val currentEdit = editState.value
        val trackId = currentEdit.trackId ?: return
        val updatedName = currentEdit.name.trim()
        if (updatedName.isBlank()) {
            errorMessage.value = "FX tracks need a name."
            return
        }
        val track = uiState.value.tracks.firstOrNull { it.id == trackId } ?: return
        viewModelScope.launch {
            fxRepository.upsertTrack(
                track.copy(
                    name = updatedName,
                    tags = currentEdit.tags.sorted(),
                ),
            )
            editState.value = FxEditState()
        }
    }

    fun deleteEditedTrack() {
        val currentEdit = editState.value
        val trackId = currentEdit.trackId ?: return
        val track = uiState.value.tracks.firstOrNull { it.id == trackId } ?: return
        viewModelScope.launch {
            trashVaultRepository.trashFxTrack(trackId)
            fxRepository.deleteTrack(trackId)
            fxTrackTrashRepository.recordDeletedTrack(track.name)
            sceneRepository.removeSoundboardEffect(track.name)
            if (previewState.value.trackId == trackId) {
                stopPreviewInternal()
            }
            editState.value = FxEditState()
        }
    }

    fun playPreview(track: FxTrack) {
        viewModelScope.launch {
            mixedMusicPlayer.previewTrack(track.filePath)
            previewState.value = FxPreviewState(trackId = track.id, isPlaying = true)
            fxRepository.incrementPlayCount(track.id)
            motionSystemStateRepository.record(
                type = MotionTransitionType.SHARED_Y_AXIS_ENTER,
                source = "library/sound-effects",
                target = "library/mini-player",
            )
        }
    }

    fun pausePreview() {
        mixedMusicPlayer.pausePreview()
        previewState.update { state -> state.copy(isPlaying = false) }
    }

    fun resumePreview() {
        val track = uiState.value.previewTrack ?: return
        playPreview(track)
    }

    fun playPreviousPreview() {
        val tracks = uiState.value.tracks
        val currentTrackId = previewState.value.trackId ?: return
        val currentIndex = tracks.indexOfFirst { it.id == currentTrackId }
        val targetTrack = tracks.getOrNull((currentIndex - 1).coerceAtLeast(0)) ?: return
        playPreview(targetTrack)
    }

    fun playNextPreview() {
        val tracks = uiState.value.tracks
        val currentTrackId = previewState.value.trackId ?: return
        val currentIndex = tracks.indexOfFirst { it.id == currentTrackId }
        if (currentIndex == -1 || currentIndex >= tracks.lastIndex) return
        val targetTrack = tracks.getOrNull(currentIndex + 1) ?: return
        playPreview(targetTrack)
    }

    fun stopAndHidePreview() {
        stopPreviewInternal()
    }

    fun clearError() {
        errorMessage.value = null
    }

    private fun stopPreviewInternal() {
        mixedMusicPlayer.stopPreview()
        previewState.value = FxPreviewState()
        motionSystemStateRepository.record(
            type = MotionTransitionType.SHARED_Y_AXIS_EXIT,
            source = "library/mini-player",
            target = "library/sound-effects",
        )
    }
}

private data class FxEditState(
    val trackId: Long? = null,
    val name: String = "",
    val tags: Set<String> = emptySet(),
)

private data class FxPreviewState(
    val trackId: Long? = null,
    val isPlaying: Boolean = false,
)

private fun String.asTagSuffix(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "_")
    .trim('_')

private fun Long.toDurationLabel(): String {
    val totalSeconds = (coerceAtLeast(0L) / 1_000L).toInt().absoluteValue
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
