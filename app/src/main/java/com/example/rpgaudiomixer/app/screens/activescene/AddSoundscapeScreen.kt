package com.example.rpgaudiomixer.app.screens.activescene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSoundscapeScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddSoundscapeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.testTag("addSoundscapeScreen"),
        topBar = {
            TopAppBar(
                title = { Text("Add Soundscape") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            if (uiState.availableTracks.isEmpty()) {
                Text(
                    text = "No audio tracks in library",
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                val addedNames = uiState.addedCategories.map { it.name }.toSet()
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(uiState.availableTracks, key = { it.id }) { track ->
                        val alreadyAdded = track.displayName in addedNames
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = track.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f),
                            )
                            if (alreadyAdded) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Already added",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.testTag("alreadyAddedIndicator_${track.displayName}"),
                                )
                            } else {
                                IconButton(
                                    onClick = { viewModel.addSoundscape(track.displayName) },
                                    modifier = Modifier.testTag("addSoundscapeButton_${track.displayName}"),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add soundscape",
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
