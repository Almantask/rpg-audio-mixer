package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.Scene

/**
 * Scene card component showing name, description, tags, and actions.
 *
 * Features:
 * - Scene name and description
 * - Tag chips display
 * - Play button
 * - Swipe-to-delete action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SceneCard(
    scene: Scene,
    onClick: () -> Unit,
    onPlay: () -> Unit,
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
        modifier = modifier.testTag("SceneCard_${scene.id}")
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = scene.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("SceneCard_${scene.id}_Name")
                        )
                        if (scene.description != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = scene.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("SceneCard_${scene.id}_Description")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onPlay,
                        modifier = Modifier.testTag("SceneCard_${scene.id}_PlayButton")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Scene",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (scene.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TagRow(
                        tags = scene.tags,
                        modifier = Modifier.testTag("SceneCard_${scene.id}_Tags")
                    )
                }
            }
        }
    }
}

@Composable
private fun TagRow(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.take(3).forEach { tag ->
            AssistChip(
                onClick = { /* Tags are read-only in this context */ },
                label = { Text(tag, style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier.testTag("TagChip_$tag")
            )
        }
        if (tags.size > 3) {
            AssistChip(
                onClick = { },
                label = { Text("+${tags.size - 3}", style = MaterialTheme.typography.labelSmall) }
            )
        }
    }
}
