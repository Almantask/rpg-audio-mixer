package com.example.rpgaudiomixer.app.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.domain.model.IntensityLevel
import com.example.rpgaudiomixer.domain.model.SceneSoundscapeCategory

@Composable
fun SoundscapeCategoryCard(
    categoryState: SceneSoundscapeCategory,
    isPlaying: Boolean,
    currentTrackName: String?,
    onPlayPause: () -> Unit,
    onRandomize: () -> Unit,
    onIntensityChange: (IntensityLevel) -> Unit,
    onMixVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val borderBrush = if (isPlaying) {
        Brush.sweepGradient(
            listOf(Gold.copy(alpha = glowAlpha), CardSurface, Gold.copy(alpha = glowAlpha))
        )
    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isPlaying) 2.dp else 0.dp,
                brush = borderBrush,
                shape = Shapes.medium
            ),
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BlackBg),
                    contentAlignment = Alignment.Center
                ) {
                    categoryState.category.iconResId?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            tint = Gold,
                            modifier = Modifier.size(24.dp)
                        )
                    } ?: Icon(
                        Icons.Default.Landscape,
                        contentDescription = null,
                        tint = Gold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and Track
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = categoryState.category.name.uppercase(),
                        style = Typography.titleMedium,
                        color = Gold,
                        fontWeight = FontWeight.Bold
                    )
                    AnimatedContent(
                        targetState = currentTrackName,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "trackName"
                    ) { name ->
                        Text(
                            text = name?.uppercase() ?: "SILENT",
                            style = Typography.labelSmall,
                            color = if (name != null) Gold.copy(alpha = 0.7f) else Color.Gray,
                            maxLines = 1
                        )
                    }
                }

                val isEmptyCategory = categoryState.category.trackCounts.values.sum() == 0

                // Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onRandomize, enabled = !isEmptyCategory) {
                        Icon(
                            Icons.Default.Casino, 
                            contentDescription = "Random Track", 
                            tint = if (isEmptyCategory) Color.Gray.copy(alpha = 0.3f) else Gold
                        )
                    }
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isEmptyCategory) Color.Gray.copy(alpha = 0.3f) else Gold),
                        enabled = !isEmptyCategory
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = if (isEmptyCategory) Color.Gray.copy(alpha = 0.5f) else BlackBg
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mix Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MIX",
                    style = Typography.labelLarge,
                    color = Gold.copy(alpha = 0.5f),
                    modifier = Modifier.width(40.dp)
                )
                Slider(
                    value = categoryState.mixVolume,
                    onValueChange = onMixVolumeChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Gold,
                        activeTrackColor = Gold,
                        inactiveTrackColor = Gold.copy(alpha = 0.1f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Intensity Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "INTENSITY",
                    style = Typography.labelLarge,
                    color = Gold.copy(alpha = 0.5f)
                )
                
                ActiveSceneIntensitySelector(
                    selectedLevel = categoryState.intensityLevel,
                    trackCounts = categoryState.category.trackCounts,
                    onLevelSelected = onIntensityChange
                )
            }
        }
    }
}

@Composable
fun ActiveSceneIntensitySelector(
    selectedLevel: IntensityLevel,
    trackCounts: Map<IntensityLevel, Int>,
    onLevelSelected: (IntensityLevel) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(Shapes.small)
            .background(BlackBg)
            .padding(2.dp)
    ) {
        IntensityLevel.values().forEach { level ->
            val count = trackCounts[level] ?: 0
            val isEnabled = count > 0
            val isSelected = selectedLevel == level
            
            Surface(
                onClick = { if (isEnabled) onLevelSelected(level) else {} },
                color = if (isSelected) Gold else Color.Transparent,
                shape = Shapes.small,
                modifier = Modifier
                    .width(40.dp)
                    .height(32.dp),
                enabled = isEnabled
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = level.name,
                        color = when {
                            isSelected -> BlackBg
                            !isEnabled -> Color.Gray.copy(alpha = 0.3f)
                            else -> Gold.copy(alpha = 0.5f)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MasterAtmosphereSlider(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "MASTER ATMOSPHERE",
                style = Typography.labelLarge,
                color = Gold,
                fontWeight = FontWeight.ExtraBold
            )
            Icon(
                Icons.Default.VolumeUp,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(16.dp)
            )
        }
        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            colors = SliderDefaults.colors(
                thumbColor = Gold,
                activeTrackColor = Gold,
                inactiveTrackColor = Gold.copy(alpha = 0.1f)
            )
        )
    }
}
@Composable
fun BentoCategoryCard(
    category: com.example.rpgaudiomixer.domain.model.SoundscapeCategory,
    onEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onEdit(category.id) },
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = CardSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name.uppercase(),
                style = Typography.titleMedium,
                color = Gold,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
