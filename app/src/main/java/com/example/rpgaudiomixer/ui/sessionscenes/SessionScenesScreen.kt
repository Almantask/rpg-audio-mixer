package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.common.UiState
import com.example.rpgaudiomixer.ui.scenes.SceneCard

@Composable
fun SessionScenesScreen(
    sessionId: Long,
    sessionName: String,
    onNavigateBack: () -> Unit,
    onNavigateToScene: (Long) -> Unit,
    viewModel: SessionScenesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var showImportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = sessionName,
                showBackArrow = true,
                onBack = onNavigateBack,
                onGearClick = { /* Navigate to settings */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showImportDialog = true },
                modifier = Modifier.semantics { contentDescription = "ImportScene" }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Import Scene")
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = onNavigateBack
                )
            }

            is UiState.Success -> {
                SessionScenesContent(
                    linkedScenes = state.data.linkedScenes,
                    onSceneClick = { /* Edit scene */ },
                    onPlayClick = onNavigateToScene,
                    onUnlinkScene = viewModel::unlinkScene,
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    }

    if (showImportDialog) {
        val successState = (uiState as? UiState.Success)?.data
        if (successState != null) {
            ImportScenesDialog(
                availableScenes = successState.availableScenes,
                onDismiss = { showImportDialog = false },
                onImport = { sceneIds ->
                    viewModel.linkScenes(sceneIds)
                    showImportDialog = false
                }
            )
        }
    }
}

@Composable
private fun SessionScenesContent(
    linkedScenes: List<com.example.rpgaudiomixer.domain.model.Scene>,
    onSceneClick: (Long) -> Unit,
    onPlayClick: (Long) -> Unit,
    onUnlinkScene: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (linkedScenes.isEmpty()) {
        EmptyStateView(
            title = "No Scenes",
            message = "Import scenes to use in this session",
            actionLabel = "IMPORT SCENE",
            onActionClick = { /* Trigger import dialog */ }
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(
                items = linkedScenes,
                key = { it.id }
            ) { scene ->
                SwipeToDeleteContainer(
                    onDelete = { onUnlinkScene(scene.id) }
                ) {
                    SceneCard(
                        name = scene.name,
                        description = scene.description,
                        tags = scene.tags.split(",").filter { it.isNotBlank() },
                        onCardClick = { onSceneClick(scene.id) },
                        onPlayClick = { onPlayClick(scene.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImportScenesDialog(
    availableScenes: List<com.example.rpgaudiomixer.domain.model.Scene>,
    onDismiss: () -> Unit,
    onImport: (List<Long>) -> Unit
) {
    var selectedSceneIds by remember { mutableStateOf(setOf<Long>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Scenes") },
        text = {
            if (availableScenes.isEmpty()) {
                Text("No scenes available to import. Create scenes first in the Scenes tab.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = availableScenes,
                        key = { it.id }
                    ) { scene ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics { contentDescription = "SceneImportOption_${scene.name}" },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = scene.id in selectedSceneIds,
                                onCheckedChange = { checked ->
                                    selectedSceneIds = if (checked) {
                                        selectedSceneIds + scene.id
                                    } else {
                                        selectedSceneIds - scene.id
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = scene.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (!scene.description.isNullOrBlank()) {
                                    Text(
                                        text = scene.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedSceneIds.isNotEmpty()) {
                        onImport(selectedSceneIds.toList())
                    }
                },
                enabled = selectedSceneIds.isNotEmpty()
            ) {
                Text("IMPORT")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
