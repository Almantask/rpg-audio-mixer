package com.example.rpgaudiomixer.ui.activescene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.app.components.MasterSlider

enum class ActiveSceneTab(val title: String) {
    SOUNDSCAPES("Soundscapes"),
    SOUNDBOARD("Soundboard")
}

@Composable
fun ActiveSceneSoundscapesScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCredits: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActiveSceneSoundscapesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = ActiveSceneTab.entries
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Active Scene",
                showBackArrow = true,
                onBack = onNavigateBack,
                onGearClick = onNavigateToCredits
            )
        },
        floatingActionButton = {
            // FAB for adding soundscapes on Soundscapes tab, or FX on Soundboard tab
            FloatingActionButton(
                onClick = { showAddCategoryDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (selectedTab == 0) "Add Soundscape" else "Add Effect",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab strip
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (selectedTab == index) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    )
                }
            }

            // Content
            when (tabs[selectedTab]) {
                ActiveSceneTab.SOUNDSCAPES -> {
                    SoundscapesTabContent(
                        uiState = uiState,
                        onMasterVolumeChange = viewModel::setMasterVolume,
                        onPlayPause = { category ->
                            if (category.isPlaying) {
                                viewModel.pauseCategory(category.categoryId)
                            } else {
                                if (category.currentTrackName == null) {
                                    viewModel.playCategory(category.categoryId)
                                } else {
                                    viewModel.resumeCategory(category.categoryId)
                                }
                            }
                        },
                        onRollRandom = { viewModel.rollRandom(it.categoryId) },
                        onIntensityChange = { category, intensity ->
                            viewModel.setIntensity(category.categoryId, intensity)
                        },
                        onMixVolumeChange = { category, volume ->
                            viewModel.setMix(category.categoryId, volume)
                        }
                    )
                }
                ActiveSceneTab.SOUNDBOARD -> {
                    ActiveSceneSoundboardContent()
                }
            }
        }
    }

    errorMessage?.let { message ->
        ErrorDialog(
            message = message,
            onDismiss = viewModel::clearError
        )
    }

    // TODO: Add category selection dialog
    if (showAddCategoryDialog) {
        showAddCategoryDialog = false
        // Placeholder - will be implemented with MultiSelectPickerSheet
    }
}

@Composable
private fun SoundscapesTabContent(
    uiState: ActiveSceneSoundscapesUiState,
    onMasterVolumeChange: (Float) -> Unit,
    onPlayPause: (ActiveSceneCategory) -> Unit,
    onRollRandom: (ActiveSceneCategory) -> Unit,
    onIntensityChange: (ActiveSceneCategory, com.example.rpgaudiomixer.domain.model.IntensityLevel) -> Unit,
    onMixVolumeChange: (ActiveSceneCategory, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is ActiveSceneSoundscapesUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        is ActiveSceneSoundscapesUiState.Success -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Master volume slider
                MasterSlider(
                    label = "Master Atmosphere",
                    value = uiState.masterVolume,
                    onValueChange = onMasterVolumeChange
                )

                // Category list
                if (uiState.categories.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "No soundscapes in this scene",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap + to add soundscapes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.categories, key = { it.categoryId }) { category ->
                            ActiveSceneCategoryCard(
                                category = category,
                                onPlayPauseClick = { onPlayPause(category) },
                                onRollRandomClick = { onRollRandom(category) },
                                onIntensityChange = { intensity ->
                                    onIntensityChange(category, intensity)
                                },
                                onMixVolumeChange = { volume ->
                                    onMixVolumeChange(category, volume)
                                }
                            )
                        }
                    }
                }
            }
        }

        is ActiveSceneSoundscapesUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
