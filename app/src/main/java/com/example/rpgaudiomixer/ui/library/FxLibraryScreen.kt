package com.example.rpgaudiomixer.ui.library

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.LoadingStateView
import com.example.rpgaudiomixer.app.components.TagRow
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.model.FxTrack

private val predefinedFxTags = listOf("Combat", "Nature", "Creature", "Magic", "Impact")

@Composable
fun FxLibraryScreen(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    viewModel: FxLibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var editingTrack by remember { mutableStateOf<FxTrack?>(null) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            val name = uri.lastPathSegment?.substringAfterLast('/') ?: "Imported FX"
            viewModel.importTrack(name = name, filePath = uri.toString())
        }
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            viewModel.onLibraryHidden()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onLibraryHidden()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Sound Effects",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Action & Environmental FX",
            style = MaterialTheme.typography.bodyLarge,
        )

        when (val state = uiState) {
            FxLibraryUiState.Loading -> LoadingStateView(label = "Loading FX…")
            is FxLibraryUiState.Error -> ErrorDialog(
                message = state.message,
                onDismiss = viewModel::dismissError,
            )
            is FxLibraryUiState.Success -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { filePickerLauncher.launch(arrayOf("audio/*")) },
                    ) {
                        Text("Import FX")
                    }
                    if (state.isDemoDownloadVisible) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = viewModel::downloadDemoTracks,
                            enabled = !state.isDownloadingDemoTracks,
                        ) {
                            if (state.isDownloadingDemoTracks) {
                                CircularProgressIndicator(strokeWidth = 2.dp)
                            } else {
                                Text("Get Demo FX")
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    label = { Text("Search FX") },
                    modifier = Modifier.fillMaxWidth(),
                )

                if (state.tracks.isEmpty()) {
                    EmptyStateView(
                        title = "No FX tracks yet",
                        actionLabel = "Import FX",
                        onAction = { filePickerLauncher.launch(arrayOf("audio/*")) },
                        illustration = "✨",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.tracks, key = { track -> track.id }) { track ->
                            FxTrackRow(
                                track = track,
                                onPreview = { viewModel.previewTrack(track) },
                                onEdit = { editingTrack = track },
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = state.previewState.isVisible,
                    enter = slideInVertically(initialOffsetY = { fullHeight -> fullHeight / 2 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { fullHeight -> fullHeight / 2 }) + fadeOut(),
                ) {
                    MiniPlayerBar(
                        state = state.previewState,
                        onTogglePlayPause = viewModel::togglePreviewPlayback,
                        onPrevious = viewModel::previewPrevious,
                        onNext = viewModel::previewNext,
                    )
                }
            }
        }
    }

    editingTrack?.let { track ->
        EditFxTrackDialog(
            track = track,
            onDismiss = { editingTrack = null },
            onSave = { updatedTrack ->
                viewModel.saveTrack(updatedTrack)
                editingTrack = null
            },
            onDelete = { trackId ->
                viewModel.deleteTrack(trackId)
                editingTrack = null
            },
        )
    }
}

@Composable
private fun FxTrackRow(
    track: FxTrack,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onPreview) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Preview ${track.name}",
                            tint = ArcanumGold,
                        )
                    }
                    Column {
                        Text(
                            text = track.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = formatDuration(track.durationMs),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${track.name}",
                        tint = ArcanumGold,
                    )
                }
            }
            TagRow(tags = track.tags)
        }
    }
}

@Composable
private fun MiniPlayerBar(
    state: FxPreviewState,
    onTogglePlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "Previewing",
                    style = MaterialTheme.typography.labelMedium,
                    color = ArcanumGold,
                )
                Text(text = state.trackName)
            }
            Row {
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
                }
                IconButton(onClick = onTogglePlayPause) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next")
                }
            }
        }
    }
}

@Composable
private fun EditFxTrackDialog(
    track: FxTrack,
    onDismiss: () -> Unit,
    onSave: (FxTrack) -> Unit,
    onDelete: (Long) -> Unit,
) {
    var name by rememberSaveable(track.id) { mutableStateOf(track.name) }
    var selectedTags by rememberSaveable(track.id) { mutableStateOf(track.tags) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit FX") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Tags")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    predefinedFxTags.forEach { tag ->
                        FilterChip(
                            selected = selectedTags.contains(tag),
                            onClick = {
                                selectedTags = if (selectedTags.contains(tag)) {
                                    selectedTags - tag
                                } else {
                                    selectedTags + tag
                                }
                            },
                            label = { Text(tag) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        track.copy(
                            name = name.trim(),
                            tags = selectedTags.sorted(),
                        ),
                    )
                },
                enabled = name.trim().isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onDelete(track.id) }) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
    )
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1000L).coerceAtLeast(0L)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
