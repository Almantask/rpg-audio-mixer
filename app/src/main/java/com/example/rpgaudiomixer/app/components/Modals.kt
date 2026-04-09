package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rpgaudiomixer.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> GenericMultiSelectPickerSheet(
    title: String,
    items: List<T>,
    alreadySelectedIds: Set<Long>,
    itemIdSelector: (T) -> Long,
    itemLabelSelector: (T) -> String,
    itemSecondaryLabelSelector: (T) -> String = { "" },
    onDismiss: () -> Unit,
    onItemSelected: (Long) -> Unit,
    emptyMessage: String = "No items available."
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ArcanumCard,
        contentColor = ArcanumOnSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = ArcanumGold,
                modifier = Modifier.padding(16.dp),
                letterSpacing = 1.sp
            )

            if (items.isEmpty()) {
                Text(
                    text = emptyMessage,
                    modifier = Modifier.padding(16.dp),
                    color = ArcanumOnSurface.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn {
                    items(items) { item ->
                        val id = itemIdSelector(item)
                        val isSelected = alreadySelectedIds.contains(id)
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = { Text(itemLabelSelector(item), color = if (isSelected) ArcanumMutedGold else ArcanumGold) },
                            supportingContent = { 
                                val secondary = itemSecondaryLabelSelector(item)
                                if (secondary.isNotEmpty()) {
                                    Text(secondary, color = ArcanumOnSurface.copy(alpha = 0.4f)) 
                                }
                            },
                            trailingContent = {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Added", tint = ArcanumMutedGold)
                                } else {
                                    IconButton(onClick = { onItemSelected(id) }) {
                                        Icon(Icons.Default.Add, contentDescription = "Add", tint = ArcanumGold)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
