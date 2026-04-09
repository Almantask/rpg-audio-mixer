package com.example.rpgaudiomixer.ui.trash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.app.components.EmptyStateView
import com.example.rpgaudiomixer.domain.trash.DeletedItem
import com.example.rpgaudiomixer.domain.trash.DeletedItemType
import com.example.rpgaudiomixer.domain.trash.TrashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class TrashItemUiState(
    val id: Long,
    val name: String,
    val type: DeletedItemType,
    val typeLabel: String,
    val deletedAgoLabel: String,
)

data class TrashUiState(
    val isLoading: Boolean = true,
    val items: List<TrashItemUiState> = emptyList(),
    val errorMessage: String? = null,
)

@Composable
fun TrashRoute(
    modifier: Modifier = Modifier,
    viewModel: TrashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TrashScreen(
        uiState = uiState,
        onRestoreItem = viewModel::restoreItem,
        onPermanentlyDeleteItem = viewModel::permanentlyDeleteItem,
        onEmptyVault = viewModel::emptyVault,
        modifier = modifier,
    )
}

@Composable
fun TrashScreen(
    uiState: TrashUiState,
    onRestoreItem: (Long, DeletedItemType) -> Unit,
    onPermanentlyDeleteItem: (Long, DeletedItemType) -> Unit,
    onEmptyVault: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showEmptyVaultConfirmation by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            uiState.items.isEmpty() -> EmptyStateView(
                modifier = Modifier.align(Alignment.Center),
                illustration = Icons.Default.Inventory2,
                title = "The Vault of Echoes is empty",
                actionLabel = "Items expire after 7 days",
                onActionClick = {},
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showEmptyVaultConfirmation = true },
                    ) {
                        Icon(imageVector = Icons.Default.DeleteForever, contentDescription = null)
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Empty Vault",
                        )
                    }
                }
                items(items = uiState.items, key = TrashItemUiState::id) { item ->
                    TrashItemCard(
                        item = item,
                        onRestoreItem = onRestoreItem,
                        onPermanentlyDeleteItem = onPermanentlyDeleteItem,
                    )
                }
                item {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp),
                        text = "Items will be permanently removed 7 days after they were deleted",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    if (showEmptyVaultConfirmation) {
        AlertDialog(
            onDismissRequest = { showEmptyVaultConfirmation = false },
            title = { Text(text = "Empty Vault") },
            text = { Text(text = "Permanently delete every item in Recent Deletes?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEmptyVault()
                        showEmptyVaultConfirmation = false
                    },
                ) {
                    Text(text = "Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmptyVaultConfirmation = false }) {
                    Text(text = "Cancel")
                }
            },
        )
    }
}

@Composable
private fun TrashItemCard(
    item: TrashItemUiState,
    onRestoreItem: (Long, DeletedItemType) -> Unit,
    onPermanentlyDeleteItem: (Long, DeletedItemType) -> Unit,
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = item.typeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = item.deletedAgoLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            IconButton(onClick = { onRestoreItem(item.id, item.type) }) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Restore",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            IconButton(onClick = { onPermanentlyDeleteItem(item.id, item.type) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val trashRepository: TrashRepository,
) : ViewModel() {
    private var currentTimeProvider: () -> Long = System::currentTimeMillis
    private var mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    internal constructor(
        trashRepository: TrashRepository,
        currentTimeProvider: () -> Long,
        mainDispatcher: CoroutineDispatcher,
    ) : this(trashRepository) {
        this.currentTimeProvider = currentTimeProvider
        this.mainDispatcher = mainDispatcher
    }

    private val _uiState = MutableStateFlow(TrashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(mainDispatcher) {
            trashRepository.purgeItemsDeletedBefore(currentTimeProvider() - SEVEN_DAYS_IN_MILLIS)
            trashRepository.observeDeletedItems()
                .catch { throwable ->
                    _uiState.value = TrashUiState(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Unable to load deleted items.",
                    )
                }
                .collect { items ->
                    _uiState.value = TrashUiState(
                        isLoading = false,
                        items = items.sortedByDescending(DeletedItem::deletedAt).map(::toUiState),
                    )
                }
        }
    }

    fun restoreItem(itemId: Long, type: DeletedItemType) {
        viewModelScope.launch(mainDispatcher) {
            trashRepository.restoreItem(itemId = itemId, type = type)
        }
    }

    fun permanentlyDeleteItem(itemId: Long, type: DeletedItemType) {
        viewModelScope.launch(mainDispatcher) {
            trashRepository.permanentlyDeleteItem(itemId = itemId, type = type)
        }
    }

    fun emptyVault() {
        viewModelScope.launch(mainDispatcher) {
            trashRepository.emptyVault()
        }
    }

    private fun toUiState(item: DeletedItem): TrashItemUiState {
        val daysSinceDeletion = ((currentTimeProvider() - item.deletedAt).coerceAtLeast(0L) / ONE_DAY_IN_MILLIS).toInt()
        val deletedAgoLabel = when (daysSinceDeletion) {
            0 -> "Deleted today"
            1 -> "Deleted 1 day ago"
            else -> "Deleted $daysSinceDeletion days ago"
        }
        return TrashItemUiState(
            id = item.id,
            name = item.name,
            type = item.type,
            typeLabel = when (item.type) {
                DeletedItemType.CAMPAIGN -> "Campaign"
                DeletedItemType.SESSION -> "Session"
                DeletedItemType.SCENE -> "Scene"
                DeletedItemType.SOUNDSCAPE -> "Soundscape"
                DeletedItemType.FX -> "FX"
            },
            deletedAgoLabel = deletedAgoLabel,
        )
    }

    private companion object {
        const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
        const val SEVEN_DAYS_IN_MILLIS = 7 * ONE_DAY_IN_MILLIS
    }
}
