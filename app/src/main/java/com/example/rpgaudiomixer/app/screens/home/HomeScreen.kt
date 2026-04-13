package com.example.rpgaudiomixer.app.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.domain.model.Campaign

@Composable
fun HomeScreen(
    onNavigateToSessions: (Long) -> Unit,
    onNavigateToCredits: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Home",
                onGearClick = onNavigateToCredits,
            )
        },
    ) { padding ->
        HomeScreenContent(
            uiState = uiState,
            onNavigateToSessions = onNavigateToSessions,
            onNavigateToCredits = onNavigateToCredits,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
internal fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToSessions: (Long) -> Unit,
    onNavigateToCredits: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("homeScreen"),
    ) {
        when (uiState) {
            is HomeUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is HomeUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is HomeUiState.Success -> {
                if (uiState.activeCampaign == null) {
                    HomeEmptyState(modifier = Modifier.align(Alignment.Center))
                } else {
                    HomeDashboard(
                        activeCampaign = uiState.activeCampaign,
                        topAtmosphereTrack = uiState.topAtmosphereTrack,
                        legendaryAction = uiState.legendaryAction,
                        onNavigateToSessions = onNavigateToSessions,
                    )
                }
            }
        }
    }
}

@Composable
internal fun HomeDashboard(
    activeCampaign: Campaign,
    topAtmosphereTrack: String?,
    legendaryAction: String?,
    onNavigateToSessions: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ActiveCampaignCard(
            campaign = activeCampaign,
            onEnterDomain = { onNavigateToSessions(activeCampaign.id) },
        )

        ResumeJourneyCard()

        TopAtmosphereCard(trackName = topAtmosphereTrack)

        LegendaryActionCard(actionName = legendaryAction)
    }
}

@Composable
internal fun ActiveCampaignCard(
    campaign: Campaign,
    onEnterDomain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("activeCampaignCard"),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ACTIVE CAMPAIGN",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = campaign.name,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(onClick = onEnterDomain) {
                    Text("ENTER DOMAIN")
                }
            }
        }
    }
}

@Composable
internal fun ResumeJourneyCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("resumeJourneyCard"),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "RESUME JOURNEY",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start a scene to see it here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun TopAtmosphereCard(
    trackName: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("topAtmosphereCard"),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "TOP ATMOSPHERE",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = trackName ?: "Play soundscapes to discover your favorite",
                style = MaterialTheme.typography.bodyMedium,
                color = if (trackName != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

@Composable
internal fun LegendaryActionCard(
    actionName: String?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("legendaryActionCard"),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "LEGENDARY ACTION",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = actionName ?: "Trigger effects to find your go-to",
                style = MaterialTheme.typography.bodyMedium,
                color = if (actionName != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
}

@Composable
internal fun HomeEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.testTag("homeEmptyState"),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "No campaigns yet",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Create a campaign to begin your adventure",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
