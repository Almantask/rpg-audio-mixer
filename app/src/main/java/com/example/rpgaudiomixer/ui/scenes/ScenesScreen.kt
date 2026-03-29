package com.example.rpgaudiomixer.ui.scenes

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
fun ScenesScreen(
    modifier: Modifier = Modifier,
    viewModel: ScenesViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (uiState) {
            is ScenesUiState.Loading -> CircularProgressIndicator()
            is ScenesUiState.Error -> Text((uiState as ScenesUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is ScenesUiState.Success -> {
                val scenes = (uiState as ScenesUiState.Success).scenes
                if (scenes.isEmpty()) {
                    Text("No scenes found", style = MaterialTheme.typography.bodyLarge)
                } else {
                    scenes.forEach { scene ->
                        Text(scene.name, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}
