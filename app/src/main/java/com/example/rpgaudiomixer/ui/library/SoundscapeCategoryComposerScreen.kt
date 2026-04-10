package com.example.rpgaudiomixer.ui.library

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.ui.components.IntensitySelector
import com.example.rpgaudiomixer.ui.components.MixSlider

@Composable
fun SoundscapeCategoryComposerRoute(
    onNavigateBack: () -> Unit,
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddTrackDialog by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }

    SoundscapeCategoryComposerScreen(
        uiState = uiState,
        onNavigateBack = {
            if (uiState.hasUnsavedChanges) {
                showUnsavedChangesDialog = true
            } else {
                onNavigateBack()
            }
        },
        onAddTrack = { showAddTrackDialog = true },
        onDeleteTrack = viewModel::deleteTrack,
        onUpdateIntensity = viewModel::updateTrackIntensity,
        onUpdateMixVolume = viewModel::updateTrackMixVolume,
        onSaveChanges = {
            viewModel.saveChanges()
            onNavigateBack()
        },
        onErrorDismiss = viewModel::clearError
    )

    if (showAddTrackDialog) {
        AddTrackDialog(
            onDismiss = { showAddTrackDialog = false },
            onConfirm = { name, uri, intensity, mix ->
                viewModel.addTrack(name, uri, intensity, mix)
                showAddTrackDialog = false
            }
        )
    }

    if (showUnsavedChangesDialog) {
        UnsavedChangesDialog(
            onDismiss = { showUnsavedChangesDialog = false },
            onDiscard = onNavigateBack,
            onSave = {
                viewModel.saveChanges()
                onNavigateBack()
            }
        )
    }
}

@Composable
private fun SoundscapeCategoryComposerScreen(
    uiState: ComposerUiState,
    onNavigateBack: () -> Unit,
    onAddTrack: () -> Unit,
    onDeleteTrack: (Long) -> Unit,
    onUpdateIntensity: (Long, IntensityLevel) -> Unit,
    onUpdateMixVolume: (Long, Float) -> Unit,
    onSaveChanges: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = uiState.category?.name ?: "Composer",
                showBackArrow = true,
                onBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTrack) {
                Icon(Icons.Default.Add, contentDescription = "Invoke New Soundscape")
            }
        },
        bottomBar = {
            if (uiState.hasUnsavedChanges) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = onSaveChanges) {
                        Text("SAVE COMPOSITION")
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.tracks.isEmpty() -> {
                EmptyComposerState(
                    onAddTrack = onAddTrack,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tracks) { track ->
                        SoundscapeTrackCard(
                            track = track,
                            onDeleteClick = { onDeleteTrack(track.id) },
                            onIntensityChange = { intensity ->
                                onUpdateIntensity(track.id, intensity)
                            },
                            onMixVolumeChange = { volume ->
                                onUpdateMixVolume(track.id, volume)
                            }
                        )
                    }
                }
            }
        }

        if (uiState.errorMessage != null) {
            ErrorDialog(
                message = uiState.errorMessage,
                onDismiss = onErrorDismiss
            )
        }
    }
}

@Composable
private fun SoundscapeTrackCard(
    track: SoundscapeTrack,
    onDeleteClick: () -> Unit,
    onIntensityChange: (IntensityLevel) -> Unit,
    onMixVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            IntensitySelector(
                selectedLevel = track.intensityLevel,
                availableLevels = IntensityLevel.entries.toSet(),
                onLevelSelected = onIntensityChange,
                modifier = Modifier.padding(top = 12.dp)
            )

            MixSlider(
                label = "MIX",
                value = track.mixVolume,
                onValueChange = onMixVolumeChange,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun EmptyComposerState(
    onAddTrack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No soundscape tracks yet",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            TextButton(
                onClick = onAddTrack,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("INVOKE NEW SOUNDSCAPE")
            }
        }
    }
}

@Composable
private fun AddTrackDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Uri, IntensityLevel, Float) -> Unit
) {
    var trackName by remember { mutableStateOf("") }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedIntensity by remember { mutableStateOf(IntensityLevel.I) }
    var mixVolume by remember { mutableFloatStateOf(1.0f) }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { selectedUri = it }
    }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Soundscape Track") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    value = trackName,
                    onValueChange = { trackName = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = { audioPickerLauncher.launch(arrayOf("audio/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedUri == null) "SELECT AUDIO FILE" else "Audio file selected")
                }

                IntensitySelector(
                    selectedLevel = selectedIntensity,
                    availableLevels = IntensityLevel.entries.toSet(),
                    onLevelSelected = { selectedIntensity = it }
                )

                MixSlider(
                    label = "MIX",
                    value = mixVolume,
                    onValueChange = { mixVolume = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedUri?.let { uri ->
                        onConfirm(trackName, uri, selectedIntensity, mixVolume)
                    }
                },
                enabled = trackName.isNotBlank() && selectedUri != null
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

@Composable
private fun UnsavedChangesDialog(
    onDismiss: () -> Unit,
    onDiscard: () -> Unit,
    onSave: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Unsaved Changes") },
        text = { Text("You have unsaved changes. What would you like to do?") },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("SAVE")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDiscard) {
                    Text("DISCARD")
                }
                TextButton(onClick = onDismiss) {
                    Text("CANCEL")
                }
            }
        }
    )
}
