package com.example.rpgaudiomixer.ui.campaigns

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.CampaignCard
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.Campaign

@Composable
fun CampaignsScreen(
    onOpenCampaign: (Campaign) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampaignsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (val state = uiState) {
            CampaignsUiState.Loading -> {
                Text("Loading campaigns…", style = MaterialTheme.typography.bodyLarge)
            }

            is CampaignsUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = { },
                )
            }

            is CampaignsUiState.Success -> {
                if (state.campaigns.isEmpty()) {
                    EmptyStateView(
                        title = "No campaigns yet",
                        actionLabel = "Scribe New Tale",
                        onAction = { showCreateDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(
                            items = state.campaigns,
                            key = { campaign -> campaign.id },
                        ) { campaign ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteCampaign(campaign.id) },
                            ) {
                                CampaignCard(
                                    campaign = campaign,
                                    onResume = { selectedCampaign ->
                                        viewModel.openCampaign(selectedCampaign.id)
                                        onOpenCampaign(selectedCampaign)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showCreateDialog = true },
        ) {
            Text("New Campaign")
        }
    }

    if (showCreateDialog) {
        CreateCampaignDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, coverArtUri ->
                viewModel.createCampaign(name = name, coverArtUri = coverArtUri)
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, coverArtUri: String?) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var coverArtUri by rememberSaveable { mutableStateOf<String?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        coverArtUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New Campaign")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Campaign name") },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                ) {
                    Text("Cover Art")
                }
                Text(
                    text = coverArtUri?.let { "Selected cover art: $it" } ?: "No cover art selected",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onCreate(name, coverArtUri) },
                enabled = name.trim().isNotBlank(),
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
