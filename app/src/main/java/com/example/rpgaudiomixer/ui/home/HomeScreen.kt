package com.example.rpgaudiomixer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.screens.MainScreenTestTags
import com.example.rpgaudiomixer.domain.model.FxTrack

object HomeTestTags {
    const val ACTIVE_CAMPAIGN_CARD = "Home_ActiveCampaign_Card"
    const val ACTIVE_CAMPAIGN_EMPTY = "Home_ActiveCampaign_Empty"
    const val ENTER_DOMAIN_BUTTON = "Home_EnterDomain_Button"
    const val RESUME_CARD = "Home_Resume_Card"
    const val RESUME_EMPTY = "Home_Resume_Empty"
    const val RESUME_ENTER_BUTTON = "Home_Resume_Enter_Button"
    const val TOP_ATMOSPHERE_CARD = "Home_TopAtmosphere_Card"
    const val LEGENDARY_ACTION_CARD = "Home_LegendaryAction_Card"
}

@Composable
fun HomeRoute(
    onOpenCampaign: (Long) -> Unit,
    onOpenResumeScene: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    HomeScreen(
        uiState = uiState,
        onOpenCampaign = {
            uiState.activeCampaign?.id?.let(onOpenCampaign)
        },
        onOpenResumeScene = {
            uiState.resumeScene?.sceneId?.let(onOpenResumeScene)
        },
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onOpenCampaign: () -> Unit,
    onOpenResumeScene: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag(MainScreenTestTags.HOME),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        HomeSectionTitle("Active Campaign")
        if (uiState.activeCampaign == null) {
            PlaceholderCard(
                modifier = Modifier.testTag(HomeTestTags.ACTIVE_CAMPAIGN_EMPTY),
                title = "Create or open a campaign to enter your domain.",
                supportingText = "Your most recently played campaign will appear here.",
            )
        } else {
            ActionCard(
                modifier = Modifier.testTag(HomeTestTags.ACTIVE_CAMPAIGN_CARD),
                title = uiState.activeCampaign.name,
                supportingText = "Return to your campaign sessions.",
                buttonLabel = "Enter Domain",
                buttonTag = HomeTestTags.ENTER_DOMAIN_BUTTON,
                onClick = onOpenCampaign,
            )
        }

        if (uiState.activeCampaign != null) {
            HomeSectionTitle("Resume Journey")
            if (uiState.resumeScene == null) {
                PlaceholderCard(
                    modifier = Modifier.testTag(HomeTestTags.RESUME_EMPTY),
                    title = "Open a scene to resume your journey.",
                    supportingText = "Your last opened scene in this campaign will appear here.",
                )
            } else {
                ActionCard(
                    modifier = Modifier.testTag(HomeTestTags.RESUME_CARD),
                    title = uiState.resumeScene.sceneName,
                    supportingText = uiState.resumeScene.sceneDescription ?: "Jump back into the last opened scene.",
                    buttonLabel = "Enter",
                    buttonTag = HomeTestTags.RESUME_ENTER_BUTTON,
                    onClick = onOpenResumeScene,
                )
            }
        }

        HomeSectionTitle("Top Atmosphere")
        SummaryCard(
            modifier = Modifier.testTag(HomeTestTags.TOP_ATMOSPHERE_CARD),
            title = uiState.topAtmosphere?.trackName ?: "No atmosphere played yet",
            supportingText = uiState.topAtmosphere?.categoryName ?: "Play a soundscape loop to crown a favorite.",
        )

        HomeSectionTitle("Legendary Action")
        SummaryCard(
            modifier = Modifier.testTag(HomeTestTags.LEGENDARY_ACTION_CARD),
            title = uiState.legendaryAction?.name ?: "No legendary action yet",
            supportingText = uiState.legendaryAction.toLegendaryActionSubtitle(),
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun HomeSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun ActionCard(
    title: String,
    supportingText: String,
    buttonLabel: String,
    buttonTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                modifier = Modifier.testTag(buttonTag),
                onClick = onClick,
            ) {
                Text(buttonLabel)
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    supportingText: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun PlaceholderCard(
    title: String,
    supportingText: String,
    modifier: Modifier = Modifier,
) {
    SummaryCard(
        modifier = modifier,
        title = title,
        supportingText = supportingText,
    )
}

private fun FxTrack?.toLegendaryActionSubtitle(): String {
    val categoryLabel = this?.tags?.firstOrNull()
    return when {
        this == null -> "Trigger an FX track to earn a legend."
        categoryLabel.isNullOrBlank() -> "Most played FX"
        else -> categoryLabel
    }
}
