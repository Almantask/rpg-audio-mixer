package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.app.theme.*

@Composable
fun SceneCard(
    scene: Scene,
    onPlay: (Long) -> Unit,
    onClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        onClick = { onClick(scene.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scene.name.uppercase(),
                        style = Typography.titleLarge,
                        color = Gold,
                        maxLines = 1
                    )
                    if (scene.description != null) {
                        Text(
                            text = scene.description,
                            style = Typography.bodyMedium,
                            color = Gold.copy(alpha = 0.7f),
                            maxLines = 2
                        )
                    }
                }
                
                IconButton(
                    onClick = { onPlay(scene.id) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Gold,
                        contentColor = BlackBg
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play Scene")
                }
            }

            if (scene.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(scene.tags) { tag ->
                        TagChip(tag = tag)
                    }
                }
            }
        }
    }
}

