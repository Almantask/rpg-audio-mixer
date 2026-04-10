package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.*

object TagSystem {
    val predefinedTags = listOf(
        "Tavern", "Forest", "Combat", "City", "Dungeon", 
        "Ocean", "Mountain", "Cave", "Desert", "Magic"
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagSelector(
    selectedTags: List<String>,
    onTagToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "SUGGESTED TAGS",
            style = MaterialTheme.typography.labelSmall,
            color = ArcanumOnSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TagSystem.predefinedTags.forEach { tag ->
                val isSelected = selectedTags.contains(tag)
                FilterChip(
                    selected = isSelected,
                    onClick = { onTagToggle(tag) },
                    label = { Text(tag) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = ArcanumBlack,
                        labelColor = ArcanumOnSurface.copy(alpha = 0.6f),
                        selectedContainerColor = ArcanumGold.copy(alpha = 0.2f),
                        selectedLabelColor = ArcanumGold
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = ArcanumOnSurface.copy(alpha = 0.1f),
                        selectedBorderColor = ArcanumGold
                    )
                )
            }
        }
    }
}
