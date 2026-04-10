package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun SceneCard(
    scene: Scene,
    onOpenScene: () -> Unit,
    onPlayScene: () -> Unit,
    onEditScene: (() -> Unit)? = null,
    onCloneScene: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenScene),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = scene.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                TagRow(tags = scene.tags)
                Text(
                    text = "${scene.soundscapeCount} soundscapes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                scene.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            onEditScene?.let { editScene ->
                IconButton(onClick = editScene) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ${scene.name}",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            onCloneScene?.let { cloneScene ->
                IconButton(onClick = cloneScene) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Clone ${scene.name}",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            IconButton(onClick = onPlayScene) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play ${scene.name}",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
