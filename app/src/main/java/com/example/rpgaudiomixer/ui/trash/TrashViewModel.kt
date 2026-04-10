package com.example.rpgaudiomixer.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.TrashItem
import com.example.rpgaudiomixer.domain.model.TrashItemType
import com.example.rpgaudiomixer.domain.trash.TrashRepository
import com.example.rpgaudiomixer.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val trashRepository: TrashRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<TrashItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<TrashItem>>> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            runCatching {
                trashRepository.purgeExpired()
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to clean up expired trash."
            }
        }
        observeTrash()
    }

    fun restore(itemType: TrashItemType, id: Long) {
        viewModelScope.launch {
            runCatching {
                trashRepository.restore(itemType, id)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to restore item."
            }
        }
    }

    fun permanentlyDelete(itemType: TrashItemType, id: Long) {
        viewModelScope.launch {
            runCatching {
                trashRepository.permanentlyDelete(itemType, id)
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to permanently delete item."
            }
        }
    }

    fun emptyVault() {
        viewModelScope.launch {
            runCatching {
                trashRepository.emptyVault()
            }.onFailure { throwable ->
                _errorMessage.value = throwable.message ?: "Unable to empty the vault."
            }
        }
    }

    fun dismissError() {
        _errorMessage.value = null
    }

    private fun observeTrash() {
        viewModelScope.launch {
            trashRepository.observeDeletedItems()
                .catch { throwable ->
                    _uiState.value = UiState.Error(
                        throwable.message ?: "Unable to load deleted items.",
                    )
                }
                .collect { items ->
                    _uiState.value = UiState.Success(items)
                }
        }
    }
}
