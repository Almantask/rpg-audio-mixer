package com.example.rpgaudiomixer.ui.soundscapecomposer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

@Composable
fun SoundscapeCategoryComposerScreen(
    categoryId: String,
    onBackClick: () -> Unit,
    viewModelFactory: SoundscapeCategoryComposerViewModel.Factory
) {
    val viewModel: SoundscapeCategoryComposerViewModel = viewModelFactory.create(categoryId)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showImportDialog by viewModel.showImportDialog.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (uiState is SoundscapeComposerUiState.Success) {
                FloatingActionButton(
                    onClick = { viewModel.showImportDialog() },
                    modifier = Modifier.testTag("ComposerScreen_FAB"),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Invoke New Soundscape")
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiState) {
                is SoundscapeComposerUiState.Loading -> {
                    LoadingContent()
                }
                is SoundscapeComposerUiState.Success -> {
                    val state = uiState as SoundscapeComposerUiState.Success
                    ComposerContent(
                        categoryName = state.category.name,
                        tracks = state.tracks,
                        onUpdateTrack = { viewModel.updateTrack(it) },
                        onDeleteTrack = { viewModel.deleteTrack(it) }
                    )
                }
                is SoundscapeComposerUiState.Error -> {
                    ErrorContent(message = (uiState as SoundscapeComposerUiState.Error).message)
                }
            }
        }
    }

    if (showImportDialog) {
        ImportTrackDialog(
            onDismiss = { viewModel.hideImportDialog() },
            onConfirm = { name, filePath, intensity, mixVolume ->
                viewModel.createTrack(name, filePath, intensity, mixVolume)
            }
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.testTag("Composer_Loading"))
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.testTag("Composer_ErrorTitle")
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("Composer_ErrorMessage")
        )
    }
}

@Composable
private fun ComposerContent(
    categoryName: String,
    tracks: List<SoundscapeTrack>,
    onUpdateTrack: (SoundscapeTrack) -> Unit,
    onDeleteTrack: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Text(
            text = categoryName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(16.dp)
                .testTag("Composer_CategoryTitle")
        )

        if (tracks.isEmpty()) {
            EmptyTracksContent()
        } else {
            TracksList(
                tracks = tracks,
                onUpdateTrack = onUpdateTrack,
                onDeleteTrack = onDeleteTrack
            )
        }
    }
}

@Composable
private fun EmptyTracksContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No tracks yet. Tap + to add a soundscape.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.testTag("Composer_EmptyState")
        )
    }
}

@Composable
private fun TracksList(
    tracks: List<SoundscapeTrack>,
    onUpdateTrack: (SoundscapeTrack) -> Unit,
    onDeleteTrack: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("Composer_TracksList"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tracks) { track ->
            TrackCard(
                track = track,
                onUpdateTrack = onUpdateTrack,
                onDeleteTrack = onDeleteTrack
            )
        }
    }
}

@Composable
private fun TrackCard(
    track: SoundscapeTrack,
    onUpdateTrack: (SoundscapeTrack) -> Unit,
    onDeleteTrack: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("TrackCard_${track.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = { onDeleteTrack(track.id) },
                    modifier = Modifier.testTag("TrackCard_${track.id}_DeleteButton")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Intensity selector
            Text("Intensity:", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IntensityLevel.entries.forEach { level ->
                    FilterChip(
                        selected = track.intensityLevel == level,
                        onClick = {
                            onUpdateTrack(track.copy(intensityLevel = level))
                        },
                        label = { Text(level.displayName) },
                        modifier = Modifier.testTag("TrackCard_${track.id}_Intensity_${level.displayName}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mix volume slider
            Text("MIX: ${(track.mixVolume * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
            Slider(
                value = track.mixVolume,
                onValueChange = { newVolume ->
                    onUpdateTrack(track.copy(mixVolume = newVolume))
                },
                valueRange = 0f..1f,
                modifier = Modifier.testTag("TrackCard_${track.id}_MixSlider")
            )
        }
    }
}

@Composable
private fun ImportTrackDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, IntensityLevel, Float) -> Unit
) {
    var trackName by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("/placeholder/path.mp3") }
    var selectedIntensity by remember { mutableStateOf(IntensityLevel.I) }
    var mixVolume by remember { mutableFloatStateOf(1.0f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Invoke New Soundscape") },
        text = {
            Column {
                OutlinedTextField(
                    value = trackName,
                    onValueChange = { trackName = it },
                    label = { Text("Track Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ImportTrackDialog_NameField"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Intensity:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IntensityLevel.entries.forEach { level ->
                        FilterChip(
                            selected = selectedIntensity == level,
                            onClick = { selectedIntensity = level },
                            label = { Text(level.displayName) },
                            modifier = Modifier.testTag("ImportTrackDialog_Intensity_${level.displayName}")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (trackName.isNotBlank()) {
                        onConfirm(trackName.trim(), filePath, selectedIntensity, mixVolume)
                    }
                },
                enabled = trackName.isNotBlank(),
                modifier = Modifier.testTag("ImportTrackDialog_ConfirmButton")
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("ImportTrackDialog_CancelButton")
            ) {
                Text("Cancel")
            }
        }
    )
}
