package com.example.rpgaudiomixer.ui.campaigns

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.Campaign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CampaignsTestTags {
    const val LIST = "Campaigns_List"
    const val EMPTY_ILLUSTRATION = "Campaigns_Empty_Illustration"
    const val EMPTY_BUTTON = "Campaigns_Empty_Button"
    const val NEW_BUTTON = "Campaigns_New_Button"
    const val CREATE_DIALOG = "Campaigns_Create_Dialog"
    const val NAME_INPUT = "Campaigns_Create_Name"
    const val COVER_ART_PICKER = "Campaigns_Create_Cover"
    const val COVER_ART_PREVIEW = "Campaigns_Create_Cover_Preview"
    const val SESSIONS_SCREEN = "Campaigns_Sessions_Screen"

    fun card(name: String): String = "Campaigns_Card_${name.asTagSuffix()}"
    fun resumeButton(name: String): String = "Campaigns_Resume_${name.asTagSuffix()}"
}

@Composable
fun CampaignsRoute(
    onOpenSessions: (Long) -> Unit,
    viewModel: CampaignsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        viewModel.onCoverArtPicked(uri?.toString())
    }

    CampaignsScreen(
        uiState = uiState,
        onNewCampaignClick = viewModel::openCreateDialog,
        onDraftNameChange = viewModel::updateDraftName,
        onConfirmCreate = viewModel::confirmCreateCampaign,
        onDismissCreate = viewModel::dismissCreateDialog,
        onPickCoverArt = {
            if (viewModel.useSystemPhotoPicker) {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            }
        },
        onDeleteCampaign = viewModel::deleteCampaign,
        onResumeCampaign = { campaign ->
            onOpenSessions(campaign.id)
        },
        onDismissError = viewModel::clearError,
    )
}

@Composable
fun CampaignsScreen(
    uiState: CampaignsUiState,
    onNewCampaignClick: () -> Unit,
    onDraftNameChange: (String) -> Unit,
    onConfirmCreate: () -> Unit,
    onDismissCreate: () -> Unit,
    onPickCoverArt: () -> Unit,
    onDeleteCampaign: (Campaign) -> Unit,
    onResumeCampaign: (Campaign) -> Unit,
    onDismissError: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(MainScreenTestTags.CAMPAIGNS),
    ) {
        if (uiState.campaigns.isEmpty()) {
            EmptyStateView(onCreateCampaign = onNewCampaignClick)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag(CampaignsTestTags.LIST),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = uiState.campaigns,
                    key = { campaign -> campaign.id },
                ) { campaign ->
                    SwipeToDeleteContainer(
                        campaign = campaign,
                        onDeleteCampaign = onDeleteCampaign,
                    ) {
                        CampaignCard(
                            campaign = campaign,
                            onResumeCampaign = onResumeCampaign,
                        )
                    }
                }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding()
                .testTag(CampaignsTestTags.NEW_BUTTON),
            onClick = onNewCampaignClick,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
            Text("New Campaign")
        }

        if (uiState.showCreateDialog) {
            CreateCampaignDialog(
                draftName = uiState.draftName,
                draftCoverArtUri = uiState.draftCoverArtUri,
                onDraftNameChange = onDraftNameChange,
                onConfirmCreate = onConfirmCreate,
                onDismissCreate = onDismissCreate,
                onPickCoverArt = onPickCoverArt,
            )
        }

        ErrorDialog(
            message = uiState.errorMessage,
            onDismiss = onDismissError,
        )
    }
}

@Composable
private fun EmptyStateView(
    onCreateCampaign: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp),
                )
                .testTag(CampaignsTestTags.EMPTY_ILLUSTRATION),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Collections,
                contentDescription = null,
                tint = ArcanumGold,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "No campaigns yet",
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "Scribe a new tale to begin your archive.",
        )
        FilledTonalButton(
            modifier = Modifier
                .padding(top = 20.dp)
                .testTag(CampaignsTestTags.EMPTY_BUTTON),
            onClick = onCreateCampaign,
        ) {
            Text("Scribe New Tale")
        }
    }
}

@Composable
private fun CreateCampaignDialog(
    draftName: String,
    draftCoverArtUri: String?,
    onDraftNameChange: (String) -> Unit,
    onConfirmCreate: () -> Unit,
    onDismissCreate: () -> Unit,
    onPickCoverArt: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.testTag(CampaignsTestTags.CREATE_DIALOG),
        onDismissRequest = onDismissCreate,
        title = {
            Text("New Campaign")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(CampaignsTestTags.NAME_INPUT),
                    value = draftName,
                    onValueChange = onDraftNameChange,
                    singleLine = true,
                    label = { Text("Campaign name") },
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                        .background(
                            color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp),
                        )
                        .clickable(onClick = onPickCoverArt)
                        .padding(16.dp)
                        .testTag(CampaignsTestTags.COVER_ART_PICKER),
                    contentAlignment = Alignment.Center,
                ) {
                    if (draftCoverArtUri == null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                            )
                            Text("Tap to choose cover art")
                        }
                    } else {
                        Text(
                            modifier = Modifier.testTag(CampaignsTestTags.COVER_ART_PREVIEW),
                            text = "Cover art selected",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmCreate) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissCreate) {
                Text("Cancel")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDeleteContainer(
    campaign: Campaign,
    onDeleteCampaign: (Campaign) -> Unit,
    content: @Composable () -> Unit,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart ||
                dismissValue == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
            ) {
                onDeleteCampaign(campaign)
                true
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(24.dp),
                    )
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "Move to Trash",
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
        content = content,
    )
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    onResumeCampaign: (Campaign) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CampaignsTestTags.card(campaign.name))
            .clickable { onResumeCampaign(campaign) },
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(18.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (campaign.coverArtUri == null) "No Art" else "Art",
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = campaign.name,
                    color = ArcanumGold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "Last played ${campaign.lastPlayedAt.toReadableDate()}",
                )
                Button(
                    modifier = Modifier.testTag(CampaignsTestTags.resumeButton(campaign.name)),
                    onClick = { onResumeCampaign(campaign) },
                ) {
                    Text("Resume")
                }
            }
        }
    }
}

private fun String.asTagSuffix(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "_")
    .trim('_')

private fun Long.toReadableDate(): String {
    val format = SimpleDateFormat("MMM d, yyyy", Locale.US)
    return format.format(Date(this))
}
