package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@Composable
fun SoundscapeCategoryCard(
    categoryName: String,
    currentTrackName: String?,
    isPlaying: Boolean,
    mixVolume: Float,
    intensityLevel: IntensityLevel,
    availableIntensities: Set<IntensityLevel>,
    onPlayPauseClick: () -> Unit,
    onRollRandomClick: () -> Unit,
    onIntensityChange: (IntensityLevel) -> Unit,
    onMixVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderStroke = if (isPlaying) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = "SoundscapeCategoryCard_$categoryName" },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = borderStroke
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Category name + controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Random roll button (d20)
                    IconButton(
                        onClick = onRollRandomClick,
                        enabled = availableIntensities.isNotEmpty(),
                        modifier = Modifier.semantics { contentDescription = "RollRandom_$categoryName" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Roll Random",
                            tint = if (availableIntensities.isNotEmpty())
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }

                    // Play/Pause button
                    IconButton(
                        onClick = onPlayPauseClick,
                        enabled = availableIntensities.isNotEmpty(),
                        modifier = Modifier.semantics { contentDescription = "PlayPause_$categoryName" }
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = if (availableIntensities.isNotEmpty())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }

            // Current track name
            if (currentTrackName != null) {
                Text(
                    text = "♪ $currentTrackName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { contentDescription = "CurrentTrack_$categoryName" }
                )
            }

            // Intensity selector
            IntensitySelector(
                selectedIntensity = intensityLevel,
                availableIntensities = availableIntensities,
                onIntensitySelected = onIntensityChange
            )

            // Mix volume slider
            MixSlider(
                label = "MIX",
                value = mixVolume,
                onValueChange = onMixVolumeChange,
                enabled = true
            )
        }
    }
}
