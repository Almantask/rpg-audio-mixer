package com.example.rpgaudiomixer.ui.trash

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.app.components.ErrorDialog
import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import com.example.rpgaudiomixer.ui.UiState

@Composable
fun TrashRoute(
    modifier: Modifier = Modifier,
    viewModel: TrashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    TrashScreen(
        uiState = uiState,
        errorMessage = errorMessage,
        onRestore = viewModel::restore,
        onPermanentlyDelete = viewModel::permanentlyDelete,
        onEmptyVault = viewModel::emptyVault,
        onDismissError = viewModel::dismissError,
        modifier = modifier,
    )
}

@Composable
private fun TrashScreen(
    uiState: UiState<List<TrashItem>>,
    errorMessage: String?,
    onRestore: (TrashItemType, Long) -> Unit,
    onPermanentlyDelete: (TrashItemType, Long) -> Unit,
    onEmptyVault: () -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showEmptyVaultDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            is UiState.Error -> {
                EmptyStateView(
                    modifier = Modifier.align(Alignment.Center),
                    icon = Icons.Rounded.Inventory2,
                    title = "Unable to load the vault",
                    body = uiState.message,
                    actionLabel = "Dismiss",
                    onAction = onDismissError,
                )
            }

            is UiState.Success -> {
                TrashContent(
                    items = uiState.data,
                    onRestore = onRestore,
                    onPermanentlyDelete = onPermanentlyDelete,
                    onEmptyVault = { showEmptyVaultDialog = true },
                )
            }
        }

        if (showEmptyVaultDialog) {
            AlertDialog(
                onDismissRequest = { showEmptyVaultDialog = false },
                title = { Text("Empty Vault") },
                text = {
                    Text("Permanently delete every item currently stored in the Vault of Echoes?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showEmptyVaultDialog = false
                            onEmptyVault()
                        },
                    ) {
                        Text("Empty Vault")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEmptyVaultDialog = false }) {
                        Text("Cancel")
                    }
                },
            )
        }
    }

    ErrorDialog(
        message = errorMessage,
        onDismiss = onDismissError,
    )
}

@Composable
private fun TrashContent(
    items: List<TrashItem>,
    onRestore: (TrashItemType, Long) -> Unit,
    onPermanentlyDelete: (TrashItemType, Long) -> Unit,
    onEmptyVault: () -> Unit,
) {
    if (items.isEmpty()) {
        EmptyStateView(
            modifier = Modifier.fillMaxSize(),
            icon = Icons.Rounded.Inventory2,
            title = "The vault is empty",
            body = "Items will be permanently removed 7 days after they were deleted.",
            actionLabel = "Nothing to Restore",
            onAction = {},
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "The Vault of Echoes",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Lost fragments of your journey. Recover them before the ethereal mists claim them forever.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onEmptyVault,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteForever,
                        contentDescription = null,
                    )
                    Text(
                        text = "EMPTY VAULT",
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
        }
        items(items, key = { item -> "${item.type}-${item.id}" }) { item ->
            TrashItemCard(
                item = item,
                onRestore = { onRestore(item.type, item.id) },
                onDelete = { onPermanentlyDelete(item.type, item.id) },
            )
        }
        item {
            Text(
                text = "Items will be permanently removed 7 days after deletion.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TrashItemCard(
    item: TrashItem,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = item.type.name.replace("_", " "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                item.subtitle?.takeIf { subtitle -> subtitle.isNotBlank() }?.let { subtitle ->
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = DateUtils.getRelativeTimeSpanString(item.deletedAt).toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = onRestore) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = "Restore item",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete permanently",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
