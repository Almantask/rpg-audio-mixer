package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@Composable
fun IntensitySelector(
    selectedIntensity: IntensityLevel,
    onIntensitySelected: (IntensityLevel) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IntensityLevel.entries.forEach { level ->
            val isSelected = selectedIntensity == level
            val backgroundColor = when {
                !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            val textColor = when {
                !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Text(
                text = level.label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable(enabled = enabled) { onIntensitySelected(level) }
                    .padding(8.dp)
                    .semantics {
                        contentDescription = "Intensity Level ${level.label}${if (isSelected) ", selected" else ""}${if (!enabled) ", disabled" else ""}"
                    }
                    .testTag("IntensityButton_${level.label}"),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
