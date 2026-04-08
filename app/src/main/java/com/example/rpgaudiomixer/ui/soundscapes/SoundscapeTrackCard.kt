package com.example.rpgaudiomixer.ui.soundscapes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SoundscapeTrack

/**
 * Soundscape track card showing name, intensity selector, MIX slider, and delete action.
 *
 * Features:
 * - Track name display
 * - Intensity level selector (I, II, III)
 * - MIX volume slider (0-100%)
 * - Swipe-to-delete action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundscapeTrackCard(
    track: SoundscapeTrack,
    onIntensityChange: (IntensityLevel) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = modifier.testTag("SoundscapeTrack_${track.id}")
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Track name
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("SoundscapeTrack_${track.id}_Name")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Intensity selector
                Text(
                    text = "Intensity",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("SoundscapeTrack_${track.id}_IntensitySelector")
                ) {
                    IntensityLevel.entries.forEach { level ->
                        FilterChip(
                            selected = track.intensityLevel == level,
                            onClick = { onIntensityChange(level) },
                            label = { Text(level.displayName) },
                            modifier = Modifier.testTag("SoundscapeTrack_${track.id}_Intensity_${level.displayName}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // MIX slider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "MIX",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(50.dp)
                    )
                    Slider(
                        value = track.mixVolume,
                        onValueChange = onVolumeChange,
                        valueRange = 0f..1f,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("SoundscapeTrack_${track.id}_MixSlider")
                    )
                    Text(
                        text = "${(track.mixVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .width(50.dp)
                            .testTag("SoundscapeTrack_${track.id}_MixValue")
                    )
                }
            }
        }
    }
}
