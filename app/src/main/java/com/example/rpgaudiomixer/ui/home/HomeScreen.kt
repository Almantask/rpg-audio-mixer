package com.example.rpgaudiomixer.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.common.UiState

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCampaign: (Long) -> Unit = {},
    onNavigateToScene: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is UiState.Success -> {
                if (state.data.activeCampaign == null && state.data.resumeScene == null) {
                    EmptyStateView(
                        title = "Welcome to Arcanum Audio",
                        message = "Start by creating your first campaign or scene",
                        actionLabel = "GET STARTED",
                        onAction = { /* Navigate to campaigns */ }
                    )
                } else {
                    HomeScreenContent(
                        state = state.data,
                        onNavigateToCampaign = onNavigateToCampaign,
                        onNavigateToScene = onNavigateToScene
                    )
                }
            }
            is UiState.Error -> {
                EmptyStateView(
                    title = "Error",
                    message = state.message,
                    actionLabel = "Retry",
                    onAction = { /* Retry */ }
                )
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    onNavigateToCampaign: (Long) -> Unit,
    onNavigateToScene: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Campaign Card
        item {
            state.activeCampaign?.let { campaign ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToCampaign(campaign.id) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "ACTIVE CAMPAIGN",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = campaign.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigateToCampaign(campaign.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ENTER DOMAIN")
                        }
                    }
                }
            }
        }

        // Resume Journey Card
        item {
            state.resumeScene?.let { scene ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToScene(scene.id) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "RESUME JOURNEY",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = scene.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        scene.description?.let { description ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigateToScene(scene.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ENTER")
                        }
                    }
                }
            }
        }

        // Statistics Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Atmosphere
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "TOP ATMOSPHERE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.topAtmosphere?.name ?: "None",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (state.topAtmosphere != null) {
                            Text(
                                text = "${state.topAtmosphere.playCount} plays",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Legendary Action
                Card(
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "LEGENDARY ACTION",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.legendaryAction?.name ?: "None",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (state.legendaryAction != null) {
                            Text(
                                text = "${state.legendaryAction.playCount} plays",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
