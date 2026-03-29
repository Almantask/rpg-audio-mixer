package com.example.rpgaudiomixer.ui.composer

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
fun ComposerScreen(
    categoryId: Long,
    modifier: Modifier = Modifier,
    viewModel: ComposerViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(categoryId) {
        viewModel.loadCategory(categoryId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (uiState) {
            is ComposerUiState.Loading -> CircularProgressIndicator()
            is ComposerUiState.Error -> Text((uiState as ComposerUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is ComposerUiState.Success -> {
                val category = (uiState as ComposerUiState.Success).category
                Text("Editing: ${category.name}", style = MaterialTheme.typography.headlineSmall)
                androidx.compose.foundation.lazy.LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(category.intensityLevels.size) { idx ->
                        val level = category.intensityLevels[idx]
                        Column {
                            Text("Intensity Level: ${level.level}", style = MaterialTheme.typography.titleMedium)
                            if (level.tracks.isEmpty()) {
                                Text("No tracks", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                level.tracks.forEach { track ->
                                    androidx.compose.foundation.layout.Row(
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(track.name, modifier = Modifier.weight(1f))
                                        // TODO: Add artwork preview if needed
                                        // IconButton removed due to icon compatibility issues
                                    }
                                }
                            }
                            androidx.compose.material3.Button(
                                onClick = { /* TODO: Add track to this level */ },
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text("Add Track")
                            }
                        }
                    }
                }
                androidx.compose.material3.Button(
                    onClick = { /* TODO: Add new intensity level */ },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Add Intensity Level")
                }
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    androidx.compose.material3.Button(
                        onClick = { /* TODO: Save changes */ },
                        enabled = true
                    ) { Text("Save") }
                    androidx.compose.material3.OutlinedButton(
                        onClick = { /* TODO: Cancel changes */ }
                    ) { Text("Cancel") }
                }
            }
        }
    }
}
