package com.example.rpgaudiomixer.ui.soundscapes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.AudioFilePickerButton
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.IntensitySelector
import com.example.rpgaudiomixer.app.components.MixSlider
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.navigation.AppRoute
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
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

data class SoundscapeCategoryComposerUiState(
    val isLoading: Boolean = true,
    val category: SoundscapeCategory? = null,
    val tracks: List<SoundscapeTrack> = emptyList(),
    val hasUnsavedChanges: Boolean = false,
    val isDiscardChangesDialogVisible: Boolean = false,
    val shouldNavigateBack: Boolean = false,
    val errorMessage: String? = null,
)

@Composable
fun SoundscapeCategoryComposerRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.shouldNavigateBack) {
        if (uiState.shouldNavigateBack) {
            viewModel.onNavigatedBack()
            onNavigateBack()
        }
    }

    BackHandler {
        viewModel.requestNavigateBack()
    }

    SoundscapeCategoryComposerScreen(
        uiState = uiState,
        onImportTrack = viewModel::importTrack,
        onUpdateTrackName = viewModel::updateTrackName,
        onUpdateTrackIntensity = viewModel::updateTrackIntensity,
        onUpdateTrackMix = viewModel::updateTrackMix,
        onRemoveTrack = viewModel::removeTrack,
        onSaveComposition = viewModel::saveComposition,
        onDismissDiscardDialog = viewModel::dismissDiscardChangesDialog,
        onDiscardChanges = viewModel::discardChanges,
        modifier = modifier,
    )
}

@Composable
fun SoundscapeCategoryComposerScreen(
    uiState: SoundscapeCategoryComposerUiState,
    onImportTrack: (String) -> Unit,
    onUpdateTrackName: (Long, String) -> Unit,
    onUpdateTrackIntensity: (Long, IntensityLevel) -> Unit,
    onUpdateTrackMix: (Long, Float) -> Unit,
    onRemoveTrack: (Long) -> Unit,
    onSaveComposition: () -> Unit,
    onDismissDiscardDialog: () -> Unit,
    onDiscardChanges: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var errorMessage by rememberSaveable(uiState.errorMessage) { mutableStateOf(uiState.errorMessage) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = uiState.category?.name ?: "Soundscape Composer",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Build layered atmospheric pools and save them globally.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (uiState.tracks.isEmpty()) {
                item {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = "No soundscapes yet. Invoke a new soundscape to begin composing.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            items(items = uiState.tracks, key = SoundscapeTrack::id) { track ->
                SwipeToDeleteContainer(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onDelete = { onRemoveTrack(track.id) },
                ) {
                    SoundscapeTrackEditorCard(
                        track = track,
                        onUpdateTrackName = { onUpdateTrackName(track.id, it) },
                        onUpdateTrackIntensity = { onUpdateTrackIntensity(track.id, it) },
                        onUpdateTrackMix = { onUpdateTrackMix(track.id, it) },
                    )
                }
            }
            item {
                AudioFilePickerButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = "Invoke New Soundscape",
                    onAudioPicked = onImportTrack,
                )
            }
            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = onSaveComposition,
                ) {
                    Text(text = "Save Composition")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (uiState.isDiscardChangesDialogVisible) {
            AlertDialog(
                onDismissRequest = onDismissDiscardDialog,
                title = { Text(text = "Discard changes?") },
                text = { Text(text = "You have unsaved changes in this composition.") },
                confirmButton = {
                    Button(onClick = onDiscardChanges) {
                        Text(text = "Discard")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDiscardDialog) {
                        Text(text = "Keep Editing")
                    }
                },
            )
        }

        ErrorDialog(
            message = errorMessage,
            onDismiss = { errorMessage = null },
        )
    }
}

@Composable
private fun SoundscapeTrackEditorCard(
    track: SoundscapeTrack,
    onUpdateTrackName: (String) -> Unit,
    onUpdateTrackIntensity: (IntensityLevel) -> Unit,
    onUpdateTrackMix: (Float) -> Unit,
) {
    com.example.rpgaudiomixer.app.components.BentoCard(
        onClick = {},
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = track.name,
                onValueChange = onUpdateTrackName,
                label = { Text(text = "Soundscape name") },
                singleLine = true,
            )
            IntensitySelector(
                selectedLevel = track.intensityLevel,
                onSelectLevel = onUpdateTrackIntensity,
            )
            MixSlider(
                mixVolume = track.mixVolume,
                onMixChanged = onUpdateTrackMix,
            )
            Text(
                text = track.filePath.substringAfterLast('/'),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val soundscapeRepository: SoundscapeRepository,
) : ViewModel() {
    private val categoryId: Long = requireNotNull(savedStateHandle[AppRoute.SOUNDSCAPE_CATEGORY_ID_ARG])
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main
    private var originalTracks: List<SoundscapeTrack> = emptyList()
    private var nextTemporaryTrackId: Long = -1L

    internal constructor(
        categoryId: Long,
        soundscapeRepository: SoundscapeRepository,
        mainDispatcher: CoroutineDispatcher,
    ) : this(
        savedStateHandle = SavedStateHandle(mapOf(AppRoute.SOUNDSCAPE_CATEGORY_ID_ARG to categoryId)),
        soundscapeRepository = soundscapeRepository,
    ) {
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(SoundscapeCategoryComposerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            combine(
                soundscapeRepository.observeCategory(categoryId),
                soundscapeRepository.observeTracks(categoryId),
            ) { category, tracks ->
                category to tracks
            }
                .catch { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load composition.",
                    )
                }
                .collect { (category, tracks) ->
                    val currentState = _uiState.value
                    if (!currentState.hasUnsavedChanges) {
                        originalTracks = tracks
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            category = category,
                            tracks = tracks,
                        )
                    } else {
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            category = category,
                        )
                    }
                }
        }
    }

    fun importTrack(sourceUri: String) {
        viewModelScope.launch(mainDispatcher) {
            val importedTrack = soundscapeRepository.importTrack(categoryId = categoryId, sourceUri = sourceUri)
                .let { track ->
                    if (track.id > 0L) {
                        track
                    } else {
                        track.copy(id = nextTemporaryTrackId--)
                    }
                }
            updateDraft(_uiState.value.tracks + importedTrack)
        }
    }

    fun updateTrackName(trackId: Long, name: String) {
        updateDraft(
            _uiState.value.tracks.map { track ->
                if (track.id == trackId) {
                    track.copy(name = name)
                } else {
                    track
                }
            }
        )
    }

    fun updateTrackIntensity(trackId: Long, intensityLevel: IntensityLevel) {
        updateDraft(
            _uiState.value.tracks.map { track ->
                if (track.id == trackId) {
                    track.copy(intensityLevel = intensityLevel)
                } else {
                    track
                }
            }
        )
    }

    fun updateTrackMix(trackId: Long, mixVolume: Float) {
        updateDraft(
            _uiState.value.tracks.map { track ->
                if (track.id == trackId) {
                    track.copy(mixVolume = mixVolume)
                } else {
                    track
                }
            }
        )
    }

    fun removeTrack(trackId: Long) {
        updateDraft(_uiState.value.tracks.filterNot { it.id == trackId })
    }

    fun saveComposition() {
        viewModelScope.launch(mainDispatcher) {
            soundscapeRepository.saveTracks(categoryId = categoryId, tracks = _uiState.value.tracks)
            originalTracks = _uiState.value.tracks
            _uiState.value = _uiState.value.copy(
                hasUnsavedChanges = false,
                shouldNavigateBack = true,
            )
        }
    }

    fun requestNavigateBack() {
        _uiState.value = if (_uiState.value.hasUnsavedChanges) {
            _uiState.value.copy(isDiscardChangesDialogVisible = true)
        } else {
            _uiState.value.copy(shouldNavigateBack = true)
        }
    }

    fun dismissDiscardChangesDialog() {
        _uiState.value = _uiState.value.copy(isDiscardChangesDialogVisible = false)
    }

    fun discardChanges() {
        _uiState.value = _uiState.value.copy(
            tracks = originalTracks,
            hasUnsavedChanges = false,
            isDiscardChangesDialogVisible = false,
            shouldNavigateBack = true,
        )
    }

    fun onNavigatedBack() {
        _uiState.value = _uiState.value.copy(shouldNavigateBack = false)
    }

    private fun updateDraft(tracks: List<SoundscapeTrack>) {
        _uiState.value = _uiState.value.copy(
            tracks = tracks,
            hasUnsavedChanges = tracks != originalTracks,
        )
    }
}
