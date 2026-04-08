package com.example.rpgaudiomixer.ui.soundscapes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.IntensityLevel

/**
 * Soundscape Category Composer screen for managing tracks within a category.
 *
 * Features:
 * - List of tracks with intensity selectors and MIX sliders
 * - Empty state with "Invoke New Soundscape" prompt
 * - FAB to add new track via audio file picker
 * - Swipe-to-delete tracks
 * - Update track intensity and volume
 */
@Composable
fun SoundscapeCategoryComposerScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "audio_file"
            viewModel.addTrack(
                name = fileName,
                filePath = it.toString(),
                intensityLevel = IntensityLevel.I
            )
        }
    }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = when (val state = uiState) {
                    is SoundscapeCategoryComposerUiState.Success -> state.category.name
                    else -> "Category Composer"
                },
                showBackArrow = true,
                onBackClick = onNavigateBack,
                onGearClick = null
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { audioPickerLauncher.launch("audio/*") },
                modifier = Modifier.testTag("CategoryComposer_AddTrackFab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Track")
            }
        },
        modifier = modifier
    ) { padding ->
        when (val state = uiState) {
            is SoundscapeCategoryComposerUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is SoundscapeCategoryComposerUiState.Success -> {
                if (state.tracks.isEmpty()) {
                    EmptyCategoryComposerState(
                        onAddClick = { audioPickerLauncher.launch("audio/*") },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .testTag("CategoryComposer_TrackList"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = state.tracks,
                            key = { it.id }
                        ) { track ->
                            SoundscapeTrackCard(
                                track = track,
                                onIntensityChange = { newIntensity ->
                                    viewModel.updateTrackIntensity(track, newIntensity)
                                },
                                onVolumeChange = { newVolume ->
                                    viewModel.updateTrackVolume(track, newVolume)
                                },
                                onDelete = { viewModel.deleteTrack(track.id) }
                            )
                        }
                    }
                }
            }
            is SoundscapeCategoryComposerUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { viewModel.clearError() }
                )
            }
        }
    }
}

/**
 * Empty state shown when no tracks exist in the category.
 */
@Composable
private fun EmptyCategoryComposerState(
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.testTag("CategoryComposer_EmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No soundscapes yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Invoke your first soundscape to begin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            modifier = Modifier.testTag("CategoryComposer_EmptyState_AddButton")
        ) {
            Text("Invoke New Soundscape")
        }
    }
}
