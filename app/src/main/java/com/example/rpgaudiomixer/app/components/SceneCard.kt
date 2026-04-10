package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumSurfaceVariant
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun SceneCard(
    scene: Scene,
    soundscapeCount: Int,
    onOpen: (Scene) -> Unit,
    onPlay: (Scene) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArcanumSurfaceVariant),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOpen(scene) },
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = scene.name,
                    style = MaterialTheme.typography.titleLarge,
                )
                TagRow(tags = scene.tags)
                Text(
                    text = "$soundscapeCount soundscapes",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            IconButton(onClick = { onPlay(scene) }) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play ${scene.name}",
                    tint = ArcanumGold,
                )
            }
        }
    }
}
