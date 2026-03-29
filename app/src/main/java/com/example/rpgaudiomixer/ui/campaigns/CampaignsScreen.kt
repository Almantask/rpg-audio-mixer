package com.example.rpgaudiomixer.ui.campaigns

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CampaignsScreen(
    modifier: Modifier = Modifier,
    viewModel: CampaignsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (uiState) {
            is CampaignsUiState.Loading -> CircularProgressIndicator()
            is CampaignsUiState.Error -> Text((uiState as CampaignsUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is CampaignsUiState.Success -> {
                val campaigns = (uiState as CampaignsUiState.Success).campaigns
                if (campaigns.isEmpty()) {
                    Text("No campaigns found", style = MaterialTheme.typography.bodyLarge)
                } else {
                    campaigns.forEach { campaign ->
                        Text(campaign.name, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}
