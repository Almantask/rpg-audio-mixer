package com.example.rpgaudiomixer.ui.fx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SearchBar
import com.example.rpgaudiomixer.domain.model.FxTrack

@Composable
fun FxLibraryContent(
    modifier: Modifier = Modifier,
    viewModel: FxLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showImportDialog by remember { mutableStateOf(false) }
    var trackToEdit by remember { mutableStateOf<FxTrack?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                placeholder = "Search FX by name or tags..."
            )

            when (val state = uiState) {
                is FxLibraryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }

                is FxLibraryUiState.Success -> {
                    if (state.tracks.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (searchQuery.isBlank()) {
                                        "No FX tracks imported yet"
                                    } else {
                                        "No FX tracks match your search"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (searchQuery.isBlank()) {
                                    Text(
                                        text = "Tap + to import your first sound effect",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.tracks, key = { it.id }) { track ->
                                FxTrackRow(
                                    track = track,
                                    onEditClick = { trackToEdit = track }
                                )
                            }
                        }
                    }
                }

                is FxLibraryUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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

        FloatingActionButton(
            onClick = { showImportDialog = true },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Import FX",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    if (showImportDialog) {
        ImportFxDialog(
            onDismiss = { showImportDialog = false },
            onImport = { name, filePath, tags ->
                viewModel.importFxTrack(name, filePath, tags)
            }
        )
    }

    trackToEdit?.let { track ->
        EditFxDialog(
            track = track,
            onDismiss = { trackToEdit = null },
            onSave = { name, tags ->
                viewModel.updateFxTrack(track.id, name, tags)
            },
            onDelete = {
                viewModel.deleteFxTrack(track.id)
            }
        )
    }

    errorMessage?.let { message ->
        ErrorDialog(
            message = message,
            onDismiss = viewModel::clearError
        )
    }
}
