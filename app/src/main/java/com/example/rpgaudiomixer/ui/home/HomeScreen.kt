package com.example.rpgaudiomixer.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun HomeScreen(
    onOpenCampaign: (Long, String) -> Unit,
    onOpenScene: (Scene, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Welcome back to Arcanum Audio.",
            style = MaterialTheme.typography.bodyLarge,
        )

        uiState.activeCampaign?.let { campaign ->
            DashboardCard(
                title = "Active Campaign",
                headline = campaign.name,
                supporting = "Pick up where your table last left off.",
                actionLabel = "Enter Domain",
                onAction = {
                    viewModel.openCampaign(campaign.id)
                    onOpenCampaign(campaign.id, campaign.name)
                },
            )
        } ?: EmptyStateView(
            title = "Create a campaign to begin your next story arc.",
            actionLabel = "Scribe New Tale",
            onAction = { },
            modifier = Modifier.fillMaxWidth(),
        )

        uiState.resumeScene?.let { scene ->
            DashboardCard(
                title = "Resume Journey",
                headline = scene.name,
                supporting = "Return straight to your last opened scene.",
                actionLabel = "Enter",
                onAction = { onOpenScene(scene, true) },
            )
        }

        uiState.topAtmosphere?.let { track ->
            DashboardCard(
                title = "Top Atmosphere",
                headline = track.name,
                supporting = track.categoryName,
            )
        }

        uiState.legendaryAction?.let { track ->
            DashboardCard(
                title = "Legendary Action",
                headline = track.name,
                supporting = track.tags.firstOrNull() ?: "Sound Effect",
            )
        }

        uiState.errorMessage?.let { message ->
            ErrorDialog(
                message = message,
                onDismiss = { },
            )
        }
    }
}

@Composable
private fun DashboardCard(
    title: String,
    headline: String,
    supporting: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (actionLabel != null && onAction != null) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAction,
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}
