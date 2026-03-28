package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState
import com.example.rpgaudiomixer.ui.components.PrimaryButton
import com.example.rpgaudiomixer.ui.components.SceneCard

@Composable
fun SessionScenesScreen(
    sessionId: Long,
    onOpenScene: (Long) -> Unit,
    onBack: () -> Unit,
    onCredits: () -> Unit,
    viewModel: SessionScenesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState(sessionId).collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(
            onCredits = onCredits,
            showBack = true,
            onBack = onBack,
        )

        state.session?.let { session ->
            Text(
                text = session.name,
                style = MaterialTheme.typography.displayMedium,
                color = ArcanumGold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            if (state.scenes.isEmpty() && !state.isLoading) {
                item {
                    EmptyState(
                        title = "No Scenes Yet",
                        subtitle = "Import a scene to get started.",
                    )
                }
            }

            items(state.scenes, key = { it.id }) { scene ->
                SceneCard(
                    scene = scene,
                    onPlay = { onOpenScene(scene.id) },
                    onClick = { onOpenScene(scene.id) },
                )
            }

            item {
                PrimaryButton(
                    text = "+ IMPORT SCENE",
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddDialog) {
        ImportSceneDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description ->
                viewModel.importScene(sessionId, name, description)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun ImportSceneDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Scene") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Scene Name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), description.trim()) },
            ) {
                Text("CREATE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        },
    )
}
