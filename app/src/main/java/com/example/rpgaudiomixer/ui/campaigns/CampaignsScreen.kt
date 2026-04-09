package com.example.rpgaudiomixer.ui.campaigns

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.ui.common.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CampaignsScreenTestTags {
    const val EMPTY_STATE = "Campaigns_EmptyState"
    const val NEW_CAMPAIGN_BUTTON = "Campaigns_NewCampaignButton"
    const val CREATE_DIALOG = "Campaigns_CreateDialog"
    const val NAME_INPUT = "Campaigns_NameInput"
    const val COVER_ART_BUTTON = "Campaigns_CoverArtButton"
}

@Composable
fun CampaignsRoute(
    onOpenCampaign: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CampaignsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CampaignsScreen(
        uiState = uiState,
        onCreateCampaign = viewModel::createCampaign,
        onDeleteCampaign = viewModel::deleteCampaign,
        onOpenCampaign = onOpenCampaign,
        modifier = modifier,
    )
}

@Composable
fun CampaignsScreen(
    uiState: UiState<List<Campaign>>,
    onCreateCampaign: (String, String?) -> Unit,
    onDeleteCampaign: (Long) -> Unit,
    onOpenCampaign: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (val state = uiState) {
            UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is UiState.Error -> {
                var errorMessage by remember(state.message) { mutableStateOf(state.message) }
                ErrorDialog(
                    message = errorMessage,
                    onDismiss = { errorMessage = null },
                )
            }

            is UiState.Success -> {
                if (state.data.isEmpty()) {
                        EmptyStateView(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .testTag(CampaignsScreenTestTags.EMPTY_STATE),
                            illustration = Icons.Default.MenuBook,
                            title = "No campaigns yet",
                            actionLabel = "Scribe New Tale",
                            onActionClick = { showCreateDialog = true },
                        )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        items(
                            items = state.data,
                            key = Campaign::id,
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
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag(CampaignsScreenTestTags.NEW_CAMPAIGN_BUTTON),
                                onClick = { showCreateDialog = true },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                )
                                Spacer(modifier = Modifier.size(8.dp))
                                Text(text = "New Campaign")
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreateCampaignDialog(
                onDismiss = { showCreateDialog = false },
                onCreateCampaign = { name, coverArtUri ->
                    onCreateCampaign(name, coverArtUri)
                    showCreateDialog = false
                },
            )
        }
    }
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    onOpenCampaign: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenCampaign),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CampaignCoverArt(
                coverArtUri = campaign.coverArtUri,
                modifier = Modifier.size(88.dp),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = campaign.lastPlayedAt.toLastPlayedLabel(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextButton(onClick = onOpenCampaign) {
                    Text(text = "Resume")
                }
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
    var coverArtUri by rememberSaveable { mutableStateOf<String?>(null) }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            coverArtUri = uri?.toString()
        },
    )

    AlertDialog(
        modifier = Modifier.testTag(CampaignsScreenTestTags.CREATE_DIALOG),
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Create a New Campaign")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CampaignsScreenTestTags.NAME_INPUT),
                    value = campaignName,
                    onValueChange = { campaignName = it },
                    singleLine = true,
                    label = {
                        Text(text = "Campaign name")
                    },
                )
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CampaignsScreenTestTags.COVER_ART_BUTTON),
                    onClick = {
                        photoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (coverArtUri == null) {
                            "Choose Cover Art"
                        } else {
                            "Cover Art Selected"
                        },
                    )
                }
                if (coverArtUri != null) {
                    CampaignCoverArt(
                        coverArtUri = coverArtUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreateCampaign(campaignName, coverArtUri) },
                enabled = campaignName.isNotBlank(),
            ) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
    )
}

@Composable
private fun CampaignCoverArt(
    coverArtUri: String?,
    modifier: Modifier = Modifier,
) {
    if (coverArtUri == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                ),
                shape = RoundedCornerShape(16.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
        return
    }

    AsyncImage(
        model = coverArtUri,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
    )
}

private fun Long.toLastPlayedLabel(): String {
    if (this <= 0L) {
        return "Not played yet"
    }

    return "Last played ${campaignDateFormatter().format(Date(this))}"
}

private fun campaignDateFormatter(): SimpleDateFormat = SimpleDateFormat(
    "MMM d, yyyy",
    Locale.getDefault(),
)
