package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rpgaudiomixer.app.settings.SettingsSyncPolicy
import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import com.example.rpgaudiomixer.ui.trash.TrashUiState
import com.example.rpgaudiomixer.ui.trash.TrashViewModel

@Composable
fun ScenesScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "SCENES tab screen",
        body = "Your scene library will appear here.",
        modifier = modifier,
    )
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = "Audio Library screen",
        body = "Your soundscape and FX libraries will appear here.",
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    onOpenTrash: () -> Unit,
    modifier: Modifier = Modifier,
    currentTimeMillis: () -> Long = { System.currentTimeMillis() },
) {
    val uriHandler = LocalUriHandler.current
    val syncPolicy = remember { SettingsSyncPolicy() }
    var lastSyncedAtMillis by rememberSaveable { mutableLongStateOf(0L) }
    val effectiveLastSyncedAtMillis = lastSyncedAtMillis.takeIf { it > 0L }
    val isSyncEnabled = syncPolicy.isSyncAvailable(
        currentTimeMillis = currentTimeMillis(),
        lastSyncedAtMillis = effectiveLastSyncedAtMillis,
    )

    ScreenContainer(
        modifier = modifier,
    ) {
        Text(
            text = "Arcanum Audio",
            style = MaterialTheme.typography.displaySmall,
        )
        Text(
            text = "Behind the Screen",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text("Version 1.0")
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { lastSyncedAtMillis = currentTimeMillis() },
            enabled = isSyncEnabled,
        ) {
            Text("Sync Purchases & Free Tracks")
        }
        Text(
            text = if (isSyncEnabled) {
                "Available once per day."
            } else {
                "Sync is cooling down for 24 hours after use."
            },
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(onClick = onOpenTrash) {
            Text("Restore Recent Deletes")
        }
        Text("Credits", style = MaterialTheme.typography.titleMedium)
        Text("Design & Development — Almantask")
        Text("Made with ❤️ for GMs everywhere")
        Text("Links", style = MaterialTheme.typography.titleMedium)
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { uriHandler.openUri("https://github.com/Almantask/rpg-audio-mixer") },
        ) {
            Text("Documentation")
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { uriHandler.openUri("https://discord.gg") },
        ) {
            Text("Discord community")
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { uriHandler.openUri("mailto:support@arcanumaudio.example") },
        ) {
            Text("Contact / support email")
        }
    }
}

@Composable
fun TrashScreen(
    viewModel: TrashViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    currentTimeMillis: () -> Long = { System.currentTimeMillis() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showEmptyVaultConfirmation by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        when (val state = uiState) {
            TrashUiState.Loading -> Text("Loading recent deletes…")
            is TrashUiState.Error -> Text(state.message)
            is TrashUiState.Success -> {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "The Vault of Echoes",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = "Lost fragments of your journey. Recover them before the ethereal mists claim them forever.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    OutlinedButton(
                        onClick = { showEmptyVaultConfirmation = true },
                        enabled = state.items.isNotEmpty(),
                    ) {
                        Text("Empty Vault")
                    }

                    if (state.items.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Inventory2,
                                contentDescription = null,
                            )
                            Text("The vault is empty.")
                            Text("Items will be permanently removed 7 days after they were deleted.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(state.items, key = { item -> "${item.type}-${item.id}" }) { item ->
                                TrashItemCard(
                                    item = item,
                                    daysAgo = ((currentTimeMillis() - item.deletedAt).coerceAtLeast(0L) / MILLIS_PER_DAY).toInt(),
                                    onRestore = { viewModel.restoreItem(item.id, item.type) },
                                    onDelete = { viewModel.permanentlyDeleteItem(item.id, item.type) },
                                )
                            }
                            item {
                                Text("Items will be permanently removed 7 days after they were deleted")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showEmptyVaultConfirmation) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showEmptyVaultConfirmation = false },
            title = { Text("Empty Vault") },
            text = { Text("Permanently delete every item in Recent Deletes?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEmptyVaultConfirmation = false
                        viewModel.emptyVault()
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyVaultConfirmation = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
fun CampaignSessionsScreen(
    campaignName: String,
    modifier: Modifier = Modifier,
) {
    PlaceholderScreen(
        title = if (campaignName.isBlank()) {
            "Campaign Sessions"
        } else {
            "Sessions list for $campaignName"
        },
        body = "Campaign sessions will appear here.",
        modifier = modifier,
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    ScreenContainer(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun ScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        content()
    }
}

@Composable
private fun TrashItemCard(
    item: TrashItem,
    daysAgo: Int,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text(item.type.label, style = MaterialTheme.typography.labelLarge)
                Text("Deleted $daysAgo day${if (daysAgo == 1) "" else "s"} ago")
            }
            Row {
                IconButton(onClick = onRestore) {
                    Icon(Icons.Outlined.History, contentDescription = "Restore")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Delete permanently")
                }
            }
        }
    }
}

private val TrashItemType.label: String
    get() = when (this) {
        TrashItemType.CAMPAIGN -> "Campaign"
        TrashItemType.SESSION -> "Session"
        TrashItemType.SCENE -> "Scene"
        TrashItemType.SOUNDSCAPE -> "Soundscape"
        TrashItemType.FX -> "FX"
    }

private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
