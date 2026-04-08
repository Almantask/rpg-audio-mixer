package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun SessionScenesScreen(
    viewModel: SessionScenesViewModel = hiltViewModel(),
    onSceneClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showImportDialog by viewModel.showImportDialog.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (uiState is SessionScenesUiState.Success) {
                val hasAvailableScenes = (uiState as SessionScenesUiState.Success).availableScenes.isNotEmpty()
                if (hasAvailableScenes) {
                    FloatingActionButton(
                        onClick = { viewModel.showImportDialog() },
                        modifier = Modifier.testTag("SessionScenesScreen_FAB"),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Import Scene")
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiState) {
                is SessionScenesUiState.Loading -> {
                    LoadingContent()
                }
                is SessionScenesUiState.Success -> {
                    val linkedScenes = (uiState as SessionScenesUiState.Success).linkedScenes
                    if (linkedScenes.isEmpty()) {
                        EmptyStateContent(onImportClick = { viewModel.showImportDialog() })
                    } else {
                        ScenesList(
                            scenes = linkedScenes,
                            onSceneClick = onSceneClick,
                            onUnlinkScene = { viewModel.unlinkScene(it) }
                        )
                    }
                }
                is SessionScenesUiState.Error -> {
                    ErrorContent(message = (uiState as SessionScenesUiState.Error).message)
                }
            }
        }
    }

    if (showImportDialog && uiState is SessionScenesUiState.Success) {
        val availableScenes = (uiState as SessionScenesUiState.Success).availableScenes
        ImportSceneDialog(
            availableScenes = availableScenes,
            onDismiss = { viewModel.hideImportDialog() },
            onConfirm = { sceneIds ->
                sceneIds.forEach { viewModel.linkScene(it) }
            }
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("SessionScenesScreen_Loading"),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun EmptyStateContent(onImportClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag("SessionScenesScreen_EmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Scenes Linked",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Import scenes from your library to this session",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onImportClick,
            modifier = Modifier.testTag("SessionScenesScreen_ImportSceneButton"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Import Scene")
        }
    }
}

@Composable
private fun ScenesList(
    scenes: List<Scene>,
    onSceneClick: (String) -> Unit,
    onUnlinkScene: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("SessionScenesScreen_List"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(scenes, key = { it.id }) { scene ->
            SceneCard(
                scene = scene,
                onClick = { onSceneClick(scene.id) },
                onUnlink = { onUnlinkScene(scene.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SceneCard(
    scene: Scene,
    onClick: () -> Unit,
    onUnlink: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("SceneCard_${scene.name}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = scene.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("SceneCard_${scene.name}_Name")
            )
            if (scene.description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scene.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
            .testTag("SessionScenesScreen_Error"),
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
