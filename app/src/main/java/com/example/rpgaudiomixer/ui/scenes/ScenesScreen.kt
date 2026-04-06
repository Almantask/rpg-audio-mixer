package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun ScenesScreen(
    onSceneSelected: (Long) -> Unit = {},
    onGearClick: () -> Unit = {},
    viewModel: ScenesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    if (uiState is ScenesUiState.Error) {
        errorMessage = (uiState as ScenesUiState.Error).message
    }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Scenes",
                onGearClick = onGearClick,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is ScenesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ScenesUiState.Success -> {
                    ScenesList(
                        scenes = state.scenes,
                        onSceneSelected = onSceneSelected,
                        onDeleteScene = { viewModel.deleteScene(it) },
                        onAddNew = { showCreateDialog = true },
                    )
                }
                is ScenesUiState.Error -> {
                    EmptyStateView(
                        message = "No scenes yet.",
                        actionLabel = "Add New Scene",
                        onAction = { showCreateDialog = true },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateSceneDialog(
            onConfirm = { name, description ->
                viewModel.createScene(name = name, description = description, tags = emptyList())
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }

    ErrorDialog(message = errorMessage, onDismiss = { errorMessage = null })
}

@Composable
private fun ScenesList(
    scenes: List<Scene>,
    onSceneSelected: (Long) -> Unit,
    onDeleteScene: (Long) -> Unit,
    onAddNew: () -> Unit,
) {
    if (scenes.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            EmptyStateView(
                message = "No scenes yet. Create your first scene!",
                actionLabel = "Add New Scene",
                onAction = onAddNew,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(scenes, key = { it.id }) { scene ->
            SwipeToDeleteContainer(
                onDelete = { onDeleteScene(scene.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SceneCard(
                    scene = scene,
                    onPlay = { onSceneSelected(scene.id) },
                    onTap = { onSceneSelected(scene.id) },
                )
            }
        }
        item {
            Button(
                onClick = onAddNew,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
            ) {
                Text("+ Add New Scene", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun SceneCard(
    scene: Scene,
    onPlay: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onTap,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = scene.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumGold,
                )
                scene.description?.let { desc ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = desc, style = MaterialTheme.typography.bodySmall)
                }
                if (scene.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scene.tags.joinToString(" · "),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
            ) {
                Text("▶", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun CreateSceneDialog(
    onConfirm: (name: String, description: String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Scene") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Scene name") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), description.trim().ifBlank { null }) },
                enabled = name.isNotBlank(),
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
