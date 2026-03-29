package com.example.rpgaudiomixer.ui.sessionscenes

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
fun SessionScenesScreen(
    sessionId: Long,
    modifier: Modifier = Modifier,
    viewModel: SessionScenesViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Load scenes when entering the screen
    androidx.compose.runtime.LaunchedEffect(sessionId) {
        viewModel.loadScenes(sessionId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (uiState) {
            is SessionScenesUiState.Loading -> CircularProgressIndicator()
            is SessionScenesUiState.Error -> Text((uiState as SessionScenesUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is SessionScenesUiState.Success -> {
                val scenes = (uiState as SessionScenesUiState.Success).scenes
                if (scenes.isEmpty()) {
                    Text("No scenes linked to this session", style = MaterialTheme.typography.bodyLarge)
                } else {
                    scenes.forEach { scene ->
                        Text(scene.name, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}
