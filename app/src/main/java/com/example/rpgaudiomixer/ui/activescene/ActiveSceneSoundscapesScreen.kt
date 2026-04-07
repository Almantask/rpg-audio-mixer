package com.example.rpgaudiomixer.ui.activescene

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.*
import com.example.rpgaudiomixer.common.UiState

@Composable
fun ActiveSceneSoundscapesScreen(
    sceneId: Long,
    viewModel: ActiveSceneSoundscapesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    LaunchedEffect(sceneId) {
        viewModel.loadScene(sceneId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Active Scene",
                showBackArrow = true,
                onBack = onNavigateBack,
                onGearClick = { /* Navigate to credits */ }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Show soundscape selection overlay */ },
                modifier = Modifier.semantics { contentDescription = "AddSoundscape" }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Soundscape")
            }
        }
    ) { paddingValues ->
        ActiveSceneSoundscapesScreenContent(
            uiState = uiState,
            onMasterVolumeChange = viewModel::setMasterVolume,
            onPlayPauseClick = { categoryId, isPlaying ->
                if (isPlaying) {
                    viewModel.pauseCategory(categoryId)
                } else {
                    viewModel.playCategory(categoryId)
                }
            },
            onRollRandomClick = viewModel::rollRandom,
            onIntensityChange = viewModel::setIntensity,
            onMixVolumeChange = viewModel::setMix,
            onRemoveCategory = viewModel::removeCategory,
            onAddCategory = { /* Show soundscape selection overlay */ },
            onNavigateBack = onNavigateBack,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
internal fun ActiveSceneSoundscapesScreenContent(
    uiState: UiState<ActiveSceneSoundscapeUiState>,
    onMasterVolumeChange: (Float) -> Unit,
    onPlayPauseClick: (Long, Boolean) -> Unit,
    onRollRandomClick: (Long) -> Unit,
    onIntensityChange: (Long, com.example.rpgaudiomixer.domain.model.IntensityLevel) -> Unit,
    onMixVolumeChange: (Long, Float) -> Unit,
    onRemoveCategory: (Long) -> Unit,
    onAddCategory: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (val state = uiState) {
        is UiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Error -> {
            ErrorDialog(
                message = state.message,
                onDismiss = onNavigateBack
            )
        }

        is UiState.Success -> {
            ActiveSceneSoundscapesContent(
                state = state.data,
                onMasterVolumeChange = onMasterVolumeChange,
                onPlayPauseClick = onPlayPauseClick,
                onRollRandomClick = onRollRandomClick,
                onIntensityChange = onIntensityChange,
                onMixVolumeChange = onMixVolumeChange,
                onRemoveCategory = onRemoveCategory,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ActiveSceneSoundscapesContent(
    state: ActiveSceneSoundscapeUiState,
    onMasterVolumeChange: (Float) -> Unit,
    onPlayPauseClick: (Long, Boolean) -> Unit,
    onRollRandomClick: (Long) -> Unit,
    onIntensityChange: (Long, com.example.rpgaudiomixer.domain.model.IntensityLevel) -> Unit,
    onMixVolumeChange: (Long, Float) -> Unit,
    onRemoveCategory: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Master Atmosphere Slider
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "MasterAtmosphereCard" },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Master Atmosphere",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MixSlider(
                        label = "VOLUME",
                        value = state.masterVolume,
                        onValueChange = onMasterVolumeChange
                    )
                }
            }
        }

        // Soundscape Category Cards
        items(
            items = state.categories,
            key = { it.sceneSoundscape.categoryId }
        ) { categoryState ->
            SwipeToDeleteContainer(
                onDelete = { onRemoveCategory(categoryState.sceneSoundscape.categoryId) }
            ) {
                SoundscapeCategoryCard(
                    categoryName = categoryState.sceneSoundscape.categoryName,
                    currentTrackName = categoryState.currentTrackName,
                    isPlaying = categoryState.isPlaying,
                    mixVolume = categoryState.sceneSoundscape.mixVolume,
                    intensityLevel = categoryState.sceneSoundscape.intensityLevel,
                    availableIntensities = categoryState.availableIntensities,
                    onPlayPauseClick = {
                        onPlayPauseClick(
                            categoryState.sceneSoundscape.categoryId,
                            categoryState.isPlaying
                        )
                    },
                    onRollRandomClick = {
                        onRollRandomClick(categoryState.sceneSoundscape.categoryId)
                    },
                    onIntensityChange = { intensity ->
                        onIntensityChange(categoryState.sceneSoundscape.categoryId, intensity)
                    },
                    onMixVolumeChange = { volume ->
                        onMixVolumeChange(categoryState.sceneSoundscape.categoryId, volume)
                    }
                )
            }
        }

        // Empty state
        if (state.categories.isEmpty()) {
            item {
                EmptyStateView(
                    title = "No Soundscapes",
                    message = "Add soundscape categories to build your scene's atmosphere",
                    actionLabel = "ADD SOUNDSCAPE",
                    onActionClick = { /* Show soundscape selection overlay */ }
                )
            }
        }
    }
}
