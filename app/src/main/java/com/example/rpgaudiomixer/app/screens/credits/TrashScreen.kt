package com.example.rpgaudiomixer.app.screens.credits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.theme.BlackBg
import com.example.rpgaudiomixer.app.theme.Gold
import com.example.rpgaudiomixer.app.theme.Typography
import java.util.concurrent.TimeUnit

@Composable
fun TrashScreen(
    onBack: () -> Unit,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val items by viewModel.items.collectAsState()

    Scaffold(
        topBar = {
            ArcanumTopBar(
                title = "Vault of Echoes",
                showBackArrow = true,
                onBack = onBack
            )
        },
        containerColor = BlackBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "The Vault of Echoes",
                        style = Typography.displaySmall.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        ),
                        color = Gold
                    )
                    Text(
                        text = "Lost fragments of your journey. Recover them before the ethereal mists claim them forever.",
                        style = Typography.bodyMedium,
                        color = Gold.copy(alpha = 0.6f)
                    )
                }
                
                if (items.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.emptyVault() },
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Empty Vault",
                            tint = Gold.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Gold.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your vault is empty.",
                            style = Typography.bodyLarge,
                            color = Gold.copy(alpha = 0.4f)
                        )
                        Text(
                            text = "Items will be permanently removed 7 days after they were deleted",
                            style = Typography.labelSmall,
                            color = Gold.copy(alpha = 0.3f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(items) { item ->
                        TrashItemCard(
                            item = item,
                            onRestore = { viewModel.restore(item) },
                            onDelete = { viewModel.permanentDelete(item) }
                        )
                    }
                    
                    item {
                        Text(
                            text = "Items will be permanently removed 7 days after they were deleted",
                            style = Typography.labelSmall,
                            color = Gold.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrashItemCard(
    item: DeletedItem,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            val icon = when (item) {
                is DeletedItem.CampaignItem, is DeletedItem.SessionItem -> Icons.Default.AutoStories
                is DeletedItem.FXItem -> Icons.Default.Description
                else -> Icons.Default.Inventory2
            }
            
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gold.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Gold)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Metadata
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.name, style = Typography.titleMedium, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Gold.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.type,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = Typography.labelSmall.copy(fontSize = 10.sp),
                            color = Gold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatTimeAgo(item.deletedAt),
                        style = Typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            
            // Actions
            IconButton(onClick = onRestore) {
                Icon(imageVector = Icons.Default.History, contentDescription = "Restore", tint = Gold)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Permanently", tint = Color(0xFFFFB4AB))
            }
        }
    }
}

fun formatTimeAgo(timeMs: Long): String {
    val diff = System.currentTimeMillis() - timeMs
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    
    return when {
        days > 0 -> "Deleted $days days ago"
        hours > 0 -> "Deleted $hours hours ago"
        minutes > 0 -> "Deleted $minutes min ago"
        else -> "Deleted just now"
    }
}
