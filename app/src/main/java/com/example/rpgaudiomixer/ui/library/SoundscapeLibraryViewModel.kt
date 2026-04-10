package com.example.rpgaudiomixer.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.soundscape.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SoundscapeLibraryUiState {
    data object Loading : SoundscapeLibraryUiState
    data class Success(
        val categories: List<SoundscapeCategory>,
        val isDemoDownloadVisible: Boolean,
        val isDownloadingDemoContent: Boolean,
    ) : SoundscapeLibraryUiState

    data class Error(val message: String) : SoundscapeLibraryUiState
}

sealed interface SoundscapeLibraryNavigation {
    data class OpenComposer(
        val categoryId: Long,
        val categoryName: String,
    ) : SoundscapeLibraryNavigation
}

@HiltViewModel
class SoundscapeLibraryViewModel @Inject constructor(
    private val repository: SoundscapeRepository,
) : ViewModel() {
    private val isDownloadingDemoContent = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val _navigationEvents = MutableSharedFlow<SoundscapeLibraryNavigation>(replay = 1)
    val navigationEvents = _navigationEvents

    val uiState: StateFlow<SoundscapeLibraryUiState> = combine(
        repository.observeCategories(),
        isDownloadingDemoContent,
        errorMessage,
    ) { categories, isDownloading, error ->
        error?.let { message ->
            SoundscapeLibraryUiState.Error(message)
        } ?: SoundscapeLibraryUiState.Success(
            categories = categories,
            isDemoDownloadVisible = categories.none { it.isDemoContent },
            isDownloadingDemoContent = isDownloading,
        )
    }
        .catch { throwable ->
            emit(
                SoundscapeLibraryUiState.Error(
                    throwable.message ?: "Unable to load soundscapes.",
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SoundscapeLibraryUiState.Loading,
        )

    fun createCategory(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return
        }

        viewModelScope.launch {
            runCatching { repository.createCategory(trimmedName) }
                .onSuccess { categoryId ->
                    _navigationEvents.emit(
                        SoundscapeLibraryNavigation.OpenComposer(
                            categoryId = categoryId,
                            categoryName = trimmedName,
                        ),
                    )
                }
                .onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "Unable to create category."
                }
        }
    }

    fun openComposer(categoryId: Long, categoryName: String) {
        viewModelScope.launch {
            _navigationEvents.emit(
                SoundscapeLibraryNavigation.OpenComposer(
                    categoryId = categoryId,
                    categoryName = categoryName,
                ),
            )
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            runCatching { repository.deleteCategory(categoryId) }
                .onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "Unable to delete category."
                }
        }
    }

    fun downloadDemoSoundscapes() {
        viewModelScope.launch {
            isDownloadingDemoContent.value = true
            runCatching { repository.installDemoSoundscapes() }
                .onFailure { throwable ->
                    errorMessage.value = throwable.message ?: "Unable to download demo soundscapes."
                }
            isDownloadingDemoContent.value = false
        }
    }
}
