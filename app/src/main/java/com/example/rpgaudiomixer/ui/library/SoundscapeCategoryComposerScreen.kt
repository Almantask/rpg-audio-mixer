package com.example.rpgaudiomixer.ui.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.AudioFilePickerButton
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.IntensitySelector
import com.example.rpgaudiomixer.app.components.MixSlider
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.ui.UiState
import kotlinx.coroutines.launch

@Composable
fun SoundscapeCategoryComposerRoute(
    onNavigateBack: () -> Unit,
    onTitleChange: (String?) -> Unit,
    onBackHandlerChange: (((() -> Unit)?) -> Unit),
    modifier: Modifier = Modifier,
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    val hasUnsavedChanges = (uiState as? UiState.Success)?.data?.hasUnsavedChanges == true
    val currentTitle = (uiState as? UiState.Success)?.data?.categoryName
        ?.takeIf { categoryName -> categoryName.isNotBlank() }
        ?: "Soundscape Composer"

    DisposableEffect(currentTitle) {
        onTitleChange(currentTitle)
        onDispose { onTitleChange(null) }
    }

    DisposableEffect(hasUnsavedChanges) {
        onBackHandlerChange(
            if (hasUnsavedChanges) {
                { showDiscardDialog = true }
            } else {
                onNavigateBack
            },
        )
        onDispose { onBackHandlerChange(null) }
    }

    BackHandler(enabled = hasUnsavedChanges) {
        showDiscardDialog = true
    }

    SoundscapeCategoryComposerScreen(
        uiState = uiState,
        errorMessage = errorMessage,
        onCategoryNameChange = viewModel::updateCategoryName,
        onTrackNameChange = viewModel::updateTrackName,
        onTrackIntensityChange = viewModel::updateTrackIntensity,
        onTrackMixChange = viewModel::updateTrackMix,
        onRemoveTrack = viewModel::removeTrack,
        onImportTrack = { sourceUri ->
            coroutineScope.launch {
                runCatching {
                    importAudioFileToAppStorage(context, sourceUri)
                }.onSuccess { importedFile ->
                    viewModel.addImportedTrack(
                        displayName = importedFile.displayName,
                        filePath = importedFile.filePath,
                    )
                }.onFailure { throwable ->
                    viewModel.reportImportFailure(
                        throwable.message ?: "Unable to import the selected audio file.",
                    )
                }
            }
        },
        onSaveComposition = {
            viewModel.saveComposition(onSaved = onNavigateBack)
        },
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard changes?") },
            text = {
                Text("You have unsaved changes in this composition. Discard them and return to the library?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onNavigateBack()
                    },
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Keep Editing")
                }
            },
        )
    }
}

@Composable
private fun SoundscapeCategoryComposerScreen(
    uiState: UiState<SoundscapeComposerEditorState>,
    errorMessage: String?,
    onCategoryNameChange: (String) -> Unit,
    onTrackNameChange: (Long, String) -> Unit,
    onTrackIntensityChange: (Long, com.example.rpgaudiomixer.domain.model.IntensityLevel) -> Unit,
    onTrackMixChange: (Long, Float) -> Unit,
    onRemoveTrack: (Long) -> Unit,
    onImportTrack: (android.net.Uri) -> Unit,
    onSaveComposition: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.LibraryMusic,
                    title = "Unable to load composition",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                val editorState = uiState.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    item {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = editorState.categoryName,
                            onValueChange = onCategoryNameChange,
                            label = { Text("Category name") },
                            singleLine = true,
                        )
                    }
                    if (editorState.tracks.isEmpty()) {
                        item {
                            EmptyStateView(
                                icon = Icons.Rounded.LibraryMusic,
                                title = "No soundscapes yet",
                                body = "Import an audio file to add the first layer to this category.",
                                actionLabel = "Invoke New Soundscape",
                                onAction = {},
                            )
                        }
                    } else {
                        items(editorState.tracks, key = { track -> track.localId }) { track ->
                            SwipeToDeleteContainer(
                                onDelete = { onRemoveTrack(track.localId) },
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
                                        onValueChange = { name ->
                                            onTrackNameChange(track.localId, name)
                                        },
                                        label = { Text("Soundscape name") },
                                        singleLine = true,
                                    )
                                    IntensitySelector(
                                        selectedLevel = track.intensityLevel,
                                        onLevelSelected = { intensityLevel ->
                                            onTrackIntensityChange(track.localId, intensityLevel)
                                        },
                                    )
                                    MixSlider(
                                        mixVolume = track.mixVolume,
                                        onMixVolumeChanged = { mixVolume ->
                                            onTrackMixChange(track.localId, mixVolume)
                                        },
                                    )
                                    Text(
                                        text = track.filePath,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                    item {
                        AudioFilePickerButton(
                            modifier = Modifier.fillMaxWidth(),
                            label = "+ INVOKE NEW SOUNDSCAPE",
                            onFileSelected = onImportTrack,
                        )
                    }
                    item {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onSaveComposition,
                        ) {
                            Text("SAVE COMPOSITION")
                        }
                    }
                }
            }
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = onDismissError,
    )
}
