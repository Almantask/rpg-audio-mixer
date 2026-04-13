package com.example.rpgaudiomixer.app.screens.trash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.ArcanumEmptyState
import com.example.rpgaudiomixer.app.components.ArcanumTopBar
import com.example.rpgaudiomixer.app.domain.model.TrashItem
import java.util.concurrent.TimeUnit

@Suppress("kotlin:S6615")
@Composable
fun TrashScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEmptyVaultDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("trashScreen"),
        topBar = {
            ArcanumTopBar(
                title = "Vault of Echoes",
                onGearClick = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showEmptyVaultDialog = true }) {
                        Text("Empty Vault", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is TrashUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TrashUiState.Error -> {
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is TrashUiState.Success -> {
                if (state.items.isEmpty()) {
                    ArcanumEmptyState(
                        icon = Icons.Default.DeleteForever,
                        title = "Vault is empty",
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items, key = { "${it.type}_${it.id}" }) { item ->
                            TrashItemCard(
                                item = item,
                                onRestore = { viewModel.restore(item) },
                                onDelete = { viewModel.hardDelete(item) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEmptyVaultDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyVaultDialog = false },
            title = { Text("Empty Vault?") },
            text = {
                Text(
                    "This will permanently delete all items in the Vault of Echoes. " +
                        "This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.emptyVault()
                        showEmptyVaultDialog = false
                    }
                ) {
                    Text("Empty Vault")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyVaultDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun TrashItemCard(
    item: TrashItem,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("trashItem_${item.name}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = item.type.name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = daysAgo(item.deletedAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onRestore,
                    modifier = Modifier.testTag("restoreButton_${item.name}")
                ) {
                    Text("Restore")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    modifier = Modifier.testTag("deleteButton_${item.name}")
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

private fun daysAgo(timestamp: Long): String {
    val days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - timestamp)
    return if (days == 0L) "Today" else "$days day${if (days == 1L) "" else "s"} ago"
}
