package com.example.rpgaudiomixer.app.screens.trash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestoreFromTrash
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    onNavigateBack: () -> Unit,
    viewModel: TrashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var showEmptyVaultDialog by remember { mutableStateOf(false) }

    if (showEmptyVaultDialog) {
        AlertDialog(
            onDismissRequest = { showEmptyVaultDialog = false },
            title = { Text("Empty Vault?") },
            text = {
                Text("This will permanently destroy all items in the Vault of Echoes. This cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.emptyVault()
                        showEmptyVaultDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                    ),
                    modifier = Modifier.testTag("trashConfirmEmptyVault"),
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recent Deletes",
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        modifier = Modifier.testTag("trashScreen"),
    ) { innerPadding ->
        if (uiState.isEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.testTag("trashEmptyState"),
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoDelete,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Vault of Echoes",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All silent here. Nothing to restore.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .testTag("trashList"),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.deletedCampaigns, key = { "campaign_${it.id}" }) { campaign ->
                    TrashItemCard(
                        name = campaign.name,
                        type = "Campaign",
                        deletedAt = campaign.deletedAt,
                        onRestore = { viewModel.restoreCampaign(campaign.id) },
                        onPermanentlyDelete = { viewModel.permanentlyDeleteCampaign(campaign.id) },
                        modifier = Modifier.testTag("trashItem_${campaign.name}"),
                    )
                }

                items(uiState.deletedSessions, key = { "session_${it.id}" }) { session ->
                    TrashItemCard(
                        name = session.name,
                        type = "Session",
                        deletedAt = session.deletedAt,
                        onRestore = { viewModel.restoreSession(session.id) },
                        onPermanentlyDelete = { viewModel.permanentlyDeleteSession(session.id) },
                        modifier = Modifier.testTag("trashItem_${session.name}"),
                    )
                }

                items(uiState.deletedScenes, key = { "scene_${it.id}" }) { scene ->
                    TrashItemCard(
                        name = scene.name,
                        type = "Scene",
                        deletedAt = scene.deletedAt,
                        onRestore = { viewModel.restoreScene(scene.id) },
                        onPermanentlyDelete = { viewModel.permanentlyDeleteScene(scene.id) },
                        modifier = Modifier.testTag("trashItem_${scene.name}"),
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showEmptyVaultDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trashEmptyVaultButton"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error,
                        ),
                    ) {
                        Text("Empty Vault")
                    }
                }
            }
        }
    }
}

@Composable
private fun TrashItemCard(
    name: String,
    type: String,
    deletedAt: Long?,
    onRestore: () -> Unit,
    onPermanentlyDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val daysAgo = deletedAt?.let {
        val diff = System.currentTimeMillis() - it
        (diff / (1000L * 60 * 60 * 24)).toInt()
    }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = type,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (daysAgo != null) {
                        Text(
                            text = when (daysAgo) {
                                0 -> "Deleted today"
                                1 -> "Deleted 1 day ago"
                                else -> "Deleted $daysAgo days ago"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row {
                    IconButton(
                        onClick = onRestore,
                        modifier = Modifier.testTag("restoreButton_$name"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestoreFromTrash,
                            contentDescription = "Restore",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(
                        onClick = onPermanentlyDelete,
                        modifier = Modifier.testTag("deleteButton_$name"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}
