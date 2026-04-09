package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit,
) {
    if (message.isNullOrBlank()) {
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Something went wrong")
        },
        text = {
            Text(
                text = message,
                modifier = androidx.compose.ui.Modifier.verticalScroll(rememberScrollState()),
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss")
            }
        },
    )
}
