package com.example.rpgaudiomixer.ui.library.fx

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.FxTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FxLibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: FxLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showImportDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTrack by remember { mutableStateOf<FxTrack?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search FX...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // Tracks list
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyStateView(
                            title = "No FX Tracks",
                            message = if (searchQuery.isNotBlank()) {
                                "No tracks found matching \"$searchQuery\""
                            } else {
                                "Import your first sound effect to get started"
                            },
                            actionLabel = "IMPORT FX",
                            onAction = { showImportDialog = true }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data, key = { it.id }) { track ->
                                FxTrackRow(
                                    track = track,
                                    onEditClick = {
                                        selectedTrack = track
                                        showEditDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    errorMessage = state.message
                    EmptyStateView(
                        title = "Error Loading FX",
                        message = state.message,
                        actionLabel = "Retry",
                        onAction = { /* Retry logic */ }
                    )
                }
            }
        }

        // FAB for importing FX
        if (uiState is UiState.Success) {
            FloatingActionButton(
                onClick = { showImportDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import FX")
            }
        }
    }

    // Import FX dialog
    if (showImportDialog) {
        ImportFxDialog(
            onDismiss = { showImportDialog = false },
            onConfirm = { name, filePath, tags, duration ->
                viewModel.importTrack(name, filePath, tags, duration)
                showImportDialog = false
            }
        )
    }

    // Edit FX dialog
    if (showEditDialog && selectedTrack != null) {
        EditFxDialog(
            track = selectedTrack!!,
            onDismiss = {
                showEditDialog = false
                selectedTrack = null
            },
            onSave = { updatedTrack ->
                viewModel.updateTrack(updatedTrack)
                showEditDialog = false
                selectedTrack = null
            },
            onDelete = {
                viewModel.deleteTrack(selectedTrack!!.id)
                showEditDialog = false
                selectedTrack = null
            }
        )
    }

    // Error dialog
    errorMessage?.let { message ->
        ErrorDialog(
            message = message,
            onDismiss = { errorMessage = null }
        )
    }
}

@Composable
private fun FxTrackRow(
    track: FxTrack,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onEditClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (track.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        track.tags.take(3).forEach { tag ->
                            SuggestionChip(
                                onClick = { },
                                label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                        if (track.tags.size > 3) {
                            Text(
                                text = "+${track.tags.size - 3}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${track.durationMs / 1000}s",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun ImportFxDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, List<String>, Long) -> Unit
) {
    var trackName by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedFileUri = uri
        if (trackName.isEmpty() && uri != null) {
            trackName = uri.lastPathSegment?.substringAfterLast("/") ?: "New FX"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Sound Effect") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = trackName,
                    onValueChange = { trackName = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma-separated)") },
                    placeholder = { Text("e.g. Tavern, Combat, Magic") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { audioPickerLauncher.launch(arrayOf("audio/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedFileUri != null) "File Selected" else "SELECT AUDIO FILE")
                }

                selectedFileUri?.let { uri ->
                    Text(
                        text = "Selected: ${uri.lastPathSegment}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedFileUri?.let { uri ->
                        val tagList = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        onConfirm(trackName, uri.toString(), tagList, 0L) // TODO: Get actual duration
                    }
                },
                enabled = trackName.isNotBlank() && selectedFileUri != null
            ) {
                Text("IMPORT")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

@Composable
private fun EditFxDialog(
    track: FxTrack,
    onDismiss: () -> Unit,
    onSave: (FxTrack) -> Unit,
    onDelete: () -> Unit
) {
    var trackName by remember { mutableStateOf(track.name) }
    var tags by remember { mutableStateOf(track.tags.joinToString(", ")) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Sound Effect") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = trackName,
                    onValueChange = { trackName = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DELETE")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val tagList = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    onSave(track.copy(name = trackName, tags = tagList))
                },
                enabled = trackName.isNotBlank()
            ) {
                Text("SAVE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete FX Track") },
            text = { Text("Are you sure you want to delete \"${track.name}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("DELETE")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
