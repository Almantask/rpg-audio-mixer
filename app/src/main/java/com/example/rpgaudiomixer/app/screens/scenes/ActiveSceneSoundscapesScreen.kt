package com.example.rpgaudiomixer.app.screens.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.*
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveSceneSoundscapesScreen(
    sceneId: Long,
    onBack: () -> Unit,
    onSwitchToSoundboard: () -> Unit,
    viewModel: ActiveSceneSoundscapesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val playingTracks by viewModel.playingTracks.collectAsState()
    val playingStates by viewModel.categoryPlayingState.collectAsState()
    
    var showAddOverlay by remember { mutableStateOf(false) }
    val allCategories by viewModel.allCategories.collectAsState()
    val categoryPlayCounts by viewModel.categoryPlayCounts.collectAsState()
    
    val currentCategoryIds = remember(uiState.categories) {
        uiState.categories.map { it.category.id }.toSet()
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
                Icon(Icons.Default.Add, contentDescription = "Add Soundscape")
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
                selectedTabIndex = 0,
                containerColor = BlackBg,
                contentColor = Gold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[0]),
                        color = Gold
                    )
                },
                divider = {}
            ) {
                Tab(
                    selected = true,
                    onClick = { /* Stay here */ },
                    text = { Text("SOUNDSCAPES", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = false,
                    onClick = onSwitchToSoundboard,
                    text = { Text("SOUNDBOARD", color = Gold.copy(alpha = 0.5f)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Master Atmosphere Slider
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
            } else if (uiState.categories.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.MusicNote,
                    message = "THE AIR IS STILL",
                    ctaText = "INVOKE ATMOSPHERE",
                    onCtaClick = { showAddOverlay = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.categories, key = { it.category.id }) { sceneCat ->
                        SwipeToDeleteContainer(
                            onDelete = { viewModel.removeCategory(sceneCat.category.id) }
                        ) {
                            SoundscapeCategoryCard(
                                categoryState = sceneCat,
                                isPlaying = playingStates[sceneCat.category.id] ?: false,
                                currentTrackName = playingTracks[sceneCat.category.id],
                                onPlayPause = { viewModel.toggleCategoryPlayback(sceneCat.category.id) },
                                onRandomize = { viewModel.rollRandom(sceneCat.category.id) },
                                onIntensityChange = { viewModel.setIntensity(sceneCat.category.id, it) },
                                onMixVolumeChange = { viewModel.setMixVolume(sceneCat.category.id, it) }
                            )
                        }
                    }
                }
            }
        }
        
        if (showAddOverlay) {
            MultiSelectPickerSheet(
                title = "INVOKE ATMOSPHERE",
                items = allCategories,
                selectedItems = currentCategoryIds,
                onDismiss = { showAddOverlay = false },
                onItemSelected = { categoryId ->
                    viewModel.addCategory(categoryId)
                    showAddOverlay = false
                },
                itemLabel = { it.name },
                itemSubtitle = { 
                    val count = categoryPlayCounts[it.id] ?: 0
                    "PLAYED ${count}×"
                },
                itemId = { it.id }
            )
        }
    }
}
