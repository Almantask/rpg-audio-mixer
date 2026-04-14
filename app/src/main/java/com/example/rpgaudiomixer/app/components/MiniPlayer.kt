package com.example.rpgaudiomixer.app.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/**
 * Mini player bar that slides in from the bottom when an FX track is being previewed.
 *
 * Iter 8 – Shared Y-Axis entrance/exit animation.
 *
 * @param isVisible whether the mini player should be shown
 * @param trackName the name of the currently previewing track, or null if none
 * @param onClose called when the user taps the close button
 */
@Composable
fun MiniPlayer(
    isVisible: Boolean,
    trackName: String?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { fullHeight -> fullHeight },
        exit = slideOutVertically { fullHeight -> fullHeight },
        modifier = modifier.testTag("miniPlayer"),
    ) {
        Surface(
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = trackName ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("miniPlayerTrackName"),
                    )
                }
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("miniPlayerClose"),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close mini player",
                    )
                }
            }
        }
    }
}
