package com.example.rpgaudiomixer.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.*
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.active_scene.ActiveSceneSoundscapesViewModel
import com.example.rpgaudiomixer.app.ui.library.SoundscapeLibraryUiState
import com.example.rpgaudiomixer.app.ui.library.SoundscapeLibraryViewModel
import com.example.rpgaudiomixer.domain.library.IntensityLevel

@Composable
fun ActiveSceneScreen(
    sceneId: Long,
    autoPlay: Boolean = false,
    sessionId: Long = -1L,
    campaignId: Long = -1L,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("SOUNDSCAPES", "SOUNDBOARD")

    Column(modifier = Modifier.fillMaxSize().background(ArcanumBlack)) {
        ArcanumTopBar(
            title = "ACTIVE SCENE",
            showBackArrow = true,
            onBack = onBack
        )

        val soundscapeViewModel: ActiveSceneSoundscapesViewModel = hiltViewModel()
        
        DisposableEffect(key1 = sceneId) {
            onDispose {
                soundscapeViewModel.stopAudio()
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ArcanumBlack,
            contentColor = ArcanumGold,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ArcanumGold
                )
            },
            divider = { Divider(color = ArcanumOnSurface.copy(alpha = 0.1f)) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            letterSpacing = 2.sp
                        )
                    }
                )
            }
        }

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(slideOutHorizontally { width -> width } + fadeOut())
                }.using(
                    SizeTransform(clip = false)
                )
            },
            modifier = Modifier.weight(1f)
        ) { targetTab ->
            when (targetTab) {
                0 -> ActiveSceneSoundscapesTab(viewModel = hiltViewModel())
                1 -> ActiveSceneSoundboardTab(viewModel = hiltViewModel())
            }
        }
    }
}

@Composable
fun ActiveSceneSoundscapesTab(
    viewModel: ActiveSceneSoundscapesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddCategory by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MasterSlider(
                title = "MASTER ATMOSPHERE",
                volume = uiState.masterVolume,
                onVolumeChange = { viewModel.setMasterVolume(it) }
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ArcanumGold)
                }
            } else if (uiState.soundscapes.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.MusicNote,
                    title = "Silent Realm",
                    subtitle = "Assign soundscape categories to this scene.",
                    actionLabel = "ADD CATEGORY",
                    onAction = { showAddCategory = true }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.soundscapes, key = { it.soundscape.category.id }) { item ->
                        SoundscapeCategoryControlCard(
                            state = item,
                            onToggle = { viewModel.toggleCategory(item.soundscape.category.id) },
                            onRoll = { viewModel.rollRandom(item.soundscape.category.id) },
                            onIntensityChange = { viewModel.setIntensity(item.soundscape.category.id, it) },
                            onMixChange = { viewModel.setMixVolume(item.soundscape.category.id, it) },
                            onRemove = { viewModel.removeCategory(item.soundscape.category.id) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddCategory = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = ArcanumGold,
            contentColor = ArcanumOnGold
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Category")
        }

        if (showAddCategory) {
            // Need all categories to pick from
            // For now, I'll just use a placeholder or hilt the library VM
            SoundscapeCategoryPicker(
                onDismiss = { showAddCategory = false },
                onSelected = { /* handled in picker */ }
            )
        }
    }
}

@Composable
fun SoundscapeCategoryControlCard(
    state: com.example.rpgaudiomixer.app.ui.active_scene.SoundscapeItemState,
    onToggle: () -> Unit,
    onRoll: () -> Unit,
    onIntensityChange: (IntensityLevel) -> Unit,
    onMixChange: (Float) -> Unit,
    onRemove: () -> Unit
) {
    val isPlaying by state.isPlaying.collectAsState()
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val hasTracks = state.soundscape.category.tracks.isNotEmpty()
    val hasTracksForCurrentIntensity = state.soundscape.category.tracks.any { it.intensityLevel == state.soundscape.intensityLevel }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isPlaying) Modifier.border(2.dp, ArcanumGold.copy(alpha = glowAlpha), RoundedCornerShape(16.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(containerColor = ArcanumCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.soundscape.category.name.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (hasTracks) ArcanumGold else ArcanumOnSurface.copy(alpha = 0.3f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = when {
                            !hasTracks -> "NO TRACKS ASSIGNED"
                            !hasTracksForCurrentIntensity -> "NO TRACKS FOR THIS INTENSITY"
                            isPlaying -> "PLAYING ATMOSPHERE"
                            else -> "SILENT"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isPlaying) ArcanumMutedGold else ArcanumOnSurface.copy(alpha = 0.4f)
                    )
                }
                
                IconButton(
                    onClick = onRoll,
                    enabled = hasTracksForCurrentIntensity
                ) {
                    Icon(
                        Icons.Default.Refresh, 
                        contentDescription = "Roll Random", 
                        tint = if (hasTracksForCurrentIntensity) ArcanumGold else ArcanumOnSurface.copy(alpha = 0.2f)
                    )
                }
                
                IconButton(
                    onClick = onToggle,
                    enabled = hasTracksForCurrentIntensity || isPlaying
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = "Toggle",
                        tint = if (hasTracksForCurrentIntensity || isPlaying) ArcanumGold else ArcanumOnSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            MixSlider(
                value = state.soundscape.mixVolume,
                onValueChange = onMixChange
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            IntensitySelector(
                selectedLevel = state.soundscape.intensityLevel,
                onLevelSelected = onIntensityChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = hasTracks
            )
        }
    }
}

@Composable
fun SoundscapeCategoryPicker(
    onDismiss: () -> Unit,
    onSelected: (Long) -> Unit,
    libraryViewModel: SoundscapeLibraryViewModel = hiltViewModel(),
    activeViewModel: ActiveSceneSoundscapesViewModel = hiltViewModel()
) {
    val libraryState by libraryViewModel.uiState.collectAsState()
    val activeState by activeViewModel.uiState.collectAsState()
    
    val categories = (libraryState as? com.example.rpgaudiomixer.app.ui.library.SoundscapeLibraryUiState.Success)?.categories ?: emptyList()
    val selectedIds = activeState.soundscapes.map { it.soundscape.category.id }.toSet()

    GenericMultiSelectPickerSheet(
        title = "Inhabit Scene",
        items = categories,
        alreadySelectedIds = selectedIds,
        itemIdSelector = { it.id },
        itemLabelSelector = { it.name },
        itemSecondaryLabelSelector = { "${it.tracks.size} tracks across intensities" },
        onDismiss = onDismiss,
        onItemSelected = { 
            activeViewModel.addCategory(it)
        },
        emptyMessage = "No soundscape categories found. Create some in the Library!"
    )
}
