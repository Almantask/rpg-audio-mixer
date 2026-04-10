package com.example.rpgaudiomixer.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import com.example.rpgaudiomixer.domain.trash.TrashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface TrashUiState {
    data object Loading : TrashUiState

    data class Success(
        val items: List<TrashItem>,
    ) : TrashUiState

    data class Error(val message: String) : TrashUiState
}

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val trashRepository: TrashRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<TrashUiState>(TrashUiState.Loading)
    val uiState: StateFlow<TrashUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trashRepository.purgeExpiredItems()
            trashRepository.observeDeletedItems()
                .catch { throwable ->
                    _uiState.value = TrashUiState.Error(
                        throwable.message ?: "Unable to load recent deletes.",
                    )
                }
                .collect { items ->
                    _uiState.value = TrashUiState.Success(items = items)
                }
        }
    }

    fun restoreItem(itemId: Long, itemType: TrashItemType) {
        viewModelScope.launch {
            trashRepository.restoreItem(itemId = itemId, itemType = itemType)
        }
    }

    fun permanentlyDeleteItem(itemId: Long, itemType: TrashItemType) {
        viewModelScope.launch {
            trashRepository.permanentlyDeleteItem(itemId = itemId, itemType = itemType)
        }
    }

    fun emptyVault() {
        viewModelScope.launch {
            trashRepository.emptyVault()
        }
    }
}
