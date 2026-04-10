package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.LoadingStateView
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun ScenesScreen(
    onOpenScene: (Scene, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (val state = uiState) {
            ScenesUiState.Loading -> LoadingStateView(label = "Loading scenes…")
            is ScenesUiState.Error -> ErrorDialog(message = state.message, onDismiss = { })
            is ScenesUiState.Success -> {
                if (state.scenes.isEmpty()) {
                    EmptyStateView(
                        title = "No scenes yet",
                        actionLabel = "Add New Scene",
                        onAction = { showCreateDialog = true },
                        illustration = "🗺️",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.scenes, key = { scene -> scene.id }) { scene ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteScene(scene.id) },
                            ) {
                                SceneCard(
                                    scene = scene,
                                    soundscapeCount = 0,
                                    onOpen = { onOpenScene(it, false) },
                                    onPlay = { onOpenScene(it, true) },
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showCreateDialog = true },
        ) {
            Text("Add New Scene")
        }
    }

    if (showCreateDialog) {
        CreateSceneDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, description, tags ->
                viewModel.createScene(name = name, description = description, tags = tags)
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?, List<String>) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var customTags by rememberSaveable { mutableStateOf("") }
    var selectedPredefinedTags by rememberSaveable { mutableStateOf(emptyList<String>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Scene") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Scene name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = customTags,
                    onValueChange = { customTags = it },
                    label = { Text("Custom tags (comma separated)") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("Predefined tags")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    predefinedSceneTags.chunked(3).forEach { rowTags ->
                        androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowTags.forEach { tag ->
                                FilterChip(
                                    selected = selectedPredefinedTags.contains(tag),
                                    onClick = {
                                        selectedPredefinedTags = if (selectedPredefinedTags.contains(tag)) {
                                            selectedPredefinedTags - tag
                                        } else {
                                            selectedPredefinedTags + tag
                                        }
                                    },
                                    label = { Text(tag) },
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCreate(
                        name,
                        description.takeIf { it.isNotBlank() },
                        selectedPredefinedTags + customTags.split(","),
                    )
                },
                enabled = name.trim().isNotBlank(),
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
