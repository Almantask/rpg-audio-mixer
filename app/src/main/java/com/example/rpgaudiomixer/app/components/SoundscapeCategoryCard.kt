package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.IntensityLevel

data class SoundscapeCategoryCardModel(
    val categoryId: Long,
    val name: String,
    val currentTrackName: String?,
    val mixVolume: Float,
    val selectedIntensity: IntensityLevel,
    val enabledLevels: Set<IntensityLevel>,
    val isPlaying: Boolean,
    val canPlay: Boolean,
    val canMoveUp: Boolean,
    val canMoveDown: Boolean,
)

@Composable
fun SoundscapeCategoryCard(
    model: SoundscapeCategoryCardModel,
    onRollRandom: () -> Unit,
    onTogglePlayback: () -> Unit,
    onMixChanged: (Float) -> Unit,
    onIntensitySelected: (IntensityLevel) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.glowBorder(model.isPlaying),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = model.currentTrackName ?: "No track loaded",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(
                    onClick = onMoveUp,
                    enabled = model.canMoveUp,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowUp,
                        contentDescription = "Move up",
                    )
                }
                IconButton(
                    onClick = onMoveDown,
                    enabled = model.canMoveDown,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Move down",
                    )
                }
                IconButton(
                    onClick = onRollRandom,
                    enabled = model.canPlay,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Casino,
                        contentDescription = "Pick random track",
                    )
                }
                IconButton(
                    onClick = onTogglePlayback,
                    enabled = model.canPlay,
                ) {
                    Icon(
                        imageVector = if (model.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (model.isPlaying) "Pause category" else "Play category",
                    )
                }
            }

            MixSlider(
                mixVolume = model.mixVolume,
                onMixVolumeChanged = onMixChanged,
            )

            IntensitySelector(
                selectedLevel = model.selectedIntensity,
                enabledLevels = model.enabledLevels,
                onLevelSelected = onIntensitySelected,
            )
        }
    }
}
