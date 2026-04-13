package com.example.rpgaudiomixer.app.screens.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.rpgaudiomixer.app.domain.model.Scene

@Composable
fun SessionScenesScreen(
    onNavigateBack: () -> Unit,
    viewModel: SessionScenesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showImportDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.testTag("sessionScenesScreen"),
        floatingActionButton = {
            FloatingActionButton(onClick = { showImportDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Import Scene")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is SessionScenesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SessionScenesUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SessionScenesUiState.Success -> {
                    if (state.linkedScenes.isEmpty()) {
                        ArcanumEmptyState(
                            icon = Icons.AutoMirrored.Filled.List,
                            title = "No Scenes Linked",
                            ctaText = "Import Scene",
                            onCtaClick = { showImportDialog = true }
                        )
                    } else {
                        LinkedSceneList(
                            scenes = state.linkedScenes,
                            onUnlink = { viewModel.unlinkScene(it.id) }
                        )
                    }

                    if (showImportDialog) {
                        val unlinkedScenes = state.allScenes.filter { all ->
                            state.linkedScenes.none { linked -> linked.id == all.id }
                        }
                        ImportSceneDialog(
                            availableScenes = unlinkedScenes,
                            onDismiss = { showImportDialog = false },
                            onSelect = { scene ->
                                viewModel.linkScene(scene.id)
                                showImportDialog = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkedSceneList(
    scenes: List<Scene>,
    onUnlink: (Scene) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(scenes, key = { it.id }) { scene ->
            SwipeableSessionSceneCard(
                scene = scene,
                onUnlink = { onUnlink(scene) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableSessionSceneCard(
    scene: Scene,
    onUnlink: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onUnlink()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Unlink Scene",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        SessionSceneCard(scene = scene)
    }
}

@Composable
private fun SessionSceneCard(scene: Scene) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("SessionSceneCard")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = scene.name, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun ImportSceneDialog(
    availableScenes: List<Scene>,
    onDismiss: () -> Unit,
    onSelect: (Scene) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Scene") },
        text = {
            if (availableScenes.isEmpty()) {
                Text("No scenes available to import.")
            } else {
                LazyColumn {
                    items(availableScenes, key = { it.id }) { scene ->
                        Text(
                            text = scene.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(scene) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
