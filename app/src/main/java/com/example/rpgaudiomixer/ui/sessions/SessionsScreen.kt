package com.example.rpgaudiomixer.ui.sessions

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
fun SessionsScreen(
    campaignId: Long,
    modifier: Modifier = Modifier,
    viewModel: SessionsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load sessions when entering the screen
    androidx.compose.runtime.LaunchedEffect(campaignId) {
        viewModel.loadSessions(campaignId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (uiState) {
            is SessionsUiState.Loading -> CircularProgressIndicator()
            is SessionsUiState.Error -> Text((uiState as SessionsUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is SessionsUiState.Success -> {
                val sessions = (uiState as SessionsUiState.Success).sessions
                if (sessions.isEmpty()) {
                    Text("No sessions found", style = MaterialTheme.typography.bodyLarge)
                } else {
                    sessions.forEach { session ->
                        Text(session.name, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}
