package com.example.rpgaudiomixer.app.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.components.GradientSlider
import com.example.rpgaudiomixer.app.theme.ArcanumBlack
import com.example.rpgaudiomixer.app.theme.ArcanumCard
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDim
import com.example.rpgaudiomixer.app.theme.ArcanumDisabled
import com.example.rpgaudiomixer.app.theme.ArcanumDisabledContent
import com.example.rpgaudiomixer.app.theme.ArcanumGlow
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurface
import com.example.rpgaudiomixer.app.theme.ArcanumOnSurfaceVariant
import com.example.rpgaudiomixer.app.theme.ArcanumPink
import com.example.rpgaudiomixer.app.theme.ArcanumPurple
import com.example.rpgaudiomixer.app.theme.ArcanumSurface
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.app.viewmodel.ActiveSceneViewModel
import com.example.rpgaudiomixer.app.viewmodel.CategoryPlayState
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneFXTrack
import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory

@Composable
fun ActiveSceneScreen(
    onAddSoundscape: (Long) -> Unit,
    onAddFX: (Long) -> Unit,
    onBack: () -> Unit,
    onCredits: () -> Unit,
    viewModel: ActiveSceneViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = state.scene.name.ifBlank { "Scene" },
                onBack = onBack,
                onCredits = onCredits,
            )
        },
        containerColor = ArcanumBlack,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = ArcanumSurface,
                contentColor = ArcanumGold,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        color = ArcanumGold,
                    )
                },
            ) {
                listOf("SOUNDSCAPES", "SOUNDBOARD").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            )
                        },
                        selectedContentColor = ArcanumGold,
                        unselectedContentColor = ArcanumOnSurfaceVariant,
                    )
                }
            }

            when (selectedTab) {
                0 -> SoundscapesTab(
                    state.soundscapeCategories,
                    state.categoryPlayStates,
                    state.masterAtmosphereVolume,
                    onMasterVolumeChange = viewModel::setMasterAtmosphereVolume,
                    onTogglePlay = viewModel::toggleCategoryPlayback,
                    onD20 = viewModel::playRandomTrack,
                    onIntensityChange = viewModel::setIntensityLevel,
                    onMixVolumeChange = viewModel::setCategoryMixVolume,
                    onRemove = viewModel::removeSoundscapeCategory,
                    onAddNew = { onAddSoundscape(state.scene.id) },
                )
                1 -> SoundboardTab(
                    state.fxTracks,
                    state.playingFxIds,
                    state.masterSoundboardVolume,
                    onMasterVolumeChange = viewModel::setMasterSoundboardVolume,
                    onTap = viewModel::toggleFX,
                    onRemove = viewModel::removeFX,
                    onAddNew = { onAddFX(state.scene.id) },
                )
            }
        }
    }
}

@Composable
private fun SoundscapesTab(
    categories: List<SceneSoundscapeCategory>,
    playStates: Map<Long, CategoryPlayState>,
    masterVolume: Float,
    onMasterVolumeChange: (Float) -> Unit,
    onTogglePlay: (Long) -> Unit,
    onD20: (Long, IntensityLevel) -> Unit,
    onIntensityChange: (Long, IntensityLevel) -> Unit,
    onMixVolumeChange: (Long, Float) -> Unit,
    onRemove: (Long) -> Unit,
    onAddNew: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp),
    ) {
        // Master slider
        item {
            MasterSliderRow("MASTER ATMOSPHERE", masterVolume, onMasterVolumeChange)
        }

        // Category cards
        items(categories, key = { it.id }) { ssc ->
            val playState = playStates[ssc.id] ?: CategoryPlayState()
            SoundscapeCategoryCard(
                ssc = ssc,
                playState = playState,
                onTogglePlay = { onTogglePlay(ssc.id) },
                onD20 = { onD20(ssc.id, playState.selectedIntensity) },
                onIntensityChange = { level -> onIntensityChange(ssc.id, level) },
                onMixVolumeChange = { volume -> onMixVolumeChange(ssc.id, volume) },
                onRemove = { onRemove(ssc.id) },
            )
        }

        // Add button
        item {
            AddButton(label = "ADD NEW SOUNDSCAPE", onClick = onAddNew)
        }
    }
}

@Composable
private fun MasterSliderRow(label: String, value: Float, onChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(label, fontSize = 10.sp, letterSpacing = 1.5.sp, color = ArcanumGoldDim, fontWeight = FontWeight.Bold)
            Text("${(value * 100).toInt()}%", fontSize = 12.sp, color = ArcanumOnSurfaceVariant)
        }
        Spacer(Modifier.height(6.dp))
        GradientSlider(value = value, onValueChange = onChange)
    }
}

@Composable
private fun SoundscapeCategoryCard(
    ssc: SceneSoundscapeCategory,
    playState: CategoryPlayState,
    onTogglePlay: () -> Unit,
    onD20: () -> Unit,
    onIntensityChange: (IntensityLevel) -> Unit,
    onMixVolumeChange: (Float) -> Unit,
    onRemove: () -> Unit,
) {
    val isPlaying = playState.isPlaying
    val glowAnimation = rememberInfiniteTransition(label = "glow")
    val glowAlpha by glowAnimation.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "glowAlpha",
    )

    val borderColor = if (isPlaying) ArcanumGold.copy(alpha = glowAlpha) else ArcanumGoldDim.copy(alpha = 0.25f)
    val cardBackground = if (isPlaying) ArcanumGold.copy(alpha = 0.05f) else ArcanumCard

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(cardBackground)
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Header row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ssc.category.name,
                    fontFamily = FontFamily.Serif,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPlaying) ArcanumGold else ArcanumOnSurface,
                )
                if (isPlaying && playState.currentTrackName != null) {
                    Text(
                        text = "♪ ${playState.currentTrackName}",
                        fontSize = 11.sp,
                        color = ArcanumGold.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            // D20 random button
            IconButton(
                onClick = onD20,
                enabled = ssc.category.hasTracksFor(playState.selectedIntensity),
            ) {
                Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = "Random track",
                    tint = if (ssc.category.hasTracksFor(playState.selectedIntensity)) ArcanumPurple else ArcanumDisabledContent,
                )
            }
            // Play/Pause
            IconButton(
                onClick = onTogglePlay,
                enabled = ssc.category.hasTracksFor(playState.selectedIntensity),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) ArcanumGold else ArcanumGoldDim.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = if (isPlaying) Color(0xFF1A0E00) else ArcanumGold,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.DeleteOutline, "Remove", tint = ArcanumOnSurfaceVariant, modifier = Modifier.size(18.dp))
            }
        }

        // Intensity selector (I / II / III)
        IntensitySelector(
            category = ssc.category,
            selected = playState.selectedIntensity,
            onSelect = onIntensityChange,
        )

        // MIX slider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("MIX", fontSize = 9.sp, letterSpacing = 1.sp, color = ArcanumOnSurfaceVariant, modifier = Modifier.width(30.dp))
            GradientSlider(
                value = ssc.mixVolume,
                onValueChange = onMixVolumeChange,
                modifier = Modifier.weight(1f),
            )
            Text(
                "${(ssc.mixVolume * 100).toInt()}%",
                fontSize = 11.sp,
                color = ArcanumOnSurfaceVariant,
                modifier = Modifier.width(32.dp),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun IntensitySelector(
    category: com.example.rpgaudiomixer.domain.model.SoundscapeCategory,
    selected: IntensityLevel,
    onSelect: (IntensityLevel) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        IntensityLevel.entries.forEach { level ->
            val hasTracks = category.hasTracksFor(level)
            val isSelected = selected == level
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        when {
                            isSelected && hasTracks -> ArcanumGold.copy(alpha = 0.2f)
                            isSelected -> ArcanumSurfaceVariant
                            else -> ArcanumSurfaceVariant.copy(alpha = 0.5f)
                        }
                    )
                    .border(
                        1.dp,
                        if (isSelected) ArcanumGold else if (hasTracks) ArcanumGoldDim.copy(alpha = 0.3f) else ArcanumDisabled,
                        RoundedCornerShape(6.dp),
                    )
                    .clickable(enabled = hasTracks) { onSelect(level) }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = level.label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isSelected && hasTracks -> ArcanumGold
                        isSelected -> ArcanumOnSurfaceVariant
                        hasTracks -> ArcanumOnSurfaceVariant
                        else -> ArcanumDisabledContent
                    },
                )
            }
        }
        val selectedCount = category.tracksFor(selected).size
        if (selectedCount > 0) {
            Spacer(Modifier.width(4.dp))
            Text(
                "$selectedCount track${if (selectedCount != 1) "s" else ""}",
                fontSize = 10.sp,
                color = ArcanumOnSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
private fun SoundboardTab(
    fxTracks: List<SceneFXTrack>,
    playingFxIds: Set<Long>,
    masterVolume: Float,
    onMasterVolumeChange: (Float) -> Unit,
    onTap: (Long) -> Unit,
    onRemove: (Long) -> Unit,
    onAddNew: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        MasterSliderRow("MASTER SOUNDBOARD", masterVolume, onMasterVolumeChange)

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(fxTracks, key = { it.id }) { sceneFX ->
                val fxId = sceneFX.fxTrack.id
                val isPlaying = fxId in playingFxIds
                FXButton(
                    name = sceneFX.fxTrack.name,
                    isPlaying = isPlaying,
                    onTap = { onTap(sceneFX.id) },
                    onLongPress = { onRemove(sceneFX.id) },
                )
            }
        }

        AddButton(label = "ADD NEW EFFECT", onClick = onAddNew)
    }
}

@Composable
private fun FXButton(
    name: String,
    isPlaying: Boolean,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
) {
    val pulseTransition = rememberInfiniteTransition(label = "fxPulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulseAlpha",
    )

    val borderColor = if (isPlaying) ArcanumPink.copy(alpha = pulseAlpha) else ArcanumGoldDim.copy(alpha = 0.2f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isPlaying) ArcanumPink.copy(alpha = 0.1f) else ArcanumCard)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onTap() }
            .padding(8.dp)
            .height(72.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Stop" else "Play",
                tint = if (isPlaying) ArcanumPink else ArcanumGold,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = name,
                fontSize = 9.sp,
                color = if (isPlaying) ArcanumPink else ArcanumOnSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 12.sp,
            )
        }
    }
}

@Composable
private fun AddButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = ArcanumGold.copy(alpha = 0.15f),
            contentColor = ArcanumGold,
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArcanumGoldDim.copy(alpha = 0.4f)),
    ) {
        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}


