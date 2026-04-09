package com.example.rpgaudiomixer.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.trash.TrashItem
import com.example.rpgaudiomixer.domain.trash.TrashVaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

object TrashTestTags {
    const val EMPTY_STATE = "Trash_Empty_State"
    const val EMPTY_VAULT = "Trash_Empty_Vault"
    const val CONFIRM_DIALOG = "Trash_Confirm_Dialog"

    fun card(name: String): String = "Trash_Card_${name.asTagSuffix()}"
    fun restoreButton(name: String): String = "Trash_Restore_${name.asTagSuffix()}"
    fun deleteButton(name: String): String = "Trash_Delete_${name.asTagSuffix()}"
}

data class TrashUiState(
    val items: List<TrashItem> = emptyList(),
    val showEmptyVaultConfirmation: Boolean = false,
)

@Composable
fun TrashScreen(
    viewModel: TrashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    TrashScreen(
        uiState = uiState,
        onRestoreItem = viewModel::restoreItem,
        onDeleteItem = viewModel::deleteItem,
        onEmptyVault = viewModel::requestEmptyVault,
        onDismissEmptyVaultConfirmation = viewModel::dismissEmptyVaultConfirmation,
        onConfirmEmptyVault = viewModel::confirmEmptyVault,
    )
}

@Composable
fun TrashScreen(
    uiState: TrashUiState,
    onRestoreItem: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    onEmptyVault: () -> Unit,
    onDismissEmptyVaultConfirmation: () -> Unit,
    onConfirmEmptyVault: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag(MainScreenTestTags.TRASH),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Recent Deletes",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "The Vault of Echoes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Lost fragments of your journey. Recover them before the ethereal mists claim them forever.",
            style = MaterialTheme.typography.bodyMedium,
        )
        OutlinedButton(
            modifier = Modifier
                .align(Alignment.End)
                .testTag(TrashTestTags.EMPTY_VAULT),
            onClick = onEmptyVault,
        ) {
            Icon(Icons.Default.DeleteForever, contentDescription = null)
            Text("Empty Vault")
        }

        if (uiState.items.isEmpty()) {
            EmptyTrashState()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.items, key = TrashItem::key) { item ->
                    TrashItemCard(
                        item = item,
                        onRestore = { onRestoreItem(item.key) },
                        onDelete = { onDeleteItem(item.key) },
                    )
                }
                item {
                    FooterMessage()
                }
            }
        }

        if (uiState.showEmptyVaultConfirmation) {
            AlertDialog(
                modifier = Modifier.testTag(TrashTestTags.CONFIRM_DIALOG),
                onDismissRequest = onDismissEmptyVaultConfirmation,
                title = { Text("Empty Vault") },
                text = { Text("This permanently destroys every item in Recent Deletes.") },
                confirmButton = {
                    TextButton(onClick = onConfirmEmptyVault) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = onDismissEmptyVaultConfirmation) { Text("Cancel") }
                },
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
        modifier = Modifier
            .fillMaxWidth()
            .testTag(TrashTestTags.card(item.name)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Inventory2, contentDescription = null)
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = item.type.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Deleted ${item.deletedAtMillis.toDaysAgoLabel()}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            IconButton(
                modifier = Modifier.testTag(TrashTestTags.restoreButton(item.name)),
                onClick = onRestore,
            ) {
                Icon(Icons.Default.History, contentDescription = "Restore ${item.name}")
            }
            IconButton(
                modifier = Modifier.testTag(TrashTestTags.deleteButton(item.name)),
                onClick = onDelete,
            ) {
                Icon(Icons.Default.DeleteForever, contentDescription = "Delete ${item.name}")
            }
        }
    }
}

@Composable
private fun EmptyTrashState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(top = 32.dp)
            .testTag(TrashTestTags.EMPTY_STATE),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
        }
        Text(
            text = "The vault is empty.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        FooterMessage()
    }
}

@Composable
private fun FooterMessage() {
    Text(
        text = "Items will be permanently removed 7 days after they were deleted",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val trashVaultRepository: TrashVaultRepository,
) : ViewModel() {
    private val showEmptyVaultConfirmation = MutableStateFlow(false)

    val uiState: StateFlow<TrashUiState> = combine(
        trashVaultRepository.observeItems(),
        showEmptyVaultConfirmation,
    ) { items, showConfirmation ->
            TrashUiState(
                items = items.sortedByDescending(TrashItem::deletedAtMillis),
                showEmptyVaultConfirmation = showConfirmation,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TrashUiState(),
        )

    init {
        viewModelScope.launch {
            trashVaultRepository.purgeExpired()
        }
    }

    fun restoreItem(itemKey: String) {
        viewModelScope.launch {
            trashVaultRepository.restore(itemKey)
        }
    }

    fun deleteItem(itemKey: String) {
        viewModelScope.launch {
            trashVaultRepository.permanentlyDelete(itemKey)
        }
    }

    fun requestEmptyVault() {
        showEmptyVaultConfirmation.value = true
    }

    fun dismissEmptyVaultConfirmation() {
        showEmptyVaultConfirmation.value = false
    }

    fun confirmEmptyVault() {
        viewModelScope.launch {
            trashVaultRepository.emptyVault()
            showEmptyVaultConfirmation.value = false
        }
    }
}

private fun Long.toDaysAgoLabel(nowMillis: Long = System.currentTimeMillis()): String {
    val days = TimeUnit.MILLISECONDS.toDays((nowMillis - this).coerceAtLeast(0L))
    return "$days days ago"
}

private fun String.asTagSuffix(): String = lowercase(Locale.US)
    .replace(Regex("[^a-z0-9]+"), "_")
    .trim('_')
