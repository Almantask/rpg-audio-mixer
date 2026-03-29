package com.example.rpgaudiomixer.app.ui

import androidx.lifecycle.ViewModel
import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.storage.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScenesViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    fun getScenes(): List<Scene> = repository.getAllScenes()

    fun addSampleScene() {
        repository.addScene(Scene(name = "New Scene"))
    }

    fun addTagToScene(sceneId: String, tag: String) {
        repository.getAllScenes().find { it.id == sceneId }?.let { scene ->
            repository.updateScene(scene.copy(tags = (scene.tags + tag).distinct()))
        }
    }
}
