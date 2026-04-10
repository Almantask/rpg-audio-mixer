package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.model.FxTrack

@Composable
fun ActiveSceneSoundboardScreen(
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (val state = uiState) {
            ActiveSceneSoundboardUiState.Loading -> Text("Loading soundboard…")
            is ActiveSceneSoundboardUiState.Error -> Text(state.message)
            is ActiveSceneSoundboardUiState.Success -> {
                Text(
                    text = "Master Volume ${(state.masterVolume * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                )
                Slider(
                    value = state.masterVolume,
                    onValueChange = viewModel::setMasterVolume,
                    valueRange = 0f..1f,
                )

                if (state.fxButtons.isEmpty()) {
                    EmptyStateView(
                        title = "No effects in this soundboard yet",
                        actionLabel = "Add New Effect",
                        onAction = { showAddDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.fxButtons, key = { fx -> fx.fxTrackId }) { fx ->
                            FxButton(
                                fx = fx,
                                onPrimaryAction = {
                                    if (fx.isPlaying) {
                                        viewModel.stopFx(fx.fxTrackId)
                                    } else {
                                        viewModel.triggerFx(fx.fxTrackId)
                                    }
                                },
                                onRetrigger = { viewModel.triggerFx(fx.fxTrackId) },
                                onRemove = { viewModel.removeFx(fx.fxTrackId) },
                            )
                        }
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showAddDialog = true },
                ) {
                    Text("Add New Effect")
                }

                if (showAddDialog) {
                    AddFxDialog(
                        tracks = state.availableFxToAdd,
                        onDismiss = { showAddDialog = false },
                        onAdd = viewModel::addFx,
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
private fun FxButton(
    fx: ActiveSceneFxUiModel,
    onPrimaryAction: () -> Unit,
    onRetrigger: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
        border = if (fx.isPlaying) BorderStroke(2.dp, ArcanumGold) else null,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = fx.name,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = if (fx.isPlaying) "Playing ×${fx.playingInstanceCount}" else "Idle",
                style = MaterialTheme.typography.bodySmall,
            )
            TextButton(onClick = onPrimaryAction) {
                Text(if (fx.isPlaying) "⏸" else "▶")
            }
            TextButton(onClick = onRetrigger) {
                Text("↻")
            }
            TextButton(onClick = onRemove) {
                Text("Remove")
            }
        }
    }
}

@Composable
private fun AddFxDialog(
    tracks: List<FxTrack>,
    onDismiss: () -> Unit,
    onAdd: (Long) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Imported Effects") },
        text = {
            if (tracks.isEmpty()) {
                Text("No additional effects are available to add.")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    tracks.forEach { track ->
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                             Column {
                                 Text(track.name, style = MaterialTheme.typography.titleMedium)
                                 Text(
                                     text = "${track.durationMs}ms · PLAYED ${track.playCount}×",
                                     style = MaterialTheme.typography.bodySmall,
                                 )
                             }
                            TextButton(onClick = { onAdd(track.id) }) {
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
