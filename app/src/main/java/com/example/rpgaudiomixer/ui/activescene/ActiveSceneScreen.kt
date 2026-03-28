package com.example.rpgaudiomixer.ui.activescene

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.theme.ArcanumBorder
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumElevatedSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDark
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.app.theme.ArcanumGrayMid
import com.example.rpgaudiomixer.app.theme.IntensityColorI
import com.example.rpgaudiomixer.app.theme.IntensityColorII
import com.example.rpgaudiomixer.app.theme.IntensityColorIII
import com.example.rpgaudiomixer.domain.model.SceneFx
import com.example.rpgaudiomixer.domain.model.SceneSoundscape
import com.example.rpgaudiomixer.ui.components.ArcanumTopBar
import com.example.rpgaudiomixer.ui.components.EmptyState
import com.example.rpgaudiomixer.ui.components.PrimaryButton

private val tabs = listOf("SOUNDSCAPES", "SOUNDBOARD")

@Composable
fun ActiveSceneScreen(
    sceneId: Long,
    onBack: () -> Unit,
    onCredits: () -> Unit,
    onAddSoundscape: () -> Unit,
    onAddFx: () -> Unit,
    viewModel: ActiveSceneViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState(sceneId).collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        ArcanumTopBar(
            onCredits = onCredits,
            showBack = true,
            onBack = onBack,
        )

        state.scene?.let { scene ->
            Text(
                text = scene.name,
                style = MaterialTheme.typography.displaySmall,
                color = ArcanumGold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        // Tab row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = ArcanumGold,
            indicator = { tabPositions ->
                Box(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .height(2.dp)
                        .background(ArcanumGold),
                )
            },
            divider = {},
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selectedTab == index) ArcanumGold else ArcanumGrayMid,
                        )
                    },
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(ArcanumBorder),
        )

        when (selectedTab) {
            0 -> SoundscapesTab(
                state = state,
                sceneId = sceneId,
                onAddSoundscape = onAddSoundscape,
                viewModel = viewModel,
            )
            1 -> SoundboardTab(
                state = state,
                sceneId = sceneId,
                onAddFx = onAddFx,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
private fun SoundscapesTab(
    state: ActiveSceneUiState,
    sceneId: Long,
    onAddSoundscape: () -> Unit,
    viewModel: ActiveSceneViewModel,
) {
    var masterVolume by remember(state.scene?.atmosphereMasterVolume) {
        mutableFloatStateOf(state.scene?.atmosphereMasterVolume ?: 0.8f)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Master volume
            VolumeSlider(
                label = "ATMOSPHERE MASTER",
                value = masterVolume,
                onValueChange = { masterVolume = it },
                onValueChangeFinished = {
                    state.scene?.let { viewModel.updateAtmosphereMasterVolume(it, masterVolume) }
                },
            )
        }

        if (state.soundscapes.isEmpty() && !state.isLoading) {
            item {
                EmptyState(
                    title = "No Soundscapes",
                    subtitle = "Add atmospheres to paint the scene.",
                )
            }
        }

        items(state.soundscapes, key = { it.category.id }) { sceneSoundscape ->
            SoundscapeCard(
                sceneSoundscape = sceneSoundscape,
                onIntensityChange = { intensity ->
                    viewModel.updateSoundscapeIntensity(sceneId, sceneSoundscape.category.id, intensity)
                },
                onMixChange = { mix ->
                    viewModel.updateSoundscapeMix(sceneId, sceneSoundscape.category.id, mix)
                },
                onRemove = { viewModel.removeSoundscape(sceneId, sceneSoundscape.category.id) },
            )
        }

        item {
            PrimaryButton(
                text = "+ ADD SOUNDSCAPE",
                onClick = onAddSoundscape,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SoundboardTab(
    state: ActiveSceneUiState,
    sceneId: Long,
    onAddFx: () -> Unit,
    viewModel: ActiveSceneViewModel,
) {
    var masterVolume by remember(state.scene?.soundboardMasterVolume) {
        mutableFloatStateOf(state.scene?.soundboardMasterVolume ?: 0.8f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        VolumeSlider(
            label = "SOUNDBOARD MASTER",
            value = masterVolume,
            onValueChange = { masterVolume = it },
            onValueChangeFinished = {
                state.scene?.let { viewModel.updateSoundboardMasterVolume(it, masterVolume) }
            },
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (state.fx.isEmpty() && !state.isLoading) {
            EmptyState(
                title = "No FX on Soundboard",
                subtitle = "Add FX sounds to trigger on command.",
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(state.fx, key = { it.effect.id }) { sceneFx ->
                FxButton(
                    sceneFx = sceneFx,
                    onPlay = { /* trigger media playback */ },
                    onRemove = { viewModel.removeFx(sceneId, sceneFx.effect.id) },
                )
            }
        }

        PrimaryButton(
            text = "+ ADD FX",
            onClick = onAddFx,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VolumeSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = ArcanumGrayLight)
            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = ArcanumGold,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = ArcanumGold,
                activeTrackColor = ArcanumGold,
                inactiveTrackColor = ArcanumBorder,
            ),
        )
    }
}

@Composable
private fun SoundscapeCard(
    sceneSoundscape: SceneSoundscape,
    onIntensityChange: (Int) -> Unit,
    onMixChange: (Float) -> Unit,
    onRemove: () -> Unit,
) {
    var mixVolume by remember(sceneSoundscape.mixVolume) {
        mutableFloatStateOf(sceneSoundscape.mixVolume)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCardSurface)
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sceneSoundscape.category.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (sceneSoundscape.category.parentCategory.isNotBlank()) {
                    Text(
                        text = sceneSoundscape.category.parentCategory.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = ArcanumGrayMid,
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Remove ${sceneSoundscape.category.name}",
                    tint = ArcanumGrayMid,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Intensity buttons: I, II, III
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1 to "I", 2 to "II", 3 to "III").forEach { (level, label) ->
                val isSelected = sceneSoundscape.activeIntensity == level
                val color = when (level) {
                    1 -> IntensityColorI
                    2 -> IntensityColorII
                    else -> IntensityColorIII
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) color else ArcanumBorder,
                            shape = RoundedCornerShape(6.dp),
                        )
                        .clickable { onIntensityChange(level) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) color else ArcanumGrayMid,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Play indicator
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ArcanumGold.copy(alpha = 0.15f))
                    .border(1.dp, ArcanumGold, CircleShape)
                    .clickable { /* trigger playback */ },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = ArcanumGold,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // MIX slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "MIX",
                style = MaterialTheme.typography.labelSmall,
                color = ArcanumGrayLight,
                modifier = Modifier.width(36.dp),
            )
            Slider(
                value = mixVolume,
                onValueChange = { mixVolume = it },
                onValueChangeFinished = { onMixChange(mixVolume) },
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = ArcanumGold,
                    activeTrackColor = ArcanumGoldDark,
                    inactiveTrackColor = ArcanumBorder,
                ),
            )
            Text(
                text = "${(mixVolume * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = ArcanumGold,
                modifier = Modifier.width(40.dp),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun FxButton(
    sceneFx: SceneFx,
    onPlay: () -> Unit,
    onRemove: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumElevatedSurface)
            .border(1.dp, ArcanumBorder, RoundedCornerShape(12.dp))
            .clickable { onPlay() }
            .padding(12.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ArcanumGold.copy(alpha = 0.15f))
                    .border(1.dp, ArcanumGold, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play ${sceneFx.effect.name}",
                    tint = ArcanumGold,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sceneFx.effect.name,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                maxLines = 2,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
            )
        }
        // Remove button top-right
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopEnd),
        ) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Remove ${sceneFx.effect.name}",
                tint = ArcanumGrayMid,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}
