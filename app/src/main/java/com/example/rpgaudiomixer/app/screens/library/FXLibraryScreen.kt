package com.example.rpgaudiomixer.app.screens.library

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.*
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.domain.model.FXTrack

@Composable
fun FXLibraryScreen(
    viewModel: FXLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showEditDialog by remember { mutableStateOf<FXTrack?>(null) }
    
    val fxPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.importFX(it, it.lastPathSegment ?: "New FX") }
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Search Bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onSearchQueryChanged(it) },
                onClear = { viewModel.onSearchQueryChanged("") }
            )

            if (uiState.isLoading) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                     CircularProgressIndicator(color = Gold)
                 }
            } else if (uiState.tracks.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.MusicOff,
                    message = "THE VAULT IS SILENT",
                    ctaText = "IMPORT NEW FX",
                    onCtaClick = { fxPickerLauncher.launch("audio/*") }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tracks, key = { it.id }) { track ->
                        FXTrackRow(
                            track = track,
                            isPlaying = uiState.previewingTrack?.id == track.id && uiState.isPlayingPreview,
                            onPlayToggle = { viewModel.togglePreview(track) },
                            onEdit = { showEditDialog = track }
                        )
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { fxPickerLauncher.launch("audio/*") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = if (uiState.previewingTrack != null) 80.dp else 16.dp, end = 16.dp),
            containerColor = Gold,
            contentColor = BlackBg,
            shape = Shapes.medium
        ) {
            Icon(Icons.Default.Add, contentDescription = "Import FX")
        }

        // MiniPlayerBar
        AnimatedVisibility(
            visible = uiState.previewingTrack != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            uiState.previewingTrack?.let { track ->
                MiniPlayerBar(
                    trackName = track.name,
                    isPlaying = uiState.isPlayingPreview,
                    onPlayPauseToggle = { viewModel.togglePreview(track) },
                    onStop = { viewModel.stopPreview() }
                )
            }
        }
    }

    // Edit Dialog
    showEditDialog?.let { track ->
        EditFXDialog(
            track = track,
            onDismiss = { showEditDialog = null },
            onConfirm = { updatedTrack ->
                viewModel.updateFX(updatedTrack)
                showEditDialog = null
            },
            onDelete = {
                viewModel.deleteFX(track.id)
                showEditDialog = null
            }
        )
    }

    // Error Dialog
    uiState.errorMessage?.let { msg ->
        ErrorDialog(
            message = msg,
            onDismiss = { /* viewModel.clearError() */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("SEARCH THE ECHOES...", color = Gold.copy(alpha = 0.5f), style = Typography.bodyMedium) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Gold) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear Search", tint = Gold)
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CardSurface,
            unfocusedContainerColor = CardSurface,
            focusedIndicatorColor = Gold,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Gold,
            unfocusedTextColor = Gold
        ),
        shape = Shapes.medium,
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFXDialog(
    track: FXTrack,
    onDismiss: () -> Unit,
    onConfirm: (FXTrack) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(track.name) }
    var tagsString by remember { mutableStateOf(track.tags.joinToString(", ")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("MODIFY ECHO", color = Gold, style = Typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("NAME", color = Gold.copy(alpha = 0.5f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BlackBg,
                        unfocusedContainerColor = BlackBg,
                        focusedTextColor = Gold,
                        unfocusedTextColor = Gold,
                        focusedIndicatorColor = Gold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                TextField(
                    value = tagsString,
                    onValueChange = { tagsString = it },
                    label = { Text("TAGS (COMMA-SEPARATED)", color = Gold.copy(alpha = 0.5f)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BlackBg,
                        unfocusedContainerColor = BlackBg,
                        focusedTextColor = Gold,
                        unfocusedTextColor = Gold,
                        focusedIndicatorColor = Gold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                val predefinedTags = listOf("Tavern", "Forest", "Combat", "City", "Dungeon", "Ocean", "Mountain", "Cave", "Desert", "Magic")
                
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(predefinedTags) { tag ->
                        val currentTags = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val isSelected = currentTags.contains(tag)
                        
                        Box(
                            modifier = Modifier
                                .clip(Shapes.small)
                                .background(if (isSelected) Gold else BlackBg)
                                .border(1.dp, Gold, Shapes.small)
                                .clickable {
                                    val tagsSet = currentTags.toMutableSet()
                                    if (isSelected) tagsSet.remove(tag) else tagsSet.add(tag)
                                    tagsString = tagsSet.joinToString(", ")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tag.uppercase(),
                                style = Typography.labelSmall,
                                color = if (isSelected) BlackBg else Gold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        track.copy(
                            name = name,
                            tags = tagsString.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        )
                    )
                }
            ) {
                Text("REFORGE", color = Gold, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onDelete) {
                    Text("BANISH", color = ErrorRed)
                }
                TextButton(onClick = onDismiss) {
                    Text("CANCEL", color = Gold.copy(alpha = 0.5f))
                }
            }
        },
        containerColor = CardSurface,
        shape = Shapes.medium
    )
}
