package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SceneCard
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.scenes.ScenesViewModel
import com.example.rpgaudiomixer.app.ui.scenes.ScenesUiState
import com.example.rpgaudiomixer.domain.scene.Scene

@Composable
fun ScenesScreen(
    viewModel: ScenesViewModel = hiltViewModel(),
    onSceneClick: (Long, Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is ScenesUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ArcanumGold
                )
            }
            is ScenesUiState.Success -> {
                if (state.scenes.isEmpty()) {
                    EmptyStateView(
                        illustration = Icons.Default.Map,
                        title = "No scenes found",
                        subtitle = "Create a scene to set the atmosphere.",
                        actionLabel = "CREATE FIRST SCENE",
                        onAction = { showCreateDialog = true }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.scenes, key = { it.id }) { scene ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteScene(scene.id) }
                            ) {
                                SceneCard(
                                    scene = scene,
                                    onPlayClick = { onSceneClick(scene.id, true) },
                                    onCardClick = { onSceneClick(scene.id, false) }
                                )
                            }
                        }
                    }
                }
            }
            is ScenesUiState.Error -> {
                Text(
                    text = state.message,
                    color = ArcanumErrorRed,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = ArcanumGold,
            contentColor = ArcanumOnGold
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Scene")
        }

        if (showCreateDialog) {
            CreateSceneDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, tags ->
                    viewModel.createScene(name, tags = tags)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumCard,
        title = { Text("New Scene", color = ArcanumGold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Scene Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcanumGold,
                        focusedLabelColor = ArcanumGold,
                        unfocusedLabelColor = ArcanumOnSurface.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma separated)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcanumGold,
                        focusedLabelColor = ArcanumGold,
                        unfocusedLabelColor = ArcanumOnSurface.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (name.isNotBlank()) {
                        val tags = tagsInput.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                        onConfirm(name, tags)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("SCRIBE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ArcanumOnSurface)
            }
        }
    )
}
