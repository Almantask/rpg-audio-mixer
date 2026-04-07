package com.example.rpgaudiomixer.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.domain.model.Campaign
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.model.TrackStats

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onEnterDomain: (String) -> Unit = {},
    onEnterScene: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        onEnterDomain = onEnterDomain,
        onEnterScene = onEnterScene
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onEnterDomain: (String) -> Unit,
    onEnterScene: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.testTag("HomeScreen_Loading"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is HomeUiState.Success -> {
                SuccessContent(
                    state = uiState,
                    onEnterDomain = onEnterDomain,
                    onEnterScene = onEnterScene
                )
            }
            is HomeUiState.Error -> {
                ErrorContent(message = uiState.message)
            }
        }
    }
}

@Composable
private fun SuccessContent(
    state: HomeUiState.Success,
    onEnterDomain: (String) -> Unit,
    onEnterScene: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("HomeScreen_Content"),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Active Campaign Card
        if (state.activeCampaign != null) {
            ActiveCampaignCard(
                campaign = state.activeCampaign,
                onEnterDomain = { onEnterDomain(state.activeCampaign.id) }
            )
        } else {
            EmptyCampaignCard()
        }

        // Resume Journey Card
        if (state.lastScene != null) {
            ResumeJourneyCard(
                scene = state.lastScene,
                onEnter = { onEnterScene(state.lastScene.id) }
            )
        }

        // Track Stats Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Atmosphere
            if (state.topAtmosphere != null) {
                TrackStatsCard(
                    title = "Top Atmosphere",
                    trackStats = state.topAtmosphere,
                    modifier = Modifier.weight(1f)
                )
            }

            // Legendary Action
            if (state.legendaryAction != null) {
                TrackStatsCard(
                    title = "Legendary Action",
                    trackStats = state.legendaryAction,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActiveCampaignCard(
    campaign: Campaign,
    onEnterDomain: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("HomeScreen_ActiveCampaign"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Active Campaign",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = campaign.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("HomeScreen_CampaignName")
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onEnterDomain,
                modifier = Modifier.testTag("HomeScreen_EnterDomainButton"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Enter Domain")
            }
        }
    }
}

@Composable
private fun EmptyCampaignCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("HomeScreen_EmptyCampaign"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No Active Campaign",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Create your first campaign to begin your adventure",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.testTag("HomeScreen_CreateCampaignPrompt")
            )
        }
    }
}

@Composable
private fun ResumeJourneyCard(
    scene: Scene,
    onEnter: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("HomeScreen_ResumeJourney"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Resume Journey",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scene.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("HomeScreen_LastSceneName")
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onEnter,
                modifier = Modifier.testTag("HomeScreen_EnterSceneButton"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Enter")
            }
        }
    }
}

@Composable
private fun TrackStatsCard(
    title: String,
    trackStats: TrackStats,
    modifier: Modifier = Modifier
) {
    val testTag = when (title) {
        "Top Atmosphere" -> "HomeScreen_TopAtmosphere"
        "Legendary Action" -> "HomeScreen_LegendaryAction"
        else -> "HomeScreen_TrackStats"
    }

    Card(
        modifier = modifier.testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trackStats.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.testTag("${testTag}_TrackName")
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${trackStats.playCount} plays",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String) {
    Column(
        modifier = Modifier.testTag("HomeScreen_Error"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("HomeScreen_ErrorMessage")
        )
    }
}
