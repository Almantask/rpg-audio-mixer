package com.example.rpgaudiomixer.app.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumEmptyState

@Suppress("kotlin:S6615")
@Composable
fun HomeScreen(
    onNavigateToCampaignSessions: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is HomeUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                is HomeUiState.Success -> {
                    HomeContent(
                        state = state,
                        onNavigateToCampaignSessions = onNavigateToCampaignSessions
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Success,
    onNavigateToCampaignSessions: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Campaign Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("campaignCard")
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (state.latestCampaign != null) {
                    Text(
                        text = state.latestCampaign.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { onNavigateToCampaignSessions(state.latestCampaign.id) }) {
                        Text("Enter Domain")
                    }
                } else {
                    ArcanumEmptyState(
                        icon = Icons.Default.Campaign,
                        title = "No Active Campaign",
                        ctaText = "Create your first campaign",
                        onCtaClick = {}
                    )
                }
            }
        }

        // Resume Journey Card — only shown when both campaign and scene exist
        if (state.latestCampaign != null && state.latestScene != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("resumeJourneyCard")
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.latestScene.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* scene playback — coming later */ }) {
                        Text("Enter")
                    }
                }
            }
        }
    }
}
