package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.ArcanumError
import com.example.rpgaudiomixer.app.theme.ArcanumPrimary

@Composable
fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit,
) {
    if (message.isNullOrBlank()) return

    AlertDialog(
        modifier = Modifier.testTag("ErrorDialog"),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Error",
                color = ArcanumError,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .testTag("ErrorDialog_Message"),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("ErrorDialog_DismissButton"),
            ) {
                Text(
                    text = "Dismiss",
                    color = ArcanumPrimary,
                    fontWeight = FontWeight.Medium,
                )
            }
        },
    )
}
