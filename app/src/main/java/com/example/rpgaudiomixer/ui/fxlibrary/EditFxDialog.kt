package com.example.rpgaudiomixer.ui.fxlibrary

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.FxTrack

@Composable
fun EditFxDialog(
    track: FxTrack,
    onDismiss: () -> Unit,
    onConfirm: (FxTrack) -> Unit,
    onDelete: (String) -> Unit
) {
    var fxName by remember { mutableStateOf(track.name) }
    var tagsInput by remember { mutableStateOf(track.tags.joinToString(", ")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit FX Track",
                modifier = Modifier.testTag("EditFxDialog_Title")
            )
        },
        text = {
            Column {
                Text("Edit track details:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = fxName,
                    onValueChange = { fxName = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("EditFxDialog_NameInput")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma-separated)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("EditFxDialog_TagsInput")
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onDelete(track.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("EditFxDialog_DeleteButton")
                ) {
                    Text("Delete")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fxName.isNotBlank()) {
                        val tags = tagsInput
                            .split(",")
                            .map { it.trim() }
                            .filter { it.isNotBlank() }
                        val updatedTrack = track.copy(name = fxName, tags = tags)
                        onConfirm(updatedTrack)
                    }
                },
                enabled = fxName.isNotBlank(),
                modifier = Modifier.testTag("EditFxDialog_SaveButton")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("EditFxDialog_CancelButton")
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.testTag("EditFxDialog")
    )
}
