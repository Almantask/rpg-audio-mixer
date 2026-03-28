package com.example.rpgaudiomixer.ui.soundscapecomposer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgaudiomixer.domain.model.SoundscapeCategory
import com.example.rpgaudiomixer.domain.model.SoundscapeLayer
import com.example.rpgaudiomixer.domain.storage.SoundscapeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SoundscapeComposerUiState(
    val category: SoundscapeCategory? = null,
    val layers: List<SoundscapeLayer> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class SoundscapeComposerViewModel @Inject constructor(
    private val soundscapeRepository: SoundscapeRepository,
) : ViewModel() {

    fun uiState(categoryId: Long): StateFlow<SoundscapeComposerUiState> = combine(
        soundscapeRepository.getCategoryById(categoryId),
        soundscapeRepository.getLayersForCategory(categoryId),
    ) { category, layers ->
        SoundscapeComposerUiState(
            category = category,
            layers = layers.sortedBy { it.intensity },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SoundscapeComposerUiState(),
    )

    fun addLayer(categoryId: Long, name: String, filePath: String, intensity: Int) {
        viewModelScope.launch {
            soundscapeRepository.insertLayer(
                SoundscapeLayer(
                    categoryId = categoryId,
                    name = name,
                    trackFilePath = filePath,
                    intensity = intensity,
                ),
            )
        }
    }

    fun updateLayerIntensity(layer: SoundscapeLayer, intensity: Int) {
        viewModelScope.launch {
            soundscapeRepository.updateLayer(layer.copy(intensity = intensity))
        }
    }

    fun updateLayerMix(layer: SoundscapeLayer, mix: Float) {
        viewModelScope.launch {
            soundscapeRepository.updateLayer(layer.copy(mix = mix))
        }
    }

    fun removeLayer(layer: SoundscapeLayer) {
        viewModelScope.launch {
            soundscapeRepository.deleteLayer(layer)
        }
    }

    fun saveCategory(category: SoundscapeCategory, name: String) {
        viewModelScope.launch {
            soundscapeRepository.updateCategory(category.copy(name = name))
        }
    }
}
