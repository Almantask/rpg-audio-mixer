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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.Campaign

@Composable
fun CampaignsScreen(
    viewModel: CampaignsViewModel = hiltViewModel(),
    onNavigateToSessions: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is CampaignsUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is CampaignsUiState.Error -> {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is CampaignsUiState.Success -> {
                if (state.campaigns.isEmpty()) {
                    EmptyState(
                        onCreateClick = { showCreateDialog = true },
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    CampaignsList(
                        campaigns = state.campaigns,
                        onCampaignClick = onNavigateToSessions,
                        onDeleteCampaign = viewModel::deleteCampaign,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Campaign"
                    )
                }
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

        ErrorDialog(
            message = errorMessage,
            onDismiss = viewModel::dismissError
        )
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
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(campaigns, key = { it.id }) { campaign ->
            CampaignCard(
                campaign = campaign,
                onClick = { onCampaignClick(campaign.id) },
                onDelete = { onDeleteCampaign(campaign.id) }
            )
        }
    }
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last played: ${formatTimestamp(campaign.lastPlayedAt)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
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
            text = "No Campaigns",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Create your first campaign to begin",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Scribe New Tale")
        }
    }
}

@Composable
private fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<Uri?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        coverUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Campaign") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Campaign Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (coverUri == null) "Select Cover Art" else "Cover Art Selected")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, coverUri?.toString()) },
                enabled = name.isNotBlank()
            ) {
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

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 0 -> "$days days ago"
        hours > 0 -> "$hours hours ago"
        minutes > 0 -> "$minutes minutes ago"
        else -> "Just now"
    }
}
