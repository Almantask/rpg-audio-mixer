package com.example.rpgaudiomixer.app.ui

import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.storage.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class ActiveSceneUiState(
    val scene: Scene?,
    val masterVolume: Float = 1f,
    val categoryVolume: Map<String, Float> = emptyMap(),
    val soundboardVolume: Float = 1f,
    val isPlaying: Boolean = false
)

@HiltViewModel
class ActiveSceneViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    fun getScene(sceneId: String): Scene? = repository.getAllScenes().firstOrNull { it.id == sceneId }

    fun getDefaultState(sceneId: String): ActiveSceneUiState {
        val scene = getScene(sceneId)
        val categoryVolume = scene?.soundscapeCategoryIds?.associateWith { 1f } ?: emptyMap()
        return ActiveSceneUiState(scene = scene, categoryVolume = categoryVolume)
    }

    fun getAllSoundscapeCategories() = repository.getAllSoundscapeCategories()

    fun getAllSoundEffects() = repository.getAllSoundEffects()
}

