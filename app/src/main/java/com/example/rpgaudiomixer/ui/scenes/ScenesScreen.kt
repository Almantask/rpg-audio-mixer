package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.Scene

/**
 * Global Scenes list screen with create/delete functionality.
 *
 * Features:
 * - Scrollable list of scene cards
 * - Empty state with "Create First Scene" prompt
 * - FAB to create new scene
 * - Scene cards with play button and tag chips
 * - Swipe-to-delete scenes
 */
@Composable
fun ScenesScreen(
    onNavigateToScene: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScenesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Scenes",
                showBackArrow = true,
                onBackClick = onNavigateBack,
                onGearClick = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.testTag("Scenes_CreateFab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Scene")
            }
        },
        modifier = modifier
    ) { padding ->
        when (val state = uiState) {
            is ScenesUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ScenesUiState.Success -> {
                if (state.scenes.isEmpty()) {
                    EmptyScenesState(
                        onCreateClick = { showCreateDialog = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                } else {
                    ScenesList(
                        scenes = state.scenes,
                        onSceneClick = onNavigateToScene,
                        onPlayScene = { sceneId ->
                            // TODO: Navigate to active scene and start playback
                        },
                        onDeleteScene = viewModel::deleteScene,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                }
            }
            is ScenesUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = viewModel::clearError
                )
            }
        }

        if (showCreateDialog) {
            CreateSceneDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, description, tags ->
                    viewModel.createScene(name, description, tags)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
private fun ScenesList(
    scenes: List<Scene>,
    onSceneClick: (Long) -> Unit,
    onPlayScene: (Long) -> Unit,
    onDeleteScene: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.testTag("Scenes_List"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = scenes,
            key = { it.id }
        ) { scene ->
            SceneCard(
                scene = scene,
                onClick = { onSceneClick(scene.id) },
                onPlay = { onPlayScene(scene.id) },
                onDelete = { onDeleteScene(scene.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EmptyScenesState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.testTag("Scenes_EmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🗺️",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No scenes yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create your first audio scene",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            modifier = Modifier.testTag("Scenes_EmptyState_CreateButton")
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create First Scene")
        }
    }
}

@Composable
private fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String?, tags: List<String>) -> Unit
) {
    var sceneName by remember { mutableStateOf("") }
    var sceneDescription by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Scene") },
        text = {
            Column {
                OutlinedTextField(
                    value = sceneName,
                    onValueChange = { sceneName = it },
                    label = { Text("Scene Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateScene_NameField")
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = sceneDescription,
                    onValueChange = { sceneDescription = it },
                    label = { Text("Description (optional)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateScene_DescriptionField")
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma-separated)") },
                    placeholder = { Text("e.g. Tavern, Forest, Combat") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateScene_TagsField")
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (sceneName.isNotBlank()) {
                        val tags = tagsInput
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        onCreate(
                            sceneName.trim(),
                            sceneDescription.takeIf { it.isNotBlank() },
                            tags
                        )
                    }
                },
                enabled = sceneName.isNotBlank(),
                modifier = Modifier.testTag("CreateScene_ConfirmButton")
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CreateScene_CancelButton")
            ) {
                Text("Cancel")
            }
        }
    )
}
