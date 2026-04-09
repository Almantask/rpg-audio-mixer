package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun ImportScenesDialog(
    availableScenes: List<Scene>,
    linkedSceneIds: List<Long>,
    onDismiss: () -> Unit,
    onImport: (List<Long>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSceneIds by remember { mutableStateOf(emptySet<Long>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Import Scenes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (availableScenes.isEmpty()) {
                    Text(
                        text = "No scenes available. Create scenes in the Scenes tab first.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    Text(
                        text = "Select scenes to import to this session:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(availableScenes, key = { it.id }) { scene ->
                            val isLinked = linkedSceneIds.contains(scene.id)
                            val isSelected = selectedSceneIds.contains(scene.id)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ScenePickerItem_${scene.name}"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        selectedSceneIds = if (checked) {
                                            selectedSceneIds + scene.id
                                        } else {
                                            selectedSceneIds - scene.id
                                        }
                                    },
                                    enabled = !isLinked
                                )
                                Column {
                                    Text(
                                        text = scene.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isLinked) {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    if (isLinked) {
                                        Text(
                                            text = "Already linked",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedSceneIds.isNotEmpty()) {
                        onImport(selectedSceneIds.toList())
                    }
                },
                enabled = selectedSceneIds.isNotEmpty(),
                modifier = Modifier.testTag("ConfirmImportButton")
            ) {
                Text(
                    text = "Import",
                    color = if (selectedSceneIds.isNotEmpty()) {
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
        modifier = modifier.testTag("ImportScenesDialog")
    )
}
