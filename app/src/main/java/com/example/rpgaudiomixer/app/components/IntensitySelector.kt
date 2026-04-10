package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.IntensityLevel

@Composable
fun IntensitySelector(
    selectedLevel: IntensityLevel,
    onLevelSelected: (IntensityLevel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        IntensityLevel.entries.forEach { level ->
            FilterChip(
                selected = level == selectedLevel,
                onClick = { onLevelSelected(level) },
                label = {
                    Text(
                        text = level.label,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }
    }
}
