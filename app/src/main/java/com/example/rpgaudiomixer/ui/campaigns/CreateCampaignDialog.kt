package com.example.rpgaudiomixer.ui.campaigns

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var campaignName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("CreateCampaignDialog"),
        title = {
            Text("New Campaign")
        },
        text = {
            Column {
                Text(
                    text = "Enter a name for your new campaign",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = campaignName,
                    onValueChange = { campaignName = it },
                    label = { Text("Campaign Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateCampaignDialog_NameInput"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (campaignName.isNotBlank()) {
                        onConfirm(campaignName.trim())
                    }
                },
                modifier = Modifier.testTag("CreateCampaignDialog_ConfirmButton"),
                enabled = campaignName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CreateCampaignDialog_CancelButton")
            ) {
                Text("Cancel")
            }
        }
    )
}
