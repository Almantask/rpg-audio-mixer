package com.example.rpgaudiomixer.ui.soundscapes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

@Composable
fun SoundscapeCategoryComposerScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToCredits: () -> Unit = {},
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val showAddTrackDialog by viewModel.showAddTrackDialog.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            val title = when (val state = uiState) {
                is SoundscapeCategoryComposerUiState.Success -> state.category.name
                else -> "Composer"
            }
            ArcanumTopBar(
                title = title,
                showBackArrow = true,
                onBack = onNavigateBack,
                onGearClick = onNavigateToCredits
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddTrackDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("AddTrackFAB")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Invoke New Soundscape",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is SoundscapeCategoryComposerUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SoundscapeCategoryComposerUiState.Success -> {
                    if (state.tracks.isEmpty()) {
                        EmptyTracksState(
                            onAddTrack = { viewModel.showAddTrackDialog() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        TracksList(
                            tracks = state.tracks,
                            onIntensityChanged = { track, intensity ->
                                viewModel.updateTrackIntensity(track, intensity)
                            },
                            onMixVolumeChanged = { track, volume ->
                                viewModel.updateTrackMixVolume(track, volume)
                            },
                            onDeleteTrack = { viewModel.deleteTrack(it) }
                        )
                    }
                }
                is SoundscapeCategoryComposerUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showAddTrackDialog) {
        AddTrackDialog(
            onDismiss = { viewModel.hideAddTrackDialog() },
            onAdd = { name, filePath, intensity, mixVolume ->
                viewModel.addTrack(name, filePath, intensity, mixVolume)
            }
        )
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = { viewModel.clearError() }
    )
}

@Composable
private fun TracksList(
    tracks: List<SoundscapeTrack>,
    onIntensityChanged: (SoundscapeTrack, com.example.rpgaudiomixer.domain.model.IntensityLevel) -> Unit,
    onMixVolumeChanged: (SoundscapeTrack, Float) -> Unit,
    onDeleteTrack: (SoundscapeTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tracks, key = { it.id }) { track ->
            SoundscapeTrackCard(
                track = track,
                onIntensityChanged = { intensity ->
                    onIntensityChanged(track, intensity)
                },
                onMixVolumeChanged = { volume ->
                    onMixVolumeChanged(track, volume)
                },
                onDelete = { onDeleteTrack(track) }
            )
        }
    }
}

@Composable
private fun EmptyTracksState(
    onAddTrack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
            .testTag("EmptyTracksState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎼",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Tracks Yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Invoke a new soundscape to start composing",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
