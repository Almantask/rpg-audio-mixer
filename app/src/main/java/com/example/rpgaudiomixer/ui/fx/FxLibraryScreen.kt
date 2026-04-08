package com.example.rpgaudiomixer.ui.fx

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog

/**
 * FX Library screen showing all FX tracks.
 *
 * Features:
 * - Search bar for filtering by name or tags
 * - Scrollable list of FX track cards
 * - Empty state with "Import FX" prompt
 * - FAB to import new FX track
 * - Mini player for preview (TODO)
 */
@Composable
fun FxLibraryScreen(
    onNavigateToFxEdit: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FxLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "audio_file"
            viewModel.importFxTrack(
                name = fileName.removeSuffix(".mp3").removeSuffix(".wav").removeSuffix(".ogg"),
                filePath = it.toString()
            )
        }
    }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Sound Effects",
                showBackArrow = false,
                onGearClick = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { audioPickerLauncher.launch("audio/*") },
                modifier = Modifier.testTag("FxLibrary_ImportFab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import FX")
            }
        },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.search(it) },
                label = { Text("Search by name or tags") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("FxLibrary_SearchField")
            )

            when (val state = uiState) {
                is FxLibraryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is FxLibraryUiState.Success -> {
                    if (state.fxTracks.isEmpty()) {
                        EmptyFxLibraryState(
                            onImportClick = { audioPickerLauncher.launch("audio/*") },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("FxLibrary_List"),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.fxTracks,
                                key = { it.id }
                            ) { track ->
                                FxTrackCard(
                                    track = track,
                                    onPlay = {
                                        // TODO: Implement mini player preview
                                    },
                                    onEdit = { onNavigateToFxEdit(track.id) }
                                )
                            }
                        }
                    }
                }
                is FxLibraryUiState.Error -> {
                    ErrorDialog(
                        message = state.message,
                        onDismiss = { viewModel.clearError() }
                    )
                }
            }
        }
    }
}

/**
 * Empty state shown when no FX tracks exist.
 */
@Composable
private fun EmptyFxLibraryState(
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.testTag("FxLibrary_EmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No sound effects yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Import your first FX track to begin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onImportClick,
            modifier = Modifier.testTag("FxLibrary_EmptyState_ImportButton")
        ) {
            Text("Import FX")
        }
    }
}
