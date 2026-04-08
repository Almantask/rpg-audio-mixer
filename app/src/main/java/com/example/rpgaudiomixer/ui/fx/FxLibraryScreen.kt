package com.example.rpgaudiomixer.ui.fx

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.AudioFilePickerButton
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.FxTrackRow
import com.example.rpgaudiomixer.app.components.MiniPlayerBar
import com.example.rpgaudiomixer.app.components.SearchBar
import com.example.rpgaudiomixer.domain.fx.FxPreviewPlayer
import com.example.rpgaudiomixer.domain.fx.FxRepository
import com.example.rpgaudiomixer.domain.model.FxTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private val predefinedFxTags = listOf("Combat", "Creature", "Magic", "Nature", "Weather")

data class FxEditorState(
    val trackId: Long,
    val originalTrack: FxTrack,
    val name: String,
    val selectedTags: Set<String>,
)

data class FxPreviewUiState(
    val isVisible: Boolean = false,
    val currentTrackName: String = "",
    val isPlaying: Boolean = false,
)

data class FxLibraryUiState(
    val isLoading: Boolean = true,
    val allTracks: List<FxTrack> = emptyList(),
    val visibleTracks: List<FxTrack> = emptyList(),
    val searchQuery: String = "",
    val selectedTag: String? = null,
    val showDemoButton: Boolean = true,
    val isDownloadingDemo: Boolean = false,
    val editorState: FxEditorState? = null,
    val previewState: FxPreviewUiState = FxPreviewUiState(),
    val errorMessage: String? = null,
)

@Composable
fun FxLibraryRoute(
    modifier: Modifier = Modifier,
    viewModel: FxLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPreview()
        }
    }

    FxLibraryScreen(
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onSelectTag = viewModel::selectTag,
        onImportFx = viewModel::importFxTrack,
        onDownloadDemoFx = viewModel::downloadDemoFxTracks,
        onOpenEditor = viewModel::openEditor,
        onUpdateEditorName = viewModel::updateEditorName,
        onToggleEditorTag = viewModel::toggleEditorTag,
        onSaveEditor = viewModel::saveEditorChanges,
        onDeleteTrack = viewModel::deleteEditedTrack,
        onDismissEditor = viewModel::dismissEditor,
        onPreview = viewModel::playPreview,
        onTogglePreview = {
            if (uiState.previewState.isPlaying) viewModel.pausePreview() else viewModel.resumePreview()
        },
        onDismissError = viewModel::clearError,
        modifier = modifier,
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun FxLibraryScreen(
    uiState: FxLibraryUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectTag: (String?) -> Unit,
    onImportFx: (String) -> Unit,
    onDownloadDemoFx: () -> Unit,
    onOpenEditor: (FxTrack) -> Unit,
    onUpdateEditorName: (String) -> Unit,
    onToggleEditorTag: (String) -> Unit,
    onSaveEditor: () -> Unit,
    onDeleteTrack: () -> Unit,
    onDismissEditor: () -> Unit,
    onPreview: (FxTrack) -> Unit,
    onTogglePreview: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Sound Effects",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            text = "Action & environmental FX",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        AudioFilePickerButton(
                            text = "Import FX",
                            onAudioPicked = onImportFx,
                        )
                        if (uiState.showDemoButton) {
                            Button(
                                onClick = onDownloadDemoFx,
                                enabled = !uiState.isDownloadingDemo,
                            ) {
                                if (uiState.isDownloadingDemo) {
                                    CircularProgressIndicator()
                                } else {
                                    Text(text = "Get Demo FX")
                                }
                            }
                        }
                    }
                }
                item {
                    SearchBar(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = "Search sounds by name",
                    )
                }
                item {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = uiState.selectedTag == null,
                            onClick = { onSelectTag(null) },
                            label = { Text(text = "All") },
                        )
                        predefinedFxTags.forEach { tag ->
                            FilterChip(
                                selected = uiState.selectedTag == tag,
                                onClick = { onSelectTag(if (uiState.selectedTag == tag) null else tag) },
                                label = { Text(text = tag) },
                            )
                        }
                    }
                }
                if (uiState.visibleTracks.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = "Import your first sound effect",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            AudioFilePickerButton(
                                text = "Import FX",
                                onAudioPicked = onImportFx,
                            )
                        }
                    }
                } else {
                    items(items = uiState.visibleTracks, key = FxTrack::id) { track ->
                        FxTrackRow(
                            track = track,
                            onPreview = { onPreview(track) },
                            onEdit = { onOpenEditor(track) },
                        )
                    }
                }
                item {
                    AnimatedVisibility(visible = uiState.previewState.isVisible) {
                        MiniPlayerBar(
                            trackName = uiState.previewState.currentTrackName,
                            isPlaying = uiState.previewState.isPlaying,
                            onTogglePlayback = onTogglePreview,
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        uiState.editorState?.let { editorState ->
            FxEditorDialog(
                editorState = editorState,
                onUpdateName = onUpdateEditorName,
                onToggleTag = onToggleEditorTag,
                onSave = onSaveEditor,
                onDelete = onDeleteTrack,
                onDismiss = onDismissEditor,
            )
        }

        ErrorDialog(
            message = uiState.errorMessage,
            onDismiss = onDismissError,
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun FxEditorDialog(
    editorState: FxEditorState,
    onUpdateName: (String) -> Unit,
    onToggleTag: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit ${editorState.originalTrack.name}") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = editorState.name,
                    onValueChange = onUpdateName,
                    label = { Text(text = "Name") },
                    singleLine = true,
                )
                Text(
                    text = "Tags",
                    style = MaterialTheme.typography.labelLarge,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    predefinedFxTags.forEach { tag ->
                        FilterChip(
                            selected = tag in editorState.selectedTags,
                            onClick = { onToggleTag(tag) },
                            label = { Text(text = tag) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = editorState.name.isNotBlank(),
            ) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextButton(onClick = onDelete) {
                    Text(text = "Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
            }
        },
    )
}

@HiltViewModel
class FxLibraryViewModel @Inject constructor(
    private val fxRepository: FxRepository,
    private val fxPreviewPlayer: FxPreviewPlayer,
) : ViewModel() {
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    private var currentPreviewTrack: FxTrack? = null

    internal constructor(
        fxRepository: FxRepository,
        fxPreviewPlayer: FxPreviewPlayer,
        mainDispatcher: CoroutineDispatcher,
    ) : this(
        fxRepository = fxRepository,
        fxPreviewPlayer = fxPreviewPlayer,
    ) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(FxLibraryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeTracks()
    }

    private fun observeTracks() {
        viewModelScope.launch(mainDispatcher) {
            combine(
                fxRepository.observeFxTracks(),
                fxRepository.observeHasDemoFxTracks(),
            ) { tracks, hasDemoTracks ->
                tracks to hasDemoTracks
            }
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load sound effects.",
                    )
                }
                .collect { (tracks, hasDemoTracks) ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        allTracks = tracks,
                        showDemoButton = !hasDemoTracks,
                    ).filtered()
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query).filtered()
    }

    fun selectTag(tag: String?) {
        _uiState.value = _uiState.value.copy(selectedTag = tag).filtered()
    }

    fun importFxTrack(sourceUri: String) {
        viewModelScope.launch(mainDispatcher) {
            runCatching { fxRepository.importFxTrack(sourceUri) }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = throwable.message ?: "The file could not be read as audio.",
                    )
                }
        }
    }

    fun openEditor(track: FxTrack) {
        _uiState.value = _uiState.value.copy(
            editorState = FxEditorState(
                trackId = track.id,
                originalTrack = track,
                name = track.name,
                selectedTags = track.tags.toSet(),
            )
        )
    }

    fun updateEditorName(name: String) {
        val editorState = _uiState.value.editorState ?: return
        _uiState.value = _uiState.value.copy(
            editorState = editorState.copy(name = name),
        )
    }

    fun toggleEditorTag(tag: String) {
        val editorState = _uiState.value.editorState ?: return
        val updatedTags = editorState.selectedTags.toMutableSet().apply {
            if (!add(tag)) remove(tag)
        }
        _uiState.value = _uiState.value.copy(
            editorState = editorState.copy(selectedTags = updatedTags),
        )
    }

    fun saveEditorChanges() {
        val editorState = _uiState.value.editorState ?: return
        viewModelScope.launch(mainDispatcher) {
            fxRepository.updateFxTrack(
                editorState.originalTrack.copy(
                    name = editorState.name.trim(),
                    tags = editorState.selectedTags.sorted(),
                )
            )
            _uiState.value = _uiState.value.copy(editorState = null)
        }
    }

    fun deleteEditedTrack() {
        val editorState = _uiState.value.editorState ?: return
        viewModelScope.launch(mainDispatcher) {
            fxRepository.softDeleteFxTrack(editorState.trackId)
            if (currentPreviewTrack?.id == editorState.trackId) {
                stopPreview()
            }
            _uiState.value = _uiState.value.copy(editorState = null)
        }
    }

    fun dismissEditor() {
        _uiState.value = _uiState.value.copy(editorState = null)
    }

    fun downloadDemoFxTracks() {
        viewModelScope.launch(mainDispatcher) {
            _uiState.value = _uiState.value.copy(isDownloadingDemo = true)
            fxRepository.seedDemoFxTracks()
            _uiState.value = _uiState.value.copy(
                isDownloadingDemo = false,
                showDemoButton = false,
            )
        }
    }

    fun playPreview(track: FxTrack) {
        currentPreviewTrack = track
        fxPreviewPlayer.play(track.filePath)
        _uiState.value = _uiState.value.copy(
            previewState = FxPreviewUiState(
                isVisible = true,
                currentTrackName = track.name,
                isPlaying = true,
            )
        )
    }

    fun pausePreview() {
        fxPreviewPlayer.pause()
        _uiState.value = _uiState.value.copy(
            previewState = _uiState.value.previewState.copy(isPlaying = false),
        )
    }

    fun resumePreview() {
        val track = currentPreviewTrack ?: return
        fxPreviewPlayer.play(track.filePath)
        _uiState.value = _uiState.value.copy(
            previewState = _uiState.value.previewState.copy(isPlaying = true),
        )
    }

    fun stopPreview() {
        fxPreviewPlayer.stop()
        currentPreviewTrack = null
        _uiState.value = _uiState.value.copy(previewState = FxPreviewUiState())
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

private fun FxLibraryUiState.filtered(): FxLibraryUiState {
    val filteredTracks = allTracks
        .filter { track ->
            searchQuery.isBlank() || track.name.contains(searchQuery.trim(), ignoreCase = true)
        }
        .filter { track ->
            selectedTag == null || selectedTag in track.tags
    }
    return copy(visibleTracks = filteredTracks)
}
