package com.example.rpgaudiomixer.ui.fx

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.FxTrack

/**
 * FX track card showing name, tags, play button, and edit action.
 *
 * Features:
 * - Track name
 * - Tag chips (up to 3, with overflow indicator)
 * - Play button for preview
 * - Edit icon button
 */
@Composable
fun FxTrackCard(
    track: FxTrack,
    onPlay: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("FxTrack_${track.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play button
            IconButton(
                onClick = onPlay,
                modifier = Modifier.testTag("FxTrack_${track.id}_PlayButton")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Track name and tags
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("FxTrack_${track.id}_Name")
                )

                if (track.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    TagChipsRow(
                        tags = track.tags,
                        trackId = track.id
                    )
                }
            }

            // Edit button
            IconButton(
                onClick = onEdit,
                modifier = Modifier.testTag("FxTrack_${track.id}_EditButton")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Tag chips row with overflow handling (shows up to 3 tags + overflow indicator).
 */
@Composable
private fun TagChipsRow(
    tags: List<String>,
    trackId: Long,
    modifier: Modifier = Modifier
) {
    val displayTags = tags.take(3)
    val overflowCount = (tags.size - 3).coerceAtLeast(0)

    Row(
        modifier = modifier.testTag("FxTrack_${trackId}_Tags"),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        displayTags.forEach { tag ->
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.testTag("FxTrack_${trackId}_Tag_$tag")
            )
        }

        if (overflowCount > 0) {
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = "+$overflowCount",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.testTag("FxTrack_${trackId}_Tag_Overflow")
            )
        }
    }
}
