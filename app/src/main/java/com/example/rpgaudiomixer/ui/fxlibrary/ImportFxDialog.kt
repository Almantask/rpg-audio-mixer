package com.example.rpgaudiomixer.ui.fxlibrary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ImportFxDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, filePath: String, tags: List<String>) -> Unit
) {
    var fxName by remember { mutableStateOf("") }
    var filePath by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Import FX Track",
                modifier = Modifier.testTag("ImportFxDialog_Title")
            )
        },
        text = {
            Column {
                Text("Enter the FX track details:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fxName,
                    onValueChange = { fxName = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ImportFxDialog_NameInput")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = filePath,
                    onValueChange = { filePath = it },
                    label = { Text("File Path") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ImportFxDialog_FilePathInput")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma-separated)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ImportFxDialog_TagsInput")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fxName.isNotBlank() && filePath.isNotBlank()) {
                        val tags = tagsInput
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        onConfirm(fxName, filePath, tags)
                    }
                },
                enabled = fxName.isNotBlank() && filePath.isNotBlank(),
                modifier = Modifier.testTag("ImportFxDialog_ConfirmButton")
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("ImportFxDialog_CancelButton")
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.testTag("ImportFxDialog")
    )
}
