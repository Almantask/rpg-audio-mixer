package com.example.rpgaudiomixer.app.screens.library

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumEmptyState
import com.example.rpgaudiomixer.app.domain.model.AudioTrack

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val displayName = resolveDisplayName(context, uri)
        viewModel.addFile(uri, displayName)
    }

    Scaffold(
        modifier = Modifier.testTag("libraryScreen"),
        floatingActionButton = {
            if (uiState is LibraryUiState.Content) {
                FloatingActionButton(
                    onClick = { filePickerLauncher.launch(arrayOf("audio/*")) },
                    modifier = Modifier.testTag("importFab"),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Import Sound")
                }
            }
        },
    ) { padding ->
        when (val state = uiState) {
            is LibraryUiState.Empty -> {
                ArcanumEmptyState(
                    icon = Icons.Default.MusicNote,
                    title = "Your Library is Empty",
                    ctaText = "Import Sounds",
                    onCtaClick = { filePickerLauncher.launch(arrayOf("audio/*")) },
                    modifier = Modifier.padding(padding),
                )
            }

            is LibraryUiState.Content -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(
                        items = state.tracks,
                        key = { _, item -> item.uri },
                    ) { index, item ->
                        AudioTrackCard(
                            track = item,
                            isPlaying = item.uri == state.playingUri,
                            onPlayToggle = { viewModel.playPreview(Uri.parse(item.uri)) },
                            modifier = Modifier.testTag("audioFileCard_$index"),
                            playButtonTestTag = "playButton_$index",
                        )
                    }
                }
            }
        }
    }
}

// ── Internal composables ────────────────────────────────────────

@Composable
private fun AudioTrackCard(
    track: AudioTrack,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    modifier: Modifier = Modifier,
    playButtonTestTag: String = "",
) {
    val containerColor = if (isPlaying) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        CardDefaults.cardColors().containerColor
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = track.displayName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )

            IconButton(
                onClick = onPlayToggle,
                modifier = Modifier.testTag(playButtonTestTag),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                )
            }
        }
    }
}

// ── Helpers ─────────────────────────────────────────────────────

private fun resolveDisplayName(context: android.content.Context, uri: Uri): String {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex >= 0 && cursor.moveToFirst()) {
            return cursor.getString(nameIndex)
        }
    }
    return uri.lastPathSegment ?: "Unknown"
}
