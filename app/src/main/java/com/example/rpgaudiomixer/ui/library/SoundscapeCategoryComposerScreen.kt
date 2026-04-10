package com.example.rpgaudiomixer.ui.library

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

@Composable
fun SoundscapeCategoryComposerScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "Imported Soundscape"
            viewModel.addImportedTrack(name = fileName, filePath = uri.toString())
        }
    }

    BackHandler(onBack = viewModel::onBackRequested)

    LaunchedEffect(viewModel) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                SoundscapeCategoryComposerNavigation.NavigateBack -> onNavigateBack()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (val state = uiState) {
            SoundscapeCategoryComposerUiState.Loading -> Text("Loading composer…")
            is SoundscapeCategoryComposerUiState.Error -> ErrorDialog(message = state.message, onDismiss = { })
            is SoundscapeCategoryComposerUiState.Success -> {
                Text(
                    text = state.categoryName,
                    style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                )
                if (state.tracks.isEmpty()) {
                    Text("No soundscapes yet. Invoke a new soundscape to begin.")
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.tracks, key = { track -> track.id }) { track ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.removeTrack(track.id) },
                            ) {
                                SoundscapeTrackCard(
                                    track = track,
                                    onIntensityChanged = { intensity ->
                                        viewModel.updateTrackIntensity(track.id, intensity)
                                    },
                                    onMixChanged = { mix ->
                                        viewModel.updateTrackMix(track.id, mix)
                                    },
                                )
                            }
                        }
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { audioPickerLauncher.launch(arrayOf("audio/*")) },
                ) {
                    Text("Invoke New Soundscape")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = viewModel::saveComposition,
                ) {
                    Text("Save Composition")
                }

                if (state.showDiscardChangesDialog) {
                    AlertDialog(
                        onDismissRequest = viewModel::dismissDiscardDialog,
                        title = { Text("Discard changes?") },
                        text = { Text("You have unsaved changes in this composer.") },
                        confirmButton = {
                            TextButton(onClick = viewModel::discardChangesAndNavigateBack) {
                                Text("Discard")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = viewModel::dismissDiscardDialog) {
                                Text("Keep Editing")
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SoundscapeTrackCard(
    track: SoundscapeTrack,
    onIntensityChanged: (IntensityLevel) -> Unit,
    onMixChanged: (Int) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = track.name,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Intensity: ${track.intensityLevel.label}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IntensityLevel.entries.forEach { intensity ->
                        Row {
                            RadioButton(
                                selected = track.intensityLevel == intensity,
                                onClick = { onIntensityChanged(intensity) },
                            )
                            Text(
                                text = intensity.label,
                                modifier = Modifier.padding(top = 12.dp),
                            )
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("MIX ${track.mixVolumePercent}%")
                Slider(
                    value = track.mixVolumePercent.toFloat(),
                    onValueChange = { value -> onMixChanged(value.toInt()) },
                    valueRange = 0f..100f,
                )
            }
            Text(
                text = track.filePath,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
            )
        }
    }
}
