package com.example.rpgaudiomixer.ui.audiolibrary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AudioLibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: AudioLibraryViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Soundscapes", "Sound Effects")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }
        when (uiState) {
            is AudioLibraryUiState.Loading -> CircularProgressIndicator()
            is AudioLibraryUiState.Error -> Text((uiState as AudioLibraryUiState.Error).message, color = MaterialTheme.colorScheme.error)
            is AudioLibraryUiState.Success -> {
                val soundscapes = (uiState as AudioLibraryUiState.Success).soundscapes
                val fx = (uiState as AudioLibraryUiState.Success).fx
                if (selectedTab == 0) {
                    if (soundscapes.isEmpty()) {
                        Text("No soundscape categories found", style = MaterialTheme.typography.bodyLarge)
                    } else {
                        soundscapes.forEach { cat ->
                            Text(cat.name, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                } else {
                    if (fx.isEmpty()) {
                        Text("No sound effects found", style = MaterialTheme.typography.bodyLarge)
                    } else {
                        fx.forEach { f ->
                            Text(f.name, style = MaterialTheme.typography.headlineSmall)
                        }
                    }
                }
            }
        }
    }
}
