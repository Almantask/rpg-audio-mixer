package com.example.rpgaudiomixer.test.acceptance.fakes

import com.example.rpgaudiomixer.domain.model.Scene
import com.example.rpgaudiomixer.domain.repository.SceneRepository

class FakeSceneRepository : SceneRepository {

    private val scenes = mutableListOf<Scene>()

    fun setScenes(vararg scenes: Scene) {
        this.scenes.clear()
        this.scenes.addAll(scenes)
    }

    fun clear() {
        scenes.clear()
    }

    override suspend fun getLastOpenedSceneInCampaign(campaignId: String): Scene? {
        return scenes
            .filter { it.campaignId == campaignId }
            .maxByOrNull { it.lastOpenedAt ?: return@maxByOrNull null }
    }

    override suspend fun getSceneById(id: String): Scene? {
        return scenes.find { it.id == id }
    }
}
