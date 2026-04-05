package com.example.rpgaudiomixer.app.screens.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.*
import com.example.rpgaudiomixer.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSceneSoundboardScreen(
    sceneId: Long,
    onBack: () -> Unit,
    onSwitchToSoundscapes: () -> Unit,
    viewModel: ActiveSceneSoundboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val playingFxIds by viewModel.playingFxIds.collectAsState()
    val allFx by viewModel.allFx.collectAsState()
    
    var showAddOverlay by remember { mutableStateOf(false) }
    val currentFxIds = remember(uiState.fxTracks) {
        uiState.fxTracks.map { it.id }.toSet()
    }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "ACTIVE REALM",
                showBackArrow = true,
                onBack = onBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddOverlay = true },
                containerColor = Gold,
                contentColor = BlackBg,
                shape = Shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add FX")
            }
        },
        containerColor = BlackBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = 1,
                containerColor = BlackBg,
                contentColor = Gold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[1]),
                        color = Gold
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = false,
                    onClick = onSwitchToSoundscapes,
                    text = { Text("SOUNDSCAPES", color = Gold.copy(alpha = 0.5f)) }
                )
                Tab(
                    selected = true,
                    onClick = { /* Stay here */ },
                    text = { Text("SOUNDBOARD", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Master Soundboard Slider
            MasterAtmosphereSlider(
                volume = uiState.masterVolume,
                onVolumeChange = { viewModel.setMasterVolume(it) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (uiState.fxTracks.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.MusicNote,
                    message = "THE VOID IS SILENT",
                    ctaText = "SUMMON EFFECTS",
                    onCtaClick = { showAddOverlay = true }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        items(uiState.fxTracks, key = { it.id }) { track ->
                            FxButton(
                                track = track,
                                isPlaying = playingFxIds.contains(track.id),
                                onClick = { viewModel.triggerFx(track) }
                            )
                        }
                    }
                    
                    // TODO: Drag and drop reordering and FlamesDeleteZone logic would go here.
                    // For now, only the visual grid is provided as per Iteration 7 goal.
                }
            }
        }
        
        if (showAddOverlay) {
            MultiSelectPickerSheet(
                title = "SUMMON EFFECTS",
                items = allFx,
                selectedItems = currentFxIds,
                onDismiss = { showAddOverlay = false },
                onItemSelected = { fxId ->
                    viewModel.addFx(fxId)
                    showAddOverlay = false
                },
                itemLabel = { it.name },
                itemSubtitle = { "PLAYED ${it.playCount}×" },
                itemId = { it.id }
            )
        }
    }
}
