package com.example.rpgaudiomixer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.rpgaudiomixer.app.theme.ArcanumBorder
import com.example.rpgaudiomixer.app.theme.ArcanumCardSurface
import com.example.rpgaudiomixer.app.theme.ArcanumGold
import com.example.rpgaudiomixer.app.theme.ArcanumGoldDark
import com.example.rpgaudiomixer.app.theme.ArcanumGrayLight
import com.example.rpgaudiomixer.domain.model.Scene

@Composable
fun SceneCard(
    scene: Scene,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(ArcanumCardSurface)
            .border(1.dp, ArcanumBorder, RoundedCornerShape(12.dp))
            .then(
                Modifier.padding(0.dp)
            ),
    ) {
        // Cover art with gradient overlay
        if (scene.coverArtUri != null) {
            AsyncImage(
                model = scene.coverArtUri,
                contentDescription = scene.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    ),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(ArcanumCardSurface, Color.Black)
                        )
                    ),
            )
        }

        // Tags row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            scene.tags.take(2).forEach { tag ->
                TagChip(text = tag)
            }
        }

        // Play button top-right
        IconButton(
            onClick = onPlay,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape),
        ) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play",
                tint = ArcanumGold,
                modifier = Modifier.size(24.dp),
            )
        }

        // Content at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(12.dp),
        ) {
            Text(
                text = scene.name,
                style = MaterialTheme.typography.headlineLarge,
                color = ArcanumGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = scene.description,
                style = MaterialTheme.typography.bodySmall,
                color = ArcanumGrayLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "▶ Played ${scene.playCount}x",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumGrayLight,
                )
            }
        }
    }
}

@Composable
fun TagChip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.dp, ArcanumGoldDark, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = ArcanumGold,
        )
    }
}
