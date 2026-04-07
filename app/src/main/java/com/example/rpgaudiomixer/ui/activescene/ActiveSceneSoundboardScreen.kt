package com.example.rpgaudiomixer.ui.activescene

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.MixSlider
import com.example.rpgaudiomixer.common.UiState

@Composable
internal fun ActiveSceneSoundboardScreenContent(
    uiState: UiState<ActiveSceneSoundboardUiState>,
    onMasterVolumeChange: (Float) -> Unit,
    onEffectClick: (Long) -> Unit,
    onRemoveEffect: (Long) -> Unit,
    onAddEffect: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { /* Handle error dismissal */ }
                )
            }
        }

        is UiState.Success -> {
            ActiveSceneSoundboardContent(
                state = state.data,
                onMasterVolumeChange = onMasterVolumeChange,
                onEffectClick = onEffectClick,
                onRemoveEffect = onRemoveEffect,
                onAddEffect = onAddEffect,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ActiveSceneSoundboardContent(
    state: ActiveSceneSoundboardUiState,
    onMasterVolumeChange: (Float) -> Unit,
    onEffectClick: (Long) -> Unit,
    onRemoveEffect: (Long) -> Unit,
    onAddEffect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Master Volume Slider
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "MasterVolumeCard" },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Master Volume",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                MixSlider(
                    label = "VOLUME",
                    value = state.masterVolume,
                    onValueChange = onMasterVolumeChange
                )
            }
        }

        // Effect Buttons Grid
        if (state.effects.isEmpty()) {
            EmptyStateView(
                title = "No Effects",
                message = "Add sound effects to your soundboard",
                actionLabel = "ADD EFFECT",
                onActionClick = onAddEffect
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = state.effects,
                    key = { it.fx.id }
                ) { effectState ->
                    EffectButton(
                        name = effectState.fx.name,
                        isPlaying = effectState.isPlaying,
                        onClick = { onEffectClick(effectState.fx.id) }
                    )
                }

                // Add Effect Button
                item {
                    OutlinedButton(
                        onClick = onAddEffect,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .semantics { contentDescription = "AddEffect" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Effect",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EffectButton(
    name: String,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .semantics { contentDescription = "EffectButton_$name" },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPlaying) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2
        )
    }
}
