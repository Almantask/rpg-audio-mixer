package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory

@Composable
fun ActiveSceneSoundscapesScreen(
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundscapesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (val state = uiState) {
            ActiveSceneSoundscapesUiState.Loading -> Text("Loading soundscapes…")
            is ActiveSceneSoundscapesUiState.Error -> Text(state.message)
            is ActiveSceneSoundscapesUiState.Success -> {
                Text(
                    text = "Master Atmosphere ${(state.masterVolume * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                )
                Slider(
                    value = state.masterVolume,
                    onValueChange = viewModel::setMasterVolume,
                    valueRange = 0f..1f,
                )

                if (state.soundscapes.isEmpty()) {
                    EmptyStateView(
                        title = "No soundscapes in this scene yet",
                        actionLabel = "Add New Soundscape",
                        onAction = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.soundscapes, key = { soundscape -> soundscape.categoryId }) { soundscape ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.removeCategory(soundscape.categoryId) },
                            ) {
                                SoundscapeCategoryCard(
                                    soundscape = soundscape,
                                    onPlayPause = {
                                        if (soundscape.isPlaying) {
                                            viewModel.pauseCategory(soundscape.categoryId)
                                        } else {
                                            viewModel.playCategory(soundscape.categoryId)
                                        }
                                    },
                                    onRoll = { viewModel.playCategory(soundscape.categoryId) },
                                    onMixChanged = { viewModel.setMix(soundscape.categoryId, it) },
                                    onIntensitySelected = { level ->
                                        viewModel.setIntensity(soundscape.categoryId, level)
                                    },
                                )
                            }
                        }
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showAddDialog = true },
                ) {
                    Text("+ Add New Soundscape")
                }

                if (showAddDialog) {
                    AddSoundscapeDialog(
                        categories = state.availableCategoriesToAdd,
                        onDismiss = { showAddDialog = false },
                        onAdd = { categoryId ->
                            viewModel.addCategory(categoryId)
                        },
                    )
                }
            }
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = viewModel::dismissError,
    )
}

@Composable
private fun SoundscapeCategoryCard(
    soundscape: ActiveSceneSoundscapeUiModel,
    onPlayPause: () -> Unit,
    onRoll: () -> Unit,
    onMixChanged: (Float) -> Unit,
    onIntensitySelected: (IntensityLevel) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
        border = if (soundscape.isPlaying) {
            BorderStroke(width = 2.dp, color = ArcanumGold)
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    soundscape.themeLabel?.let { label ->
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = ArcanumGold,
                        )
                    }
                    Text(
                        text = soundscape.categoryName,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onRoll) {
                        Text("🎲")
                    }
                    TextButton(onClick = onPlayPause) {
                        Text(if (soundscape.isPlaying) "⏸" else "▶")
                    }
                }
            }
            Text(
                text = soundscape.currentTrackName ?: "No track selected",
                style = MaterialTheme.typography.bodyMedium,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("MIX ${(soundscape.mixVolume * 100).toInt()}%")
                Slider(
                    value = soundscape.mixVolume,
                    onValueChange = onMixChanged,
                    valueRange = 0f..1f,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IntensityLevel.entries.forEach { intensity ->
                    FilterChip(
                        selected = soundscape.selectedIntensity == intensity,
                        onClick = { onIntensitySelected(intensity) },
                        enabled = intensity in soundscape.availableIntensityLevels,
                        label = { Text(intensity.label) },
                    )
                }
            }
        }
    }
}

@Composable
private fun AddSoundscapeDialog(
    categories: List<SoundscapeCategory>,
    onDismiss: () -> Unit,
    onAdd: (Long) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Imported Soundscapes") },
        text = {
            if (categories.isEmpty()) {
                Text("No additional soundscapes are available to add.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(category.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "I: ${category.levelOneCount} · II: ${category.levelTwoCount} · III: ${category.levelThreeCount}",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            TextButton(onClick = { onAdd(category.id) }) {
                                Text("+")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        },
    )
}
