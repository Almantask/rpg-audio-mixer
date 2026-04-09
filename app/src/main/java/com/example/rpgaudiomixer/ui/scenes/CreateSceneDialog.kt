package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
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
import com.example.rpgaudiomixer.domain.model.PredefinedTags

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, description: String?, tags: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var sceneName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tagsText by remember { mutableStateOf("") }
    var selectedPredefinedTags by remember { mutableStateOf(setOf<String>()) }

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

                // Predefined tag suggestions
                Text(
                    text = "Quick Tags",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    PredefinedTags.ALL.forEach { tag ->
                        FilterChip(
                            selected = tag in selectedPredefinedTags,
                            onClick = {
                                selectedPredefinedTags = if (tag in selectedPredefinedTags) {
                                    selectedPredefinedTags - tag
                                } else {
                                    selectedPredefinedTags + tag
                                }
                            },
                            label = { Text(tag) },
                            modifier = Modifier.testTag("TagChip_$tag")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Custom Tags (comma-separated)") },
                    placeholder = { Text("e.g., boss fight, night") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("SceneTagsInput")
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Select quick tags or add custom tags to organize your scenes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (sceneName.isNotBlank()) {
                        // Combine predefined tags and custom tags
                        val customTags = if (tagsText.isBlank()) {
                            emptyList()
                        } else {
                            tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        }
                        val allTags = (selectedPredefinedTags + customTags).distinct()
                        val desc = description.ifBlank { null }
                        onCreate(sceneName.trim(), desc, allTags)
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
