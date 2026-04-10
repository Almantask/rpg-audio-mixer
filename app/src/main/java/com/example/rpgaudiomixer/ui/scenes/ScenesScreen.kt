package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.ui.UiState

@Composable
fun ScenesRoute(
    onOpenScene: (Long, Boolean) -> Unit,
    onTitleChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onTitleChange(null)
        onDispose { onTitleChange(null) }
    }

    ScenesScreen(
        uiState = uiState,
        errorMessage = errorMessage,
        onOpenScene = onOpenScene,
        onCreateScene = viewModel::createScene,
        onDeleteScene = viewModel::deleteScene,
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
private fun ScenesScreen(
    uiState: UiState<List<Scene>>,
    errorMessage: String?,
    onOpenScene: (Long, Boolean) -> Unit,
    onCreateScene: (String, String?, String) -> Unit,
    onDeleteScene: (Long) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.PhotoLibrary,
                    title = "Unable to load scenes",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                ScenesContent(
                    scenes = uiState.data,
                    onOpenScene = onOpenScene,
                    onCreateClick = { showCreateDialog = true },
                    onDeleteScene = onDeleteScene,
                )
            }
        }

        if (showCreateDialog) {
            CreateSceneDialog(
                onDismiss = { showCreateDialog = false },
                onCreateScene = { name, description, tags ->
                    onCreateScene(name, description, tags)
                    if (name.isNotBlank()) {
                        showCreateDialog = false
                    }
                },
            )
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = onDismissError,
    )
}

@Composable
private fun ScenesContent(
    scenes: List<Scene>,
    onOpenScene: (Long, Boolean) -> Unit,
    onCreateClick: () -> Unit,
    onDeleteScene: (Long) -> Unit,
) {
    if (scenes.isEmpty()) {
        EmptyStateView(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            icon = Icons.Rounded.PhotoLibrary,
            title = "No scenes yet",
            body = "Add your first global scene, then reuse it across sessions.",
            actionLabel = "Add New Scene",
            onAction = onCreateClick,
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items = scenes, key = { scene -> scene.id }) { scene ->
            SwipeToDeleteContainer(
                onDelete = { onDeleteScene(scene.id) },
            ) {
                SceneCard(
                    scene = scene,
                    onOpenScene = { onOpenScene(scene.id, false) },
                    onPlayScene = { onOpenScene(scene.id, true) },
                )
            }
        }
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreateClick,
            ) {
                Text("+ ADD NEW SCENE")
            }
        }
    }
}

@Composable
private fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreateScene: (String, String?, String) -> Unit,
) {
    var sceneName by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var tagsText by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Scene") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = sceneName,
                        onValueChange = { sceneName = it },
                        label = { Text("Scene name") },
                        singleLine = true,
                    )
                }
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (optional)") },
                        minLines = 3,
                    )
                }
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = tagsText,
                        onValueChange = { tagsText = it },
                        label = { Text("Tags (comma separated)") },
                        placeholder = { Text("Tavern, Forest, Combat") },
                        maxLines = 3,
                    )
                }
                item {
                    Text(
                        text = "Tags are optional and help you scan the scene library quickly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onCreateScene(sceneName, description, tagsText) }) {
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
