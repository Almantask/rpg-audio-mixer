package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@Composable
fun IntensitySelector(
    selectedIntensity: IntensityLevel,
    availableIntensities: Set<IntensityLevel>,
    onIntensitySelected: (IntensityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.semantics { contentDescription = "IntensitySelector" },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IntensityLevel.entries.forEach { level ->
            val isSelected = selectedIntensity == level
            val isAvailable = availableIntensities.contains(level)
            val isEnabled = isAvailable

            IntensityButton(
                label = level.label,
                isSelected = isSelected,
                isEnabled = isEnabled,
                onClick = { if (isEnabled) onIntensitySelected(level) },
                modifier = Modifier.semantics { contentDescription = "Intensity_${level.label}" }
            )
        }
    }
}

@Composable
private fun IntensityButton(
    label: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected && isEnabled -> MaterialTheme.colorScheme.primary
        isEnabled -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
    }

    val textColor = when {
        isSelected && isEnabled -> MaterialTheme.colorScheme.onPrimary
        isEnabled -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }

    val borderColor = when {
        isSelected && isEnabled -> MaterialTheme.colorScheme.primary
        isEnabled -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
    }
}
