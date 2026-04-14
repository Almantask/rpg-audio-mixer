package com.example.rpgaudiomixer.app.screens.activescene

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.domain.model.SoundscapeCategory

private val GoldColor = Color(0xFFFFD700)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ActiveSceneScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAddSoundscape: () -> Unit = {},
    onNavigateToAddFx: () -> Unit = {},
    viewModel: ActiveSceneViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.testTag("activeSceneScreen"),
        topBar = {
            TopAppBar(
                title = { Text("Active Scene") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back",
                        )
                    }
                },
                actions = {
                    val locked = (uiState as? ActiveSceneUiState.Success)?.isLocked ?: false
                    // Global Stop button
                    IconButton(
                        onClick = { viewModel.globalStop() },
                        enabled = !locked,
                        modifier = Modifier.testTag("globalStopButton"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.StopCircle,
                            contentDescription = "Global Stop",
                        )
                    }
                    // Lock / Unlock – long-press to unlock
                    IconButton(
                        onClick = { if (!locked) viewModel.lock() },
                        modifier = Modifier
                            .testTag("lockButton")
                            .combinedClickable(
                                onClick = { if (!locked) viewModel.lock() },
                                onLongClick = { if (locked) viewModel.unlock() },
                            ),
                    ) {
                        Icon(
                            imageVector = if (locked) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (locked) "Locked" else "Unlocked",
                            tint = if (locked) GoldColor else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            val locked = (uiState as? ActiveSceneUiState.Success)?.isLocked ?: false
            if (!locked) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExtendedFloatingActionButton(
                        onClick = onNavigateToAddFx,
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Add Effect") },
                        modifier = Modifier.testTag("addFxFab"),
                    )
                    ExtendedFloatingActionButton(
                        onClick = onNavigateToAddSoundscape,
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("Add Soundscape") },
                        modifier = Modifier.testTag("addSoundscapeFab"),
                    )
                }
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            when (val state = uiState) {
                is ActiveSceneUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ActiveSceneUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                is ActiveSceneUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Master Intensity selector
                        MasterIntensitySelector(
                            selectedLevel = state.masterIntensity,
                            isLocked = state.isLocked,
                            onLevelSelected = { viewModel.setMasterIntensity(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("masterIntensity"),
                        )

                        if (state.categories.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("No categories yet")
                            }
                        } else {
                            ReorderableCategoryList(
                                categories = state.categories,
                                playingCategories = state.playingCategories,
                                isLocked = state.isLocked,
                                masterIntensity = state.masterIntensity,
                                onPlayPause = { viewModel.toggleCategory(it) },
                                onD20Click = { viewModel.triggerD20(it) },
                                onReorder = { viewModel.reorderCategories(it) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReorderableCategoryList(
    categories: List<SoundscapeCategory>,
    playingCategories: Set<String>,
    isLocked: Boolean,
    masterIntensity: Int,
    onPlayPause: (SoundscapeCategory) -> Unit,
    onD20Click: (SoundscapeCategory) -> Unit,
    onReorder: (List<SoundscapeCategory>) -> Unit,
) {
    // Local mutable list for immediate visual reordering
    var mutableCategories by remember(categories) { mutableStateOf(categories) }

    // Drag state: which index is being dragged and current Y offset
    var draggingIndex by remember { mutableIntStateOf(-1) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val itemHeightPx = 200f // approximate card height used for hit-testing

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("categoryList"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(mutableCategories, key = { _, cat -> cat.id }) { index, category ->
            val isDragging = index == draggingIndex

            SoundscapeCategoryCard(
                category = category,
                isPlaying = category.name in playingCategories,
                isLocked = isLocked,
                masterIntensity = masterIntensity,
                onPlayPause = { onPlayPause(category) },
                onD20Click = { onD20Click(category) },
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer {
                        translationY = if (isDragging) dragOffset else 0f
                        alpha = if (isDragging) 0.85f else 1f
                        scaleX = if (isDragging) 1.03f else 1f
                        scaleY = if (isDragging) 1.03f else 1f
                    }
                    .testTag("categoryCard_${category.name}")
                    .pointerInput(isLocked) {
                        if (isLocked) return@pointerInput
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggingIndex = index
                                dragOffset = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount.y

                                // Determine target index from drag offset
                                val targetIndex = (index + (dragOffset / itemHeightPx).toInt())
                                    .coerceIn(0, mutableCategories.lastIndex)

                                if (targetIndex != draggingIndex) {
                                    val updated = mutableCategories.toMutableList()
                                    val item = updated.removeAt(draggingIndex)
                                    updated.add(targetIndex, item)
                                    mutableCategories = updated
                                    dragOffset -= (targetIndex - draggingIndex) * itemHeightPx
                                    draggingIndex = targetIndex
                                }
                            },
                            onDragEnd = {
                                draggingIndex = -1
                                dragOffset = 0f
                                onReorder(mutableCategories)
                            },
                            onDragCancel = {
                                draggingIndex = -1
                                dragOffset = 0f
                                mutableCategories = categories
                            },
                        )
                    },
            )
        }
    }
}

@Composable
private fun MasterIntensitySelector(
    selectedLevel: Int,
    isLocked: Boolean,
    onLevelSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        listOf("I", "II", "III").forEachIndexed { index, label ->
            val level = index + 1
            Button(
                onClick = { onLevelSelected(level) },
                enabled = !isLocked,
                modifier = Modifier
                    .weight(1f)
                    .testTag("masterIntensityButton_$label"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedLevel == level) GoldColor else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (selectedLevel == level) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                ),
            ) {
                Text("Intensity Level $label")
            }
        }
    }
}

@Composable
private fun SoundscapeCategoryCard(
    category: SoundscapeCategory,
    isPlaying: Boolean,
    isLocked: Boolean,
    masterIntensity: Int,
    onPlayPause: () -> Unit,
    onD20Click: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var activeIntensity by rememberSaveable(category.id) { mutableIntStateOf(masterIntensity) }

    // Sync per-card intensity when master changes
    activeIntensity = masterIntensity

    val borderModifier = if (isPlaying) {
        Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp),
            )
    } else {
        Modifier.fillMaxWidth()
    }

    Card(
        modifier = modifier.then(borderModifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                ) {
                    // Drag handle - visible when not locked
                    if (!isLocked) {
                        Icon(
                            imageVector = Icons.Default.DragHandle,
                            contentDescription = "Drag to reorder",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("dragHandle_${category.name}"),
                        )
                    }
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .testTag("categoryPlayingGlow_${category.name}")
                                .padding(end = 4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    IconButton(
                        onClick = { onD20Click() },
                        enabled = !isLocked,
                        modifier = Modifier.testTag("d20Button_${category.name}"),
                    ) {
                        Text(
                            text = "D20",
                            color = if (isLocked) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                    IconButton(
                        onClick = onPlayPause,
                        enabled = !isLocked,
                        modifier = Modifier.testTag("playPauseButton_${category.name}"),
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("I", "II", "III").forEachIndexed { index, label ->
                    val level = index + 1
                    Button(
                        onClick = { activeIntensity = level },
                        enabled = !isLocked,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("intensityButton_${category.name}_$label"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeIntensity == level) GoldColor else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (activeIntensity == level) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                        ),
                    ) {
                        Text(text = "Intensity Level $label")
                    }
                }
            }
        }
    }
}
