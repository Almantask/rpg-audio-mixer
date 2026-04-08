package com.example.rpgaudiomixer.ui.fxlibrary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.domain.model.FxTrack

@Composable
fun FxLibraryScreen(
    viewModel: FxLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showImportDialog by viewModel.showImportDialog.collectAsStateWithLifecycle()
    val showEditDialog by viewModel.showEditDialog.collectAsStateWithLifecycle()
    val editingTrack by viewModel.editingTrack.collectAsStateWithLifecycle()
    val isDownloadingDemo by viewModel.isDownloadingDemo.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            if (uiState is FxLibraryUiState.Success) {
                FloatingActionButton(
                    onClick = { viewModel.showImportDialog() },
                    modifier = Modifier.testTag("FxLibraryScreen_FAB"),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Import FX")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search bar
            if (uiState is FxLibraryUiState.Success) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        if (it.isEmpty()) {
                            viewModel.clearSearch()
                        } else {
                            viewModel.searchFxTracks(it)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    is FxLibraryUiState.Loading -> {
                        LoadingContent()
                    }
                    is FxLibraryUiState.Success -> {
                        val tracks = (uiState as FxLibraryUiState.Success).tracks
                        if (tracks.isEmpty() && searchQuery.isEmpty()) {
                            EmptyStateContent(
                                onImportClick = { viewModel.showImportDialog() },
                                onGetDemoClick = { viewModel.downloadDemoFxTracks() },
                                isDownloadingDemo = isDownloadingDemo
                            )
                        } else {
                            FxTracksList(
                                tracks = tracks,
                                onEditClick = { viewModel.showEditDialog(it) }
                            )
                        }
                    }
                    is FxLibraryUiState.Error -> {
                        ErrorContent(message = (uiState as FxLibraryUiState.Error).message)
                    }
                }
            }
        }
    }

    if (showImportDialog) {
        ImportFxDialog(
            onDismiss = { viewModel.hideImportDialog() },
            onConfirm = { name, filePath, tags ->
                viewModel.importFxTrack(name, filePath, tags)
            }
        )
    }

    if (showEditDialog && editingTrack != null) {
        EditFxDialog(
            track = editingTrack!!,
            onDismiss = { viewModel.hideEditDialog() },
            onConfirm = { viewModel.updateFxTrack(it) },
            onDelete = { viewModel.deleteFxTrack(it) }
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.testTag("FxLibraryScreen_SearchBar"),
        placeholder = { Text("Search FX tracks...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("FxLibraryScreen_Loading"),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun EmptyStateContent(
    onImportClick: () -> Unit,
    onGetDemoClick: () -> Unit,
    isDownloadingDemo: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("FxLibraryScreen_EmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No FX Tracks Yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Import sound effects to build your audio library",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onImportClick,
            modifier = Modifier.testTag("FxLibraryScreen_ImportFxButton"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Import FX")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isDownloadingDemo) {
            CircularProgressIndicator(
                modifier = Modifier.testTag("FxLibraryScreen_DemoDownloadProgress")
            )
        } else {
            OutlinedButton(
                onClick = onGetDemoClick,
                modifier = Modifier.testTag("FxLibraryScreen_GetDemoFxButton")
            ) {
                Text("Get Demo FX")
            }
        }
    }
}

@Composable
private fun FxTracksList(
    tracks: List<FxTrack>,
    onEditClick: (FxTrack) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("FxLibraryScreen_List"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tracks, key = { it.id }) { track ->
            FxTrackCard(
                track = track,
                onEditClick = { onEditClick(track) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FxTrackCard(
    track: FxTrack,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("FxTrackCard_${track.name}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("FxTrackCard_${track.name}_Name")
                )
                if (track.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        track.tags.take(3).forEach { tag ->
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.testTag("FxTrackCard_${track.name}_Tag_$tag")
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.testTag("FxTrackCard_${track.name}_EditButton")
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("FxLibraryScreen_Error"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
