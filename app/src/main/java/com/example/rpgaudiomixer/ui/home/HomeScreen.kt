package com.example.rpgaudiomixer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.ui.UiState

@Composable
fun HomeRoute(
    onOpenCampaign: (Long) -> Unit,
    onResumeScene: (Long) -> Unit,
    onBrowseCampaigns: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onOpenCampaign = onOpenCampaign,
        onResumeScene = onResumeScene,
        onBrowseCampaigns = onBrowseCampaigns,
        modifier = modifier,
    )
}

@Composable
private fun HomeScreen(
    uiState: UiState<HomeDashboardContent>,
    onOpenCampaign: (Long) -> Unit,
    onResumeScene: (Long) -> Unit,
    onBrowseCampaigns: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.Home,
                    title = "Unable to load home",
                    body = uiState.message,
                    actionLabel = "Browse Campaigns",
                    onAction = onBrowseCampaigns,
                )
            }

            is UiState.Success -> {
                HomeDashboard(
                    content = uiState.data,
                    onOpenCampaign = onOpenCampaign,
                    onResumeScene = onResumeScene,
                    onBrowseCampaigns = onBrowseCampaigns,
                )
            }
        }
    }
}

@Composable
private fun HomeDashboard(
    content: HomeDashboardContent,
    onOpenCampaign: (Long) -> Unit,
    onResumeScene: (Long) -> Unit,
    onBrowseCampaigns: () -> Unit,
) {
    if (content.activeCampaign == null) {
        EmptyStateView(
            modifier = Modifier.fillMaxSize(),
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            title = "No active campaign",
            body = "Create or open a campaign to unlock your Home dashboard.",
            actionLabel = "Open Campaigns",
            onAction = onBrowseCampaigns,
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            DashboardSectionTitle(title = "ACTIVE CAMPAIGN")
        }
        item {
            HeroDashboardCard(
                icon = Icons.AutoMirrored.Rounded.MenuBook,
                title = content.activeCampaign.name,
                body = content.activeCampaign.coverArtUri ?: "Your most recently played campaign is ready.",
                actionLabel = "ENTER DOMAIN",
                onAction = { onOpenCampaign(content.activeCampaign.id) },
            )
        }
        item {
            DashboardSectionTitle(title = "RESUME JOURNEY")
        }
        item {
            ResumeJourneyCard(
                scene = content.resumeScene,
                onResumeScene = onResumeScene,
            )
        }
        item {
            DashboardSectionTitle(title = "TOP ATMOSPHERE")
        }
        item {
            AudioHighlightCard(
                icon = Icons.Rounded.MusicNote,
                title = content.topAtmosphere?.trackName ?: "No atmosphere plays yet",
                subtitle = content.topAtmosphere?.let { "${it.categoryName} • ${it.playCount} plays" }
                    ?: "Start a soundscape from an active scene to crown a favourite.",
            )
        }
        item {
            DashboardSectionTitle(title = "LEGENDARY ACTION")
        }
        item {
            AudioHighlightCard(
                icon = Icons.Rounded.Bolt,
                title = content.legendaryAction?.trackName ?: "No legendary action yet",
                subtitle = content.legendaryAction?.let { "${it.categoryName} • ${it.playCount} plays" }
                    ?: "Trigger an effect in the soundboard to feature it here.",
            )
        }
    }
}

@Composable
private fun DashboardSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun HeroDashboardCard(
    icon: ImageVector,
    title: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Button(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
private fun ResumeJourneyCard(
    scene: Scene?,
    onResumeScene: (Long) -> Unit,
) {
    if (scene == null) {
        AudioHighlightCard(
            icon = Icons.Rounded.Explore,
            title = "No recent scene",
            subtitle = "Open a session scene to keep your journey within reach.",
        )
        return
    }

    HeroDashboardCard(
        icon = Icons.Rounded.AutoStories,
        title = scene.name,
        body = scene.description ?: "Return to the latest opened scene with autoplay.",
        actionLabel = "ENTER",
        onAction = { onResumeScene(scene.id) },
    )
}

@Composable
private fun AudioHighlightCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
