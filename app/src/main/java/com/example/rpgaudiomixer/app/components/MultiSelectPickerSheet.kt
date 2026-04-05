package com.example.rpgaudiomixer.app.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.app.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MultiSelectPickerSheet(
    title: String,
    items: List<T>,
    selectedItems: Set<Long>,
    onDismiss: () -> Unit,
    onItemSelected: (Long) -> Unit,
    itemLabel: (T) -> String,
    itemSubtitle: ((T) -> String?)? = null,
    itemId: (T) -> Long
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = Typography.titleLarge,
                    color = Gold,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Gold)
                }
            }

            Divider(color = Gold.copy(alpha = 0.1f))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(items) { item ->
                    val id = itemId(item)
                    val isSelected = selectedItems.contains(id)
                    
                    ListItem(
                        headlineContent = {
                            Text(
                                text = itemLabel(item).uppercase(),
                                style = Typography.bodyLarge,
                                color = if (isSelected) Gold else Gold.copy(alpha = 0.5f)
                            )
                        },
                        supportingContent = itemSubtitle?.let { subtitleProvider ->
                            subtitleProvider(item)?.let { subtitle ->
                                {
                                    Text(
                                        text = subtitle,
                                        style = Typography.bodySmall,
                                        color = Gold.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        },
                        trailingContent = {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Selected", tint = Gold)
                            } else {
                                IconButton(onClick = { onItemSelected(id) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Gold)
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}
