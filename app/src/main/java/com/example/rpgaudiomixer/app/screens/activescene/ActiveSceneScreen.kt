package com.example.rpgaudiomixer.app.screens.activescene

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory

private val GoldColor = Color(0xFFFFD700)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSceneScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddSoundscape: () -> Unit = {},
    onNavigateToAddFx: () -> Unit = {},
    viewModel: ActiveSceneViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.testTag("activeSceneScreen"),
        topBar = {
            TopAppBar(
                title = { Text("Active Scene") },
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
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToAddFx,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Effect") },
                    modifier = Modifier.testTag("addFxFab"),
                )
                ExtendedFloatingActionButton(
                    onClick = onNavigateToAddSoundscape,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Add Soundscape") },
                    modifier = Modifier.testTag("addSoundscapeFab"),
                )
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            when (val state = uiState) {
                is ActiveSceneUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ActiveSceneUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                is ActiveSceneUiState.Success -> {
                    if (state.categories.isEmpty()) {
                        Text(
                            text = "No categories yet",
                            modifier = Modifier.align(Alignment.Center),
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(state.categories, key = { it.id }) { category ->
                                SoundscapeCategoryCard(
                                    category = category,
                                    isPlaying = category.name in state.playingCategories,
                                    onPlayPause = { viewModel.toggleCategory(category) },
                                    onD20Click = { viewModel.triggerD20(category) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundscapeCategoryCard(
    category: SoundscapeCategory,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onD20Click: () -> Unit = {},
) {
    var activeIntensity by rememberSaveable(category.id) { mutableIntStateOf(1) }

    val cardModifier = if (isPlaying) {
        Modifier
            .fillMaxWidth()
            .testTag("categoryCard_${category.name}")
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp),
            )
    } else {
        Modifier
            .fillMaxWidth()
            .testTag("categoryCard_${category.name}")
    }

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .testTag("categoryPlayingGlow_${category.name}")
                                .padding(end = 4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    IconButton(
                        onClick = { onD20Click() },
                        modifier = Modifier.testTag("d20Button_${category.name}"),
                    ) {
                        Text("D20")
                    }
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier.testTag("playPauseButton_${category.name}"),
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("I", "II", "III").forEachIndexed { index, label ->
                    val level = index + 1
                    Button(
                        onClick = { activeIntensity = level },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("intensityButton_${category.name}_$label"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeIntensity == level) GoldColor else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (activeIntensity == level) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Text(text = "Intensity Level $label")
                    }
                }
            }
        }
    }
}
