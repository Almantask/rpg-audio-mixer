package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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

object ErrorDialogTestTags {
    const val DIALOG = "ErrorDialog"
    const val MESSAGE = "ErrorDialog_Message"
    const val DISMISS_BUTTON = "ErrorDialog_DismissButton"
}

@Composable
fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit,
) {
    if (message.isNullOrBlank()) {
        return
    }

    AlertDialog(
        modifier = Modifier.testTag(ErrorDialogTestTags.DIALOG),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "An ominous interruption",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                modifier = Modifier
                    .testTag(ErrorDialogTestTags.MESSAGE)
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
                    .verticalScroll(rememberScrollState()),
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.testTag(ErrorDialogTestTags.DISMISS_BUTTON),
                onClick = onDismiss,
            ) {
                Text("Dismiss")
            }
        },
    )
}
