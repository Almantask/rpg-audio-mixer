package com.example.rpgaudiomixer.ui.activescene

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceDim
import com.example.rpgaudiomixer.app.theme.ArcanumPurple
import com.example.rpgaudiomixer.domain.model.FxTrack
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@Composable
fun ActiveSceneScreen(
    sceneName: String = "Scene",
    onBack: () -> Unit = {},
    onGearClick: () -> Unit = {},
    soundscapesViewModel: ActiveSceneSoundscapesViewModel = hiltViewModel(),
    soundboardViewModel: ActiveSceneSoundboardViewModel = hiltViewModel(),
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = sceneName,
                showBackArrow = true,
                onBack = onBack,
                onGearClick = onGearClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = ArcanumGold,
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Soundscapes",
                            color = if (selectedTab == 0) ArcanumGold else ArcanumOnSurfaceDim,
                        )
                    },
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Soundboard",
                            color = if (selectedTab == 1) ArcanumGold else ArcanumOnSurfaceDim,
                        )
                    },
                )
            }

            when (selectedTab) {
                0 -> SoundscapesTab(viewModel = soundscapesViewModel)
                1 -> SoundboardTab(viewModel = soundboardViewModel)
            }
        }
    }
}

@Composable
fun SoundscapesTab(
    viewModel: ActiveSceneSoundscapesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ActiveSceneSoundscapesUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is ActiveSceneSoundscapesUiState.Success -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text("Master Atmosphere", style = MaterialTheme.typography.labelLarge)
                        Slider(
                            value = state.masterVolume,
                            onValueChange = { viewModel.setMasterVolume(it) },
                            colors = SliderDefaults.colors(thumbColor = ArcanumGold, activeTrackColor = ArcanumGold),
                        )
                    }
                }
                items(state.categoryStates, key = { it.category.id }) { catState ->
                    SoundscapeCategoryCard(
                        categoryState = catState,
                        onPlay = { viewModel.playCategory(catState.category.id) },
                        onPause = { viewModel.pauseCategory(catState.category.id) },
                        onRoll = { viewModel.rollRandom(catState.category.id) },
                        onMixChange = { vol -> viewModel.setMix(catState.category.id, vol) },
                        onIntensityChange = { level -> viewModel.setIntensity(catState.category.id, level) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
                item {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
                    ) {
                        Text("+ Add New Soundscape", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
        is ActiveSceneSoundscapesUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SoundscapeCategoryCard(
    categoryState: CategoryState,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onRoll: () -> Unit,
    onMixChange: (Float) -> Unit,
    onIntensityChange: (IntensityLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (categoryState.isPlaying) ArcanumPurple else Color.Transparent

    Card(
        modifier = modifier.fillMaxWidth(),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = categoryState.category.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumGold,
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(
                    onClick = onRoll,
                    border = BorderStroke(1.dp, ArcanumGold),
                ) {
                    Text("🎲", style = MaterialTheme.typography.labelMedium)
                }
                Button(
                    onClick = if (categoryState.isPlaying) onPause else onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
                ) {
                    Text(if (categoryState.isPlaying) "⏸" else "▶", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            categoryState.currentTrackName?.let { trackName ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = trackName, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("MIX", style = MaterialTheme.typography.labelSmall)
            Slider(
                value = categoryState.mixVolume,
                onValueChange = onMixChange,
                colors = SliderDefaults.colors(thumbColor = ArcanumGold, activeTrackColor = ArcanumGold),
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IntensityLevel.entries.forEach { level ->
                    val available = level in categoryState.availableIntensities
                    val selected = level == categoryState.intensity
                    OutlinedButton(
                        onClick = { if (available) onIntensityChange(level) },
                        enabled = available,
                        border = BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) ArcanumGold else ArcanumOnSurfaceDim,
                        ),
                    ) {
                        Text(
                            text = level.label,
                            color = if (selected) ArcanumGold else ArcanumOnSurfaceDim,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SoundboardTab(
    viewModel: ActiveSceneSoundboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ActiveSceneSoundboardUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
        is ActiveSceneSoundboardUiState.Success -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text("Master", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = state.masterVolume,
                        onValueChange = { viewModel.setMasterVolume(it) },
                        colors = SliderDefaults.colors(thumbColor = ArcanumGold, activeTrackColor = ArcanumGold),
                    )
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                ) {
                    items(state.fxTracks, key = { it.id }) { fxTrack ->
                        FxButton(
                            track = fxTrack,
                            onTrigger = { viewModel.triggerFxWithStats(fxTrack) },
                        )
                    }
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ArcanumGold),
                ) {
                    Text("+ Add New Effect", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
        is ActiveSceneSoundboardUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Error: ${state.message}",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun FxButton(
    track: FxTrack,
    onTrigger: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onTrigger,
        modifier = modifier.padding(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(
            text = track.name,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            color = ArcanumGold,
        )
    }
}
