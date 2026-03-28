package com.example.rpgaudiomixer.ui.campaigns

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.rpgaudiomixer.app.theme.ArcanumBorder
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.app.theme.ArcanumGrayMid
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState
import com.example.rpgaudiomixer.ui.components.PrimaryButton

@Composable
fun CampaignsScreen(
    onOpenCampaign: (Long) -> Unit,
    onCredits: () -> Unit,
    viewModel: CampaignsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(onCredits = onCredits)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (state.campaigns.isEmpty() && !state.isLoading) {
                item {
                    EmptyState(
                        title = "No Campaigns Yet",
                        subtitle = "Scribe your first tale below.",
                    )
                }
            }

            // Hero card for the first (most recent) campaign
            state.campaigns.firstOrNull()?.let { hero ->
                item {
                    CampaignHeroCard(
                        campaign = hero,
                        onResume = { onOpenCampaign(hero.id) },
                    )
                }
            }

            // Smaller cards for the rest
            items(state.campaigns.drop(1), key = { it.id }) { campaign ->
                CampaignCard(
                    campaign = campaign,
                    onClick = { onOpenCampaign(campaign.id) },
                )
            }

            // "Scribe New Tale" card at the bottom
            item {
                NewCampaignCard(onClick = { showAddDialog = true })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showAddDialog) {
        AddCampaignDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description ->
                viewModel.addCampaign(name, description)
                showAddDialog = false
            },
        )
    }
}

@Composable
private fun CampaignHeroCard(campaign: Campaign, onResume: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .height(240.dp)
            .background(ArcanumCardSurface)
            .clickable { onResume() },
    ) {
        if (campaign.coverArtUri != null) {
            AsyncImage(
                model = campaign.coverArtUri,
                contentDescription = campaign.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)))
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Text(
                text = campaign.name,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (campaign.description.isNotBlank()) {
                Text(
                    text = campaign.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ArcanumGrayLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            PrimaryButton(text = "RESUME", onClick = onResume, modifier = Modifier.fillMaxWidth(0.5f))
        }
    }
}

@Composable
private fun CampaignCard(campaign: Campaign, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCardSurface)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ArcanumBorder),
        ) {
            if (campaign.coverArtUri != null) {
                AsyncImage(
                    model = campaign.coverArtUri,
                    contentDescription = campaign.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = campaign.name,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (campaign.description.isNotBlank()) {
                Text(
                    text = campaign.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ArcanumGrayMid,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        PrimaryButton(text = "OPEN", onClick = onClick)
    }
}

@Composable
private fun NewCampaignCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCardSurface)
            .clickable { onClick() }
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = ArcanumGold,
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "  ✦ SCRIBE NEW TALE",
                style = MaterialTheme.typography.titleMedium,
                color = ArcanumGold,
            )
        }
    }
}

@Composable
private fun AddCampaignDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Campaign") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Campaign Name") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), description.trim()) },
            ) {
                Text("SCRIBE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        },
    )
}
