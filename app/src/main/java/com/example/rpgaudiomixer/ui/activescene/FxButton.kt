package com.example.rpgaudiomixer.ui.activescene

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.components.glowBorder
import com.example.rpgaudiomixer.domain.model.ActiveSceneFx

/**
 * FX Button component for the soundboard grid.
 *
 * Displays an FX track as a button with:
 * - Play/Pause icon (▶/⏸)
 * - FX name
 * - Glow effect when playing
 * - Instance count badge when multiple instances are playing
 */
@Composable
fun FxButton(
    fx: ActiveSceneFx,
    onTriggerClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .glowBorder(fx.isPlaying),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = {
            if (fx.isPlaying) {
                // When playing, tapping triggers a new instance (re-trigger)
                onTriggerClick()
            } else {
                onTriggerClick()
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Play/Pause icon button
                IconButton(
                    onClick = {
                        if (fx.isPlaying) {
                            onStopClick()
                        } else {
                            onTriggerClick()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (fx.isPlaying) {
                            Icons.Default.Pause
                        } else {
                            Icons.Default.PlayArrow
                        },
                        contentDescription = if (fx.isPlaying) "Stop" else "Play",
                        tint = if (fx.isPlaying) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                // FX name
                Text(
                    text = fx.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Instance count (only shown when multiple instances are playing)
                if (fx.activeInstanceCount > 1) {
                    Text(
                        text = "×${fx.activeInstanceCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
