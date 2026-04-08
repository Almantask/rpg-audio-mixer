package com.example.rpgaudiomixer.ui.soundscapes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Soundscape category card showing name, track counts per intensity level, and edit action.
 *
 * Features:
 * - Category name
 * - Track count breakdown (I: X · II: Y · III: Z)
 * - Edit icon button
 * - Swipe-to-delete action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoundscapeCategoryCard(
    categoryWithCounts: CategoryWithTrackCounts,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = modifier.testTag("SoundscapeCategory_${categoryWithCounts.category.id}")
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = categoryWithCounts.category.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("SoundscapeCategory_${categoryWithCounts.category.id}_Name")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatTrackCounts(
                            categoryWithCounts.levelICounts,
                            categoryWithCounts.levelIICounts,
                            categoryWithCounts.levelIIICounts
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("SoundscapeCategory_${categoryWithCounts.category.id}_Counts")
                    )
                }

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.testTag("SoundscapeCategory_${categoryWithCounts.category.id}_EditButton")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Category",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Format track counts as "I: X · II: Y · III: Z"
 */
private fun formatTrackCounts(levelI: Int, levelII: Int, levelIII: Int): String {
    return if (levelI == 0 && levelII == 0 && levelIII == 0) {
        "No tracks"
    } else {
        "I: $levelI · II: $levelII · III: $levelIII"
    }
}
