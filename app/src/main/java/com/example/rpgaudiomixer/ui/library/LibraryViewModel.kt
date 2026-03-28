package com.example.rpgaudiomixer.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.FxEffect
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.storage.FxRepository
import com.example.rpgaudiomixer.domain.storage.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val soundscapeCategories: List<SoundscapeCategory> = emptyList(),
    val fxEffects: List<FxEffect> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository,
    private val fxRepository: FxRepository,
) : ViewModel() {

    val uiState: StateFlow<LibraryUiState> = combine(
        soundscapeRepository.getAllCategories(),
        fxRepository.getAllEffects(),
    ) { categories, fx ->
        LibraryUiState(
            soundscapeCategories = categories.sortedBy { it.name },
            fxEffects = fx.sortedBy { it.name },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState(),
    )

    fun importFxEffect(name: String, filePath: String, tags: List<String>) {
        viewModelScope.launch {
            fxRepository.insert(FxEffect(name = name, trackFilePath = filePath, tags = tags))
        }
    }

    fun deleteEffect(effect: FxEffect) {
        viewModelScope.launch { fxRepository.delete(effect) }
    }

    fun addSoundscapeCategory(name: String, parentCategory: String) {
        viewModelScope.launch {
            soundscapeRepository.insertCategory(
                SoundscapeCategory(name = name, parentCategory = parentCategory),
            )
        }
    }

    fun deleteCategory(category: SoundscapeCategory) {
        viewModelScope.launch { soundscapeRepository.deleteCategory(category) }
    }
}
