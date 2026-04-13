package com.example.rpgaudiomixer.app.screens.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.material3.TextField
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
fun ScenesScreen(
    onNavigateToActiveScene: (Long, Boolean) -> Unit,
    onNavigateToCredits: () -> Unit,
    viewModel: ScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Scenes",
                onGearClick = onNavigateToCredits,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.testTag("addSceneFab"),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Scene")
            }
        },
    ) { padding ->
        ScenesContent(
            uiState = uiState,
            onPlayClick = { sceneId -> onNavigateToActiveScene(sceneId, true) },
            onCloneClick = { sceneId -> viewModel.cloneScene(sceneId) },
            onDelete = { sceneId -> viewModel.deleteScene(sceneId) },
            onAddClick = { showCreateDialog = true },
            modifier = Modifier.padding(padding),
        )
    }

    if (showCreateDialog) {
        CreateSceneDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, description ->
                viewModel.createScene(name, description)
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun ScenesContent(
    uiState: ScenesUiState,
    onPlayClick: (Long) -> Unit,
    onCloneClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is ScenesUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is ScenesUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            is ScenesUiState.Success -> {
                if (uiState.scenes.isEmpty()) {
                    ArcanumEmptyState(
                        icon = Icons.Default.MusicNote,
                        title = "No Scenes Yet",
                        ctaText = "+ ADD NEW SCENE",
                        onCtaClick = onAddClick,
                    )
                } else {
                    SceneList(
                        scenes = uiState.scenes,
                        onPlayClick = onPlayClick,
                        onCloneClick = onCloneClick,
                        onDelete = onDelete,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SceneList(
    scenes: List<Scene>,
    onPlayClick: (Long) -> Unit,
    onCloneClick: (Long) -> Unit,
    onDelete: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("sceneList"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(scenes, key = { it.id }) { scene ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { value ->
                    if (value == SwipeToDismissBoxValue.StartToEnd) {
                        onDelete(scene.id)
                        true
                    } else {
                        false
                    }
                },
            )
            SwipeToDismissBox(
                state = dismissState,
                backgroundContent = { SceneSwipeDeleteBackground() },
                enableDismissFromStartToEnd = true,
                enableDismissFromEndToStart = false,
            ) {
                SceneCard(
                    scene = scene,
                    onPlayClick = { onPlayClick(scene.id) },
                    onCloneClick = { onCloneClick(scene.id) },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SceneCard(
    scene: Scene,
    onPlayClick: () -> Unit,
    onCloneClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("SceneCard"),
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
                onClick = onCloneClick,
                modifier = Modifier.testTag("cloneSceneButton"),
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Clone Scene",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
private fun SceneSwipeDeleteBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Scene") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Scene Name") },
                    modifier = Modifier.testTag("sceneNameInput"),
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    modifier = Modifier.testTag("sceneDescriptionInput"),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(name, description.ifBlank { null })
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("createSceneButton"),
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
