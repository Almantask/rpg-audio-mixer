package com.example.rpgaudiomixer.app.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.domain.model.FXTrack

@Composable
fun FxButton(
    track: FXTrack,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fxGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val borderBrush = if (isPlaying) {
        Brush.radialGradient(
            colors = listOf(Gold.copy(alpha = glowAlpha), Color.Transparent)
        )

    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(Shapes.medium)
            .background(if (isPlaying) Gold.copy(alpha = 0.1f) else CardSurface)
            .border(
                width = if (isPlaying) 2.dp else 1.dp,
                color = if (isPlaying) Gold else Gold.copy(alpha = 0.2f),
                shape = Shapes.medium
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint = if (isPlaying) Gold else Gold.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(24.dp)
                    .then(if (isPlaying) Modifier.size(24.dp * scale) else Modifier)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = track.name.uppercase(),
                style = Typography.labelSmall,
                color = if (isPlaying) Gold else Gold.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
