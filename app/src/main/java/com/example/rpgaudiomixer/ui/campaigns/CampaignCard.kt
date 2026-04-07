package com.example.rpgaudiomixer.ui.campaigns

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rpgaudiomixer.domain.model.Campaign
import java.text.SimpleDateFormat
import java.util.*

/**
 * Campaign card component showing cover art, name, last played date, and actions.
 *
 * Features:
 * - Cover art display (or placeholder)
 * - Campaign name
 * - Last played timestamp
 * - RESUME button
 * - Swipe-to-delete action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampaignCard(
    campaign: Campaign,
    onClick: () -> Unit,
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
        modifier = modifier.testTag("CampaignCard_${campaign.id}")
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
                        text = campaign.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("CampaignCard_${campaign.id}_Name")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatLastPlayed(campaign.lastPlayedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("CampaignCard_${campaign.id}_LastPlayed")
                    )
                }

                FilledTonalButton(
                    onClick = onClick,
                    modifier = Modifier.testTag("CampaignCard_${campaign.id}_ResumeButton")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("RESUME")
                }
            }
        }
    }
}

private fun formatLastPlayed(timestamp: Long): String {
    if (timestamp == 0L) return "Never played"

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return "Last played: ${dateFormat.format(Date(timestamp))}"
}
