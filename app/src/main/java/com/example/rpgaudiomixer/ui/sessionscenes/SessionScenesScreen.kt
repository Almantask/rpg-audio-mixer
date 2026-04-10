package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.ui.scenes.SceneCard

@Composable
fun SessionScenesScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToActiveScene: (Long, Boolean, Long?) -> Unit = { _, _, _ -> },
    onNavigateToCredits: () -> Unit = {},
    viewModel: SessionScenesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val showImportDialog by viewModel.showImportDialog.collectAsState()
    val availableScenes by viewModel.availableScenes.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val campaignId by viewModel.campaignId.collectAsState()

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Session Scenes",
                showBackArrow = true,
                onBackClick = onNavigateBack,
                onGearClick = onNavigateToCredits
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showImportDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("ImportSceneFAB")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Import Scene",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is SessionScenesUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SessionScenesUiState.Success -> {
                    if (state.scenes.isEmpty()) {
                        EmptySessionScenesState(
                            onImportScene = { viewModel.showImportDialog() },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        SessionScenesList(
                            scenes = state.scenes,
                            onSceneCardClick = { onNavigateToActiveScene(it.id, false, campaignId) },
                            onScenePlayClick = { onNavigateToActiveScene(it.id, true, campaignId) },
                            onUnlinkScene = { viewModel.unlinkScene(it) }
                        )
                    }
                }
                is SessionScenesUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showImportDialog) {
        ImportScenesDialog(
            availableScenes = availableScenes,
            linkedSceneIds = when (val state = uiState) {
                is SessionScenesUiState.Success -> state.scenes.map { it.id }
                else -> emptyList()
            },
            onDismiss = { viewModel.hideImportDialog() },
            onImport = { sceneIds ->
                viewModel.importScenes(sceneIds)
            }
        )
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = { viewModel.clearError() }
    )
}

@Composable
private fun SessionScenesList(
    scenes: List<Scene>,
    onSceneCardClick: (Scene) -> Unit,
    onScenePlayClick: (Scene) -> Unit,
    onUnlinkScene: (Scene) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(scenes, key = { it.id }) { scene ->
            SceneCard(
                scene = scene,
                onCardClick = { onSceneCardClick(scene) },
                onPlayClick = { onScenePlayClick(scene) },
                onDelete = { onUnlinkScene(scene) }
            )
        }
    }
}

@Composable
private fun EmptySessionScenesState(
    onImportScene: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
            .testTag("EmptySessionScenesState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🗺️",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Scenes Linked",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Import scenes from your library for this session",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
