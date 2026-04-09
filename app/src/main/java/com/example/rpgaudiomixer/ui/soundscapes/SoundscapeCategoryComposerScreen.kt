package com.example.rpgaudiomixer.ui.soundscapes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

object SoundscapeComposerTestTags {
    const val SCREEN = "Soundscape_Composer_Screen"
    const val INVOKE_BUTTON = "Soundscape_Composer_Invoke"
    const val SAVE_BUTTON = "Soundscape_Composer_Save"
    const val DISCARD_DIALOG = "Soundscape_Composer_Discard_Dialog"

    fun card(name: String): String = "Soundscape_Track_${name.asTagSuffix()}"
    fun mixSlider(name: String): String = "Soundscape_Mix_${name.asTagSuffix()}"
    fun intensity(name: String, intensityLevel: IntensityLevel): String = "Soundscape_Intensity_${name.asTagSuffix()}_${intensityLevel.label}"
}

data class SoundscapeComposerUiState(
    val isLoading: Boolean = true,
    val categoryName: String = "",
    val tracks: List<SoundscapeTrack> = emptyList(),
    val showDiscardDialog: Boolean = false,
    val navigateBack: Boolean = false,
    val errorMessage: String? = null,
)

@Composable
fun SoundscapeCategoryComposerRoute(
    onNavigateBack: () -> Unit,
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel(),
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

    LaunchedEffect(uiState.navigateBack) {
        if (uiState.navigateBack) {
            onNavigateBack()
            viewModel.onNavigateBackHandled()
        }
    }

    SoundscapeCategoryComposerScreen(
        uiState = uiState,
        useSystemAudioPicker = viewModel.useSystemAudioPicker,
        onInvokeNewSoundscape = {
            viewModel.requestAudioImport()
            if (viewModel.useSystemAudioPicker) {
                audioPickerLauncher.launch(arrayOf("audio/*"))
            }
        },
        onTrackNameChange = viewModel::updateTrackName,
        onTrackIntensityChange = viewModel::updateTrackIntensity,
        onTrackMixChange = viewModel::updateTrackMix,
        onDeleteTrack = viewModel::deleteTrack,
        onSaveComposition = viewModel::saveComposition,
        onDismissDiscardDialog = viewModel::dismissDiscardDialog,
        onConfirmDiscardChanges = viewModel::confirmDiscardChanges,
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun SoundscapeCategoryComposerScreen(
    uiState: SoundscapeComposerUiState,
    useSystemAudioPicker: Boolean,
    onInvokeNewSoundscape: () -> Unit,
    onTrackNameChange: (Long, String) -> Unit,
    onTrackIntensityChange: (Long, IntensityLevel) -> Unit,
    onTrackMixChange: (Long, Int) -> Unit,
    onDeleteTrack: (Long) -> Unit,
    onSaveComposition: () -> Unit,
    onDismissDiscardDialog: () -> Unit,
    onConfirmDiscardChanges: () -> Unit,
    onDismissError: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(SoundscapeComposerTestTags.SCREEN),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(modifier = Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(uiState.categoryName, style = MaterialTheme.typography.headlineMedium, color = ArcanumGold, fontWeight = FontWeight.Bold)
                    if (!useSystemAudioPicker) {
                        Text("Audio picker is managed by the acceptance harness.")
                    }
                }
            }
            items(uiState.tracks, key = SoundscapeTrack::id) { track ->
                SwipeToDeleteTrackContainer(track = track, onDeleteTrack = onDeleteTrack) {
                    SoundscapeTrackCard(
                        track = track,
                        onTrackNameChange = onTrackNameChange,
                        onTrackIntensityChange = onTrackIntensityChange,
                        onTrackMixChange = onTrackMixChange,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(SoundscapeComposerTestTags.INVOKE_BUTTON),
                onClick = onInvokeNewSoundscape,
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Invoke New Soundscape")
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(SoundscapeComposerTestTags.SAVE_BUTTON),
                onClick = onSaveComposition,
            ) {
                Text("Save Composition")
            }
        }

        if (uiState.showDiscardDialog) {
            AlertDialog(
                modifier = Modifier.testTag(SoundscapeComposerTestTags.DISCARD_DIALOG),
                onDismissRequest = onDismissDiscardDialog,
                title = { Text("Discard changes?") },
                text = { Text("You have unsaved changes in this composition.") },
                confirmButton = {
                    TextButton(onClick = onConfirmDiscardChanges) { Text("Discard") }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDiscardDialog) { Text("Keep editing") }
                },
            )
        }

        ErrorDialog(message = uiState.errorMessage, onDismiss = onDismissError)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteTrackContainer(
    track: SoundscapeTrack,
    onDeleteTrack: (Long) -> Unit,
    content: @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.StartToEnd || value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteTrack(track.id)
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text("Remove soundscape", color = MaterialTheme.colorScheme.onErrorContainer)
            }
        },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SoundscapeTrackCard(
    track: SoundscapeTrack,
    onTrackNameChange: (Long, String) -> Unit,
    onTrackIntensityChange: (Long, IntensityLevel) -> Unit,
    onTrackMixChange: (Long, Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(SoundscapeComposerTestTags.card(track.name)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = track.name,
                onValueChange = { onTrackNameChange(track.id, it) },
                singleLine = true,
                label = { Text("Soundscape name") },
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Intensity: ${track.intensityLevel.label}", fontWeight = FontWeight.SemiBold)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    IntensityLevel.entries.forEach { intensity ->
                        SegmentedButton(
                            selected = track.intensityLevel == intensity,
                            onClick = { onTrackIntensityChange(track.id, intensity) },
                            shape = androidx.compose.material3.SegmentedButtonDefaults.itemShape(
                                index = intensity.ordinal,
                                count = IntensityLevel.entries.size,
                            ),
                            modifier = Modifier.testTag(SoundscapeComposerTestTags.intensity(track.name, intensity)),
                        ) {
                            Text(intensity.label)
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("MIX ${track.mixVolumePercent}%", fontWeight = FontWeight.SemiBold)
                Slider(
                    modifier = Modifier.testTag(SoundscapeComposerTestTags.mixSlider(track.name)),
                    value = track.mixVolumePercent.toFloat(),
                    valueRange = 0f..100f,
                    onValueChange = { value -> onTrackMixChange(track.id, value.toInt()) },
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null)
                Text(track.filePath.substringAfterLast('/'))
            }
        }
    }
}

@HiltViewModel
class SoundscapeCategoryComposerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SoundscapeRepository,
    private val audioSelectionRepository: SoundscapeAudioSelectionRepository,
    private val audioFileImporter: SoundscapeAudioFileImporter,
    private val backRequestRepository: SoundscapeComposerBackRequestRepository,
    audioPickerMode: SoundscapeAudioPickerMode,
) : ViewModel() {
    private val categoryId = requireNotNull(savedStateHandle.get<String>("categoryId")) {
        "Navigation argument 'categoryId' is missing."
    }.toLongOrNull() ?: error("Navigation argument 'categoryId' must be a valid numeric value.")

    private val _uiState = MutableStateFlow(SoundscapeComposerUiState())
    val uiState: StateFlow<SoundscapeComposerUiState> = _uiState.asStateFlow()
    val useSystemAudioPicker: Boolean = audioPickerMode.useSystemAudioPicker

    private var persistedCategory: SoundscapeCategory? = null
    private var draftTracks: List<SoundscapeTrack> = emptyList()
    private var draftCategoryName: String = ""
    private var hasUnsavedChanges: Boolean = false
    private var observeJob: Job? = null

    init {
        observeCategory()
        observePickedAudio()
        observeBackRequests()
    }

    private fun observeCategory() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repository.observeCategory(categoryId).collect { category ->
                persistedCategory = category
                if (!hasUnsavedChanges && category != null) {
                    draftCategoryName = category.name
                    draftTracks = category.tracks
                    emitState()
                }
            }
        }
    }

    private fun observePickedAudio() {
        viewModelScope.launch {
            audioSelectionRepository.selectedAudio.filterNotNull().collect { picked ->
                if (picked.categoryId != categoryId) {
                    return@collect
                }
                draftTracks = draftTracks + SoundscapeTrack(
                    id = nextDraftTrackId(),
                    categoryId = categoryId,
                    name = picked.displayName,
                    filePath = picked.filePath,
                    intensityLevel = IntensityLevel.I,
                    mixVolumePercent = 100,
                )
                hasUnsavedChanges = true
                audioSelectionRepository.consumeSelection()
                emitState()
            }
        }
    }

    private fun observeBackRequests() {
        viewModelScope.launch {
            backRequestRepository.requests.collect {
                if (hasUnsavedChanges) {
                    _uiState.update { it.copy(showDiscardDialog = true) }
                } else {
                    _uiState.update { it.copy(navigateBack = true) }
                }
            }
        }
    }

    fun requestAudioImport() {
        audioSelectionRepository.requestPicker(categoryId)
    }

    fun onAudioPicked(uri: Uri) {
        val imported = audioFileImporter.importAudio(uri)
        if (imported == null) {
            audioSelectionRepository.closePicker()
            _uiState.update { it.copy(errorMessage = "Failed to import audio file.") }
            return
        }
        audioSelectionRepository.submitSelection(categoryId, imported.first, imported.second)
    }

    fun closePicker() {
        audioSelectionRepository.closePicker()
    }

    fun updateTrackName(trackId: Long, name: String) {
        draftTracks = draftTracks.map { track -> if (track.id == trackId) track.copy(name = name) else track }
        hasUnsavedChanges = true
        emitState()
    }

    fun updateTrackIntensity(trackId: Long, intensityLevel: IntensityLevel) {
        draftTracks = draftTracks.map { track ->
            if (track.id == trackId) track.copy(intensityLevel = intensityLevel) else track
        }
        hasUnsavedChanges = true
        emitState()
    }

    fun updateTrackMix(trackId: Long, mixPercent: Int) {
        draftTracks = draftTracks.map { track ->
            if (track.id == trackId) track.copy(mixVolumePercent = mixPercent.coerceIn(0, 100)) else track
        }
        hasUnsavedChanges = true
        emitState()
    }

    fun deleteTrack(trackId: Long) {
        draftTracks = draftTracks.filterNot { track -> track.id == trackId }
        hasUnsavedChanges = true
        emitState()
    }

    fun saveComposition() {
        viewModelScope.launch {
            repository.replaceTracks(categoryId, draftTracks)
            persistedCategory = persistedCategory?.copy(tracks = draftTracks)
            hasUnsavedChanges = false
            emitState()
        }
    }

    fun dismissDiscardDialog() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    fun confirmDiscardChanges() {
        val category = persistedCategory
        draftCategoryName = category?.name.orEmpty()
        draftTracks = category?.tracks.orEmpty()
        hasUnsavedChanges = false
        _uiState.update { it.copy(showDiscardDialog = false, navigateBack = true) }
    }

    fun onNavigateBackHandled() {
        _uiState.update { it.copy(navigateBack = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun emitState() {
        val category = persistedCategory
        _uiState.value = SoundscapeComposerUiState(
            isLoading = false,
            categoryName = draftCategoryName.ifBlank { category?.name.orEmpty() },
            tracks = draftTracks,
            errorMessage = _uiState.value.errorMessage,
        )
    }

    private fun nextDraftTrackId(): Long = (draftTracks.maxOfOrNull(SoundscapeTrack::id) ?: 0L) + 1_000_000L
}

private fun String.asTagSuffix(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "_")
    .trim('_')
