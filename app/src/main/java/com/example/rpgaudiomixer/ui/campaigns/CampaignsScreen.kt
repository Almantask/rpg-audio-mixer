package com.example.rpgaudiomixer.ui.campaigns

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.format.DateFormat
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.Campaign
import java.util.Date

@Composable
fun CampaignsScreen(
    onCampaignSelected: (Long) -> Unit = {},
    onGearClick: () -> Unit = {},
    viewModel: CampaignsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    if (uiState is CampaignsUiState.Error) {
        errorMessage = (uiState as CampaignsUiState.Error).message
    }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Campaigns",
                onGearClick = onGearClick,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when (val state = uiState) {
                is CampaignsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CampaignsUiState.Success -> {
                    CampaignsList(
                        campaigns = state.campaigns,
                        onCampaignSelected = onCampaignSelected,
                        onDeleteCampaign = { viewModel.deleteCampaign(it) },
                        onAddNew = { showCreateDialog = true },
                    )
                }
                is CampaignsUiState.Error -> {
                    EmptyStateView(
                        message = "No campaigns yet. Start your first story!",
                        actionLabel = "Scribe New Tale",
                        onAction = { showCreateDialog = true },
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCampaignDialog(
            onConfirm = { name ->
                viewModel.createCampaign(name = name, coverArtUri = null)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = { errorMessage = null },
    )
}

@Composable
private fun CampaignsList(
    campaigns: List<Campaign>,
    onCampaignSelected: (Long) -> Unit,
    onDeleteCampaign: (Long) -> Unit,
    onAddNew: () -> Unit,
) {
    if (campaigns.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            EmptyStateView(
                message = "No campaigns yet. Start your first story!",
                actionLabel = "Scribe New Tale",
                onAction = onAddNew,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(campaigns, key = { it.id }) { campaign ->
            SwipeToDeleteContainer(
                onDelete = { onDeleteCampaign(campaign.id) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                CampaignCard(
                    campaign = campaign,
                    onResume = { onCampaignSelected(campaign.id) },
                )
            }
        }
        item {
            Button(
                onClick = onAddNew,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
            ) {
                Text("+ New Campaign", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    onResume: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "📖", style = MaterialTheme.typography.headlineMedium)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumGold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                val dateText = if (campaign.lastPlayedAt > 0) {
                    DateFormat.format("MMM d, yyyy", Date(campaign.lastPlayedAt)).toString()
                } else {
                    "Never played"
                }
                Text(
                    text = "Last played: $dateText",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Button(
                onClick = onResume,
                colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
            ) {
                Text("Resume →", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun CreateCampaignDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Campaign") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Campaign name") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim()) },
                enabled = name.isNotBlank(),
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
