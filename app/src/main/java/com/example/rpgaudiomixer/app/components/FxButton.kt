package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

data class FxButtonModel(
    val fxTrackId: Long,
    val name: String,
    val playCount: Int,
    val activeInstanceCount: Int,
    val canMoveUp: Boolean,
    val canMoveDown: Boolean,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FxButton(
    model: FxButtonModel,
    onTrigger: () -> Unit,
    onStop: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onArmDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .glowBorder(model.activeInstanceCount > 0)
            .combinedClickable(
                onClick = onTrigger,
                onLongClick = onArmDelete,
            ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = model.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = if (model.activeInstanceCount > 0) {
                    "LIVE ×${model.activeInstanceCount}"
                } else {
                    "Played ${model.playCount}×"
                },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(onClick = onMoveUp, enabled = model.canMoveUp) {
                    Icon(Icons.Rounded.KeyboardArrowUp, contentDescription = "Move up")
                }
                IconButton(onClick = onMoveDown, enabled = model.canMoveDown) {
                    Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "Move down")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(onClick = onTrigger) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Trigger effect")
                }
                IconButton(
                    onClick = onStop,
                    enabled = model.activeInstanceCount > 0,
                ) {
                    Icon(Icons.Rounded.Pause, contentDescription = "Stop effect")
                }
                IconButton(onClick = onArmDelete) {
                    Icon(Icons.Rounded.Delete, contentDescription = "Remove effect")
                }
            }
        }
    }
}
