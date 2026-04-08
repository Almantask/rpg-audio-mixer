package com.example.rpgaudiomixer.ui.library.soundscapes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.IntensitySelector
import com.example.rpgaudiomixer.app.components.MixSlider
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundscapeComposerScreen(
    categoryId: Long,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundscapeComposerViewModel = hiltViewModel()
) {
    val category by viewModel.category.collectAsStateWithLifecycle()
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsStateWithLifecycle()

    var showAddTrackDialog by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category?.name ?: "Composer") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasUnsavedChanges) {
                            showUnsavedChangesDialog = true
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (hasUnsavedChanges) {
                        IconButton(onClick = { viewModel.saveAllChanges() }) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTrackDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Invoke New Soundscape")
            }
        }
    ) { paddingValues ->
        Box(modifier = modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = tracks) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        EmptyStateView(
                            title = "No Tracks Yet",
                            message = "Add your first soundscape track to this category",
                            actionLabel = "+ INVOKE NEW SOUNDSCAPE",
                            onAction = { showAddTrackDialog = true }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data, key = { it.track.id }) { trackState ->
                                SwipeToDeleteContainer(
                                    onDelete = { viewModel.deleteTrack(trackState.track.id) }
                                ) {
                                    SoundscapeTrackCard(
                                        track = trackState.track,
                                        onIntensityChange = { newIntensity ->
                                            viewModel.updateTrackIntensity(trackState.track.id, newIntensity)
                                        },
                                        onVolumeChange = { newVolume ->
                                            viewModel.updateTrackMixVolume(trackState.track.id, newVolume)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    errorMessage = state.message
                    EmptyStateView(
                        title = "Error Loading Tracks",
                        message = state.message,
                        actionLabel = "Retry",
                        onAction = { /* Retry logic */ }
                    )
                }
            }
        }
    }

    // Add track dialog
    if (showAddTrackDialog) {
        AddTrackDialog(
            onDismiss = { showAddTrackDialog = false },
            onConfirm = { name, filePath, intensity ->
                viewModel.addTrack(name, filePath, intensity)
                showAddTrackDialog = false
            }
        )
    }

    // Unsaved changes dialog
    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = { Text("Unsaved Changes") },
            text = { Text("You have unsaved changes. Do you want to save them before leaving?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveAllChanges()
                    onNavigateBack()
                }) {
                    Text("SAVE")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showUnsavedChangesDialog = false
                    onNavigateBack()
                }) {
                    Text("DISCARD")
                }
            },
            neutralButton = {
                TextButton(onClick = { showUnsavedChangesDialog = false }) {
                    Text("CANCEL")
                }
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
private fun SoundscapeTrackCard(
    track: com.example.rpgaudiomixer.domain.model.SoundscapeTrack,
    onIntensityChange: (IntensityLevel) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = track.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Intensity",
                    style = MaterialTheme.typography.bodyMedium
                )
                IntensitySelector(
                    selectedLevel = track.intensityLevel,
                    onLevelSelected = onIntensityChange,
                    availableLevels = IntensityLevel.entries.toSet()
                )
            }

            Column {
                Text(
                    text = "MIX Volume",
                    style = MaterialTheme.typography.bodyMedium
                )
                MixSlider(
                    value = track.mixVolume,
                    onValueChange = onVolumeChange
                )
            }
        }
    }
}

@Composable
private fun AddTrackDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, IntensityLevel) -> Unit
) {
    var trackName by remember { mutableStateOf("") }
    var selectedIntensity by remember { mutableStateOf(IntensityLevel.I) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedFileUri = uri
        // Extract filename if possible
        if (trackName.isEmpty() && uri != null) {
            trackName = uri.lastPathSegment?.substringAfterLast("/") ?: "New Track"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Invoke New Soundscape") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = trackName,
                    onValueChange = { trackName = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Intensity", style = MaterialTheme.typography.bodyMedium)
                    IntensitySelector(
                        selectedLevel = selectedIntensity,
                        onLevelSelected = { selectedIntensity = it },
                        availableLevels = IntensityLevel.entries.toSet()
                    )
                }

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
                        onConfirm(trackName, uri.toString(), selectedIntensity)
                    }
                },
                enabled = trackName.isNotBlank() && selectedFileUri != null
            ) {
                Text("ADD")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
