package com.example.rpgaudiomixer.ui.fx

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.rpgaudiomixer.app.components.FxTrackRow
import com.example.rpgaudiomixer.app.components.MiniPlayerBar
import com.example.rpgaudiomixer.app.components.SearchBar
import com.example.rpgaudiomixer.domain.media.PreviewPlaybackState
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.ui.UiState
import com.example.rpgaudiomixer.ui.library.importAudioFileToAppStorage
import kotlinx.coroutines.launch

@Composable
fun FxLibraryTabRoute(
    modifier: Modifier = Modifier,
    viewModel: FxLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val previewState by viewModel.previewState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopPreview()
        }
    }

    FxLibraryScreen(
        uiState = uiState,
        previewState = previewState,
        errorMessage = errorMessage,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onTagFilterChange = viewModel::updateSelectedTag,
        onSortOptionChange = viewModel::updateSortOption,
        onPreviewTrack = viewModel::previewTrack,
        onTogglePlayback = viewModel::togglePreviewPlayback,
        onPlayPrevious = viewModel::playPreviousPreview,
        onPlayNext = viewModel::playNextPreview,
        onImportTrack = { sourceUri ->
            coroutineScope.launch {
                runCatching {
                    importAudioFileToAppStorage(
                        context = context,
                        sourceUri = sourceUri,
                        targetFolderName = "fx",
                    )
                }.onSuccess { importedFile ->
                    viewModel.importFxTrack(
                        displayName = importedFile.displayName,
                        filePath = importedFile.filePath,
                        durationMs = importedFile.durationMs,
                    )
                }.onFailure { throwable ->
                    viewModel.reportImportFailure(
                        throwable.message ?: "Unable to import FX track.",
                    )
                }
            }
        },
        onSaveTrackEdits = viewModel::saveTrackEdits,
        onDeleteTrack = viewModel::deleteTrack,
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
private fun FxLibraryScreen(
    uiState: UiState<FxLibraryContentState>,
    previewState: PreviewPlaybackState,
    errorMessage: String?,
    onSearchQueryChange: (String) -> Unit,
    onTagFilterChange: (String?) -> Unit,
    onSortOptionChange: (FxSortOption) -> Unit,
    onPreviewTrack: (List<FxTrack>, Long) -> Unit,
    onTogglePlayback: () -> Unit,
    onPlayPrevious: () -> Unit,
    onPlayNext: () -> Unit,
    onImportTrack: (Uri) -> Unit,
    onSaveTrackEdits: (FxTrack, String, String) -> Unit,
    onDeleteTrack: (Long) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editingTrack by remember { mutableStateOf<FxTrack?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.LibraryMusic,
                    title = "Unable to load FX library",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                val contentState = uiState.data
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Sound Effects",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            text = "Action & environmental FX",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        AudioFilePickerButton(
                            label = "IMPORT FX",
                            onFileSelected = onImportTrack,
                        )
                        SearchBar(
                            query = contentState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            placeholder = "Search FX by name or tag",
                        )
                        ScrollableTabRow(selectedTabIndex = contentState.sortOption.ordinal) {
                            FxSortOption.entries.forEach { option ->
                                Tab(
                                    selected = option == contentState.sortOption,
                                    onClick = { onSortOptionChange(option) },
                                    text = { Text(option.name.replace("_", " ")) },
                                )
                            }
                        }
                        if (contentState.availableTags.isNotEmpty()) {
                            androidx.compose.foundation.layout.FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                FilterChip(
                                    selected = contentState.selectedTag == null,
                                    onClick = { onTagFilterChange(null) },
                                    label = { Text("All") },
                                )
                                contentState.availableTags.forEach { tag ->
                                    FilterChip(
                                        selected = contentState.selectedTag == tag,
                                        onClick = { onTagFilterChange(tag) },
                                        label = { Text(tag) },
                                    )
                                }
                            }
                        }
                    }

                    if (contentState.tracks.isEmpty()) {
                        EmptyStateView(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            icon = Icons.Rounded.LibraryMusic,
                            title = "No FX tracks yet",
                            body = "Import a sound effect to start building your one-shot library.",
                            actionLabel = "Import FX",
                            onAction = { },
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            state = rememberLazyListState(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(contentState.tracks, key = { track -> track.id }) { track ->
                                FxTrackRow(
                                    track = track,
                                    onPreview = { onPreviewTrack(contentState.tracks, track.id) },
                                    onEdit = { editingTrack = track },
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = previewState.currentItem != null,
                        enter = slideInVertically { fullHeight -> fullHeight },
                        exit = slideOutVertically { fullHeight -> fullHeight },
                    ) {
                        MiniPlayerBar(
                            modifier = Modifier.padding(16.dp),
                            title = previewState.currentItem?.title.orEmpty(),
                            isPlaying = previewState.isPlaying,
                            canSkipPrevious = previewState.canSkipPrevious,
                            canSkipNext = previewState.canSkipNext,
                            onPrevious = onPlayPrevious,
                            onPlayPause = onTogglePlayback,
                            onNext = onPlayNext,
                        )
                    }
                }
            }
        }

        editingTrack?.let { track ->
            FxTrackEditDialog(
                track = track,
                onDismiss = { editingTrack = null },
                onSave = { updatedName, updatedTags ->
                    onSaveTrackEdits(track, updatedName, updatedTags)
                    editingTrack = null
                },
                onDelete = {
                    onDeleteTrack(track.id)
                    editingTrack = null
                },
            )
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = onDismissError,
    )
}

@Composable
private fun FxTrackEditDialog(
    track: FxTrack,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onDelete: () -> Unit,
) {
    var name by rememberSaveable(track.id) { mutableStateOf(track.name) }
    var tagsText by rememberSaveable(track.id) { mutableStateOf(track.tags.joinToString(", ")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit FX Track") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                androidx.compose.material3.OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                )
                androidx.compose.material3.OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Tags") },
                    placeholder = { Text("Combat, Impact") },
                    maxLines = 3,
                )
                Text(
                    text = track.filePath,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(name, tagsText) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        icon = {
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        },
    )
}
