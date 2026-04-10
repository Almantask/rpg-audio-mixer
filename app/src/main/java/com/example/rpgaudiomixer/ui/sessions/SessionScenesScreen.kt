package com.example.rpgaudiomixer.ui.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun SessionScenesScreen(
    onOpenScene: (Scene, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showImportDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (val state = uiState) {
            SessionScenesUiState.Loading -> Text("Loading session scenes…")
            is SessionScenesUiState.Error -> ErrorDialog(message = state.message, onDismiss = { })
            is SessionScenesUiState.Success -> {
                Text(
                    text = state.session?.name ?: "Session Scenes",
                    style = MaterialTheme.typography.headlineMedium,
                )
                if (state.linkedScenes.isEmpty()) {
                    EmptyStateView(
                        title = "No scenes linked yet",
                        actionLabel = "Import Scene",
                        onAction = { showImportDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.linkedScenes, key = { scene -> scene.id }) { scene ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.unlinkScene(scene.id) },
                            ) {
                                SceneCard(
                                    scene = scene,
                                    soundscapeCount = 0,
                                    onOpen = {
                                        viewModel.onSceneOpened(it.id)
                                        onOpenScene(it, false)
                                    },
                                    onPlay = {
                                        viewModel.onSceneOpened(it.id)
                                        onOpenScene(it, true)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showImportDialog = true },
        ) {
            Text("Import Scene")
        }
    }

    val state = uiState
    if (showImportDialog && state is SessionScenesUiState.Success) {
        ImportScenesDialog(
            availableScenes = state.availableScenesToImport,
            onDismiss = { showImportDialog = false },
            onConfirm = { selectedIds ->
                viewModel.importScenes(selectedIds)
                showImportDialog = false
            },
        )
    }
}

@Composable
private fun ImportScenesDialog(
    availableScenes: List<Scene>,
    onDismiss: () -> Unit,
    onConfirm: (List<Long>) -> Unit,
) {
    val selectedSceneIds = remember { mutableStateListOf<Long>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Scene") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (availableScenes.isEmpty()) {
                    Text("No more scenes available to import.")
                } else {
                    availableScenes.forEach { scene ->
                        androidx.compose.foundation.layout.Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(scene.name)
                            Checkbox(
                                checked = scene.id in selectedSceneIds,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedSceneIds.add(scene.id)
                                    } else {
                                        selectedSceneIds.remove(scene.id)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedSceneIds.toList()) },
                enabled = selectedSceneIds.isNotEmpty(),
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
