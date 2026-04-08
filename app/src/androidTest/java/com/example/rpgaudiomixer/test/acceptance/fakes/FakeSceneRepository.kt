package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import java.util.UUID

class FakeSceneRepository : SceneRepository {

    private val scenes = mutableListOf<Scene>()

    fun setScenes(vararg scenes: Scene) {
        this.scenes.clear()
        this.scenes.addAll(scenes)
    }

    fun addScene(scene: Scene) {
        scenes.add(scene)
    }

    fun clear() {
        scenes.clear()
    }

    override suspend fun getLastOpenedSceneInCampaign(campaignId: String): Scene? {
        return scenes
            .filter { it.lastOpenedAt != null }
            .maxByOrNull { it.lastOpenedAt!! }
    }

    override suspend fun getSceneById(id: String): Scene? {
        return scenes.find { it.id == id }
    }

    override suspend fun getAllScenes(): List<Scene> {
        return scenes.toList()
    }

    override suspend fun createScene(name: String, description: String?, tags: List<String>): Scene {
        val newScene = Scene(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            tags = tags
        )
        scenes.add(newScene)
        return newScene
    }

    override suspend fun updateScene(scene: Scene) {
        val index = scenes.indexOfFirst { it.id == scene.id }
        if (index != -1) {
            scenes[index] = scene
        }
    }

    override suspend fun deleteScene(id: String) {
        scenes.removeAll { it.id == id }
    }
}
