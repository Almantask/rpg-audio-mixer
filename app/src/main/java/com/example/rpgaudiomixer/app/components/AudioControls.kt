package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.domain.library.IntensityLevel

@Composable
fun IntensitySelector(
    selectedLevel: IntensityLevel,
    onLevelSelected: (IntensityLevel) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(ArcanumBlack)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IntensityLevel.values().forEach { level ->
            val isSelected = level == selectedLevel
            Box(
                modifier = Modifier
                    .size(width = 48.dp, height = 32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) ArcanumGold.copy(alpha = if (enabled) 1f else 0.2f) else Color.Transparent)
                    .clickable(enabled = enabled) { onLevelSelected(level) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) {
                        if (enabled) ArcanumOnGold else ArcanumGold.copy(alpha = 0.5f)
                    } else {
                        if (enabled) ArcanumGold else ArcanumGold.copy(alpha = 0.2f)
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MixSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MIX",
            style = MaterialTheme.typography.labelSmall,
            color = ArcanumMutedGold,
            modifier = Modifier.width(32.dp)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = ArcanumGold,
                activeTrackColor = ArcanumGold,
                inactiveTrackColor = ArcanumOnSurface.copy(alpha = 0.1f)
            )
        )
        Text(
            text = "${(value * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = ArcanumGold,
            modifier = Modifier.width(40.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
fun MasterSlider(
    title: String,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ArcanumCard.copy(alpha = 0.5f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = ArcanumGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumGold,
                    letterSpacing = 1.sp
                )
            }
            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                colors = SliderDefaults.colors(
                    thumbColor = ArcanumGold,
                    activeTrackColor = ArcanumGold,
                    inactiveTrackColor = ArcanumOnSurface.copy(alpha = 0.1f)
                )
            )
        }
    }
}
