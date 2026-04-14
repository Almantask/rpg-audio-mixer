package com.example.rpgaudiomixer.app.screens.activescene

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Suppress("kotlin:S6615")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSceneScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCredits: () -> Unit,
    viewModel: ActiveSceneViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.testTag("activeSceneScreen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = (uiState as? ActiveSceneUiState.Ready)?.sceneName ?: "Scene",
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCredits) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is ActiveSceneUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is ActiveSceneUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                is ActiveSceneUiState.Ready -> {
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = { Text("Soundscapes") },
                        )
                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = { Text("Soundboard") },
                        )
                    }
                    AnimatedContent(
                        targetState = selectedTabIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                            } else {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                            }
                        },
                        label = "tabContent",
                    ) { tabIndex ->
                        when (tabIndex) {
                            0 -> SoundscapesTab(
                                state = state,
                                onPlayCategory = viewModel::playCategory,
                                onPauseCategory = viewModel::pauseCategory,
                                onSetIntensity = viewModel::setCategoryIntensity,
                                onSetMix = viewModel::setCategoryMix,
                                onSetMasterVolume = viewModel::setMasterAtmosphereVolume,
                                onStopAll = viewModel::stopAll,
                                onUpdateNotes = viewModel::updateSceneNotes,
                                onSetMasterIntensity = viewModel::setMasterIntensity,
                            )

                            1 -> SoundboardTab(
                                state = state,
                                onPlayFx = viewModel::playFx,
                                onSetMasterVolume = viewModel::setMasterFxVolume,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SoundscapesTab(
    state: ActiveSceneUiState.Ready,
    onPlayCategory: (Long) -> Unit,
    onPauseCategory: (Long) -> Unit,
    onSetIntensity: (Long, Int) -> Unit,
    onSetMix: (Long, Float) -> Unit,
    onSetMasterVolume: (Float) -> Unit,
    onStopAll: () -> Unit,
    onUpdateNotes: (String) -> Unit,
    onSetMasterIntensity: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("soundscapesTab"),
    ) {
        // Panic button
        Button(
            onClick = onStopAll,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("globalStopButton"),
        ) {
            Text("⏹ STOP ALL", color = MaterialTheme.colorScheme.onError)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Master",
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = state.masterAtmosphereVolume,
                onValueChange = onSetMasterVolume,
                modifier = Modifier
                    .weight(1f)
                    .testTag("masterAtmosphereSlider"),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Intensity", style = MaterialTheme.typography.labelLarge)
            listOf(1, 2, 3).forEach { level ->
                val label = when (level) { 1 -> "I"; 2 -> "II"; else -> "III" }
                if (level == state.masterIntensityLevel) {
                    FilledTonalButton(
                        onClick = { onSetMasterIntensity(level) },
                        modifier = Modifier.testTag("masterIntensityButton_$level"),
                    ) { Text(label) }
                } else {
                    OutlinedButton(
                        onClick = { onSetMasterIntensity(level) },
                        modifier = Modifier.testTag("masterIntensityButton_$level"),
                    ) { Text(label) }
                }
            }
        }
        // Scene Notes
        var showNotes by rememberSaveable { mutableStateOf(false) }
        TextButton(
            onClick = { showNotes = !showNotes },
            modifier = Modifier.padding(horizontal = 16.dp).testTag("toggleNotesButton"),
        ) {
            Text(if (showNotes) "Hide Notes" else "Scene Notes")
        }
        if (showNotes) {
            OutlinedTextField(
                value = state.sceneNotes ?: "",
                onValueChange = onUpdateNotes,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(120.dp)
                    .testTag("sceneNotesField"),
                placeholder = { Text("DM notes, cues, descriptions...") },
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.categories, key = { it.id }) { category ->
                CategoryCard(
                    category = category,
                    onPlay = { onPlayCategory(category.id) },
                    onPause = { onPauseCategory(category.id) },
                    onSetIntensity = { level -> onSetIntensity(category.id, level) },
                    onSetMix = { volume -> onSetMix(category.id, volume) },
                )
            }
        }
    }
}

@Composable
private fun SoundboardTab(
    state: ActiveSceneUiState.Ready,
    onPlayFx: (Long) -> Unit,
    onSetMasterVolume: (Float) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("soundboardTab"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Master",
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Slider(
                value = state.masterFxVolume,
                onValueChange = onSetMasterVolume,
                modifier = Modifier
                    .weight(1f)
                    .testTag("masterFxSlider"),
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(state.fxButtons, key = { it.trackId }) { fx ->
                FxButton(
                    fx = fx,
                    onPlay = { onPlayFx(fx.trackId) },
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: CategoryUiModel,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSetIntensity: (Int) -> Unit,
    onSetMix: (Float) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha",
    )
    val glowModifier = if (category.isPlaying) {
        Modifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
            shape = MaterialTheme.shapes.medium,
        )
    } else {
        Modifier
    }
    Card(
        modifier = glowModifier.then(
            Modifier
                .fillMaxWidth()
                .testTag("categoryCard_${category.id}"),
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = { if (category.isPlaying) onPause() else onPlay() },
                    modifier = Modifier.testTag("categoryPlayButton_${category.id}"),
                ) {
                    Icon(
                        imageVector = if (category.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (category.isPlaying) "Pause" else "Play",
                    )
                }
                IconButton(
                    onClick = onPlay,
                    modifier = Modifier.testTag("categoryD20Button_${category.id}"),
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Random",
                    )
                }
            }
            if (category.currentTrackName != null) {
                Text(
                    text = category.currentTrackName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "MIX",
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = category.mixVolume,
                    onValueChange = onSetMix,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("categoryMixSlider_${category.id}"),
                )
            }
            IntensitySelector(
                categoryId = category.id,
                currentLevel = category.intensityLevel,
                availableIntensities = category.availableIntensities,
                onSetIntensity = onSetIntensity,
            )
        }
    }
}

@Composable
private fun IntensitySelector(
    categoryId: Long,
    currentLevel: Int,
    availableIntensities: Set<Int>,
    onSetIntensity: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 4.dp),
    ) {
        listOf(1, 2, 3).forEach { level ->
            val label = when (level) {
                1 -> "I"
                2 -> "II"
                else -> "III"
            }
            val isAvailable = level in availableIntensities
            val isSelected = level == currentLevel
            when {
                isSelected -> FilledTonalButton(
                    onClick = { onSetIntensity(level) },
                    modifier = Modifier.testTag("intensityButton_${categoryId}_$level"),
                ) {
                    Text(label)
                }

                isAvailable -> OutlinedButton(
                    onClick = { onSetIntensity(level) },
                    modifier = Modifier.testTag("intensityButton_${categoryId}_$level"),
                ) {
                    Text(label)
                }

                else -> OutlinedButton(
                    onClick = {},
                    enabled = false,
                    colors = ButtonDefaults.outlinedButtonColors(
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    ),
                    modifier = Modifier.testTag("intensityButton_${categoryId}_$level"),
                ) {
                    Text(label)
                }
            }
        }
    }
}

@Composable
private fun FxButton(
    fx: FxButtonUiModel,
    onPlay: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fxGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "fxGlowAlpha",
    )
    val glowModifier = if (fx.isPlaying) {
        Modifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = glowAlpha),
            shape = MaterialTheme.shapes.small,
        )
    } else {
        Modifier
    }
    Button(
        onClick = onPlay,
        modifier = glowModifier.then(
            Modifier
                .fillMaxWidth()
                .testTag("fxButton_${fx.trackId}"),
        ),
        colors = if (fx.isPlaying) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
            )
        } else {
            ButtonDefaults.buttonColors()
        },
    ) {
        Text(
            text = fx.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
        )
    }
}
