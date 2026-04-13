package com.example.rpgaudiomixer.app.screens.campaigns

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.domain.model.Campaign

@Suppress("kotlin:S6615")
@Composable
fun CampaignsScreen(
    onNavigateToSessions: (Long) -> Unit,
    viewModel: CampaignsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Campaign")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is CampaignsUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CampaignsUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                is CampaignsUiState.Success -> {
                    if (state.campaigns.isEmpty()) {
                        EmptyCampaignsState(onAddClick = { showCreateDialog = true })
                    } else {
                        CampaignList(
                            campaigns = state.campaigns,
                            onItemClick = onNavigateToSessions,
                            onDelete = { viewModel.deleteCampaign(it) }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCampaignDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                viewModel.createCampaign(name)
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun CampaignList(
    campaigns: List<Campaign>,
    onItemClick: (Long) -> Unit,
    onDelete: (Campaign) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(campaigns, key = { it.id }) { campaign ->
            SwipeableCampaignCard(
                campaign = campaign,
                onClick = { onItemClick(campaign.id) },
                onDelete = { onDelete(campaign) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableCampaignCard(
    campaign: Campaign,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Campaign",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    ) {
        CampaignCard(
            campaign = campaign,
            onClick = onClick
        )
    }
}

@Composable
fun CampaignCard(
    campaign: Campaign,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().testTag("CampaignCard")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = campaign.name, style = MaterialTheme.typography.titleLarge)
                Text(text = "Last played: ${campaign.lastPlayedAt}", style = MaterialTheme.typography.bodyMedium)
            }
            Button(onClick = onClick) {
                Text("RESUME")
            }
        }
    }
}

@Composable
fun EmptyCampaignsState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("NEW CAMPAIGN", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onAddClick) {
            Text("Scribe New Tale")
        }
    }
}

@Composable
fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Campaign") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Campaign Name") }
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

