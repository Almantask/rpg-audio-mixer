package com.example.rpgaudiomixer.ui.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun CreateSessionDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var sessionName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "New Session",
                modifier = Modifier.testTag("CreateSessionDialog_Title")
            )
        },
        text = {
            Column {
                Text("Enter the session name:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = sessionName,
                    onValueChange = { sessionName = it },
                    label = { Text("Session Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateSessionDialog_NameInput")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sessionName.isNotBlank()) {
                        onConfirm(sessionName)
                    }
                },
                enabled = sessionName.isNotBlank(),
                modifier = Modifier.testTag("CreateSessionDialog_ConfirmButton")
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CreateSessionDialog_CancelButton")
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier.testTag("CreateSessionDialog")
    )
}
