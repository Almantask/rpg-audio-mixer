package com.example.rpgaudiomixer.app.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.GenericMultiSelectPickerSheet
import com.example.rpgaudiomixer.app.components.MasterSlider
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.active_scene.ActiveSceneSoundboardViewModel
import com.example.rpgaudiomixer.app.ui.active_scene.FxItemState
import com.example.rpgaudiomixer.app.ui.library.FxLibraryUiState
import com.example.rpgaudiomixer.app.ui.library.FxLibraryViewModel

@Composable
fun ActiveSceneSoundboardTab(
    viewModel: ActiveSceneSoundboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddFx by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            MasterSlider(
                title = "MASTER SOUNDBOARD",
                volume = uiState.masterVolume,
                onVolumeChange = { viewModel.setMasterVolume(it) }
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ArcanumGold)
                }
            } else if (uiState.effects.isEmpty()) {
                EmptyStateView(
                    illustration = Icons.Default.VolumeOff,
                    title = "Silent Soundboard",
                    subtitle = "Fill the grid with stings, clashes, and spells.",
                    actionLabel = "SUMMON EFFECTS",
                    onAction = { showAddFx = true }
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.effects, key = { it.fx.id }) { item ->
                        FxButton(
                            state = item,
                            onClick = { viewModel.triggerFx(item.fx) },
                            onLongClick = { /* Drag handled by parent if implemented */ },
                            onRemove = { viewModel.removeFx(item.fx.id) }
                        )
                    }
                }
            }
        }

        // Add Button
        FloatingActionButton(
            onClick = { showAddFx = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = ArcanumGold,
            contentColor = ArcanumOnGold
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Effect")
        }

        // Flames Delete Zone (Optional/Placeholder for now as per spec)
        // In a full implementation, this would be visible during drag
        
        if (showAddFx) {
            FxPickerOverlay(
                onDismiss = { showAddFx = false },
                onFxSelected = { viewModel.addFx(it) }
            )
        }
    }
}

@Composable
fun FxButton(
    state: FxItemState,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRemove: () -> Unit
) {
    // Glow animation for "playing" state (though Soundboard is fire-and-forget)
    // We'll simulate a brief glow on click
    var isGlowing by remember { mutableStateOf(false) }
    val glowAlpha by animateFloatAsState(
        targetValue = if (isGlowing) 0.8f else 0f,
        animationSpec = tween(300),
        finishedListener = { if (it == 0.8f) isGlowing = false },
        label = "glow"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { onLongClick() },
                    onDrag = { _, _ -> },
                    onDragEnd = { },
                    onDragCancel = { }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ArcanumCard)
                .clickable { 
                    isGlowing = true
                    onClick() 
                }
                .border(
                    width = 2.dp,
                    brush = Brush.radialGradient(
                        colors = listOf(ArcanumGold.copy(alpha = glowAlpha), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Icon or first letter
            Text(
                text = state.fx.name.take(1).uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                color = ArcanumGold.copy(alpha = 0.5f)
            )
            
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = ArcanumGold,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = state.fx.name.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = ArcanumOnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun FxPickerOverlay(
    onDismiss: () -> Unit,
    onFxSelected: (Long) -> Unit,
    fxLibraryViewModel: FxLibraryViewModel = hiltViewModel(),
    soundboardViewModel: ActiveSceneSoundboardViewModel = hiltViewModel()
) {
    val libraryState by fxLibraryViewModel.uiState.collectAsState()
    val activeState by soundboardViewModel.uiState.collectAsState()
    
    val effects = libraryState.tracks
    val alreadySelectedIds = activeState.effects.map { it.fx.id }.toSet()

    GenericMultiSelectPickerSheet(
        title = "Summon to Soundboard",
        items = effects,
        alreadySelectedIds = alreadySelectedIds,
        itemIdSelector = { it.id },
        itemLabelSelector = { it.name },
        itemSecondaryLabelSelector = { it.tags.joinToString(", ") },
        onDismiss = onDismiss,
        onItemSelected = { onFxSelected(it) },
        emptyMessage = "No sound effects found. Import some in the Library!"
    )
}
