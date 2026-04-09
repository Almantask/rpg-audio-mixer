package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String?, tags: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var sceneName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Scene",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = sceneName,
                    onValueChange = { sceneName = it },
                    label = { Text("Scene Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("SceneNameInput")
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("SceneDescriptionInput")
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Tags (comma-separated)") },
                    placeholder = { Text("e.g., Tavern, Combat, Night") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("SceneTagsInput")
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tags help you organize and find your scenes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (sceneName.isNotBlank()) {
                        val tags = if (tagsText.isBlank()) {
                            emptyList()
                        } else {
                            tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        }
                        val desc = description.ifBlank { null }
                        onCreate(sceneName.trim(), desc, tags)
                    }
                },
                enabled = sceneName.isNotBlank(),
                modifier = Modifier.testTag("ConfirmCreateButton")
            ) {
                Text(
                    text = "Create",
                    color = if (sceneName.isNotBlank()) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CancelButton")
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("CreateSceneDialog")
    )
}
