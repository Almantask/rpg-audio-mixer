package com.example.rpgaudiomixer.ui.fx

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.PredefinedTags

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditFxDialog(
    track: FxTrack,
    onDismiss: () -> Unit,
    onSave: (name: String, tags: List<String>) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(track.name) }
    var tagsText by remember { mutableStateOf(track.tags.filter { it !in PredefinedTags.ALL }.joinToString(", ")) }
    var selectedPredefinedTags by remember { mutableStateOf(track.tags.filter { it in PredefinedTags.ALL }.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit FX Track") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Predefined tag suggestions
                Text(
                    text = "Quick Tags",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
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
                            label = { Text(tag) }
                        )
                    }
                }

                OutlinedTextField(
                    value = tagsText,
                    onValueChange = { tagsText = it },
                    label = { Text("Custom Tags (comma-separated)") },
                    placeholder = { Text("e.g., boss fight, night") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = {
                        onDelete()
                        onDismiss()
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                TextButton(
                    onClick = {
                        // Combine predefined tags and custom tags
                        val customTags = tagsText.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() }
                        val allTags = (selectedPredefinedTags + customTags).distinct()
                        onSave(name.trim(), allTags)
                        onDismiss()
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
