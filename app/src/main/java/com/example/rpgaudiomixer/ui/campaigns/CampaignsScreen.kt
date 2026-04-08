package com.example.rpgaudiomixer.ui.campaigns

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.ui.common.UiState
import java.text.SimpleDateFormat
import java.util.*

/**
 * Campaigns screen
 * Lists all campaigns sorted by most recently played
 */
@Composable
fun CampaignsScreen(
    modifier: Modifier = Modifier,
    viewModel: CampaignsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showCreateDialog by viewModel.showCreateDialog.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    EmptyState(
                        onCreateClick = { viewModel.showCreateDialog() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    CampaignsList(
                        campaigns = state.data,
                        onDeleteCampaign = { viewModel.deleteCampaign(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            is UiState.Error -> {
                ErrorState(
                    message = state.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // FAB for creating new campaign
        if (uiState is UiState.Success && (uiState as UiState.Success).data.isNotEmpty()) {
            FloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Campaign"
                )
            }
        }
    }

    // Create campaign dialog
    if (showCreateDialog) {
        CreateCampaignDialog(
            onDismiss = { viewModel.hideCreateDialog() },
            onCreate = { name, coverUri ->
                viewModel.createCampaign(name, coverUri)
            }
        )
    }

    // Error dialog
    ErrorDialog(
        message = errorMessage,
        onDismiss = { viewModel.clearError() }
    )
}

@Composable
private fun CampaignsList(
    campaigns: List<Campaign>,
    onDeleteCampaign: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(campaigns, key = { it.id }) { campaign ->
            CampaignCard(
                campaign = campaign,
                onDelete = { onDeleteCampaign(campaign.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CampaignCard(
    campaign: Campaign,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = campaign.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Last played: ${formatDate(campaign.lastPlayedAt)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* TODO: Navigate to sessions */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("RESUME")
                }

                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("DELETE")
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Campaigns Yet",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Begin your adventure by creating your first campaign",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("SCRIBE NEW TALE")
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error Loading Campaigns",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Campaign",
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Campaign Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cover art picker coming in future iterations",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name.trim(), null)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(
                    text = "CREATE",
                    color = if (name.isNotBlank())
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "CANCEL",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}
