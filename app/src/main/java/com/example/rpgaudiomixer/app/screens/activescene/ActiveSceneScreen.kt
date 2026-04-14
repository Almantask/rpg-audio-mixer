package com.example.rpgaudiomixer.app.screens.activescene

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StopCircle
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
                actions = {
                    val locked = (uiState as? ActiveSceneUiState.Success)?.isLocked ?: false
                    // Global Stop button
                    IconButton(
                        onClick = { viewModel.globalStop() },
                        enabled = !locked,
                        modifier = Modifier.testTag("globalStopButton"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.StopCircle,
                            contentDescription = "Global Stop",
                        )
                    }
                    // Lock / Unlock – long-press to unlock
                    IconButton(
                        onClick = { if (!locked) viewModel.lock() },
                        modifier = Modifier
                            .testTag("lockButton")
                            .combinedClickable(
                                onClick = { if (!locked) viewModel.lock() },
                                onLongClick = { if (locked) viewModel.unlock() },
                            ),
                    ) {
                        Icon(
                            imageVector = if (locked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (locked) "Locked" else "Unlocked",
                            tint = if (locked) GoldColor else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            val locked = (uiState as? ActiveSceneUiState.Success)?.isLocked ?: false
            if (!locked) {
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
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Master Intensity selector
                        MasterIntensitySelector(
                            selectedLevel = state.masterIntensity,
                            isLocked = state.isLocked,
                            onLevelSelected = { viewModel.setMasterIntensity(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("masterIntensity"),
                        )

                        if (state.categories.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("No categories yet")
                            }
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
                                        isLocked = state.isLocked,
                                        masterIntensity = state.masterIntensity,
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
}

@Composable
private fun MasterIntensitySelector(
    selectedLevel: Int,
    isLocked: Boolean,
    onLevelSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf("I", "II", "III").forEachIndexed { index, label ->
            val level = index + 1
            Button(
                onClick = { onLevelSelected(level) },
                enabled = !isLocked,
                modifier = Modifier
                    .weight(1f)
                    .testTag("masterIntensityButton_$label"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedLevel == level) GoldColor else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedLevel == level) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                ),
            ) {
                Text("Intensity Level $label")
            }
        }
    }
}

@Composable
private fun SoundscapeCategoryCard(
    category: SoundscapeCategory,
    isPlaying: Boolean,
    isLocked: Boolean,
    masterIntensity: Int,
    onPlayPause: () -> Unit,
    onD20Click: () -> Unit = {},
) {
    var activeIntensity by rememberSaveable(category.id) { mutableIntStateOf(masterIntensity) }

    // Sync per-card intensity when master changes
    activeIntensity = masterIntensity

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
                        enabled = !isLocked,
                        modifier = Modifier.testTag("d20Button_${category.name}"),
                    ) {
                        Text(
                            text = "D20",
                            color = if (isLocked) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                    IconButton(
                        onClick = onPlayPause,
                        enabled = !isLocked,
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
                        enabled = !isLocked,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("intensityButton_${category.name}_$label"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeIntensity == level) GoldColor else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (activeIntensity == level) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                        ),
                    ) {
                        Text(text = "Intensity Level $label")
                    }
                }
            }
        }
    }
}
