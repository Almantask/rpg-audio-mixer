package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.domain.scene.Scene
import com.example.rpgaudiomixer.domain.session.Session
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SessionCard(
    session: Session,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ArcanumCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ArcanumBlack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = ArcanumMutedGold,
                    modifier = Modifier.size(32.dp)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = session.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = ArcanumGold,
                    maxLines = 1
                )
                Text(
                    text = formatDate(session.date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ArcanumOnSurface.copy(alpha = 0.7f)
                )
            }

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ArcanumGold)),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text("ENTER", color = ArcanumGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SceneCard(
    scene: Scene,
    onPlayClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = ArcanumCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scene.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = ArcanumGold
                    )
                    if (scene.description != null) {
                        Text(
                            text = scene.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ArcanumOnSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(ArcanumGold, RoundedCornerShape(24.dp))
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play Scene",
                        tint = ArcanumOnGold
                    )
                }
            }

            if (scene.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    scene.tags.take(3).forEach { tag ->
                        TagChip(tag)
                    }
                    if (scene.tags.size > 3) {
                        Text(
                            text = "+${scene.tags.size - 3}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArcanumOnSurface.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TagChip(tag: String) {
    Surface(
        color = ArcanumMutedGold.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ArcanumMutedGold.copy(alpha = 0.5f))
    ) {
        Text(
            text = tag.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = ArcanumGold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            letterSpacing = 1.sp
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
