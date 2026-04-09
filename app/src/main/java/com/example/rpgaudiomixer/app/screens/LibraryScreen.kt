package com.example.rpgaudiomixer.app.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.*
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.library.FxLibraryViewModel
import com.example.rpgaudiomixer.app.ui.library.SoundscapeLibraryUiState
import com.example.rpgaudiomixer.app.ui.library.SoundscapeLibraryViewModel
import com.example.rpgaudiomixer.domain.library.FxTrack
import com.example.rpgaudiomixer.domain.library.IntensityLevel
import com.example.rpgaudiomixer.domain.library.SoundscapeCategory

@Composable
fun LibraryScreen(
    onCategoryClick: (Long) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("SOUNDSCAPES", "SOUND EFFECTS")
    val fxViewModel: FxLibraryViewModel = hiltViewModel()
    
    val previewTitle by fxViewModel.currentPreviewTitle.collectAsState()
    val isPreviewing by fxViewModel.isPreviewing.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            fxViewModel.stopPreview()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(ArcanumBlack)) {
        Column(modifier = Modifier.fillMaxSize()) {
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

            when (selectedTab) {
                0 -> SoundscapesTab(onCategoryClick = onCategoryClick)
                1 -> SoundEffectsTab(viewModel = fxViewModel)
            }
        }

        MiniPlayerBar(
            title = previewTitle,
            isPlaying = isPreviewing,
            onToggle = { /* handled via viewModel toggle in individual items */ },
            onClose = { fxViewModel.stopPreview() },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
fun SoundscapesTab(
    viewModel: SoundscapeLibraryViewModel = hiltViewModel(),
    onCategoryClick: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is SoundscapeLibraryUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = ArcanumGold
                )
            }
            is SoundscapeLibraryUiState.Success -> {
                if (state.categories.isEmpty()) {
                    EmptyStateView(
                        illustration = Icons.Default.MusicNote,
                        title = "Silence in the library",
                        subtitle = "Compose your first category of soundscapes.",
                        actionLabel = "NEW COMPOSITION",
                        onAction = { showCreateDialog = true }
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 88.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.categories, key = { it.id }) { category ->
                            SwipeToDeleteContainer(
                                onDelete = { viewModel.deleteCategory(category.id) }
                            ) {
                                BentoCategoryCard(
                                    category = category,
                                    onClick = { onCategoryClick(category.id) }
                                )
                            }
                        }
                    }
                }
            }
            is SoundscapeLibraryUiState.Error -> {
                Text(text = state.message, color = ArcanumErrorRed, modifier = Modifier.align(Alignment.Center))
            }
        }

        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = ArcanumGold,
            contentColor = ArcanumOnGold
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Category")
        }

        if (showCreateDialog) {
            CreateCategoryDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name ->
                    viewModel.createCategory(name)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
fun SoundEffectsTab(
    viewModel: FxLibraryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showEditTrack by remember { mutableStateOf<FxTrack?>(null) }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val path = com.example.rpgaudiomixer.app.util.FileUtil.copyAudioToInternal(context, it)
            if (path != null) {
                viewModel.importFx("New Effect", path, 0L)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                placeholder = "SEARCH EFFECTS..."
            )

            if (uiState.tracks.isEmpty() && !uiState.isLoading) {
                EmptyStateView(
                    illustration = Icons.Default.MusicNote,
                    title = "The silence is deafening",
                    subtitle = "Import your first sound effect.",
                    actionLabel = "IMPORT FX",
                    onAction = { audioPicker.launch(arrayOf("audio/*")) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.tracks, key = { it.id }) { track ->
                        SwipeToDeleteContainer(onDelete = { viewModel.deleteFx(track.id) }) {
                            FxTrackRow(
                                track = track,
                                onPreviewClick = { viewModel.togglePreview(track) },
                                onEditClick = { showEditTrack = track }
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { audioPicker.launch(arrayOf("audio/*")) },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = ArcanumGold,
            contentColor = ArcanumOnGold
        ) {
            Icon(Icons.Default.Add, contentDescription = "Import FX")
        }

        showEditTrack?.let { track ->
            EditFxDialog(
                track = track,
                onDismiss = { showEditTrack = null },
                onConfirm = { updatedTrack ->
                    viewModel.updateFx(updatedTrack)
                    showEditTrack = null
                }
            )
        }
    }
}

@Composable
fun BentoCategoryCard(
    category: SoundscapeCategory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(140.dp),
        colors = CardDefaults.cardColors(containerColor = ArcanumCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
            Text(
                text = category.name.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = ArcanumGold,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                IntensityCountBadge(IntensityLevel.I, category.tracks.count { it.intensityLevel == IntensityLevel.I })
                IntensityCountBadge(IntensityLevel.II, category.tracks.count { it.intensityLevel == IntensityLevel.II })
                IntensityCountBadge(IntensityLevel.III, category.tracks.count { it.intensityLevel == IntensityLevel.III })
            }
        }
    }
}

@Composable
fun IntensityCountBadge(level: IntensityLevel, count: Int) {
    val isZero = count == 0
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = level.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (isZero) ArcanumOnSurface.copy(alpha = 0.3f) else ArcanumGold
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = if (isZero) ArcanumOnSurface.copy(alpha = 0.2f) else ArcanumMutedGold,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun FxTrackRow(
    track: FxTrack,
    onPreviewClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Surface(
        onClick = onPreviewClick,
        modifier = Modifier.fillMaxWidth().height(72.dp),
        color = ArcanumCard,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArcanumBlack),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = ArcanumMutedGold)
            }
            
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = ArcanumGold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    track.tags.take(3).forEach { tag ->
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArcanumOnSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = ArcanumMutedGold)
            }
        }
    }
}

@Composable
fun CreateCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumCard,
        title = { Text("New Composition", color = ArcanumGold) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Category Name") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ArcanumGold,
                    focusedLabelColor = ArcanumGold,
                    unfocusedLabelColor = ArcanumOnSurface.copy(alpha = 0.7f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("SCRIBE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ArcanumOnSurface)
            }
        }
    )
}

@Composable
fun EditFxDialog(
    track: FxTrack,
    onDismiss: () -> Unit,
    onConfirm: (FxTrack) -> Unit
) {
    var name by remember { mutableStateOf(track.name) }
    var tagsInput by remember { mutableStateOf(track.tags.joinToString(", ")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = ArcanumCard,
        title = { Text("Refine Effect", color = ArcanumGold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArcanumGold, focusedLabelColor = ArcanumGold),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tags (comma separated)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ArcanumGold, focusedLabelColor = ArcanumGold),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalTags = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    onConfirm(track.copy(name = name, tags = finalTags))
                }
            ) {
                Text("SCRIBE", color = ArcanumGold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = ArcanumOnSurface)
            }
        }
    )
}
