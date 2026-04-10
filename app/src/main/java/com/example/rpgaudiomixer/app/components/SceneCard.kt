package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun SceneCard(
    scene: Scene,
    onOpenScene: () -> Unit,
    onPlayScene: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenScene),
            ) {
                Text(
                    text = scene.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = ArcanumGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (scene.description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = scene.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                if (scene.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    TagRow(tags = scene.tags)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${scene.soundscapeCount} soundscapes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onPlayScene) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = "Play scene",
                    tint = ArcanumGold,
                )
            }
        }
    }
}
