package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rpgaudiomixer.app.theme.*
import com.example.rpgaudiomixer.app.ui.credits.TrashItem
import com.example.rpgaudiomixer.app.ui.credits.TrashViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    viewModel: TrashViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VAULT OF ECHOES", color = ArcanumGold, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = ArcanumGold)
                    }
                },
                actions = {
                    if (uiState.items.isNotEmpty()) {
                        TextButton(onClick = { viewModel.emptyVault() }) {
                            Text("EMPTY", color = ArcanumErrorRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ArcanumBlack)
            )
        },
        containerColor = ArcanumBlack
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ArcanumGold)
                }
            } else if (uiState.items.isEmpty()) {
                EmptyVaultState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.items) { item ->
                        TrashItemRow(
                            item = item,
                            onRestore = { viewModel.restore(item) },
                            onDelete = { viewModel.permanentlyDelete(item) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                        Text(
                            text = "Items will be permanently removed 7 days after deletion",
                            style = MaterialTheme.typography.labelSmall,
                            color = ArcanumOnSurface.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrashItemRow(
    item: TrashItem,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(item.deletedAt))

    Card(
        colors = CardDefaults.cardColors(containerColor = ArcanumCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.typeName.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumMutedGold
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = ArcanumOnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Deleted $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = ArcanumOnSurface.copy(alpha = 0.5f)
                )
            }

            IconButton(onClick = onRestore) {
                Icon(Icons.Default.Restore, contentDescription = "Restore", tint = ArcanumGold)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteForever, contentDescription = "Delete Permanently", tint = ArcanumErrorRed)
            }
        }
    }
}

@Composable
private fun EmptyVaultState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Inventory2,
            contentDescription = null,
            tint = ArcanumCard,
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The Vault is Empty",
            style = MaterialTheme.typography.headlineSmall,
            color = ArcanumOnSurface.copy(alpha = 0.5f)
        )
        Text(
            text = "Recent deletions will appear here for 7 days",
            style = MaterialTheme.typography.bodyMedium,
            color = ArcanumOnSurface.copy(alpha = 0.3f)
        )
    }
}
