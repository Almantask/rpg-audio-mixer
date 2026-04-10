package com.example.rpgaudiomixer.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@Composable
fun IntensitySelector(
    selectedLevel: IntensityLevel,
    availableLevels: Set<IntensityLevel>,
    onLevelSelected: (IntensityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IntensityLevel.entries.forEach { level ->
            val isSelected = level == selectedLevel
            val isAvailable = level in availableLevels
            val textColor = when {
                isSelected -> MaterialTheme.colorScheme.primary
                isAvailable -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            }
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

            Text(
                text = level.displayName,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, borderColor, RoundedCornerShape(4.dp))
                    .clickable(enabled = isAvailable) { onLevelSelected(level) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}
