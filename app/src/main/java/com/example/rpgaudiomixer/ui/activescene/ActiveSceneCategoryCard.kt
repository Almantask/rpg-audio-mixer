package com.example.rpgaudiomixer.ui.activescene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.components.IntensitySelector
import com.example.rpgaudiomixer.app.components.MixSlider
import com.example.rpgaudiomixer.app.components.glowBorder
import com.example.rpgaudiomixer.domain.model.ActiveSceneCategory
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@Composable
fun ActiveSceneCategoryCard(
    category: ActiveSceneCategory,
    onPlayPauseClick: () -> Unit,
    onRollRandomClick: () -> Unit,
    onIntensityChange: (IntensityLevel) -> Unit,
    onMixVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .glowBorder(category.isPlaying),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with name and controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = category.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Roll random (d20 dice)
                    IconButton(
                        onClick = onRollRandomClick,
                        enabled = category.availableTracks.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Roll Random",
                            tint = if (category.availableTracks.isNotEmpty()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            }
                        )
                    }

                    // Play/Pause
                    IconButton(
                        onClick = onPlayPauseClick,
                        enabled = category.availableTracks.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = if (category.isPlaying) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = if (category.isPlaying) "Pause" else "Play",
                            tint = if (category.availableTracks.isNotEmpty()) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            }
                        )
                    }
                }
            }

            // Current track name
            if (category.currentTrackName != null) {
                Text(
                    text = "Playing: ${category.currentTrackName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Intensity selector (disabled when no tracks available)
            IntensitySelector(
                selected = category.intensityLevel,
                onIntensitySelected = onIntensityChange,
                enabled = category.availableTracks.isNotEmpty()
            )

            // Mix volume slider
            MixSlider(
                label = "MIX",
                value = category.mixVolume,
                onValueChange = onMixVolumeChange
            )
        }
    }
}
