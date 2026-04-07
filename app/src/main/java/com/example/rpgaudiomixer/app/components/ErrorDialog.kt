package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (message != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            modifier = modifier.testTag("ErrorDialog"),
            title = {
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = message,
                        modifier = Modifier
                            .testTag("ErrorDialog_Message")
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("ErrorDialog_DismissButton")
                ) {
                    Text(
                        text = "Dismiss",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        )
    }
}
