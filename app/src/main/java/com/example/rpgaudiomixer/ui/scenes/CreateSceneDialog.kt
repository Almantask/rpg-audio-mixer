package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun CreateSceneDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var sceneName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "New Scene",
                modifier = Modifier.testTag("CreateSceneDialog_Title")
            )
        },
        text = {
            Column {
                Text("Enter the scene name:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sceneName,
                    onValueChange = { sceneName = it },
                    label = { Text("Scene Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateSceneDialog_NameInput")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sceneName.isNotBlank()) {
                        onConfirm(sceneName)
                    }
                },
                enabled = sceneName.isNotBlank(),
                modifier = Modifier.testTag("CreateSceneDialog_ConfirmButton")
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CreateSceneDialog_CancelButton")
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.testTag("CreateSceneDialog")
    )
}
