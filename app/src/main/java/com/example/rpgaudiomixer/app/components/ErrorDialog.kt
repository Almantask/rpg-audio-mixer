package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

/**
 * Reusable error dialog for Arcanum Audio
 * - Modal overlay with semi-transparent backdrop
 * - Scrollable message text
 * - Dismiss button
 */
@Composable
fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (message != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
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
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("ErrorDialog_Message")
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp),
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            modifier = modifier
                .testTag("ErrorDialog")
                .background(
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
        )
    }
}
