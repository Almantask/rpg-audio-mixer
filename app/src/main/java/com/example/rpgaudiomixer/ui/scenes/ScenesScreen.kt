package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.ui.common.UiState

@Composable
fun ScenesScreen(
    onSceneClick: (Long) -> Unit,
    viewModel: ScenesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyStateView(
                        message = "No scenes yet. Create your first scene!",
                        buttonText = "Add New Scene",
                        onButtonClick = { showCreateDialog = true }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data, key = { it.id }) { scene ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteScene(scene.id) }
                            ) {
                                SceneCard(
                                    scene = scene,
                                    onClick = { onSceneClick(scene.id) }
                                )
                            }
                        }
                    }
                }
            }
            is UiState.Error -> {
                EmptyStateView(
                    message = state.message,
                    buttonText = "Retry",
                    onButtonClick = { }
                )
            }
        }

        if (uiState is UiState.Success) {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .padding(16.dp)
                    .align(androidx.compose.ui.Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Scene")
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

    ErrorDialog(
        message = errorMessage,
        onDismiss = { viewModel.clearError() }
    )
}

@Composable
private fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Scene") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Scene Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Tags (comma-separated)") },
                    placeholder = { Text("Tavern, Combat, Forest") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val tags = tagsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    onCreate(name, description.ifBlank { null }, tags)
                },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
