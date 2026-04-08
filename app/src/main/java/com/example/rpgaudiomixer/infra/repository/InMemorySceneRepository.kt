package com.example.rpgaudiomixer.infra.repository

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemorySceneRepository @Inject constructor() : SceneRepository {

    private val scenes = mutableListOf<Scene>()

    override suspend fun getLastOpenedSceneInCampaign(campaignId: String): Scene? {
        // For now, return the most recently opened scene globally
        // In a real implementation with sessions, we'd filter by campaign's sessions
        return scenes
            .filter { it.lastOpenedAt != null }
            .maxByOrNull { it.lastOpenedAt!! }
    }

    override suspend fun getSceneById(id: String): Scene? {
        return scenes.firstOrNull { it.id == id }
    }

    override suspend fun getAllScenes(): List<Scene> {
        return scenes.toList()
    }

    override suspend fun createScene(name: String, description: String?, tags: List<String>): Scene {
        val scene = Scene(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            tags = tags
        )
        scenes.add(scene)
        return scene
    }

    override suspend fun updateScene(scene: Scene) {
        val index = scenes.indexOfFirst { it.id == scene.id }
        if (index != -1) {
            scenes[index] = scene
        }
    }

    override suspend fun deleteScene(id: String) {
        scenes.removeIf { it.id == id }
    }
}

