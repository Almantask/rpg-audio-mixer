package com.example.rpgaudiomixer.app.screens.library

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.SwipeToDeleteContainer
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun SoundscapeCategoryComposerScreen(
    categoryId: Long,
    viewModel: SoundscapeCategoryComposerViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(categoryId) {
        viewModel.setCategoryId(categoryId)
    }

    val tracks by viewModel.tracks.collectAsState()
    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsState()
    
    val trackPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { viewModel.addTrack(it.lastPathSegment ?: "Unnamed Track", it.toString()) }
        }
    )

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "ARCHITECT",
                showBackArrow = true,
                onBack = onBack,
                actions = {
                    if (hasUnsavedChanges) {
                        IconButton(onClick = { viewModel.saveChanges() }) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = Gold)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { trackPickerLauncher.launch("audio/*") },
                containerColor = Gold,
                contentColor = BlackBg,
                shape = Shapes.medium
            ) {
                Icon(Icons.Default.Add, contentDescription = "Scribe New Track")
            }
        },
        containerColor = BlackBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (tracks.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.Block,
                    message = "THE VOID IS SILENT",
                    ctaText = "ADD NEW TRACK",
                    onCtaClick = { trackPickerLauncher.launch("audio/*") }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(tracks, key = { it.id }) { track ->
                        SwipeToDeleteContainer(
                            onDelete = { viewModel.removeTrack(track.id) }
                        ) {
                            TrackComposerCard(
                                track = track,
                                onIntensityChange = { viewModel.updateTrackIntensity(track.id, it) },
                                onMixChange = { viewModel.updateTrackMix(track.id, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackComposerCard(
    track: SoundscapeTrack,
    onIntensityChange: (IntensityLevel) -> Unit,
    onMixChange: (Float) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = track.name.uppercase(),
                style = Typography.bodyLarge,
                color = Gold,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MIX",
                    style = Typography.labelLarge,
                    color = Gold.copy(alpha = 0.5f),
                    modifier = Modifier.width(40.dp)
                )
                Slider(
                    value = track.mixVolume,
                    onValueChange = onMixChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Gold,
                        activeTrackColor = Gold,
                        inactiveTrackColor = Gold.copy(alpha = 0.1f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "INTENSITY",
                    style = Typography.labelLarge,
                    color = Gold.copy(alpha = 0.5f)
                )
                
                SegmentedIntensitySelector(
                    selectedLevel = track.intensityLevel,
                    onLevelSelected = onIntensityChange
                )
            }
        }
    }
}

@Composable
fun SegmentedIntensitySelector(
    selectedLevel: IntensityLevel,
    onLevelSelected: (IntensityLevel) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(Shapes.small)
            .background(BlackBg)
            .padding(2.dp)
    ) {
        IntensityLevel.values().forEach { level ->
            val isSelected = selectedLevel == level
            Surface(
                onClick = { onLevelSelected(level) },
                color = if (isSelected) Gold else Color.Transparent,
                shape = Shapes.small,
                modifier = Modifier
                    .width(40.dp)
                    .height(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = level.name,
                        color = if (isSelected) BlackBg else Gold.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
