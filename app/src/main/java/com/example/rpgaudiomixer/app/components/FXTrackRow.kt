package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.domain.model.FXTrack
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun FXTrackRow(
    track: FXTrack,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play/Stop Button
            IconButton(
                onClick = onPlayToggle,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isPlaying) Gold else Gold.copy(alpha = 0.1f),
                    contentColor = if (isPlaying) BlackBg else Gold
                ),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Stop Preview" else "Play Preview"
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.name.uppercase(),
                    style = Typography.bodyLarge,
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                
                if (track.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        track.tags.take(3).forEach { tag ->
                            TagChip(tag = tag)
                        }
                    }
                }
            }

            // Duration and Edit
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatDuration(track.durationMs),
                    style = Typography.labelSmall,
                    color = Gold.copy(alpha = 0.5f)
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit FX",
                        tint = Gold.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
