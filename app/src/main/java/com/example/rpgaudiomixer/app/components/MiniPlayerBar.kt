package com.example.rpgaudiomixer.app.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun MiniPlayerBar(
    trackName: String,
    isPlaying: Boolean,
    onPlayPauseToggle: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = CardSurface,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PREVIEWING",
                    style = Typography.labelSmall,
                    color = Gold.copy(alpha = 0.5f)
                )
                Text(
                    text = trackName.uppercase(),
                    style = Typography.bodyLarge,
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            IconButton(onClick = onPlayPauseToggle) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause Preview" else "Play Preview",
                    tint = Gold
                )
            }

            IconButton(onClick = onStop) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop Preview",
                    tint = Gold.copy(alpha = 0.7f)
                )
            }
        }
    }
}
