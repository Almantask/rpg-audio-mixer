package com.example.rpgaudiomixer.ui.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun SceneCard(
    scene: Scene,
    modifier: Modifier = Modifier,
    playButtonTag: String? = null,
    onOpenScene: (Scene) -> Unit,
    onPlayScene: (Scene) -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenScene(scene) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = scene.name,
                    color = ArcanumGold,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (scene.tags.isNotEmpty()) {
                    TagRow(tags = scene.tags)
                }
                if (scene.soundscapeCategoryNames.isNotEmpty()) {
                    TagRow(tags = scene.soundscapeCategoryNames)
                }
                Text("${scene.soundscapeCategoryNames.size} soundscapes")
            }
            IconButton(
                modifier = if (playButtonTag == null) Modifier else Modifier.then(Modifier.testTag(playButtonTag)),
                onClick = { onPlayScene(scene) },
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play ${scene.name}")
            }
        }
    }
}

@Composable
fun TagRow(
    tags: List<String>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tags.forEach { tag ->
            AssistChip(
                onClick = {},
                label = { Text(tag) },
            )
        }
    }
}
