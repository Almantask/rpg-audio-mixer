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
import com.example.rpgaudiomixer.app.components.SoundboardScreen
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.media.MixedMusicPlayer

@Composable
fun HomeScreen(
    musicPlayer: MixedMusicPlayer,
    onOpenCampaign: (Long, String) -> Unit,
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
            Card(
                colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "Active Campaign",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = campaign.name,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.openCampaign(campaign.id)
                            onOpenCampaign(campaign.id, campaign.name)
                        },
                    ) {
                        Text("Enter Domain")
                    }
                }
            }
        } ?: EmptyStateView(
            title = "Create a campaign to begin your next story arc.",
            actionLabel = "Scribe New Tale",
            onAction = { },
            modifier = Modifier.fillMaxWidth(),
        )

        ErrorDialog(
            message = uiState.errorMessage,
            onDismiss = { },
        )

        SoundboardScreen(mixedMusicPlayer = musicPlayer)
    }
}
