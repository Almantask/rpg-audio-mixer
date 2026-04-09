package com.example.rpgaudiomixer.ui.campaigns

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.CampaignCard
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.ui.UiState

@Composable
fun CampaignsRoute(
    onOpenCampaign: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampaignsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    CampaignsScreen(
        uiState = uiState,
        errorMessage = errorMessage,
        onOpenCampaign = onOpenCampaign,
        onCreateCampaign = viewModel::createCampaign,
        onDeleteCampaign = viewModel::deleteCampaign,
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
fun CampaignsScreen(
    uiState: UiState<List<Campaign>>,
    errorMessage: String?,
    onOpenCampaign: (Long) -> Unit,
    onCreateCampaign: (String, String?) -> Unit,
    onDeleteCampaign: (Long) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (uiState) {
            UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.MenuBook,
                    title = "Unable to load campaigns",
                    body = uiState.message,
                    actionLabel = "Try Again",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                CampaignsContent(
                    campaigns = uiState.data,
                    onOpenCampaign = onOpenCampaign,
                    onCreateClick = { showCreateDialog = true },
                    onDeleteCampaign = onDeleteCampaign,
                )
            }
        }

        if (showCreateDialog) {
            CreateCampaignDialog(
                onDismiss = { showCreateDialog = false },
                onCreateCampaign = { name, coverUri ->
                    onCreateCampaign(name, coverUri)
                    if (name.isNotBlank()) {
                        showCreateDialog = false
                    }
                },
            )
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = onDismissError,
    )
}

@Composable
private fun CampaignsContent(
    campaigns: List<Campaign>,
    onOpenCampaign: (Long) -> Unit,
    onCreateClick: () -> Unit,
    onDeleteCampaign: (Long) -> Unit,
) {
    if (campaigns.isEmpty()) {
        EmptyStateView(
            modifier = Modifier.fillMaxSize(),
            icon = Icons.Rounded.MenuBook,
            title = "No campaigns yet",
            body = "Scribe a new tale and keep your latest adventure ready to resume.",
            actionLabel = "Scribe New Tale",
            onAction = onCreateClick,
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = campaigns,
            key = { campaign -> campaign.id },
        ) { campaign ->
            SwipeToDeleteContainer(
                onDelete = { onDeleteCampaign(campaign.id) },
            ) {
                CampaignCard(
                    campaign = campaign,
                    onOpenCampaign = { onOpenCampaign(campaign.id) },
                )
            }
        }
        item {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onCreateClick,
            ) {
                Text("+ NEW CAMPAIGN")
            }
        }
    }
}

@Composable
private fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onCreateCampaign: (String, String?) -> Unit,
) {
    var campaignName by rememberSaveable { mutableStateOf("") }
    var selectedCoverUri by rememberSaveable { mutableStateOf<String?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        selectedCoverUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("New Campaign")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = campaignName,
                    onValueChange = { campaignName = it },
                    label = { Text("Campaign name") },
                    singleLine = true,
                )
                OutlinedButton(
                    onClick = {
                        imagePickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Pick campaign cover art",
                    )
                    Text("Choose cover art")
                }
                Text(
                    text = selectedCoverUri ?: "No cover art selected",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreateCampaign(campaignName, selectedCoverUri) },
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
