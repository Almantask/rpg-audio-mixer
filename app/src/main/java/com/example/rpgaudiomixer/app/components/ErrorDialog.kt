package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

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
    if (message == null) {
        return
    }

    AlertDialog(
        modifier = Modifier.testTag(ErrorDialogTestTags.DIALOG),
        onDismissRequest = onDismiss,
        text = {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag(ErrorDialogTestTags.MESSAGE),
                text = message,
            )
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.testTag(ErrorDialogTestTags.DISMISS_BUTTON),
                onClick = onDismiss,
            ) {
                Text(text = "Dismiss")
            }
        },
    )
}
