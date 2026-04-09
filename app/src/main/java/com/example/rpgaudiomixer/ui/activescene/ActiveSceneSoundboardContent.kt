package com.example.rpgaudiomixer.ui.activescene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.MasterSlider

/**
 * Soundboard tab content for the Active Scene screen.
 *
 * Displays a 4-column grid of FX buttons with:
 * - Master volume slider
 * - FX grid (4 columns)
 * - Play/pause/re-trigger support
 * - Glow effect on playing buttons
 *
 * Note: Full drag-to-reorder UI requires a third-party library like `sh.calvin.reorderable`.
 * The backend reordering functionality exists via viewModel.reorderFx().
 * For now, manual reordering is available through edit dialogs.
 */
@Composable
fun ActiveSceneSoundboardContent(
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ActiveSceneSoundboardUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is ActiveSceneSoundboardUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Master volume slider
                MasterSlider(
                    label = "Master Volume",
                    value = state.masterVolume,
                    onValueChange = viewModel::setMasterVolume
                )

                // FX grid
                if (state.fxTracks.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No effects in this soundboard",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap + to add effects",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.fxTracks, key = { it.fxTrackId }) { fx ->
                            FxButton(
                                fx = fx,
                                onTriggerClick = {
                                    viewModel.triggerFx(fx.fxTrackId)
                                },
                                onStopClick = {
                                    viewModel.stopFx(fx.fxTrackId)
                                }
                            )
                        }
                    }
                }
            }
        }

        is ActiveSceneSoundboardUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
