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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.common.UiState

@Composable
fun ScenesScreen(
    onNavigateToScene: (Long) -> Unit,
    viewModel: ScenesViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Scenes",
                showBackArrow = false,
                onBack = {},
                onGearClick = { /* Navigate to settings */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.semantics { contentDescription = "AddScene" }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Scene")
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
                    onDismiss = { /* Reload or dismiss */ }
                )
            }

            is UiState.Success -> {
                ScenesContent(
                    scenes = state.data,
                    onSceneClick = { /* Edit scene */ },
                    onPlayClick = onNavigateToScene,
                    onDeleteScene = viewModel::deleteScene,
                    modifier = modifier.padding(paddingValues)
                )
            }
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

@Composable
private fun ScenesContent(
    scenes: List<com.example.rpgaudiomixer.domain.model.Scene>,
    onSceneClick: (Long) -> Unit,
    onPlayClick: (Long) -> Unit,
    onDeleteScene: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (scenes.isEmpty()) {
        EmptyStateView(
            title = "No Scenes",
            message = "Create your first scene to start building atmospheric experiences",
            actionLabel = "CREATE SCENE",
            onActionClick = { /* Trigger create dialog */ }
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
                items = scenes,
                key = { it.id }
            ) { scene ->
                SwipeToDeleteContainer(
                    onDelete = { onDeleteScene(scene.id) }
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
private fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Scene") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Scene Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "SceneNameInput" }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "SceneDescriptionInput" }
                )

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma-separated)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "SceneTagsInput" }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(
                            name.trim(),
                            description.trim().takeIf { it.isNotBlank() },
                            tags.trim()
                        )
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("CREATE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}
