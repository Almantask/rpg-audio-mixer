package com.example.rpgaudiomixer.ui.sessionscenes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun ImportSceneDialog(
    availableScenes: List<Scene>,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    val selectedScenes = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Import Scenes",
                modifier = Modifier.testTag("ImportSceneDialog_Title")
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .testTag("ImportSceneDialog_SceneList")
            ) {
                items(availableScenes, key = { it.id }) { scene ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("ImportSceneDialog_SceneItem_${scene.name}"),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = scene.name,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = selectedScenes.contains(scene.id),
                            onCheckedChange = { checked ->
                                if (checked) {
                                    selectedScenes.add(scene.id)
                                } else {
                                    selectedScenes.remove(scene.id)
                                }
                            },
                            modifier = Modifier.testTag("ImportSceneDialog_Checkbox_${scene.name}")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedScenes.isNotEmpty()) {
                        onConfirm(selectedScenes.toList())
                    }
                },
                enabled = selectedScenes.isNotEmpty(),
                modifier = Modifier.testTag("ImportSceneDialog_ConfirmButton")
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("ImportSceneDialog_CancelButton")
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.testTag("ImportSceneDialog")
    )
}
