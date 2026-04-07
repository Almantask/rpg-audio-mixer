package com.example.rpgaudiomixer.ui.activescene

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar

enum class ActiveSceneTab {
    SOUNDSCAPES,
    SOUNDBOARD
}

@Composable
fun ActiveSceneScreen(
    sceneId: Long,
    soundscapesViewModel: ActiveSceneSoundscapesViewModel = hiltViewModel(),
    soundboardViewModel: ActiveSceneSoundboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(ActiveSceneTab.SOUNDSCAPES) }

    Scaffold(
        topBar = {
            Column {
                ArcanumTopBar(
                    title = "Active Scene",
                    showBackArrow = true,
                    onBack = onNavigateBack,
                    onGearClick = { /* Navigate to credits */ }
                )
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTab == ActiveSceneTab.SOUNDSCAPES,
                        onClick = { selectedTab = ActiveSceneTab.SOUNDSCAPES },
                        text = { Text("Soundscapes") }
                    )
                    Tab(
                        selected = selectedTab == ActiveSceneTab.SOUNDBOARD,
                        onClick = { selectedTab = ActiveSceneTab.SOUNDBOARD },
                        text = { Text("Soundboard") }
                    )
                }
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            ActiveSceneTab.SOUNDSCAPES -> {
                ActiveSceneSoundscapesContent(
                    sceneId = sceneId,
                    viewModel = soundscapesViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            ActiveSceneTab.SOUNDBOARD -> {
                ActiveSceneSoundboardContent(
                    sceneId = sceneId,
                    viewModel = soundboardViewModel,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ActiveSceneSoundscapesContent(
    sceneId: Long,
    viewModel: ActiveSceneSoundscapesViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(sceneId) {
        viewModel.loadScene(sceneId)
    }

    val uiState by viewModel.uiState.collectAsState()

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
        onNavigateBack = { /* Error will be dismissed but not navigate back */ },
        modifier = modifier
    )
}

@Composable
private fun ActiveSceneSoundboardContent(
    sceneId: Long,
    viewModel: ActiveSceneSoundboardViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(sceneId) {
        viewModel.loadScene(sceneId)
    }

    val uiState by viewModel.uiState.collectAsState()

    ActiveSceneSoundboardScreenContent(
        uiState = uiState,
        onMasterVolumeChange = viewModel::setMasterVolume,
        onEffectClick = viewModel::playEffect,
        onRemoveEffect = viewModel::removeEffect,
        onAddEffect = { /* Show FX selection overlay */ },
        modifier = modifier
    )
}
