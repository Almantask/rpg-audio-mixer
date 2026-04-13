package com.example.rpgaudiomixer.app.screens.sessionscenes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumEmptyState
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.domain.model.Scene

@Suppress("kotlin:S6615")
@Composable
fun SessionScenesScreen(
    onNavigateToActiveScene: (Long, Boolean) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToCredits: () -> Unit,
    viewModel: SessionScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allScenes by viewModel.allScenes.collectAsStateWithLifecycle()
    var showImportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Session Scenes",
                onGearClick = onNavigateToCredits,
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showImportDialog = true },
                modifier = Modifier.testTag("importSceneFab"),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import Scene")
            }
        },
    ) { padding ->
        SessionScenesContent(
            uiState = uiState,
            onPlayClick = { sceneId -> onNavigateToActiveScene(sceneId, true) },
            onUnlink = { sceneId -> viewModel.unlinkScene(sceneId) },
            onImportClick = { showImportDialog = true },
            modifier = Modifier.padding(padding),
        )
    }

    if (showImportDialog) {
        val linkedSceneIds = (uiState as? SessionScenesUiState.Success)
            ?.scenes?.map { it.id }?.toSet() ?: emptySet()
        val availableScenes = allScenes.filter { it.id !in linkedSceneIds }

        ImportSceneDialog(
            availableScenes = availableScenes,
            onDismiss = { showImportDialog = false },
            onConfirm = { selectedIds ->
                viewModel.linkScenes(selectedIds)
                showImportDialog = false
            },
        )
    }
}

@Composable
private fun SessionScenesContent(
    uiState: SessionScenesUiState,
    onPlayClick: (Long) -> Unit,
    onUnlink: (Long) -> Unit,
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is SessionScenesUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is SessionScenesUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is SessionScenesUiState.Success -> {
                if (uiState.scenes.isEmpty()) {
                    ArcanumEmptyState(
                        icon = Icons.Default.MusicNote,
                        title = "No Scenes Linked",
                        ctaText = "+ IMPORT SCENE",
                        onCtaClick = onImportClick,
                    )
                } else {
                    SessionSceneList(
                        scenes = uiState.scenes,
                        onPlayClick = onPlayClick,
                        onUnlink = onUnlink,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionSceneList(
    scenes: List<Scene>,
    onPlayClick: (Long) -> Unit,
    onUnlink: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sessionSceneList"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(scenes, key = { it.id }) { scene ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.StartToEnd) {
                        onUnlink(scene.id)
                        true
                    } else {
                        false
                    }
                },
            )
            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = { SceneSwipeUnlinkBackground() },
                enableDismissFromStartToEnd = true,
                enableDismissFromEndToStart = false,
            ) {
                SessionSceneCard(
                    scene = scene,
                    onPlayClick = { onPlayClick(scene.id) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SessionSceneCard(
    scene: Scene,
    onPlayClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("SessionSceneCard"),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scene.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (!scene.tags.isNullOrBlank()) {
                    FlowRow(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        scene.tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            .forEach { tag ->
                                AssistChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    },
                                )
                            }
                    }
                }
            }
            IconButton(
                onClick = onPlayClick,
                modifier = Modifier.testTag("playSceneButton"),
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Scene",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun SceneSwipeUnlinkBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Unlink",
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun ImportSceneDialog(
    availableScenes: List<Scene>,
    onDismiss: () -> Unit,
    onConfirm: (List<Long>) -> Unit,
) {
    var selectedIds by remember { mutableStateOf(setOf<Long>()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Scenes") },
        text = {
            if (availableScenes.isEmpty()) {
                Text("No scenes available to import.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .testTag("importSceneList"),
                ) {
                    items(availableScenes, key = { it.id }) { scene ->
                        val isChecked = scene.id in selectedIds
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIds = if (isChecked) {
                                        selectedIds - scene.id
                                    } else {
                                        selectedIds + scene.id
                                    }
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    selectedIds = if (checked) {
                                        selectedIds + scene.id
                                    } else {
                                        selectedIds - scene.id
                                    }
                                },
                            )
                            Text(
                                text = scene.name,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedIds.toList()) },
                enabled = selectedIds.isNotEmpty(),
                modifier = Modifier.testTag("confirmImportButton"),
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
