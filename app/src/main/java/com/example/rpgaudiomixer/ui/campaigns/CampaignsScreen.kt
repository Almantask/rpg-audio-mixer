package com.example.rpgaudiomixer.ui.campaigns

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.Campaign

/**
 * Campaigns list screen with create/delete functionality.
 *
 * Features:
 * - Scrollable list of campaign cards
 * - Empty state with "Scribe New Tale" prompt
 * - FAB to create new campaign
 * - Photo picker integration for cover art
 * - Swipe-to-delete campaigns
 */
@Composable
fun CampaignsScreen(
    onNavigateToCampaignSessions: (Long) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampaignsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Campaigns",
                showBackArrow = false,
                onGearClick = onNavigateToSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.testTag("Campaigns_CreateFab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Campaign")
            }
        },
        modifier = modifier
    ) { padding ->
        when (val state = uiState) {
            is CampaignsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CampaignsUiState.Success -> {
                if (state.campaigns.isEmpty()) {
                    EmptyCampaignsState(
                        onCreateClick = { showCreateDialog = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                } else {
                    CampaignsList(
                        campaigns = state.campaigns,
                        onCampaignClick = onNavigateToCampaignSessions,
                        onDeleteCampaign = viewModel::deleteCampaign,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    )
                }
            }
            is CampaignsUiState.Error -> {
                ErrorDialog(
                    message = state.message,
                    onDismiss = viewModel::clearError
                )
            }
        }

        if (showCreateDialog) {
            CreateCampaignDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, coverUri ->
                    viewModel.createCampaign(name, coverUri)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
private fun CampaignsList(
    campaigns: List<Campaign>,
    onCampaignClick: (Long) -> Unit,
    onDeleteCampaign: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.testTag("Campaigns_List"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = campaigns,
            key = { it.id }
        ) { campaign ->
            CampaignCard(
                campaign = campaign,
                onClick = { onCampaignClick(campaign.id) },
                onDelete = { onDeleteCampaign(campaign.id) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun EmptyCampaignsState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.testTag("Campaigns_EmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📜",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No campaigns yet",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Scribe your first tale",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            modifier = Modifier.testTag("Campaigns_EmptyState_CreateButton")
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scribe New Tale")
        }
    }
}

@Composable
private fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, coverUri: String?) -> Unit
) {
    var campaignName by remember { mutableStateOf("") }
    var coverArtUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        coverArtUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Campaign") },
        text = {
            Column {
                OutlinedTextField(
                    value = campaignName,
                    onValueChange = { campaignName = it },
                    label = { Text("Campaign Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("CreateCampaign_NameField")
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.testTag("CreateCampaign_PhotoPickerButton")
                ) {
                    Text(if (coverArtUri == null) "Choose Cover Art" else "Cover Art Selected")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (campaignName.isNotBlank()) {
                        onCreate(campaignName.trim(), coverArtUri?.toString())
                    }
                },
                enabled = campaignName.isNotBlank(),
                modifier = Modifier.testTag("CreateCampaign_ConfirmButton")
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("CreateCampaign_CancelButton")
            ) {
                Text("Cancel")
            }
        }
    )
}
