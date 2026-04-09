package com.example.rpgaudiomixer.app.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.domain.campaign.Campaign
import com.example.rpgaudiomixer.app.ui.campaigns.CampaignsViewModel
import com.example.rpgaudiomixer.app.ui.campaigns.CampaignsUiState
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CampaignsScreen(
    viewModel: CampaignsViewModel = hiltViewModel(),
    onCampaignClick: (Long, String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is CampaignsUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ArcanumGold
                )
            }
            is CampaignsUiState.Success -> {
                if (state.campaigns.isEmpty()) {
                    EmptyStateView(
                        illustration = Icons.Default.HistoryEdu,
                        title = "Your chronicle is blank...",
                        subtitle = "Begin your next legend today.",
                        actionLabel = "SCRIBE NEW TALE",
                        onAction = { showCreateDialog = true }
                    )
                } else {
                    CampaignsList(
                        campaigns = state.campaigns,
                        onCampaignClick = onCampaignClick,
                        onDelete = { viewModel.deleteCampaign(it) }
                    )
                }
            }
            is CampaignsUiState.Error -> {
                Text(
                    text = state.message,
                    color = ArcanumErrorRed,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = ArcanumGold,
            contentColor = ArcanumOnGold
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Campaign")
        }

        if (showCreateDialog) {
            CreateCampaignDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name, uri ->
                    viewModel.createCampaign(name, uri)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun CampaignsList(
    campaigns: List<Campaign>,
    onCampaignClick: (Long, String) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(campaigns, key = { it.id }) { campaign ->
            SwipeToDeleteContainer(
                onDelete = { onDelete(campaign.id) }
            ) {
                CampaignCard(
                    campaign = campaign,
                    onClick = { onCampaignClick(campaign.id, campaign.name) }
                )
            }
        }
    }
}

@Composable
fun CampaignCard(
    campaign: Campaign,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArcanumCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for Cover Art
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArcanumBlack),
                contentAlignment = Alignment.Center
            ) {
                if (campaign.coverArtUri != null) {
                    // In a real app we'd use Coil or similar
                    Text("📷", fontSize = 24.sp)
                } else {
                    Icon(
                        Icons.Default.HistoryEdu,
                        contentDescription = null,
                        tint = ArcanumMutedGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = ArcanumGold,
                    maxLines = 1
                )
                Text(
                    text = "Last Played: ${formatDate(campaign.lastPlayedAt)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcanumOnSurface.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArcanumGold)),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("RESUME", color = ArcanumGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var coverUri by remember { mutableStateOf<String?>(null) }
    
    val pickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        coverUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumCard,
        title = { Text("New Campaign", color = ArcanumGold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Campaign Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArcanumGold,
                        focusedLabelColor = ArcanumGold,
                        unfocusedLabelColor = ArcanumOnSurface.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedButton(
                    onClick = { 
                        pickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ArcanumGold)
                ) {
                    Text(if (coverUri == null) "PICK COVER ART" else "CHANGE PHOTO")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name, coverUri) },
                enabled = name.isNotBlank()
            ) {
                Text("SCRIBE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ArcanumOnSurface)
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
